import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

public class ExternalIdMigration {
  static Logger logger = LoggerFactory.getLoggerInstance(ExternalIdMigration.class.getName());

  static RequestParms inputParams;

  public static void main(String[] args) throws Exception {

    if (args.length == 5) {
      String csvFileInput = args[0] != null ? args[0] : "";
      String baseUrl = args[1] != null ? args[1] : "";
      String realm = args[2] != null ? args[2] : "";
      String clientId = args[3] != null ? args[3] : "";
      String apiKey = args[4] != null ? args[4] : "";
      checkValidiInputParams(args);
      logger.info("ExternalIdMigration : main:all provided params verified");
      inputParams = new RequestParms(csvFileInput, baseUrl, realm, clientId, apiKey);
      CsvManager manager = new CsvManager(inputParams);
      manager.processFile();

    } else {
      logger.error(
          " ExternalIdMigration: main: please provide all 5 input params csvFileInput, baseUrl,realm,clientId,apiKey");
    }
  }

  public static boolean isValidParams(String params) {

    if (TextUtils.isEmpty(params)) {
      return false;
    }
    return true;
  }

  public static void checkValidiInputParams(String[] paramsArray) throws Exception {

    String[] pramsNameArray = {"csvFileInput", "baseUrl", "realm", "clientId", "apiKey"};
    for (int i = 0; i < paramsArray.length; i++) {
      if (!isValidParams(paramsArray[i])) {
        throw new Exception("please provide valid value for params " + pramsNameArray[i]);
      }
    }
  }
}
