package org.sunbird.migration;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import java.io.IOException;
import java.util.*;
import org.apache.log4j.Logger;
import org.sunbird.migration.connection.Connection;
import org.sunbird.migration.connection.factory.ConnectionFactory;
import org.sunbird.migration.tracker.RecordTracker;
import org.sunbird.migration.tracker.StatusTracker;

public class RecordProcessor extends StatusTracker {

  private ConnectionFactory connectionFactory;
  public Connection connection;
  private RequestParams requestParams;
  private static Logger logger = LoggerFactory.getLoggerInstance(RecordProcessor.class.getName());

  /**
   * constructor for the class
   *
   * @param connectionFactory
   * @param requestParams
   */
  private RecordProcessor(ConnectionFactory connectionFactory, RequestParams requestParams) {
    this.connectionFactory = connectionFactory;
    this.requestParams = requestParams;
    this.connection =
        connectionFactory.getConnection(
            requestParams.getCassandraHost(),
            requestParams.getCassandraKeyspaceName(),
            requestParams.getCassandraPort());
  }

  /**
   * this method should be used to get the instance of the class..
   *
   * @param connectionFactory
   * @param requestParams
   * @return
   */
  public static RecordProcessor getInstance(
      ConnectionFactory connectionFactory, RequestParams requestParams) {
    return new RecordProcessor(connectionFactory, requestParams);
  }

  /**
   * this method is responsible to fetch the user external id data from cassandra and the table name
   * is usr_external_identity and after fetching will convert the resultSet to List<User>
   *
   * @return List<User>
   */
  private List<UserDeclareEntity> getUserDataFromDbAsList() {
    ResultSet resultSet = connection.getRecords("select * from user_declarations");
    List<UserDeclareEntity> usersList = CassandraHelper.getUserListFromResultSet(resultSet);
    logger.debug(usersList.iterator().next());
    return usersList;
  }

  /**
   * this method will be used to process externalId this method will get the count of total number
   * of records that need to be processed
   *
   * @return
   */
  public void startProcessingUserDeclarations() throws IOException {
    List<UserDeclareEntity> userSelfDeclareLists = getRecordsToBeProcessed();
    int size = userSelfDeclareLists.size();
    logger.info("Update SelfDeclare Lists");
    Map<String, Integer> characterCount = new HashMap<>();
    updateUserSelfDeclareLists(userSelfDeclareLists, characterCount);
    int count[] = new int[1];
    logger.info(
        "==================Starting Processing Self Declared Users=======================================");
    userSelfDeclareLists
        .stream()
        .forEach(
            userSelfDeclareObject -> {
              try {
                startTracingRecord(userSelfDeclareObject.getUserId());
                boolean isSuccess = performSequentialOperationOnRecord(userSelfDeclareObject);
                if (isSuccess) {
                  count[0] += 1;
                }
              } catch (Exception ex) {
                logExceptionOnProcessingRecord(
                    userSelfDeclareObject.getUserId(),
                    userSelfDeclareObject.getOrgId(),
                    userSelfDeclareObject.getPersona());
              } finally {
                endTracingRecord(userSelfDeclareObject.getUserId());
              }
            });
    logger.info(
        "==================Endingrting Processing Self Declared Users=======================================");

    // Close self declare file writer
    closeFwSelfDeclaredFailureWriterConnection();
    closeFwSelfDeclaredSuccessWriterConnection();

    connection.closeConnection();
    logger.info(
        "Total records: "
            + size
            + " Total Records to be corrected: "
            + userSelfDeclareLists.size()
            + " Successfully Self Declared Users Migrated: "
            + count[0]
            + " Total Self Declared Users Migration Failed:"
            + (userSelfDeclareLists.size() - count[0]));

    for (Map.Entry<String, Integer> character : characterCount.entrySet()) {
      logger.info("Count " + character.getKey() + ": " + character.getValue());
    }
  }

