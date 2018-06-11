package org.sunbird.telemetry.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.request.Request;

/** Created by arvind on 23/3/18. */
public class TelemetryDispatcherSunbirdLMS implements TelemetryDispatcher {

  private static ObjectMapper mapper = new ObjectMapper();
  PropertiesCache propertiesCache = PropertiesCache.getInstance();

  @Override
  public boolean dispatchTelemetryEvent(List<String> eventList) {
    try {
      List<Map<String, Object>> jsonList =
          mapper.readValue(eventList.toString(), new TypeReference<List<Map<String, Object>>>() {});
      String eventReq = getTelemetryEvent(createTelemetryRequest(jsonList));
      String response =
          HttpUtil.sendPostRequest(
              getCompleteUrl(
                  JsonKey.SUNBIRD_TELEMETRY_BASE_URL, JsonKey.SUNBIRD_TELEMETRY_API_PATH),
              eventReq,
              getSunbirdLMSHeaders());
      ProjectLogger.log("FLUSH RESPONSE : " + response);
    } catch (Exception ex) {
      ProjectLogger.log(ex.getMessage(), ex);
    }
    return true;
  }

  private Request createTelemetryRequest(List<Map<String, Object>> jsonList) {

    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.ETS, System.currentTimeMillis());
    map.put(JsonKey.EVENTS, jsonList);
    Request req = new Request();
    req.getRequest().putAll(map);
    return req;
  }

  private static String getTelemetryEvent(Request request) {
    String event = "";
    try {
      event = mapper.writeValueAsString(request);
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    return event;
  }

  public String getCompleteUrl(String baseUrlKey, String uriKey) {
    String baseSearchUrl = System.getenv(baseUrlKey);
    if (StringUtils.isBlank(baseSearchUrl)) {
      baseSearchUrl = propertiesCache.readProperty(baseUrlKey);
    }
    String uri = propertiesCache.readProperty(uriKey);
    return baseSearchUrl + uri;
  }

  public Map<String, String> getSunbirdLMSHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("accept", "application/json");
    String authKey = System.getenv(JsonKey.SUNBIRD_LMS_AUTHORIZATION);
    if (StringUtils.isBlank(authKey)) {
      authKey = PropertiesCache.getInstance().readProperty(JsonKey.SUNBIRD_LMS_AUTHORIZATION);
    }
    headers.put(JsonKey.AUTHORIZATION, JsonKey.BEARER + authKey);
    return headers;
  }
}
