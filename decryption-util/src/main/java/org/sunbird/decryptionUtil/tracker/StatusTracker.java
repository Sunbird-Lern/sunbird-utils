package org.sunbird.decryptionUtil.tracker;

import java.io.FileWriter;
import java.util.Map;
import org.apache.log4j.Logger;
import org.sunbird.decryptionUtil.LoggerFactory;
import org.sunbird.decryptionUtil.constants.DbColumnConstants;
import org.sunbird.decryptionUtil.constants.EnvConstants;

public class StatusTracker {

  private static Logger logger = LoggerFactory.getLoggerInstance(StatusTracker.class.getName());
  static FileWriter fw;

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

  public static void logFailedRecord(Map<String, String> compositeKeysMap) {
    logger.info(
        String.format(
            "Record Failed with externalId:%s provider:%s and idType:%s",
            compositeKeysMap.get(DbColumnConstants.externalId),
            compositeKeysMap.get(DbColumnConstants.provider),
            compositeKeysMap.get(DbColumnConstants.idType)));
  }

  public static void logSuccessRecord(String externalId, String provider, String idType) {
    logger.info(
        String.format(
            "Record updation success with externalId:%s provider:%s and idType:%s",
            externalId, provider, idType));
    writeSuccessRecordToFile(provider, idType, externalId);
    writeSuccessRecordToFile(provider, idType, externalId);
  }

  public static void logDeletedRecord(Map<String, String> compositeKeysMap) {
    logger.info(
        String.format(
            "Record deleted with externalId:%s provider:%s and idType:%s",
            compositeKeysMap.get(DbColumnConstants.externalId),
            compositeKeysMap.get(DbColumnConstants.provider),
            compositeKeysMap.get(DbColumnConstants.idType)));
  }

  public static void logInsertedRecord(String externalId, String provider, String idType) {
    logger.info(
        String.format(
            "Record insertion success with externalId:%s provider:%s and idType:%s",
            externalId, provider, idType));
  }

  public static void logFailedDeletedRecord(Map<String, String> compositeKeysMap) {
    logger.info(
        String.format(
            "Record failed to delete with externalId: %s provider:%s and idType:%s",
            compositeKeysMap.get(DbColumnConstants.externalId),
            compositeKeysMap.get(DbColumnConstants.provider),
            compositeKeysMap.get(DbColumnConstants.idType)));
  }

  public static void logExceptionOnProcessingRecord(Map<String, String> compositeKeysMap) {
    logger.error(
        String.format(
            "Error occurred in  decrypting  record with externalId:%s provider:%s idType:%s",
            compositeKeysMap.get(DbColumnConstants.externalId),
            compositeKeysMap.get(DbColumnConstants.provider),
            compositeKeysMap.get(DbColumnConstants.idType)));
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

  public static void writeSuccessRecordToFile(String provider, String idType, String externalId) {
    try {
      if (fw != null) {
        fw.write(String.format("%s:%s:%s", provider, idType, externalId));
        fw.write("\n");
        fw.flush();
      } else {
        fw = new FileWriter(EnvConstants.PRE_PROCESSED_RECORDS_FILE);
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(), "writeSuccessRecordToFile", e.getMessage()));
      System.exit(0);
    }
  }

  public static void closeWriterConnection() {
    try {
      if (fw != null) {
        fw.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeSuccessRecordToFile", EnvConstants.PRE_PROCESSED_RECORDS_FILE, e.getMessage()));
    }
  }
}
