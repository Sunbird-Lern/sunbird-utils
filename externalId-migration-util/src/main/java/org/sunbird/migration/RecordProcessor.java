package org.sunbird.migration;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import org.apache.log4j.Logger;
import org.sunbird.migration.connection.Connection;
import org.sunbird.migration.connection.factory.ConnectionFactory;
import org.sunbird.migration.constants.Constants;
import org.sunbird.migration.constants.DbColumnConstants;
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
  private Map<String, String> getOrgDataFromDbAsList() {
    ResultSet resultSet =
        connection.getRecords("select * from organisation where isrootorg=true allow filtering");
    Map<String, String> orgProviderMap = CassandraHelper.getOrgProviderMapFromResultSet(resultSet);
    logger.debug(orgProviderMap.entrySet().iterator().next());
    return orgProviderMap;
  }

  /**
   * this method is responsible to fetch the user external id data from cassandra and the table name
   * is usr_external_identity and after fetching will convert the resultSet to List<User>
   *
   * @return List<User>
   */
  private List<User> getUserDataFromDbAsList() {
    ResultSet resultSet = connection.getRecords("select * from usr_external_identity");
    List<User> usersList = CassandraHelper.getUserListFromResultSet(resultSet);
    logger.debug(usersList.iterator().next());
    return usersList;
  }

  /**
   * this method will be used to process externalId this method will get the count of total number
   * of records that need to be processed
   *
   * @return
   */
  public void startProcessingExternalIds() throws IOException {
    List<User> usersList = getRecordsToBeProcessed();
    Map<String, String> orgIdProviderMap = getOrgDataFromDbAsList();
    logger.info("Org Details loaded, size:" + orgIdProviderMap.size());
    Map<String, List<User>> userIdExternalIdMap = createExternalInfoMapGroupbyUserId(usersList);
    List<UserDeclareEntity> userSelfDeclareLists = new ArrayList<>();
    logger.info("Generate SelfDeclare Lists");
    getUserSelfDeclareLists(userIdExternalIdMap, userSelfDeclareLists);
    int count[] = new int[1];
    logger.info(
        "==================Starting Processing Self Declared Users=======================================");
    userSelfDeclareLists
        .stream()
        .forEach(
            userSelfDeclareObject -> {
              try {
                startTracingRecord(userSelfDeclareObject.getUserId());
                boolean isSuccess =
                    performSequentialOperationOnRecord(userSelfDeclareObject, orgIdProviderMap);
                if (isSuccess) {
                  count[0] += 1;
                }
              } catch (Exception ex) {
                logExceptionOnProcessingRecord(
                    userSelfDeclareObject.getUserId(),
                    userSelfDeclareObject.getProvider(),
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

    List<User> stateUsersList = getUserDataFromDbAsList();
    // Skip records which are not state users
    excludeNonStateUserRecords(stateUsersList);
    // check for state user preprocessed records
    logger.info("Checking state users preprocessed data");
    List<String> preProcessedRecords = RecordTracker.getStateUserPreProcessedRecordsAsList();
    logger.info("total preprocessed state Users " + preProcessedRecords.size());
    List<User> stateUsersToBeProcessed =
        removePreProcessedRecordFromList(preProcessedRecords, stateUsersList);
    logger.info(
        "==================Starting Processing State Users=======================================");
    // Update provider to OrgId
    int countUpdateUser[] = new int[1];
    stateUsersToBeProcessed
        .stream()
        .forEach(
            stateUser -> {
              try {
                startTracingRecord(stateUser.getUserId());
                boolean isSuccess =
                    performSequentialUpdateOperationOnRecord(stateUser, orgIdProviderMap);
                if (isSuccess) {
                  countUpdateUser[0] += 1;
                }
              } catch (Exception ex) {
                logExceptionOnProcessingRecord(
                    stateUser.getUserId(),
                    stateUser.getOriginalProvider(),
                    stateUser.getOriginalIdType());
              } finally {
                endTracingRecord(stateUser.getUserId());
              }
            });
    logger.info(
        "==================Ending Processing State Users=======================================");

    connection.closeConnection();
    closeFwDeleteFailureWriterConnection();
    closeFwInvalidUserWriterConnection();
    closeFwStateUserFailureWriterConnection();
    closeFwStateUserSuccessWriterConnection();
    logger.info(
        "Total Records: "
            + userSelfDeclareLists.size()
            + " Successfully Self Declared Users Migrated: "
            + count[0]
            + " Total Self Declared Users Migration Failed:"
            + (userSelfDeclareLists.size() - count[0]));
    logger.info("Total state users after migration:" + stateUsersList.size());
    logger.info(
        "Total State Users to be Updated: "
            + stateUsersList.size()
            + "  SuccessFully State Users Updated: "
            + countUpdateUser[0]
            + " Total State User Update Failed: "
            + (stateUsersList.size() - countUpdateUser[0]));
  }

  private void excludeNonStateUserRecords(List<User> stateUsersList) {
    Iterator<User> itr = stateUsersList.iterator();
    while (itr.hasNext()) {
      User user = itr.next();
      if (!(user.getProvider() != null
          && user.getIdType() != null
          && user.getOriginalProvider() != null
          && user.getOriginalIdType() != null
          && user.getProvider().equals(user.getIdType()))) {
        logCorruptedRecord(user.getUserId(), user.getIdType(), user.getProvider());
        itr.remove();
      }
    }
  }

  private boolean performSequentialUpdateOperationOnRecord(
      User stateUser, Map<String, String> orgIdProviderMap) {
    try {
      String provider = orgIdProviderMap.get(stateUser.getOriginalProvider());
      if (null == provider) {
        logger.info(
            String.format("Checking channel: %s with uppper case in state users", provider));
        provider = orgIdProviderMap.get(stateUser.getOriginalProvider().toUpperCase());
      }
      if (null != provider) {
        String query = CassandraHelper.getInsertRecordQueryForUser(stateUser, provider);
        logStateUserInsertQuery(query);
        Map<String, String> compositeKeysMap = new HashMap<>();
        compositeKeysMap.put(DbColumnConstants.userId, stateUser.getUserId());
        compositeKeysMap.put(DbColumnConstants.idType, stateUser.getIdType());
        compositeKeysMap.put(DbColumnConstants.provider, stateUser.getProvider());

        boolean isRecordInserted = connection.insertRecord(query);
        if (isRecordInserted) {
          logStateUsersInsertedRecord(
              stateUser.getUserId(), stateUser.getProvider(), stateUser.getIdType());
          boolean isRecordDeleted = connection.deleteRecord(compositeKeysMap);
          if (isRecordDeleted) {
            logDeletedRecord(compositeKeysMap);
            logStateUserSuccessRecord(stateUser.getUserId(), stateUser.getProvider());
            return true;
          } else {
            logFailedDeletedRecord(compositeKeysMap);
            logStateUserFailedRecord(stateUser.getUserId(), stateUser.getProvider());
            return false;
          }
        } else {
          logStateUserFailedRecord(stateUser.getUserId(), stateUser.getProvider());
          return false;
        }
      } else {
        logCorruptedRecord(stateUser.getUserId(), stateUser.getIdType(), stateUser.getProvider());
        return false;
      }
    } catch (Exception ex) {
      logger.error("Error creating object for userId:" + stateUser.getUserId());
      return false;
    }
  }

  private void getUserSelfDeclareLists(
      Map<String, List<User>> userIdExternalIdMap, List<UserDeclareEntity> userSelfDeclareLists) {
    int countUser = 0;
    for (Map.Entry<String, List<User>> userEntry : userIdExternalIdMap.entrySet()) {
      try {
        String userId = userEntry.getKey();
        logger.info("Creating Self Declared Object for user:" + userId);

        List<User> users = userEntry.getValue();
        UserDeclareEntity userDeclareEntity = new UserDeclareEntity();
        userDeclareEntity.setUserId(userId);

        userDeclareEntity.setPersona(Constants.TEACHER);
        userDeclareEntity.setStatus("PENDING");
        userDeclareEntity.setCreatedBy(users.get(0).getCreatedBy());
        if (null != users.get(0).getCreatedOn()) {
          userDeclareEntity.setCreatedOn(new Timestamp(users.get(0).getCreatedOn().getTime()));
        }
        // Get the last updated external Info of userID
        User lastUpdatedRecords = getLastUpdatedRecord(users);
        if (null != lastUpdatedRecords) {
          userDeclareEntity.setUpdatedBy(lastUpdatedRecords.getLastUpdatedBy());
          userDeclareEntity.setUpdatedOn(
              new Timestamp(lastUpdatedRecords.getLastUpdatedOn().getTime()));
        }
        Map<String, Object> userInfo = new HashMap<>();
        // Get the details from orginialidType, originalExternalId as idType and externalId contains
        // info in  lower case
        String provider = null;
        for (User user : users) {
          if (null != user.getOriginalIdType() && user.getOriginalIdType().contains("declared-")) {
            provider = user.getOriginalProvider();
            if (null == provider) {
              break;
            }
            userInfo.put(user.getOriginalIdType(), user.getOriginalExternalId());
          }
        }

        if (null != provider && !userInfo.isEmpty()) {
          // Get the details from orginial provider as provider contains info in  lower case
          userDeclareEntity.setProvider(provider);
          userDeclareEntity.setUserInfo(userInfo);
          userSelfDeclareLists.add(userDeclareEntity);
        }
      } catch (Exception ex) {
        logger.error("Error creating object for userId:" + userEntry.getKey());
      }
    }

    logger.info("Total User Count:" + countUser);
  }

  private User getLastUpdatedRecord(List<User> users) {
    User latestRecordsToBeUpdated = null;
    Date lastUpdatedDate = new Date(Long.MIN_VALUE);
    for (User user : users) {

      if (null != user.getLastUpdatedOn()
          && lastUpdatedDate.compareTo(user.getLastUpdatedOn()) < 0) {
        lastUpdatedDate = user.getLastUpdatedOn();
        latestRecordsToBeUpdated = user;
      }
    }
    return latestRecordsToBeUpdated;
  }

  private Map<String, List<User>> createExternalInfoMapGroupbyUserId(List<User> usersList) {
    Map<String, List<User>> userIdExternalIdMap = new HashMap<>();
    for (User user : usersList) {
      if (userIdExternalIdMap.containsKey(user.getUserId())) {
        List<User> users = userIdExternalIdMap.get(user.getUserId());
        users.add(user);
      } else {
        List<User> users = new ArrayList<>();
        users.add(user);
        userIdExternalIdMap.put(user.getUserId(), users);
      }
    }

    return userIdExternalIdMap;
  }

  /**
   * this method will perform sequential operation of db records - generate insert query - insert
   * record - insert Record passes will then delete record
   *
   * @param userDeclareEntity
   * @param orgProviderMap
   */
  private boolean performSequentialOperationOnRecord(
      UserDeclareEntity userDeclareEntity, Map<String, String> orgProviderMap) {

    try {
      String provider = orgProviderMap.get(userDeclareEntity.getProvider());
      if (null == provider) {
        logger.info(
            String.format("Checking channel: %s with uppper case in self declared", provider));
        provider = orgProviderMap.get(userDeclareEntity.getProvider().toUpperCase());
      }
      // Skip records which contains orgId which no longer exists in the system.
      if (null != provider) {
        String query = CassandraHelper.getInsertRecordQuery(userDeclareEntity);
        PreparedStatement preparedStatement = connection.getSession().prepare(query);
        BoundStatement bs =
            preparedStatement.bind(
                userDeclareEntity.getUserId(),
                provider,
                userDeclareEntity.getPersona(),
                userDeclareEntity.getStatus(),
                userDeclareEntity.getErrorType(),
                userDeclareEntity.getUserInfo(),
                userDeclareEntity.getCreatedBy(),
                CassandraHelper.getTimeStampFromDate(userDeclareEntity.getCreatedOn()),
                userDeclareEntity.getUpdatedBy(),
                CassandraHelper.getTimeStampFromDate(userDeclareEntity.getUpdatedOn()));

        logSelfDeclaredInsertQuery(query);
        connection.getSession().execute(bs);
        logSelfDeclaredInsertedRecord(
            userDeclareEntity.getUserId(), provider, userDeclareEntity.getPersona());
        boolean isUpdateOperation = true;
        for (Map.Entry<String, Object> map : userDeclareEntity.getUserInfo().entrySet()) {
          Map<String, String> keys = new HashMap<>();
          keys.put(DbColumnConstants.userId, userDeclareEntity.getUserId());
          keys.put(DbColumnConstants.idType, map.getKey());
          keys.put(DbColumnConstants.provider, userDeclareEntity.getProvider().toLowerCase());
          boolean isRecordDeleted = connection.deleteRecord(keys);
          if (!isRecordDeleted) {
            isUpdateOperation = false;
            logFailedDeletedRecord(keys);
            logSelfDeclaredFailedRecord(
                userDeclareEntity.getUserId(), userDeclareEntity.getProvider());
          } else {
            logDeletedRecord(keys);
          }
        }
        if (isUpdateOperation) {
          logSelfDeclaredSuccessRecord(
              userDeclareEntity.getUserId(), userDeclareEntity.getProvider());
          return true;
        } else {
          logSelfDeclaredFailedRecord(
              userDeclareEntity.getUserId(), userDeclareEntity.getProvider());
          return false;
        }
      } else {
        // Log reocrds whose org do not exist anymore
        logCorruptedRecord(
            userDeclareEntity.getUserId(),
            userDeclareEntity.getPersona(),
            userDeclareEntity.getProvider());
        return false;
      }
    } catch (Exception ex) {
      logSelfDeclaredFailedRecord(userDeclareEntity.getUserId(), userDeclareEntity.getProvider());
      return false;
    }
  }

  /**
   * this methods
   *
   * @param preProcessedRecords
   * @param totalRecords
   * @return
   */
  private List<User> removePreProcessedRecordFromList(
      List<String> preProcessedRecords, List<User> totalRecords) {
    totalRecords.removeIf(
        user ->
            (preProcessedRecords.contains(
                String.format("%s:%s", user.getUserId(), user.getProvider()))));
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
  private List<User> getRecordsToBeProcessed() throws IOException {
    List<User> totalUsersList = getUserDataFromDbAsList();
    logger.info("total records in db is " + totalUsersList.size());
    List<String> preProcessedRecords = RecordTracker.getSelfDeclaredPreProcessedRecordsAsList();
    logger.info("total records found preprocessed is " + preProcessedRecords.size());
    return removePreProcessedRecordFromList(preProcessedRecords, totalUsersList);
  }
}
