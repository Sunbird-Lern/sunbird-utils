import java.io.File;
import org.apache.log4j.Logger;

public class FileValidator {
  private static Logger logger = LoggerFactory.getLoggerInstance(FileValidator.class.getName());

  public static boolean isValidFile(String pathToinputFile, String pathTooutputFile) {

    if (isFileValid(pathToinputFile, pathTooutputFile)
        && validatingFileExtensions(pathToinputFile, pathTooutputFile)) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean validatingFileExtensions(String pathToInputFile, String pathToOutputFile) {
    File fileObj = new File(pathToInputFile);
    if (getFileExtension(fileObj).equals("csv")) {
      logger.info("Csv file path provided " + fileObj.getPath());
    } else {
      logger.error("invalid file extension for input csv file");
      return false;
    }
    fileObj = new File(pathToOutputFile);
    if (getFileExtension(fileObj).equals("cql")) {
      logger.info("Cql file path provided " + fileObj.getPath());
    } else {
      logger.error("invalid file extension for output cql file");
      return false;
    }
    return true;
  }

  private static String getFileExtension(File file) {

    String fileName = file.getName();
    if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
      return fileName.substring(fileName.lastIndexOf(".") + 1);
    else return "";
  }

  public static boolean isFileValid(String pathToInputFile, String pathToOutputFile) {
    try {
      File f = new File(pathToInputFile);
      if (!f.isFile()) {
        logger.error("file doesnt exists " + pathToInputFile);
        return false;
      }
      f = new File(pathToOutputFile);
      if (!f.isFile()) {
        logger.error("file doesnt exists " + pathToOutputFile);
        return false;
      }
      return true;
    } catch (Exception e) {
      logger.error("FileValidator: isValidFile: exception occurred" + e.getMessage());
      return false;
    }
  }
}
