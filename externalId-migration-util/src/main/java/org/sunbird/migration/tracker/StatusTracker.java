package org.sunbird.migration.tracker;

import java.io.FileWriter;
import java.util.Map;
import org.apache.log4j.Logger;
import org.sunbird.migration.LoggerFactory;
import org.sunbird.migration.constants.DbColumnConstants;
import org.sunbird.migration.constants.EnvConstants;

public class StatusTracker {

  private static Logger logger = LoggerFactory.getLoggerInstance(StatusTracker.class.getName());
  static FileWriter fwSelfDeclaredSuccess;
  static FileWriter fwSelfDeclaredFailure;
  static FileWriter fwStateUserSuccess;
  static FileWriter fwStateUserFailure;
  static FileWriter fwInvalidRecords;
  static FileWriter fwDeleteFailedRecords;

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

  public static void logSelfDeclaredInsertQuery(String query) {
    logger.info(String.format("the insert query generated for user_declarations %s ", query));
  }

  public static void logStateUserInsertQuery(String query) {
    logger.info(String.format("the insert query generated for usr_external_identity %s ", query));
  }

  public static void logSelfDeclaredFailedRecord(String userId, String provider) {
    logger.info(String.format("Record Failed with userId:%s provider:%s", userId, provider));
    writeSelfDeclaredFailedRecordToFile(userId, provider);
  }

