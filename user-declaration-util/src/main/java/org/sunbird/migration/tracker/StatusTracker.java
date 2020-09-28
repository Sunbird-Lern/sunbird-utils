package org.sunbird.migration.tracker;

import java.io.FileWriter;
import org.apache.log4j.Logger;
import org.sunbird.migration.LoggerFactory;
import org.sunbird.migration.constants.EnvConstants;

public class StatusTracker {

  private static Logger logger = LoggerFactory.getLoggerInstance(StatusTracker.class.getName());
  static FileWriter fwSelfDeclaredSuccess;
  static FileWriter fwSelfDeclaredFailure;

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

  public static void logSelfDeclaredUpdateQuery(String query) {
    logger.info(String.format("the insert query generated for user_declarations %s ", query));
  }

  public static void logStateUserInsertQuery(String query) {
    logger.info(String.format("the insert query generated for usr_external_identity %s ", query));
  }

  public static void logSelfDeclaredFailedRecord(String userId, String orgId, String persona) {
    logger.info(
        String.format("Record Failed with userId:%s orgId:%s persona:%s", userId, orgId, persona));
    writeSelfDeclaredFailedRecordToFile(userId, orgId, persona);
  }

  private static void writeSelfDeclaredFailedRecordToFile(
      String userId, String orgId, String persona) {
    try {
      if (fwSelfDeclaredFailure == null) {
        fwSelfDeclaredFailure = new FileWriter(EnvConstants.FAILED_SELF_DECLARED_MIGRATION_RECORDS);
      }
      fwSelfDeclaredFailure.write(String.format("%s:%s:%s", userId, orgId, persona));
      fwSelfDeclaredFailure.write("\n");
      fwSelfDeclaredFailure.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeSelfDeclaredFailedRecordToFile",
              e.getMessage()));
      System.exit(0);
    }
  }

  public static void logSelfDeclaredSuccessRecord(String userId, String orgId, String persona) {
    logger.info(
        String.format(
            "Record updation success with userId:%s orgId:%s persona:%s", userId, orgId, persona));
    writeSelfDeclaredSuccessRecordToFile(userId, orgId, persona);
  }

  public static void logSelfDeclaredUpdatedRecord(String userId, String orgId, String persona) {
    logger.info(
        String.format(
            "Record insertion success with userId:%s orgId:%s and persona:%s",
            userId, orgId, persona));
  }

  public static void logExceptionOnProcessingRecord(
      String userId, String provider, String persona) {
    logger.error(
        String.format(
            "Error occurred in  decrypting  record with userId:%s provider:%s persona:%s",
            userId, provider, persona));
  }

  public static void logTotalRecords(long count) {
    logger.info(
        String.format(
            "================================ Total Records to be processed: %s ========================================",
            count));
  }

  public static void writeSelfDeclaredSuccessRecordToFile(
      String userId, String orgId, String persona) {
    try {
      if (fwSelfDeclaredSuccess == null) {
        fwSelfDeclaredSuccess =
            new FileWriter(EnvConstants.PRE_PROCESSED_RECORDS_FILE_SELF_DECLARED);
      }
      fwSelfDeclaredSuccess.write(String.format("%s:%s:%s", userId, orgId, persona));
      fwSelfDeclaredSuccess.write("\n");
      fwSelfDeclaredSuccess.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeSelfDeclaredSuccessRecordToFile",
              e.getMessage()));
      System.exit(0);
    }
  }

  public static void closeFwSelfDeclaredSuccessWriterConnection() {
    try {
      if (fwSelfDeclaredSuccess != null) {
        fwSelfDeclaredSuccess.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeSuccessRecordToFile Pre-Processed File", e.getMessage()));
    }
  }

  public static void closeFwSelfDeclaredFailureWriterConnection() {
    try {
      if (fwSelfDeclaredFailure != null) {
        fwSelfDeclaredFailure.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeFailureRecordToFile Pre-Processed File", e.getMessage()));
    }
  }
}
