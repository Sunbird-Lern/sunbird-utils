package org.sunbird.migration.tracker;

import java.io.FileWriter;
import org.apache.log4j.Logger;
import org.sunbird.migration.LoggerFactory;
import org.sunbird.migration.constants.EnvConstants;

public class StatusTracker {

  private static Logger logger = LoggerFactory.getLoggerInstance(StatusTracker.class.getName());
  static FileWriter feedInsertSuccess;
  static FileWriter feedInsertFailure;


  public static void endTracingUserRecord(String id) {
    logger.info(
        "================================ FeedId: "
            + id
            + " ended ===========================================\n");
    logger.info("FeedId: " + id + " Stopped...");
  }

  public static void startTracingUserRecord(String id) {
    logger.info(
        "================================ FeedId: "
            + id
            + " started===========================================");
  }


  public static void logUpdateFailedRecord(String feedId) {
    logger.info(String.format("Record Failed with feedId:%s", feedId));
    writeFailedMarkRecordToFile(feedId);
  }

  public static void logUserFeedUpdateSuccessQuery(String feedId) {
    logger.info(String.format("Record passed with feedId:%s", feedId));
    writeSuccessMarkRecordToFile(feedId);
  }


  public static void logTotalRecords(long count) {
    logger.info(
        String.format(
            "================================ Total Records to be processed: %s ========================================",
            count));
  }


  public static void writeSuccessMarkRecordToFile(String feedId) {
    try {
      if (feedInsertSuccess == null) {
        feedInsertSuccess = new FileWriter(EnvConstants.PRE_PROCESSED_RECORDS_FILE_USER_FEED);
      }
      feedInsertSuccess.write(String.format("%s", feedId));
      feedInsertSuccess.write("\n");
      feedInsertSuccess.flush();
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

  public static void writeFailedMarkRecordToFile(String feedId) {
    try {
      if (feedInsertFailure == null) {
        feedInsertFailure = new FileWriter(EnvConstants.FAILED_FEED_MIGRATION_OPERATION);
      }
      feedInsertFailure.write(String.format("%s", feedId));
      feedInsertFailure.write("\n");
      feedInsertFailure.flush();
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s:%s:error occurred while writing preProcessed records to file with message %s",
              StatusTracker.class.getSimpleName(),
              "writeFailedFulMarkVisitedRecordToFile",
              e.getMessage()));
      System.exit(0);
    }
  }

  public static void closeFailedWriterConnection() {
    try {
      if (feedInsertFailure != null) {
        feedInsertFailure.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeFailureRecordToFile Pre-Processed File", e.getMessage()));
    }
  }

  public static void closeSuccessWriterConnection() {
    try {
      if (feedInsertSuccess != null) {
        feedInsertSuccess.close();
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "%s error occurred while closing connection to file %s and error is %s",
              "writeSuccessRecordToFile Pre-Processed File", e.getMessage()));
    }
  }

}