  private static void writeSelfDeclaredFailedRecordToFile(String userId, String provider) {
    try {
      if (fwSelfDeclaredFailure == null) {
        fwSelfDeclaredFailure = new FileWriter(EnvConstants.FAILED_SELF_DECLARED_MIGRATION_RECORDS);
      }
      fwSelfDeclaredFailure.write(String.format("%s:%s", userId, provider));
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

  private static void writeDeleteFailedRecordToFile(String userId, String idType, String provider) {
    try {
      if (fwDeleteFailedRecords == null) {
        fwDeleteFailedRecords = new FileWriter(EnvConstants.DELETE_FAILED_RECORDS);
      }
      fwDeleteFailedRecords.write(String.format("%s:%s", userId, provider));
      fwDeleteFailedRecords.write("\n");
      fwDeleteFailedRecords.flush();
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

  public static void logStateUserFailedRecord(String userId, String provider) {
    logger.info(String.format("Record Failed with userId:%s provider:%s", userId, provider));
    writeStateUserFailedRecordToFile(userId, provider);
  }

  private static void writeStateUserFailedRecordToFile(String userId, String provider) {
    try {
      if (fwStateUserFailure == null) {
        fwStateUserFailure = new FileWriter(EnvConstants.FAILED_STATE_MIGRATION_RECORDS);
      }
      fwStateUserFailure.write(String.format("%s:%s", userId, provider));
      fwStateUserFailure.write("\n");
      fwStateUserFailure.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeStateUserFailedRecordToFile",
              e.getMessage()));
      System.exit(0);
    }
  }

  public static void logSelfDeclaredSuccessRecord(String userId, String provider) {
    logger.info(
        String.format("Record updation success with userId:%s provider:%s", userId, provider));
    writeSelfDeclaredSuccessRecordToFile(userId, provider);
  }

  public static void logStateUserSuccessRecord(String userId, String provider) {
    logger.info(
        String.format("Record updation success with userId:%s provider:%s", userId, provider));
    writeStateUserSuccessRecordToFile(userId, provider);
  }

  public static void logDeletedRecord(Map<String, String> compositeKeysMap) {
    logger.info(
        String.format(
            "Record deleted with userId:%s provider:%s and idType:%s",
            compositeKeysMap.get(DbColumnConstants.userId),
            compositeKeysMap.get(DbColumnConstants.provider),
            compositeKeysMap.get(DbColumnConstants.idType)));
  }

  public static void logSelfDeclaredInsertedRecord(String userId, String orgId, String persona) {
    logger.info(
        String.format(
            "Record insertion success with userId:%s orgId:%s and persona:%s",
            userId, orgId, persona));
  }

  public static void logStateUsersInsertedRecord(String userId, String idType, String provider) {
    logger.info(
        String.format(
            "Record insertion success with userId:%s idType:%s and provider:%s",
            userId, idType, provider));
  }

  public static void logFailedDeletedRecord(Map<String, String> compositeKeysMap) {
    logger.info(
        String.format(
            "Record failed to delete with userId: %s provider:%s and idType:%s",
            compositeKeysMap.get(DbColumnConstants.userId),
            compositeKeysMap.get(DbColumnConstants.provider),
            compositeKeysMap.get(DbColumnConstants.idType)));
    writeDeleteFailedRecordToFile(
        compositeKeysMap.get(DbColumnConstants.userId),
        compositeKeysMap.get(DbColumnConstants.idType),
        compositeKeysMap.get(DbColumnConstants.provider));
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

  public static void logPreProcessedRecord(Map<String, String> compositeKeysMap) {
    logger.error(
        String.format(
            "Record with  externalId:%s provider:%s idType:%s pre processed",
            compositeKeysMap.get(DbColumnConstants.externalId),
            compositeKeysMap.get(DbColumnConstants.provider),
            compositeKeysMap.get(DbColumnConstants.idType)));
  }

  public static void logCorruptedRecord(String userId, String idType, String provider) {
    logger.info(
        String.format(
            "SKIPPING the record because corrupted Record found with userid='%s' AND idType= '%s' AND provider='%s'",
            userId, idType, provider));
    writeInvalidRecordToFile(userId, idType, provider);
  }

  public static void writeSelfDeclaredSuccessRecordToFile(String userId, String provider) {
    try {
      if (fwSelfDeclaredSuccess == null) {
        fwSelfDeclaredSuccess =
            new FileWriter(EnvConstants.PRE_PROCESSED_RECORDS_FILE_SELF_DECLARED);
      }
      fwSelfDeclaredSuccess.write(String.format("%s:%s", userId, provider));
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

  public static void writeStateUserSuccessRecordToFile(String userId, String provider) {
    try {
      if (fwStateUserSuccess == null) {
        fwStateUserSuccess = new FileWriter(EnvConstants.PRE_PROCESSED_RECORDS_FILE_STATE_USERS);
      }
      fwStateUserSuccess.write(String.format("%s:%s", userId, provider));
      fwStateUserSuccess.write("\n");
      fwStateUserSuccess.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeStateUserSuccessRecordToFile",
              e.getMessage()));
      System.exit(0);
    }
  }

  public static void writeInvalidRecordToFile(String userId, String idType, String provider) {
    try {
      if (fwInvalidRecords == null) {
        fwInvalidRecords = new FileWriter(EnvConstants.INVALID_RECORDS);
      }
      fwInvalidRecords.write(String.format("%s:%s:%s", userId, provider, idType));
      fwInvalidRecords.write("\n");
      fwInvalidRecords.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeInvalidUserSuccessRecordToFile",
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

  public static void closeFwStateUserSuccessWriterConnection() {
    try {
      if (fwStateUserSuccess != null) {
        fwStateUserSuccess.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeSuccessRecordToFile Pre-Processed File", e.getMessage()));
    }
  }

  public static void closeFwInvalidUserWriterConnection() {
    try {
      if (fwInvalidRecords != null) {
        fwInvalidRecords.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeInvalidRecordToFile Pre-Processed File", e.getMessage()));
    }
  }

  public static void closeFwStateUserFailureWriterConnection() {
    try {
      if (fwStateUserFailure != null) {
        fwStateUserFailure.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeFailureRecordToFile Pre-Processed File", e.getMessage()));
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

  public static void closeFwDeleteFailureWriterConnection() {
    try {
      if (fwDeleteFailedRecords != null) {
        fwDeleteFailedRecords.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeDeleteFailureRecordToFile Pre-Processed File", e.getMessage()));
    }
  }
}
