package org.sunbird.decryptionUtil;

import com.datastax.driver.core.ResultSet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sunbird.decryptionUtil.connection.Connection;
import org.sunbird.decryptionUtil.connection.factory.ConnectionFactory;
import org.sunbird.decryptionUtil.constants.DbColumnConstants;
import org.sunbird.decryptionUtil.decryption.DecryptionService;
import org.sunbird.decryptionUtil.decryption.DefaultDecryptionServiceImpl;
import org.sunbird.decryptionUtil.tracker.RecordTracker;
import org.sunbird.decryptionUtil.tracker.StatusTracker;

public class RecordProcessor extends StatusTracker {

  private ConnectionFactory connectionFactory;
  private Connection connection;
  private RequestParams requestParams;
  private DecryptionService decryptionService;
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
    this.decryptionService =
        new DefaultDecryptionServiceImpl(requestParams.getSunbirdEncryptionKey());
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
    usersList
        .stream()
        .forEach(
            userObject -> {
              Map<String, String> compositeKeysMap = getCompositeKeysMap(userObject);
              try {
                startTracingRecord(userObject.getUserId());
                User user = getDecryptedUserObject(userObject);
                performSequentialOperationOnRecord(user, compositeKeysMap);
              } catch (Exception e) {
                logExceptionOnProcessingRecord(compositeKeysMap);
              } finally {
                endTracingRecord(userObject.getUserId());
              }
            });

    connection.closeConnection();
    closeWriterConnection();
  }

  private static Map<String, String> getCompositeKeysMap(User user) {
    Map<String, String> compositeKeysMap = new HashMap<>();
    compositeKeysMap.put(DbColumnConstants.provider, user.getProvider());
    compositeKeysMap.put(DbColumnConstants.idType, user.getIdType());
    compositeKeysMap.put(DbColumnConstants.externalId, user.getExternalId());
    return compositeKeysMap;
  }

  /**
   * this method is responsible to decrypt the externalId and originalExternalId if
   * originalExternalId is null or absent it will ignore it and decrypt only externalId.
   *
   * @param userObject
   * @return userObject
   * @throws BadPaddingException
   * @throws IOException
   * @throws IllegalBlockSizeException
   */
  private User getDecryptedUserObject(User userObject)
      throws BadPaddingException, IOException, IllegalBlockSizeException {
    String externalId = decryptionService.decryptData(userObject.getExternalId());
    if (StringUtils.isNotBlank(userObject.getOriginalExternalId())) {
      String originalExternalId = decryptionService.decryptData(userObject.getOriginalExternalId());
      userObject.setOriginalExternalId(originalExternalId);
    }
    userObject.setExternalId(externalId);
    return userObject;
  }

  /**
   * this method will perform sequential operation of db records - generate insert query - decrypt
   * the externalIds and originalExternalIds - insert record - insert Record passes will then delete
   * the record...
   *
   * @param decryptedUserObject
   * @param compositeKeysMap
   */
  private void performSequentialOperationOnRecord(
      User decryptedUserObject, Map<String, String> compositeKeysMap) {

    String query = CassandraHelper.getInsertRecordQuery(decryptedUserObject);
    logQuery(query);
    boolean isRecordInserted = connection.insertRecord(query);
    if (isRecordInserted) {
      logInsertedRecord(
          decryptedUserObject.getExternalId(),
          decryptedUserObject.getProvider(),
          decryptedUserObject.getIdType());
      boolean isRecordDeleted = connection.deleteRecord(compositeKeysMap);
      if (isRecordDeleted) {
        logDeletedRecord(compositeKeysMap);
        logSuccessRecord(
            decryptedUserObject.getExternalId(),
            decryptedUserObject.getProvider(),
            decryptedUserObject.getIdType());
      } else {
        logFailedDeletedRecord(compositeKeysMap);
        logFailedRecord(compositeKeysMap);
      }
    } else {
      logFailedRecord(compositeKeysMap);
    }
  }

  private String getFormattedCompositeKeys(User user) {
    return String.format("%s:%s:%s", user.getProvider(), user.getIdType(), user.getExternalId());
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
    totalRecords.removeIf(user -> (preProcessedRecords.contains(getFormattedCompositeKeys(user))));
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
    List<String> preProcessedRecords = RecordTracker.getPreProcessedRecordsAsList();
    logger.info("total records found preprocessed is " + preProcessedRecords.size());
    return removePreProcessedRecordFromList(preProcessedRecords, totalUsersList);
  }
}
