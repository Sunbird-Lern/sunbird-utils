package org.sunbird.common.models.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.body.Body;
import com.mashape.unirest.request.body.RequestBodyEntity;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

/** @author Mahesh Kumar Gangula */
public class RestUtil {

  private static String basePath;

  static {
    basePath = System.getenv(JsonKey.EKSTEP_BASE_URL);
    if (StringUtils.isBlank(basePath)) {
      basePath = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_BASE_URL);
    }

    String apiKey = System.getenv(JsonKey.EKSTEP_AUTHORIZATION);
    if (StringUtils.isBlank(apiKey)) {
      apiKey = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_AUTHORIZATION);
    }
    Unirest.setDefaultHeader("Content-Type", "application/json");
    Unirest.setDefaultHeader("Authorization", "Bearer " + apiKey);
  }

  public static HttpResponse<JsonNode> execute(BaseRequest request) throws Exception {
    ProjectLogger.log("RestUtil:execute: request url = " + request.getHttpRequest().getUrl());
    Body body = request.getHttpRequest().getBody();
    if ((body != null) && (body instanceof RequestBodyEntity)) {
      RequestBodyEntity rbody = (RequestBodyEntity) body;
      ProjectLogger.log("RestUtil:execute: request body = " + rbody.getBody());
    }

    HttpResponse<JsonNode> response = request.asJson();
    return response;
  }

  public static String getBasePath() {
    return basePath;
  }

  public static String getFromResponse(HttpResponse<JsonNode> resp, String key) throws Exception {
    String[] nestedKeys = key.split("\\.");
    JSONObject obj = resp.getBody().getObject();

    for (int i = 0; i < nestedKeys.length - 1; i++) {
      String nestedKey = nestedKeys[i];
      if (obj.has(nestedKey)) obj = obj.getJSONObject(nestedKey);
    }

    String val = obj.getString(nestedKeys[nestedKeys.length - 1]);
    return val;
  }

  public static boolean isSuccessful(HttpResponse<JsonNode> resp) throws Exception {
    int status = resp.getStatus();
    //    String code = resp.getBody().getObject().getString("responseCode");
    //    return ((status == 200) && (code.equals("OK")));
    return (status == 200);
  }
}
