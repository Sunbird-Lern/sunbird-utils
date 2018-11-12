package org.sunbird.content.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHeaders;
import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;

public class ContentStoreUtil {

  private static Map<String, String> getHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put(
        HttpHeaders.AUTHORIZATION,
        JsonKey.BEARER + ProjectUtil.getConfigValue(JsonKey.SUNBIRD_AUTHORIZATION));
    return headers;
  }

  public static Map<String, Object> readChannel(String channel) {
    return getReadDetails(channel, JsonKey.SUNBIRD_CHANNEL_READ_API);
  }

  public static Map<String, Object> readFramework(String frameworkId) {
    return getReadDetails(frameworkId, JsonKey.SUNBIRD_FRAMEWORK_READ_API);
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> getReadDetails(String id, String readPath) {
    Map<String, String> headers = getHeaders();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> resultMap = new HashMap<>();
    ProjectLogger.log("making call to read Details ==" + id, LoggerEnum.INFO.name());
    try {
      String requestUrl =
          ProjectUtil.getConfigValue(JsonKey.SUNBIRD_API_BASE_URL)
              + ProjectUtil.getConfigValue(readPath)
              + "/"
              + id;
      String response = HttpUtil.sendGetRequest(requestUrl, headers);
      ProjectLogger.log("Read details are ==" + response, LoggerEnum.INFO.name());
      resultMap = mapper.readValue(response, Map.class);
      if (!((String) resultMap.get(JsonKey.RESPONSE_CODE)).equalsIgnoreCase(JsonKey.OK)) {
        ProjectLogger.log("ContentStoreUtil : GetReadDetails Read Error Obtained");
        return null;
      }
    } catch (Exception e) {
      ProjectLogger.log("Error found during content search parse==" + e.getMessage(), e);
    }
    return resultMap;
  }
}
