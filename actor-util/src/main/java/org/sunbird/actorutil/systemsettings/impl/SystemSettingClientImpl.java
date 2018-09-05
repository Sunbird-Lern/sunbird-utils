package org.sunbird.actorutil.systemsettings.impl;

import akka.actor.ActorRef;
import java.util.HashMap;
import java.util.Map;
import org.sunbird.actorutil.InterServiceCommunication;
import org.sunbird.actorutil.InterServiceCommunicationFactory;
import org.sunbird.actorutil.systemsettings.SystemSettingClient;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ActorOperations;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.Request;
import org.sunbird.models.systemsetting.SystemSetting;

public class SystemSettingClientImpl implements SystemSettingClient {

  private static InterServiceCommunication interServiceCommunication =
      InterServiceCommunicationFactory.getInstance();
  private static SystemSettingClientImpl systemSettingClient = null;

  public static SystemSettingClientImpl getInstance() {
    if (null == systemSettingClient) {
      systemSettingClient = new SystemSettingClientImpl();
    } else {
      return systemSettingClient;
    }
    return systemSettingClient;
  }

  @Override
  public SystemSetting getSystemSettingByField(ActorRef actorRef, String field) {
    return getResponse(actorRef, JsonKey.FIELD, field);
  }

  private SystemSetting getResponse(ActorRef actorRef, String param, Object value) {
    SystemSetting systemSetting = null;
    Request request = new Request();
    Map<String, Object> map = new HashMap<>();
    map.put(param, value);
    request.setRequest(map);
    request.setOperation(ActorOperations.GET_SYSTEM_SETTING.getValue());
    ProjectLogger.log("SystemSettingClientImpl : call getSystemSetting ", LoggerEnum.INFO);
    Object obj = interServiceCommunication.getResponse(actorRef, request);
    if (obj instanceof Response) {
      Response responseObj = (Response) obj;
      return (SystemSetting) responseObj.getResult().get(JsonKey.RESPONSE);
    } else {
      systemSetting = new SystemSetting();
    }
    return systemSetting;
  }
}
