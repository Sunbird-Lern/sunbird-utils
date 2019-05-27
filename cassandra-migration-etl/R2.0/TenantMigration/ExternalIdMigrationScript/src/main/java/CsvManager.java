import java.io.*;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class CsvManager {

  RequestParms parms;
  FileWriter fw;
  HttpClient client;
  BufferedReader br;
  static Logger logger = LoggerFactory.getLoggerInstance(CsvManager.class.getName());

  public CsvManager(RequestParms parms) throws IOException {
    this.parms = parms;
    fw = new FileWriter("failedRecords.txt");
    client = new HttpClient(parms);
  }

  public void processFile() {
    String line = "";
    String cvsSplitBy = ",";
    try {
      br = getReaderObject(parms.getCsvFileInput());
      if (br != null) {
        while ((line = br.readLine()) != null) {
          if (line.length() != 0) {
            String[] values = line.split(cvsSplitBy);
            if (StringUtils.isNotBlank(values[1]) && StringUtils.isNotBlank(values[0])) {
              if (values.length == 4) {
                performUserUpdateRequest(values[0], values[1], values[2], values[3]);
              } else {
                logger.info(
                    "Cant perform request on record with userName "
                        + values[0]
                        + "and userId "
                        + values[1]);
                logger.error("All params userName,userId,treasuryId,channel are mandatory!!!");
                continue;
              }
            } else {
              logger.error("No treasuryId found for this " + values[0] + " skipping record");
              continue;
            }

          } else {
            logger.error("No record Found  in row ");
          }
        }
      }
    } catch (Exception e) {
      logger.error("CsvManager:processFile: error in processCsv: " + e);
    } finally {
      if (br != null) {
        try {
          br.close();
          fw.close();
          logger.info("Connection Closed");
        } catch (Exception e) {
          logger.error("error in closing connection " + e.getMessage());
        }
      }
    }
  }

  public BufferedReader getReaderObject(String pathToInputFile) {
    try {
      br = new BufferedReader(new FileReader(pathToInputFile));
      br.readLine(); // escaping headers in file headers
      return br;

    } catch (FileNotFoundException e) {
      logger.error("file does not exists " + pathToInputFile);
      return null;

    } catch (IOException e) {
      logger.error("Exception occurred " + e.getMessage());
      return null;
    }
  }

  public void performUserUpdateRequest(
      String userName, String userId, String treasuryId, String channel) {
    try {
      String url = parms.getBaseUrl().concat("v1/user/update");
      String authToken = client.generateAuthToken(userName);
      logger.info("The auth token generated is " + authToken);
      Map<String, Object> reqMap = prepareUserUpdateRequest(userId, treasuryId, channel);
      if (StringUtils.isNotBlank(authToken)) {
        Map<String, Object> respMap = client.post(reqMap, url, authToken);
        int statusCode = (Integer) respMap.get("statusCode");
        if (statusCode != 200) {
          writeFailedRecordToFile(userName, userId, (String) respMap.get("errMsg"));
        } else if (statusCode == 200) {
          logger.info("User successfully updated with userName: " + userName);
        }
      } else {
        logger.error(
            "CsvManager: performUserUpdateRequest: Record with userName "
                + userName
                + " Cant be processed");
      }

    } catch (Exception e) {
      logger.error(
          "CsvManager: performUserUpdateRequest: Some Error Occurred in CsvManger:performUserUpdateRequest   "
              + e);
    }
  }

  public Map<String, Object> prepareUserUpdateRequest(
      String userId, String treasuryId, String channel) {

    Map<String, Object> reqMap = new HashMap<String, Object>();
    HashMap<String, Object> request = new HashMap<String, Object>();
    request.put("userId", userId);
    List<Map<String, Object>> externalIdList = new ArrayList<Map<String, Object>>();
    Map<String, Object> externaslIdMap = new HashMap<String, Object>();
    externaslIdMap.put("id", treasuryId);
    externaslIdMap.put("idType", channel);
    externaslIdMap.put("provider", channel);
    externalIdList.add(externaslIdMap);
    request.put("externalIds", externalIdList);
    reqMap.put("request", request);
    logger.info(
        "CsvManager:prepareUserUpdateRequest: userRequest is "
            + Collections.singletonList(reqMap.toString()));
    return reqMap;
  }

  public void writeFailedRecordToFile(String userName, String userId, String errMsg) {
    try {
      String query =
          "Failed to Update Record with userName "
              + userName
              + " and userId"
              + userId
              + " and error Message is "
              + errMsg;
      logger.info("Writing failed record " + query);
      fw.write(query + "\n");
    } catch (Exception e) {
      logger.error("CsvManager: writeFailedRecordToFile: Something went wrong in writing file");
    }
  }
}
