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

    public static void logFailedRecord(String userId) {
        logger.info(String.format("Record Failed with userId:%s", userId));
    }

    public static void logSuccessRecord(String userId, int flagsValue, boolean isStateValidated) {
        logger.info(String.format("Record inserted success with userId:%s flagsValue:%d and isStateValidated:%s", userId, flagsValue, isStateValidated));
        writeSuccessRecordToFile(userId, flagsValue, isStateValidated);
    }

    public static void logExistingRecord(String userId, int flagsValue) {
        logger.info(String.format("Existing record with userId:%s and flagsValue:%d", userId, flagsValue));
    }

    public static void logExceptionOnProcessingRecord(String userId) {
        logger.error(String.format("Error occurred in  processing state validation record with externalId:%s ", userId));
    }

    public static void logTotalRecords(long count) {
        logger.info(String.format("================================ Total Records to be processed: %s ========================================", count));
    }

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


