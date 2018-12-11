package org.sunbird.content.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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

  private static String requestParams(Map<String, String> params) {
    if (null != params) {
      StringBuilder sb = new StringBuilder();
      sb.append("?");
      int i = 0;
      for (Entry param : params.entrySet()) {
        if (i++ > 1) {
          sb.append("&");
        }
        sb.append(param.getKey()).append("=").append(param.getValue());
      }
      return sb.toString();
    } else {
      return "";
    }
  }

  public static Map<String, Object> readChannel(String channel) {
    ProjectLogger.log("ContentStoreUtil:readChannel: channel = " + channel, LoggerEnum.INFO.name());
    return handleReadRequest(channel, JsonKey.SUNBIRD_CHANNEL_READ_API);
  }

  public static Map<String, Object> readFramework(String frameworkId) {
    ProjectLogger.log(
        "ContentStoreUtil:readFramework: frameworkId = " + frameworkId, LoggerEnum.INFO.name());
    return handleReadRequest(frameworkId, JsonKey.SUNBIRD_FRAMEWORK_READ_API);
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> handleReadRequest(String id, String urlPath) {
    Map<String, String> headers = getHeaders();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> resultMap = new HashMap<>();

    ProjectLogger.log("ContentStoreUtil:handleReadRequest: id = " + id, LoggerEnum.INFO.name());

    try {
      String requestUrl =
          ProjectUtil.getConfigValue(JsonKey.SUNBIRD_API_BASE_URL)
              + ProjectUtil.getConfigValue(urlPath)
              + "/"
              + id;
      String response = HttpUtil.sendGetRequest(requestUrl, headers);

      resultMap = mapper.readValue(response, Map.class);
      if (!((String) resultMap.get(JsonKey.RESPONSE_CODE)).equalsIgnoreCase(JsonKey.OK)) {
        ProjectLogger.log(
            "ContentStoreUtil:handleReadRequest: Response code is not ok.",
            LoggerEnum.ERROR.name());
        return null;
      }
    } catch (Exception e) {
      ProjectLogger.log(
          "ContentStoreUtil:handleReadRequest: Exception occurred with error message = "
              + e.getMessage(),
          e);
    }
    return resultMap;
  }

  private static Map<String, Object> handleReadRequest(
      String id, String urlPath, Map<String, String> requestParams) {
    Map<String, String> headers = getHeaders();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> resultMap = new HashMap<>();

    ProjectLogger.log("ContentStoreUtil:handleReadRequest: id = " + id, LoggerEnum.INFO.name());

    try {
      String requestUrl =
          ProjectUtil.getConfigValue(JsonKey.SUNBIRD_API_BASE_URL)
              + ProjectUtil.getConfigValue(urlPath)
              + "/"
              + id
              + requestParams(requestParams);

      ProjectLogger.log("Sending GET Request | Request URL: " + requestUrl, LoggerEnum.INFO);

      String response = HttpUtil.sendGetRequest(requestUrl, headers);

      resultMap = mapper.readValue(response, Map.class);
      if (!((String) resultMap.get(JsonKey.RESPONSE_CODE)).equalsIgnoreCase(JsonKey.OK)) {
        ProjectLogger.log(
            "ContentStoreUtil:handleReadRequest: Response code is not ok.",
            LoggerEnum.ERROR.name());
        return null;
      }
    } catch (Exception e) {
      ProjectLogger.log(
          "ContentStoreUtil:handleReadRequest: Exception occurred with error message = "
              + e.getMessage(),
          e);
    }
    return resultMap;
  }
}
