package org.sunbird.actorutil.systemsettings.impl;

import akka.actor.ActorRef;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sunbird.actorutil.InterServiceCommunication;
import org.sunbird.actorutil.InterServiceCommunicationFactory;
import org.sunbird.actorutil.systemsettings.SystemSettingClient;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ActorOperations;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.models.systemsetting.SystemSetting;

public class SystemSettingClientImpl implements SystemSettingClient {

  private static InterServiceCommunication interServiceCommunication =
      InterServiceCommunicationFactory.getInstance();
  private static SystemSettingClientImpl systemSettingClient = null;

  public static SystemSettingClientImpl getInstance() {
    if (null == systemSettingClient) {
      systemSettingClient = new SystemSettingClientImpl();
    }
    return systemSettingClient;
  }

  @Override
  public SystemSetting getSystemSettingByField(ActorRef actorRef, String field) {
    return getSystemSetting(actorRef, JsonKey.FIELD, field);
  }

  @Override
  public List<SystemSetting> getAllSystemSettings(ActorRef actorRef) {
    Request request = new Request();
    request.setOperation(ActorOperations.GET_ALL_SYSTEM_SETTINGS.getValue());
    ProjectLogger.log("SystemSettingClientImpl: getAllSystemSettings called", LoggerEnum.INFO);
    Object obj = interServiceCommunication.getResponse(actorRef, request);
    if (obj instanceof Response) {
      Response responseObj = (Response) obj;
      return (List<SystemSetting>) responseObj.getResult().get(JsonKey.RESPONSE);
    } else if (obj instanceof ProjectCommonException) {
      throw (ProjectCommonException) obj;
    } else {
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
  }

  private SystemSetting getSystemSetting(ActorRef actorRef, String param, Object value) {
    Request request = new Request();
    Map<String, Object> map = new HashMap<>();
    map.put(param, value);
    request.setRequest(map);
    request.setOperation(ActorOperations.GET_SYSTEM_SETTING.getValue());
    ProjectLogger.log("SystemSettingClientImpl: getSystemSetting called", LoggerEnum.INFO);
    Object obj = interServiceCommunication.getResponse(actorRef, request);
    if (obj instanceof Response) {
      Response responseObj = (Response) obj;
      return (SystemSetting) responseObj.getResult().get(JsonKey.RESPONSE);
    } else if (obj instanceof ProjectCommonException) {
      throw (ProjectCommonException) obj;
    } else {
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
  }
}
