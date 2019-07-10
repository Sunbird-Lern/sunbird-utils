package org.sunbird.decryptionUtil.tracker;

import org.apache.log4j.Logger;
import org.sunbird.decryptionUtil.LoggerFactory;
import org.sunbird.decryptionUtil.constants.DbColumnConstants;

import java.io.FileWriter;
import java.util.Map;

public class StatusTracker {

    private static Logger logger = LoggerFactory.getLoggerInstance(StatusTracker.class.getName());
    static FileWriter fw;

    public static void startTracingRecord(String id) {
        logger.info("================================ UserId: " + id + " started===========================================");
    }

    public static void endTracingRecord(String id) {
        logger.info("================================ UserId: " + id + " ended ===========================================");
    }

    public static void logQuery(String query) {
        logger.info(String.format("the insert query generated %s ", query));
    }

    public static void logFailedRecord(Map<String, String> compositeKeysMap) {
        logger.info(String.format("Record Failed with externalId: %s provider: %s and idType: %s", compositeKeysMap.get(DbColumnConstants.externalId), compositeKeysMap.get(DbColumnConstants.provider), compositeKeysMap.get(DbColumnConstants.idType)));
    }

    public static void logSuccessRecord(String externalId, String provider, String idType) {
        logger.info(String.format("Record updation success with externalId: %s provider: %s and idType: %s", externalId, provider, idType));
        writeSuccessRecordToFile(provider,idType,externalId);
    }

    public static void logDeletedRecord(Map<String, String> compositeKeysMap) {
        logger.info(String.format("Record deleted with externalId: %s provider: %s and idType: %s", compositeKeysMap.get(DbColumnConstants.externalId), compositeKeysMap.get(DbColumnConstants.provider), compositeKeysMap.get(DbColumnConstants.idType)));
    }

    public static void logInsertedRecord(String externalId, String provider, String idType) {
        logger.info(String.format("Record insertion success with externalId: %s provider: %s and idType: %s", externalId, provider, idType));
    }

    public static void logFailedDeletedRecord(Map<String, String> compositeKeysMap) {
        logger.info(String.format("Record failed to delete with externalId: %s provider: %s and idType: %s", compositeKeysMap.get(DbColumnConstants.externalId), compositeKeysMap.get(DbColumnConstants.provider), compositeKeysMap.get(DbColumnConstants.idType)));
    }

    public static void logExceptionOnProcessingRecord(Map<String, String> compositeKeysMap) {
        logger.error(String.format("error occurred while processing record with externalId: %s provider: %s idType: %s", compositeKeysMap.get(DbColumnConstants.externalId), compositeKeysMap.get(DbColumnConstants.provider), compositeKeysMap.get(DbColumnConstants.idType)));
    }

    public static void logTotalRecords(long count) {
        logger.error(String.format("================================ Total Records to be processed: %s ========================================", count));
    }


    public static void writeSuccessRecordToFile(String provider, String idType, String externalId) {
        try {
            if (fw != null) {
                fw.write(String.format("%s:%s:%s", provider, idType, externalId));
            } else {
                fw = new FileWriter("successRecords.txt");
            }

        } catch (Exception e) {
        }
    }

    public static void closeWriterConnection() {
        try {
            fw.close();
        } catch (Exception e) {
        }
    }

}


