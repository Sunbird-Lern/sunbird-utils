/**
 * 
 */
package org.sunbird.common.models.util.fcm;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;

/**
 * @author Manzarul
 *
 */
public class Notification {
  public static final String FCM_URL = PropertiesCache.getInstance().getProperty(JsonKey.FCM_URL);
  private static final String FCM_ACCOUNT_KEY = System.getenv(JsonKey.SUNBIRD_FCM_ACCOUNT_KEY);
  private static Map<String,String> headerMap = new HashMap<>();
   
  static{
     headerMap.put(JsonKey.AUTHORIZATION, FCM_ACCOUNT_KEY);
     headerMap.put("Content-Type", "application/json");
  }

  public static String sendNotification(String topic, Map<String, Object> data,
      String url) {
    if (ProjectUtil.isStringNullOREmpty(FCM_ACCOUNT_KEY) || ProjectUtil.isStringNullOREmpty(FCM_URL)) {
      ProjectLogger.log("FCM account key or URL is not provided===" + FCM_URL,
          LoggerEnum.INFO.name());
      return JsonKey.FAILURE;
    }
    String response = null;
    try {
      JSONObject object1 = new JSONObject(data);
      JSONObject object = new JSONObject();
      object.put(JsonKey.DATA, object1);
      object.put(JsonKey.TO, topic);
      response =
          HttpUtil.sendPostRequest(FCM_URL, object.toString(), headerMap);
      ProjectLogger.log("FCM Notification response==" + response);
    } catch (Exception e) {
      response =  JsonKey.FAILURE;
      ProjectLogger.log(e.getMessage(), e);
    }
    return response;
  }
}
