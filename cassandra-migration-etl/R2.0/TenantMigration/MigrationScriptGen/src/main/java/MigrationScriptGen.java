import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

public class MigrationScriptGen {
  static Logger logger = LoggerFactory.getLoggerInstance(MigrationScriptGen.class.getName());

  static RequestParams inputParams;

  public static void main(String[] args) throws Exception {
    if (args.length == 7) {
      String csvFileInput = args[0] != null ? args[0] : "";
      String cqlFileOutput = args[1] != null ? args[1] : "";
      String channel = args[2] != null ? args[2] : "";
      String rootOrgId = args[3] != null ? args[3] : "";
      String apiKey = args[4] != null ? args[4] : "";
      String authToken = args[5] != null ? args[5] : "";
      String baseUrl = args[6] != null ? args[6] : "";
      checkValidiInputParams(args);
      logger.info("MigrationScriptGen:main:all provided params verified");
      inputParams =
          new RequestParams(
              csvFileInput, cqlFileOutput, channel, rootOrgId, apiKey, authToken, baseUrl);
      CsvManager manager = new CsvManager(inputParams);
      manager.processCsv();
    } else {
      logger.error(
          "Please provide all 7 params csvFileInput,cqlFileOutput, channel, rootOrgId, apiKey, authToken, baseUrl");
    }
  }

  public static boolean isValidParams(String params) {

    if (TextUtils.isEmpty(params)) {
      return false;
    }
    return true;
  }

  public static void checkValidiInputParams(String[] paramsArray) throws Exception {

    String[] pramsNameArray = {
      "csvFileInput", "cqlFileOutput", "channel", "rootOrgId", "apiKey", "authToken", "baseUrl"
    };
    for (int i = 0; i < paramsArray.length; i++) {
      if (!isValidParams(paramsArray[i])) {
        throw new Exception("please provide valid value for params " + pramsNameArray[i]);
      }
    }
  }
}
