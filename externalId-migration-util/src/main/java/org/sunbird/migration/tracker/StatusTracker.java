package org.sunbird.migration.tracker;

import java.io.FileWriter;
import java.util.Map;
import org.apache.log4j.Logger;
import org.sunbird.migration.LoggerFactory;
import org.sunbird.migration.constants.DbColumnConstants;
import org.sunbird.migration.constants.EnvConstants;

public class StatusTracker {

  private static Logger logger = LoggerFactory.getLoggerInstance(StatusTracker.class.getName());
  static FileWriter fw1;
  static FileWriter fw2;

  public static void startTracingRecord(String id) {
    logger.info(
        "================================ UserId: "
            + id
            + " started===========================================");
  }

  public static void endTracingRecord(String id) {
    logger.info(
        "================================ UserId: "
            + id
            + " ended ===========================================\n");
    logger.info("UserId: " + id + " started...");
  }

  public static void logQuery(String query) {
    logger.info(String.format("the insert query generated %s ", query));
  }

  public static void logFailedRecord(String userId, String provider) {
    logger.info(
        String.format(
            "Record Failed with userId:%s provider:%s",
           userId,provider));
  }

  public static void logSelfDeclaredSuccessRecord(String userId, String provider) {
    logger.info(
        String.format(
            "Record updation success with userId:%s provider:%s",
                userId, provider));
    writeSelfDeclaredSuccessRecordToFile(userId, provider);
  }
  public static void logStateUserSuccessRecord(String userId, String provider) {
    logger.info(
            String.format(
                    "Record updation success with userId:%s provider:%s",
                    userId, provider));
    writeStateUserSuccessRecordToFile(userId, provider);
  }

  public static void logDeletedRecord(Map<String, String> compositeKeysMap) {
    logger.info(
        String.format(
            "Record deleted with externalId:%s provider:%s and idType:%s",
            compositeKeysMap.get(DbColumnConstants.userId),
            compositeKeysMap.get(DbColumnConstants.provider),
            compositeKeysMap.get(DbColumnConstants.idType)));
  }

  public static void logInsertedRecord(String userId, String orgId, String persona) {
    logger.info(
        String.format(
            "Record insertion success with userId:%s orgId:%s and persona:%s",
                userId, orgId, persona));
  }

  public static void logFailedDeletedRecord(Map<String, String> compositeKeysMap) {
    logger.info(
        String.format(
            "Record failed to delete with userId: %s provider:%s and idType:%s",
            compositeKeysMap.get(DbColumnConstants.userId),
            compositeKeysMap.get(DbColumnConstants.provider),
            compositeKeysMap.get(DbColumnConstants.idType)));
  }

  public static void logExceptionOnProcessingRecord(String userId,String provider, String persona) {
    logger.error(
        String.format(
            "Error occurred in  decrypting  record with userId:%s provider:%s persona:%s",
          userId,provider,persona));
  }

  public static void logTotalRecords(long count) {
    logger.info(
        String.format(
            "================================ Total Records to be processed: %s ========================================",
            count));
  }

  public static void logPreProcessedRecord(Map<String, String> compositeKeysMap) {
    logger.error(
        String.format(
            "Record with  externalId:%s provider:%s idType:%s pre processed",
            compositeKeysMap.get(DbColumnConstants.externalId),
            compositeKeysMap.get(DbColumnConstants.provider),
            compositeKeysMap.get(DbColumnConstants.idType)));
  }

  public static void logCorruptedRecord(
      Map<String, String> compositeKeysMap, String orignalExternalId) {
    logger.info(
        String.format(
            "SKIPPING the record because corrupted Record found with provider='%s' AND idtype='%s' AND externalid='%s' AND orignalexternalid='%s'",
            compositeKeysMap.get(DbColumnConstants.provider),
            compositeKeysMap.get(DbColumnConstants.idType),
            compositeKeysMap.get(DbColumnConstants.externalId),
            orignalExternalId));
  }

  public static void writeSelfDeclaredSuccessRecordToFile(String userId, String provider) {
    try {
      if (fw1 == null) {
        fw1 = new FileWriter(EnvConstants.PRE_PROCESSED_RECORDS_FILE_SELF_DECLARED);
      }
        fw1.write(String.format("%s:%s",userId, provider));
        fw1.write("\n");
        fw1.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(), "writeSelfDeclaredSuccessRecordToFile", e.getMessage()));
      System.exit(0);
    }
  }

  public static void writeStateUserSuccessRecordToFile(String userId, String provider) {
    try {
      if (fw2 == null) {
        fw2 = new FileWriter(EnvConstants.PRE_PROCESSED_RECORDS_FILE_STATE_USERS);
      }
      fw2.write(String.format("%s:%s",userId, provider));
      fw2.write("\n");
      fw2.flush();
    } catch (Exception e) {
      logger.error(
              String.format(
                      "%s:%s:error occurred while writing preProcessed records to file with message %s",
                      StatusTracker.class.getSimpleName(), "writeStateUserSuccessRecordToFile", e.getMessage()));
      System.exit(0);
    }
  }

  public static void closeFw1WriterConnection() {
    try {
      if (fw1 != null) {
        fw1.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeSuccessRecordToFile Pre-Processed File", e.getMessage()));
    }
  }

  public static void closeFw2WriterConnection() {
    try {
      if (fw2 != null) {
        fw2.close();
      }
    } catch (Exception e) {
      logger.error(
              String.format(
                      "%s error occurred while closing connection to file %s and error is %s",
                      "writeSuccessRecordToFile Pre-Processed File", e.getMessage()));
    }
  }
}
