package org.sunbird.statevalidateutil.tracker;

import org.apache.log4j.Logger;
import org.sunbird.statevalidateutil.LoggerFactory;
import org.sunbird.statevalidateutil.constants.DbColumnConstants;
import org.sunbird.statevalidateutil.constants.EnvConstants;

import java.io.FileWriter;
import java.util.Map;

public class StatusTracker {

    private static Logger logger = LoggerFactory.getLoggerInstance(StatusTracker.class.getName());
    static FileWriter fw;

    public static void startTracingRecord(String id) {
        logger.info("UserId: " + id + " started...");
    }

    public static void endTracingRecord(String id) {
        logger.info("UserId: " + id + " ended...\n");
    }

    public static void logQuery(String query) {
        logger.info(String.format("the insert query generated %s ", query));
    }

    public static void logFailedRecord(Map<String, String> compositeKeysMap) {
        logger.info(String.format("Record Failed with userId:%s", compositeKeysMap.get(DbColumnConstants.userId)));
    }

    public static void logSuccessRecord(String userId, int flagsValue, boolean isStateValidated) {
        logger.info(String.format("Record updation success with userId:%s flagsValue:%d and idType:%s", userId, flagsValue, isStateValidated));
        writeSuccessRecordToFile(userId, flagsValue, isStateValidated);
    }

    public static void logDeletedRecord(Map<String, String> compositeKeysMap) {
        logger.info(String.format("Record deleted with userId:%s", compositeKeysMap.get(DbColumnConstants.userId)));
    }

    public static void logInsertedRecord(String userId, int flagsValue, boolean isStateValidated) {
        logger.info(String.format("Record insertion success with userId:%s flagsValue:%d and isStateValidated:%b",userId,flagsValue, isStateValidated));
    }

    public static void logFailedDeletedRecord(Map<String, String> compositeKeysMap) {
        logger.info(String.format("Record failed to delete with userId: %s", compositeKeysMap.get(DbColumnConstants.userId)));
    }

    /*public static void logExceptionOnProcessingRecord(Map<String, String> compositeKeysMap) {
        logger.error(String.format("Error occurred in  decrypting  record with externalId:%s provider:%s idType:%s ", compositeKeysMap.get(DbColumnConstants.externalId), compositeKeysMap.get(DbColumnConstants.provider), compositeKeysMap.get(DbColumnConstants.idType)));
    }*/

    public static void logTotalRecords(long count) {
        logger.info(String.format("================================ Total Records to be processed: %s ========================================", count));
    }

   /* public static void logPreProcessedRecord(Map<String, String> compositeKeysMap) {
        logger.error(String.format("Record with  externalId:%s provider:%s idType:%s pre processed", compositeKeysMap.get(DbColumnConstants.externalId), compositeKeysMap.get(DbColumnConstants.provider), compositeKeysMap.get(DbColumnConstants.idType)));
    }
    public static void logCorruptedRecord(Map<String, String> compositeKeysMap,String orignalExternalId) {
        logger.info(String.format("SKIPPING the record because corrupted Record found with provider='%s' AND idtype='%s' AND externalid='%s' AND orignalexternalid='%s'", compositeKeysMap.get(DbColumnConstants.provider), compositeKeysMap.get(DbColumnConstants.idType),compositeKeysMap.get(DbColumnConstants.externalId),orignalExternalId));
    }*/

    public static void writeSuccessRecordToFile(String userId, int flagsValue, boolean isStateValidated) {
        try {
            if (fw != null) {
                fw.write(String.format("%s:%d:%b", userId, flagsValue, isStateValidated));
                fw.write("\n");
                fw.flush();
            } else {
                fw = new FileWriter(EnvConstants.PRE_PROCESSED_RECORDS_FILE);
            }
        } catch (Exception e) {
            logger.error(String.format("%s:%s:error occurred while writing preProcessed records to file with message %s", StatusTracker.class.getSimpleName(), "writeSuccessRecordToFile", e.getMessage()));
            System.exit(0);
        }
    }

    public static void closeWriterConnection() {
        try {
            if (fw != null) {
                fw.close();
            }
        } catch (Exception e) {
            logger.error(String.format("%s error occurred while closing connection to file %s and error is %s", "writeSuccessRecordToFile", EnvConstants.PRE_PROCESSED_RECORDS_FILE,e.getMessage()));
        }
    }

}


