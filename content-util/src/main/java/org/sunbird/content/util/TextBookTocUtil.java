package org.sunbird.content.util;

import static java.util.Objects.isNull;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.sunbird.common.models.util.HttpUtil.sendGetRequest;
import static org.sunbird.common.models.util.JsonKey.BEARER;
import static org.sunbird.common.models.util.JsonKey.EKSTEP_BASE_URL;
import static org.sunbird.common.models.util.JsonKey.OK;
import static org.sunbird.common.models.util.JsonKey.RESPONSE_CODE;
import static org.sunbird.common.models.util.JsonKey.SUNBIRD_AUTHORIZATION;
import static org.sunbird.common.models.util.JsonKey.SUNBIRD_CONTENT_GET_HIERARCHY_API;
import static org.sunbird.common.models.util.JsonKey.SUNBIRD_CONTENT_READ_API;
import static org.sunbird.common.models.util.LoggerEnum.ERROR;
import static org.sunbird.common.models.util.LoggerEnum.INFO;
import static org.sunbird.common.models.util.ProjectLogger.log;
import static org.sunbird.common.models.util.ProjectUtil.getConfigValue;
import static org.sunbird.common.responsecode.ResponseCode.SERVER_ERROR;
import static org.sunbird.common.responsecode.ResponseCode.errorProcessingRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;

public class TextBookTocUtil {

  private static ObjectMapper mapper = new ObjectMapper();

  private static Map<String, String> getHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put(AUTHORIZATION, BEARER + getConfigValue(SUNBIRD_AUTHORIZATION));
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
    log("ContentStoreUtil::readHierarchy: contentId = " + contentId, INFO);
    Map<String, String> requestParams = new HashMap<>();
    requestParams.put("mode", "edit");
    return handleReadRequest(contentId, SUNBIRD_CONTENT_GET_HIERARCHY_API, requestParams);
  }

  public static Map<String, Object> readContent(String contentId) {
    log("ContentStoreUtil::readContent: contentId = " + contentId, INFO);
    return handleReadRequest(contentId, SUNBIRD_CONTENT_READ_API, null);
  }

  private static Map<String, Object> handleReadRequest(
      String id, String urlPath, Map<String, String> requestParams) {
    Map<String, String> headers = getHeaders();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> resultMap = new HashMap<>();

    log("ContentStoreUtil:handleReadRequest: id = " + id, INFO);

    try {
      String requestUrl =
          getConfigValue(EKSTEP_BASE_URL)
              + getConfigValue(urlPath)
              + "/"
              + id
              + requestParams(requestParams);

      log("Sending GET Request | Request URL: " + requestUrl, INFO);

      String response = sendGetRequest(requestUrl, headers);

      resultMap = mapper.readValue(response, Map.class);
      if (!((String) resultMap.get(RESPONSE_CODE)).equalsIgnoreCase(OK)) {
        log("ContentStoreUtil:handleReadRequest: Response code is not ok.", ERROR);
        return null;
      }
    } catch (Exception e) {
      log(
          "ContentStoreUtil:handleReadRequest: Exception occurred with error message = "
              + e.getMessage(),
          e);
    }
    return resultMap;
  }

  public static <T> T getObjectFrom(String s, Class<T> clazz) {
    if (StringUtils.isBlank(s)) {
      log("Invalid String cannot be converted to Map.");
      throw new ProjectCommonException(
          errorProcessingRequest.getErrorCode(),
          errorProcessingRequest.getErrorMessage(),
          SERVER_ERROR.getResponseCode());
    }

    try {
      return mapper.readValue(s, clazz);
    } catch (IOException e) {
      log("Error Mapping File input Mapping Properties.", ERROR);
      throw new ProjectCommonException(
          errorProcessingRequest.getErrorCode(),
          errorProcessingRequest.getErrorMessage(),
          SERVER_ERROR.getResponseCode());
    }
  }

  public static Object stringify(Object o) {
    if (isNull(o)) return "";
    if (o instanceof List) {
      List l = (List) o;
      if (!l.isEmpty() && l.get(0) instanceof String) {
        return String.join(",", l);
      }
    }
    if (o instanceof String[]) {
      String[] l = (String[]) o;
      if (l.length > 0) {
        return String.join(",", l);
      }
    }
    return o;
  }
}
