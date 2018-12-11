package org.sunbird.content.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;

public class TextBookTocUtil {

  private static ObjectMapper mapper = new ObjectMapper();

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

  public static Map<String, Object> readHierarchy(String contentId) {
    ProjectLogger.log(
        "ContentStoreUtil::readHierarchy: contentId = " + contentId, LoggerEnum.INFO.name());
    Map<String, String> requestParams = new HashMap<>();
    requestParams.put("mode", "edit");
    return handleReadRequest(contentId, JsonKey.SUNBIRD_CONTENT_GET_HIERARCHY_API, requestParams);
  }

  public static Map<String, Object> readContent(String contentId) {
    ProjectLogger.log(
        "ContentStoreUtil::readContent: contentId = " + contentId, LoggerEnum.INFO.name());
    return handleReadRequest(contentId, JsonKey.SUNBIRD_CONTENT_READ_API, null);
  }

  private static Map<String, Object> handleReadRequest(
      String id, String urlPath, Map<String, String> requestParams) {
    Map<String, String> headers = getHeaders();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> resultMap = new HashMap<>();

    ProjectLogger.log("ContentStoreUtil:handleReadRequest: id = " + id, LoggerEnum.INFO.name());

    try {
      String requestUrl =
          ProjectUtil.getConfigValue(JsonKey.EKSTEP_BASE_URL)
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

  public static <T> T getObjectFrom(String s, Class<T> clazz) {
    if (StringUtils.isBlank(s)) {
      ProjectLogger.log("Invalid String cannot be converted to Map.");
      throw new ProjectCommonException(
          ResponseCode.errorProcessingRequest.getErrorCode(),
          ResponseCode.errorProcessingRequest.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }

    try {
      return mapper.readValue(
          ProjectUtil.getConfigValue(JsonKey.TEXTBOOK_TOC_INPUT_MAPPING), clazz);
    } catch (IOException e) {
      ProjectLogger.log("Error Mapping File input Mapping Properties.");
      throw new ProjectCommonException(
          ResponseCode.errorProcessingRequest.getErrorCode(),
          ResponseCode.errorProcessingRequest.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
  }
}
