package org.sunbird.migration.tracker;

import java.io.FileWriter;
import org.apache.log4j.Logger;
import org.sunbird.migration.LoggerFactory;
import org.sunbird.migration.constants.EnvConstants;

public class StatusTracker {

  private static Logger logger = LoggerFactory.getLoggerInstance(StatusTracker.class.getName());
  static FileWriter fwDeleteSuccess;
  static FileWriter fwDeleteFailure;
  static FileWriter fwUpdateSuccess;
  static FileWriter fwUpdateFailure;
  static FileWriter fwDeleteGroupMemberFailure;
  static FileWriter fwDeleteGroupMemberSuccess;

  public static void startTracingRecord(String id) {
    logger.info(
        "================================ GroupId: "
            + id
            + " started===========================================");
  }

  public static void endTracingUserRecord(String id) {
    logger.info(
        "================================ UserId: "
            + id
            + " ended ===========================================\n");
    logger.info("UserId: " + id + " started...");
  }

  public static void startTracingUserRecord(String id) {
    logger.info(
        "================================ UserId: "
            + id
            + " started===========================================");
  }

  public static void endTracingRecord(String id) {
    logger.info(
        "================================ GroupId: "
            + id
            + " ended ===========================================\n");
    logger.info("UserId: " + id + " started...");
  }

  public static void logDeletedGroupQuery(String query) {
    logger.info(String.format("the delete query generated for groups %s ", query));
  }

  public static void logUserGroupQuery(String query) {
    logger.info(String.format("the query generated for user_group %s ", query));
  }

  public static void logGroupMemberQuery(String query) {
    logger.info(String.format("the query generated for group_member %s ", query));
  }

  public static void logDeleteGroupMemberFailedRecord(String userId, String groupId) {
    logger.info(String.format("Record Failed with userId:%s", userId, groupId));
    writeFailedGroupMemberMarkRecordToFile(userId, groupId);
  }

  public static void logGroupMemberDeleteSuccessQuery(String userId, String groupId) {
    logger.info(String.format("Record passed with userId:%s and groupId:%s", userId, groupId));
    writeGroupMemberSuccessMarkRecordToFile(userId, groupId);
  }

  public static void logDeleteFailedRecord(String groupId) {
    logger.info(String.format("Record Failed with groupId:%s", groupId));
    writeDeleteFailedRecordToFile(groupId);
  }

  public static void logUpdateFailedRecord(String userId) {
    logger.info(String.format("Record Failed with userId:%s", userId));
    writeFailedMarkRecordToFile(userId);
  }

  public static void logUserGroupUpdateSuccessQuery(String userId) {
    logger.info(String.format("Record passed with userId:%s", userId));
    writeSuccessMarkRecordToFile(userId);
  }

  private static void writeGroupMemberSuccessMarkRecordToFile(String userId, String groupId) {
    try {
      if (fwDeleteGroupMemberSuccess == null) {
        fwDeleteGroupMemberSuccess =
            new FileWriter(EnvConstants.PRE_PROCESSED_RECORDS_FILE_DELETED_GROUPS_MEMBER);
      }
      fwDeleteGroupMemberSuccess.write(String.format("%s:%s", userId, groupId));
      fwDeleteGroupMemberSuccess.write("\n");
      fwDeleteGroupMemberSuccess.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeDeleteFailedRecordToFile",
              e.getMessage()));
      System.exit(0);
    }
  }

  private static void writeFailedGroupMemberMarkRecordToFile(String userId, String groupId) {
    try {
      if (fwDeleteGroupMemberFailure == null) {
        fwDeleteGroupMemberFailure =
            new FileWriter(EnvConstants.FAILED_DELETE_GROUP_MEMBER_OPERATION);
      }
      fwDeleteGroupMemberFailure.write(String.format("%s:%s", userId, groupId));
      fwDeleteGroupMemberFailure.write("\n");
      fwDeleteGroupMemberFailure.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeDeleteFailedRecordToFile",
              e.getMessage()));
      System.exit(0);
    }
  }

  private static void writeDeleteFailedRecordToFile(String groupId) {
    try {
      if (fwDeleteFailure == null) {
        fwDeleteFailure = new FileWriter(EnvConstants.FAILED_DELETE_GROUP_OPERATION);
      }
      fwDeleteFailure.write(String.format("%s", groupId));
      fwDeleteFailure.write("\n");
      fwDeleteFailure.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeDeleteFailedRecordToFile",
              e.getMessage()));
      System.exit(0);
    }
  }

  public static void logSuccessfulDeleteRecord(String groupId) {
    logger.info(String.format("Record delete success with groupId:%s", groupId));
    writeDeleteSuccessRecordToFile(groupId);
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

  public static void writeDeleteSuccessRecordToFile(String groupId) {
    try {
      if (fwDeleteSuccess == null) {
        fwDeleteSuccess = new FileWriter(EnvConstants.PRE_PROCESSED_RECORDS_FILE_DELETED_GROUPS);
      }
      fwDeleteSuccess.write(String.format("%s", groupId));
      fwDeleteSuccess.write("\n");
      fwDeleteSuccess.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeDeleteSuccessRecordToFile",
              e.getMessage()));
      System.exit(0);
    }
  }

  public static void writeSuccessMarkRecordToFile(String userId) {
    try {
      if (fwUpdateSuccess == null) {
        fwUpdateSuccess = new FileWriter(EnvConstants.PRE_PROCESSED_UPDATED_USER_GROUPS);
      }
      fwUpdateSuccess.write(String.format("%s", userId));
      fwUpdateSuccess.write("\n");
      fwUpdateSuccess.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeSuccessFulMarkVisitedRecordToFile",
              e.getMessage()));
      System.exit(0);
    }
  }

  public static void writeFailedMarkRecordToFile(String userId) {
    try {
      if (fwUpdateFailure == null) {
        fwUpdateFailure = new FileWriter(EnvConstants.FAILED_UPDATED_USER_GROUP);
      }
      fwUpdateFailure.write(String.format("%s", userId));
      fwUpdateFailure.write("\n");
      fwUpdateFailure.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeSuccessFulMarkVisitedRecordToFile",
              e.getMessage()));
      System.exit(0);
    }
  }

  public static void closeFwSelfDeclaredSuccessWriterConnection() {
    try {
      if (fwDeleteSuccess != null) {
        fwDeleteSuccess.close();
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
      if (fwDeleteFailure != null) {
        fwDeleteFailure.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeFailureRecordToFile Pre-Processed File", e.getMessage()));
    }
  }

  public static void closeUserGroupSuccessWriterConnection() {
    try {
      if (fwUpdateSuccess != null) {
        fwUpdateSuccess.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeFailureRecordToFile Pre-Processed File", e.getMessage()));
    }
  }

  public static void closeUserGroupFailureWriterConnection() {
    try {
      if (fwUpdateFailure != null) {
        fwUpdateFailure.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeFailureRecordToFile Pre-Processed File", e.getMessage()));
    }
  }

  public static void closeGroupMemberSuccessWriterConnection() {
    try {
      if (fwDeleteGroupMemberSuccess != null) {
        fwDeleteGroupMemberSuccess.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeFailureRecordToFile Pre-Processed File", e.getMessage()));
    }
  }

  public static void closeGroupMemberFailureWriterConnection() {
    try {
      if (fwDeleteGroupMemberFailure != null) {
        fwDeleteGroupMemberFailure.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeFailureRecordToFile Pre-Processed File", e.getMessage()));
    }
  }
}
