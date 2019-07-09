package org.sunbird.externaliddecryption;

import com.datastax.driver.core.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.sunbird.externaliddecryption.connection.Connection;
import org.sunbird.externaliddecryption.connection.factory.ConnectionFactory;
import org.sunbird.externaliddecryption.constants.DbColumnConstants;
import org.sunbird.externaliddecryption.decryption.DecryptionService;
import org.sunbird.externaliddecryption.decryption.DefaultDecryptionServiceImpl;

public class DataProcessor {

  private ConnectionFactory connectionFactory;
  private Connection connection;
  private RequestParams requestParams;
  private DecryptionService decryptionService;
  private static Logger logger = LoggerFactory.getLoggerInstance(DataProcessor.class.getName());

  /**
   * constructor for the class
   *
   * @param connectionFactory
   * @param requestParams
   */
  private DataProcessor(ConnectionFactory connectionFactory, RequestParams requestParams) {
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
  public static DataProcessor getInstance(
      ConnectionFactory connectionFactory, RequestParams requestParams) {
    return new DataProcessor(connectionFactory, requestParams);
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
    return usersList;
  }

  /**
   * this method will be used to process externalId
   *
   * @return
   */
  public void startProcessingExternalIds() {
    List<User> usersList = getUserDataFromDbAsList();
    logger.info("the data got is " + usersList.get(0).getOriginalExternalId());
    usersList
        .stream()
        .forEach(
            userObject -> {
              Map<String, String> compositeKeysMap = getCompositeKeysMap(userObject);
              boolean isRecordDeleted = connection.deleteRecord(compositeKeysMap);
              logger.info("is record deleted " + isRecordDeleted);
              String externalId = decryptionService.decryptData(userObject.getExternalId());
              String originalExternalId =
                  decryptionService.decryptData(userObject.getOriginalExternalId());
              userObject.setExternalId(externalId);
              userObject.setOriginalExternalId(originalExternalId);
              String query = CassandraHelper.getInsertRecordQuery(userObject);
              logger.info("the insert qeury is " + query);
              connection.insertRecord(query);
            });
    connection.closeConnection();
  }

  private static Map<String, String> getCompositeKeysMap(User user) {
    Map<String, String> compositeKeysMap = new HashMap<>();
    compositeKeysMap.put(DbColumnConstants.provider, user.getProvider());
    compositeKeysMap.put(DbColumnConstants.idType, user.getIdType());
    compositeKeysMap.put(DbColumnConstants.externalId, user.getExternalId());
    return compositeKeysMap;
  }
}