  private void updateUserSelfDeclareLists(
      List<UserDeclareEntity> userSelfDeclareLists, Map<String, Integer> characterCount) {

    Iterator<UserDeclareEntity> itr = userSelfDeclareLists.iterator();
    while (itr.hasNext()) {
      UserDeclareEntity userDeclareEntity = itr.next();
      try {
        boolean removeFlag = true;
        Map<String, Object> userInfo = userDeclareEntity.getUserInfo();
        if (userInfo != null) {
          for (Map.Entry<String, Object> userEntry : userInfo.entrySet()) {
            String key = userEntry.getKey();
            String value = (String) userEntry.getValue();

            //  filtering out all declarations which are intact - i.e., those that don't contain CSV
            // offending characters and status is empty
            if ("declared-ext-id".equals(key)
                || "declared-school-name".equals(key)
                || "declared-school-udise-code".equals(key)) {
              if (value.contains("\"") || value.contains(",") || value.contains("'")) {

                if (value.contains("\"")) {
                  characterCount.put(
                      "DOUBLE_QUOTE",
                      characterCount.get("DOUBLE_QUOTE") == null
                          ? 1
                          : characterCount.get("DOUBLE_QUOTE") + 1);
                }
                if (value.contains("'")) {
                  characterCount.put(
                      "SINGLE_QUOTE",
                      characterCount.get("SINGLE_QUOTE") == null
                          ? 1
                          : characterCount.get("SINGLE_QUOTE") + 1);
                }
                if (value.contains(",")) {
                  characterCount.put(
                      "COMMA",
                      characterCount.get("COMMA") == null ? 1 : characterCount.get("COMMA") + 1);
                }

                value = value.replaceAll("[\"',]", "");
                removeFlag = false;
              }
            }

            userInfo.put(key, value);
          }
          if (userDeclareEntity.getStatus() == null || userDeclareEntity.getStatus().isEmpty()) {
            userDeclareEntity.setStatus("SUBMITTED");
            characterCount.put(
                "NULL_STATUS",
                characterCount.get("NULL_STATUS") == null
                    ? 1
                    : characterCount.get("NULL_STATUS") + 1);
            removeFlag = false;
          }
          // changing PENDING State to SUBMITTED as per new requirement
          if (userDeclareEntity.getStatus() != null
              && "PENDING".equals(userDeclareEntity.getStatus())) {
            userDeclareEntity.setStatus("SUBMITTED");
            characterCount.put(
                "PENDING_STATUS",
                characterCount.get("PENDING_STATUS") == null
                    ? 1
                    : characterCount.get("PENDING_STATUS") + 1);

            removeFlag = false;
          }
          if (removeFlag) {
            itr.remove();
          }
        }
      } catch (Exception ex) {
        logger.error("Error creating object for userId:" + userDeclareEntity.getUserId());
      }
    }
  }

  /**
   * this method will perform sequential operation of db records - generate insert query - insert
   * record - insert Record passes will then delete record
   *
   * @param userDeclareEntity
   */
  private boolean performSequentialOperationOnRecord(UserDeclareEntity userDeclareEntity) {

    try {

      String query = CassandraHelper.getUpdateRecordQuery();
      PreparedStatement preparedStatement = connection.getSession().prepare(query);
      BoundStatement bs =
          preparedStatement.bind(
              userDeclareEntity.getUserInfo(),
              userDeclareEntity.getStatus(),
              userDeclareEntity.getUserId(),
              userDeclareEntity.getOrgId(),
              userDeclareEntity.getPersona());

      logSelfDeclaredUpdateQuery(query);
      connection.getSession().execute(bs);
      logSelfDeclaredSuccessRecord(
          userDeclareEntity.getUserId(),
          userDeclareEntity.getOrgId(),
          userDeclareEntity.getPersona());

    } catch (Exception ex) {
      logSelfDeclaredFailedRecord(
          userDeclareEntity.getUserId(),
          userDeclareEntity.getOrgId(),
          userDeclareEntity.getPersona());
      return false;
    }
    return true;
  }

  /**
   * this methods
   *
   * @param preProcessedRecords
   * @param totalRecords
   * @return
   */
  private List<UserDeclareEntity> removePreProcessedRecordFromList(
      List<String> preProcessedRecords, List<UserDeclareEntity> totalRecords) {
    totalRecords.removeIf(
        user ->
            (preProcessedRecords.contains(
                String.format("%s:%s:%s", user.getUserId(), user.getOrgId(), user.getPersona()))));
    logTotalRecords(totalRecords.size());
    return totalRecords;
  }

  /**
   * this methods get the count of records from cassandra as totalUserList then this method will
   * remove the preprocessed records from totalUserList.
   *
   * @return List<String>
   * @throws IOException
   */
  private List<UserDeclareEntity> getRecordsToBeProcessed() throws IOException {
    List<UserDeclareEntity> totalUsersList = getUserDataFromDbAsList();
    logger.info("total records in db is " + totalUsersList.size());
    List<String> preProcessedRecords = RecordTracker.getSelfDeclaredPreProcessedRecordsAsList();
    logger.info("total records found preprocessed is " + preProcessedRecords.size());
    return removePreProcessedRecordFromList(preProcessedRecords, totalUsersList);
  }
}
