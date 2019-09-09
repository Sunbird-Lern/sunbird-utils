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
                            Map<String, String> compositeKeysMap = getCompositeKeysMap(userObject);
                            try {
                                startTracingRecord(userObject.getUserId());
                                boolean isStateValidated = isStateUSer(userObject.getUserId(), custodianOrgId);
                                Map<String, Object> userBooleanMap = new HashMap<>();
                                userBooleanMap.put("isStateValidated", isStateValidated);
                                userBooleanMap.put("emailVerified", userObject.getEmailVerified());
                                userBooleanMap.put("phoneVerified", userObject.getPhoneVerified());
                                userObject.setFlagsValue(calcFieldsValue(userObject, userBooleanMap));
                               // User user = getDecryptedUserObject(userObject);
                                performSequentialOperationOnRecord(userObject, compositeKeysMap, isStateValidated);
                            } catch (Exception e) {
                                logExceptionOnProcessingRecord(compositeKeysMap);
                            } finally {
                                endTracingRecord(userObject.getUserId());
                            }
                        });

        connection.closeConnection();
        closeWriterConnection();
    }

    private int calcFieldsValue(User user, Map<String, Object> userBooleanMap) {
        int userFlagValue = 0;
        Set<Map.Entry<String, Object>> mapEntry = userBooleanMap.entrySet();
        for(Map.Entry<String, Object> entry: mapEntry) {
            if(StringUtils.isNotEmpty(entry.getKey())) {
                userFlagValue += boolFlagValue(entry.getKey(), (Boolean) entry.getValue());
            }
        }
        return userFlagValue;
    }

    public int boolFlagValue(String userFlagType, boolean isFlagEnabled) {
        int decimalValue = 0;
        if(userFlagType.equals(UserBoolFlagEnum.PHONE_VERIFIED.getUserFlagType()) &&
                isFlagEnabled==UserBoolFlagEnum.PHONE_VERIFIED.isFlagEnabled()) {
            decimalValue = UserBoolFlagEnum.PHONE_VERIFIED.getUserFlagValue();
        } else if (userFlagType.equals(UserBoolFlagEnum.EMAIL_VERIFIED.getUserFlagType()) &&
                isFlagEnabled==UserBoolFlagEnum.EMAIL_VERIFIED.isFlagEnabled()) {
            decimalValue = UserBoolFlagEnum.EMAIL_VERIFIED.getUserFlagValue();
        } else if (userFlagType.equals(UserBoolFlagEnum.IS_STATE_VALIDATED.getUserFlagType()) &&
                isFlagEnabled==UserBoolFlagEnum.IS_STATE_VALIDATED.isFlagEnabled()) {
            decimalValue = UserBoolFlagEnum.IS_STATE_VALIDATED.getUserFlagValue();
        }
        return decimalValue;
    }

    private boolean isStateUSer(String userId, String custodianOrgId) {
        String orgId = null;
        ResultSet resultSet = connection.getRecords("select organisationid from user_org where userid="+userId);
        Iterator<Row> iterator = resultSet.iterator();
        Row row = iterator.next();
        orgId = row.getString(0);
        if(!orgId.equals(custodianOrgId)) {
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

    private static Map<String, String> getCompositeKeysMap(User user) {
        Map<String, String> compositeKeysMap = new HashMap<>();
        compositeKeysMap.put(DbColumnConstants.userId, user.getUserId());
        return compositeKeysMap;
    }

    /**
     * this method will perform sequential operation of db records
     * - generate insert query
     * - decrypt the externalIds and originalExternalIds
     * - insert record
     * - insert Record passes will then delete the record...
     *
     * @param user
     * @param compositeKeysMap
     */
    private void performSequentialOperationOnRecord(User user, Map<String, String> compositeKeysMap, boolean isStateValidated) {

        String query = CassandraHelper.getInsertRecordQuery(user);
        logQuery(query);
        boolean isRecordInserted = connection.insertRecord(query);
        if (isRecordInserted) {
            logInsertedRecord(user.getUserId(), user.getFlagsValue(),isStateValidated);
            logSuccessRecord(user.getUserId(), user.getFlagsValue(),isStateValidated);
        } else {
            logFailedRecord(compositeKeysMap);
        }
    }


    private String getFormattedCompositeKeys(User user) {
        return String.format("%s:", user.getUserId());
    }

    /**
     * this methods
     *
     * @param preProcessedRecords
     * @param totalRecords
     * @return
     */
    private List<User> removePreProcessedRecordFromList(List<String> preProcessedRecords, List<User> totalRecords) {
        totalRecords.removeIf(user -> (preProcessedRecords.contains(getFormattedCompositeKeys(user))));
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
