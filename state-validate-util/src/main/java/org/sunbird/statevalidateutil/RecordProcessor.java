package org.sunbird.statevalidateutil;

import com.datastax.driver.core.ResultSet;

import java.io.IOException;
import java.util.*;

import com.datastax.driver.core.Row;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sunbird.statevalidateutil.connection.Connection;
import org.sunbird.statevalidateutil.connection.factory.ConnectionFactory;
import org.sunbird.statevalidateutil.constants.DbColumnConstants;
import org.sunbird.statevalidateutil.tracker.RecordTracker;
import org.sunbird.statevalidateutil.tracker.StatusTracker;


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
    private List<User> getUserDataFromDbAsList() {
        List<User> usersList = new ArrayList<>();
        ResultSet resultSet = connection.getRecords("select * from user");
        usersList.addAll(CassandraHelper.getUserListFromResultSet(resultSet));
        return usersList;
    }

    /**
     * this method will be used to process externalId
     * this method will get the count of total number of records that need to be processed
     *
     * @return
     */
    public void startProcessingStateValidationAndFlags() throws IOException {
        String custodianOrgId = getSystemSettingCustodianOrgId();
        List<User> usersList = getRecordsToBeProcessed();
        usersList
                .stream()
                .forEach(
                        userObject -> {
                            String userId = userObject.getUserId();
                            try {

                                startTracingRecord(userId);
                                boolean stateValidated = isStateUSer(userObject.getRootOrgId(), custodianOrgId);
                                Map<String, Boolean> userBooleanMap = new HashMap<>();
                                userBooleanMap.put(DbColumnConstants.stateValidated, stateValidated);
                                userBooleanMap.put(DbColumnConstants.emailVerified, userObject.getEmailVerified());
                                userBooleanMap.put(DbColumnConstants.phoneVerified, userObject.getPhoneVerified());
                                userObject.setFlagsValue(calcFieldsValue(userBooleanMap));
                                performSequentialOperationOnRecord(userObject, stateValidated);
                            } catch (Exception e) {
                                logExceptionOnProcessingRecord(userId);
                            } finally {
                                endTracingRecord(userId);
                            }
                        });

        connection.closeConnection();
        closeWriterConnection();
    }

    private int calcFieldsValue(Map<String, Boolean> userBooleanMap) {
        int userFlagValue = 0;
        Set<Map.Entry<String, Boolean>> mapEntry = userBooleanMap.entrySet();
        for(Map.Entry<String, Boolean> entry: mapEntry) {
            if(entry.getValue()) {
                userFlagValue += boolFlagValue(entry.getKey());
            }
        }
        return userFlagValue;
    }

    public int boolFlagValue(String userFlagType) {
        int decimalValue = 0;
        if(userFlagType.equals(UserBoolFlagEnum.PHONE_VERIFIED.getUserFlagType())) {
            decimalValue = UserBoolFlagEnum.PHONE_VERIFIED.getUserFlagValue();
        } else if (userFlagType.equals(UserBoolFlagEnum.EMAIL_VERIFIED.getUserFlagType())) {
            decimalValue = UserBoolFlagEnum.EMAIL_VERIFIED.getUserFlagValue();
        } else if (userFlagType.equals(UserBoolFlagEnum.STATE_VALIDATED.getUserFlagType())) {
            decimalValue = UserBoolFlagEnum.STATE_VALIDATED.getUserFlagValue();
        }
        return decimalValue;
    }

    private boolean isStateUSer(String userRootOrgId, String custodianOrgId) {
        if(!custodianOrgId.equals(userRootOrgId)) {
            return true;
        } else {
            return false;
        }
    }

    private String getSystemSettingCustodianOrgId() {
        String custOrgId = null;
        ResultSet resultSet = connection.getRecords("select value from system_settings where field='custodianOrgId'");
        Iterator<Row> iterator = resultSet.iterator();
        Row row = iterator.next();
        custOrgId = row.getString(0);
        return custOrgId;
    }

    /*private static Map<String, String> getCompositeKeysMap(User user) {
        Map<String, String> compositeKeysMap = new HashMap<>();
        compositeKeysMap.put(DbColumnConstants.userId, user.getUserId());
        return compositeKeysMap;
    }*/

    /**
     * this method will perform sequential operation of db records
     * - generate update query
     * - update record
     * - update Record passes will then delete the record...
     *
     * @param user
     * @param isStateValidated
     */
    private void performSequentialOperationOnRecord(User user, boolean isStateValidated) {

        String query = CassandraHelper.getUpdateRecordQuery(user);
        logQuery(query);
        boolean isRecordUpdated = connection.updateRecord(query);
        if (isRecordUpdated) {
            logSuccessRecord(user.getUserId(), user.getFlagsValue(),isStateValidated);
        } else {
            logFailedRecord(user.getUserId());
        }
    }

    /**
     * this methods
     *
     * @param preProcessedRecords
     * @param totalRecords
     * @return
     */
    private List<User> removePreProcessedRecordFromList(List<String> preProcessedRecords, List<User> totalRecords) {
        totalRecords.removeIf(user -> (preProcessedRecords.contains(user.getUserId())));
        logTotalRecords(totalRecords.size());
        return totalRecords;
    }

    /**
     * this methods get the count of records from cassandra as totalUserList
     * then this method will remove the preprocessed records from totalUserList.
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
