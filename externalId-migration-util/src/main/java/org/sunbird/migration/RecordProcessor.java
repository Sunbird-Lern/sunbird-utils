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
    private Connection connection;
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
    private Map<String,String> getOrgDataFromDbAsList() {
        ResultSet resultSet = connection.getRecords("select * from organisation");
        Map<String,String> orgProviderMap = CassandraHelper.getOrgProviderMapFromResultSet(resultSet);
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
        Map<String,String> orgIdProviderMap = getOrgDataFromDbAsList();
        Map<String,List<User>> userIdExternalIdMap = createExternalInfoMapGroupbyUserId(usersList);

        List<UserDeclareEntity> userSelfDeclareLists = new ArrayList<>();
        getUserSelfDeclareLists(userIdExternalIdMap, userSelfDeclareLists);
        userSelfDeclareLists
                .stream()
                .forEach(
                        userSelfDeclareObject->{
                            try{
                                startTracingRecord(userSelfDeclareObject.getUserId());
                                performSequentialOperationOnRecord(userSelfDeclareObject,orgIdProviderMap);
                            }catch (Exception ex){
                                logExceptionOnProcessingRecord(userSelfDeclareObject.getUserId(),userSelfDeclareObject.getProvider(),
                                        userSelfDeclareObject.getPersona());
                            }finally {
                                endTracingRecord(userSelfDeclareObject.getUserId());
                            }
                 });
        connection.closeConnection();
        closeWriterConnection();

    }

    private void getUserSelfDeclareLists(Map<String, List<User>> userIdExternalIdMap, List<UserDeclareEntity> userSelfDeclareLists) {
        for (Map.Entry<String,List<User>> userEntry: userIdExternalIdMap.entrySet()){
            String userId = userEntry.getKey();
            List<User> users = userEntry.getValue();
            UserDeclareEntity userDeclareEntity = new UserDeclareEntity();
            userDeclareEntity.setUserId(userId);
            userDeclareEntity.setProvider(users.get(0).getOriginalProvider());
            userDeclareEntity.setPersona(Constants.TEACHER);
            userDeclareEntity.setCreatedBy(users.get(0).getCreatedBy());
            if(null != users.get(0).getCreatedOn()) {
                userDeclareEntity.setCreatedOn(new Timestamp(users.get(0).getCreatedOn().getTime()));
            }
            //Get the last updated external Info of userID
            User lastUpdatedRecords = getLastUpdatedRecord(users);
            if(null !=lastUpdatedRecords){
                userDeclareEntity.setUpdatedBy(lastUpdatedRecords.getLastUpdatedBy());
                userDeclareEntity.setUpdatedOn(new Timestamp(lastUpdatedRecords.getLastUpdatedOn().getTime()));
            }
            Map<String,Object> userInfo = new HashMap<>();
            for (User user: users){
                if(user.getOriginalIdType().contains("declared-")) {
                    userInfo.put(user.getOriginalExternalId(),user.getOriginalExternalId());
                }
            }
            userDeclareEntity.setUserInfo(userInfo);
            userSelfDeclareLists.add(userDeclareEntity);
        }
    }

    private User getLastUpdatedRecord(List<User> users) {
        User latestRecordsToBeUpdated = null;
        Date lastUpdatedDate = new Date(Long.MIN_VALUE);
        for (User user: users){
            if(lastUpdatedDate.compareTo(user.getLastUpdatedOn())<0){
                lastUpdatedDate = user.getLastUpdatedOn();
                latestRecordsToBeUpdated = user;
            }
        }
        return latestRecordsToBeUpdated;
    }

    private Map<String,List<User>> createExternalInfoMapGroupbyUserId(List<User> usersList) {
        Map<String,List<User>> userIdExternalIdMap = new HashMap<>();
        for (User user: usersList) {
            if(userIdExternalIdMap.containsKey(user.getUserId())){
                 List<User> users = userIdExternalIdMap.get(user.getUserId());
                 users.add(user);
            }else{
                List<User> users = new ArrayList<>();
                users.add(user);
                userIdExternalIdMap.put(user.getUserId(),users);
            }
        }

        return userIdExternalIdMap;
    }


    /**
     * this method will perform sequential operation of db records - generate insert query - decrypt
     * the externalIds and originalExternalIds - insert record - insert Record passes will then delete
     * the record...
     *
     * @param userDeclareEntity
     * @param orgProviderMap
     */
    private void performSequentialOperationOnRecord(
            UserDeclareEntity userDeclareEntity, Map<String,String> orgProviderMap) {

        try {
            String query = CassandraHelper.getInsertRecordQuery(userDeclareEntity);
            PreparedStatement preparedStatement = connection.getSession().prepare(query);
            BoundStatement bs = preparedStatement.bind(userDeclareEntity.getUserId(), orgProviderMap.get(userDeclareEntity.getProvider()), userDeclareEntity.getPersona(), userDeclareEntity.getStatus(),
                    userDeclareEntity.getErrorType(), userDeclareEntity.getUserInfo(), userDeclareEntity.getCreatedBy(),
                    CassandraHelper.getTimeStampFromDate(userDeclareEntity.getCreatedOn()), userDeclareEntity.getUpdatedBy(), CassandraHelper.getTimeStampFromDate(userDeclareEntity.getUpdatedOn()));


            logQuery(query);
            connection.getSession().execute(bs);
            logInsertedRecord(userDeclareEntity.getUserId(),orgProviderMap.get(userDeclareEntity.getProvider()),userDeclareEntity.getPersona());
            boolean isUpdateOperation = true;
            for (Map.Entry<String,Object> map: userDeclareEntity.getUserInfo().entrySet()) {
                Map<String, String> keys = new HashMap<>();
                keys.put(DbColumnConstants.userId, userDeclareEntity.getUserId());
                keys.put(DbColumnConstants.idType, map.getKey());
                keys.put(DbColumnConstants.provider,userDeclareEntity.getOrgId());
                boolean isRecordDeleted = connection.deleteRecord(keys);
                if (!isRecordDeleted) {
                    isUpdateOperation = false;
                    logFailedDeletedRecord(keys);
                } else {
                    logDeletedRecord(keys);
                }
            }
            if(isUpdateOperation) {
                logSuccessRecord(userDeclareEntity.getUserId(),userDeclareEntity.getOrgId(),userDeclareEntity.getPersona());
            }else {
                logFailedRecord(userDeclareEntity.getUserId(),userDeclareEntity.getOrgId(),userDeclareEntity.getPersona());
            }
        }catch (Exception ex) {
           logFailedRecord(userDeclareEntity.getUserId(),userDeclareEntity.getOrgId(),userDeclareEntity.getPersona());
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
