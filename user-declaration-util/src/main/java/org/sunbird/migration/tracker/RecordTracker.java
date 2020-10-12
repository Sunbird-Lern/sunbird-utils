package org.sunbird.migration.tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.sunbird.migration.LoggerFactory;
import org.sunbird.migration.constants.EnvConstants;

/**
 * this class will be used to read records which are pre processed and list of preprocessed records
 * is present isn preProcessedRecords.txt
 */
public class RecordTracker {

  private static Logger logger = LoggerFactory.getLoggerInstance(RecordTracker.class.getName());

  public static List<String> getSelfDeclaredPreProcessedRecordsAsList() throws IOException {
    List<String> successfullRecordsList = new ArrayList<>();
    File file = new File(EnvConstants.PRE_PROCESSED_RECORDS_FILE_SELF_DECLARED);
    printFilelastModified(file);
    if (file.exists()) {
      successfullRecordsList = readFileGetList(file);
    } else {
      file.createNewFile();
    }
    return successfullRecordsList;
  }

  /**
   * this method will remove the last index value from the list since it may be possible that the
   * last value may be not fully written by writer..
   *
   * @param file
   * @return
   */
  private static List<String> readFileGetList(File file) {
    List<String> preProcessedRecords = new ArrayList<>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null) {
        preProcessedRecords.add(line);
      }
    } catch (IOException e) {
      logger.error(String.format("no file found named %s creating it again....", file.getName()));
    }
    if (preProcessedRecords.size() >= 1) {
      preProcessedRecords.remove(preProcessedRecords.size() - 1);
    }
    return preProcessedRecords;
  }

  private static void printFilelastModified(File file) {
    String dateFormat = "MM/dd/yyyy HH:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    logger.info(
        String.format("%s last modified at %s", file.getName(), sdf.format(file.lastModified())));
  }
}
