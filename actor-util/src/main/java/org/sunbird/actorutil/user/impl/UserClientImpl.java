package org.sunbird.actorutil.user.impl;

import akka.actor.ActorRef;
import java.util.Map;
import org.sunbird.actorutil.InterServiceCommunication;
import org.sunbird.actorutil.InterServiceCommunicationFactory;
import org.sunbird.actorutil.user.UserClient;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ActorOperations;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

public class UserClientImpl implements UserClient {
  private static InterServiceCommunication interServiceCommunication =
      InterServiceCommunicationFactory.getInstance();

  @Override
  public String createUser(ActorRef actorRef, Map<String, Object> userMap) {
    ProjectLogger.log("UserClientImpl: createOrg called", LoggerEnum.INFO);
    return upsertUser(actorRef, userMap, ActorOperations.CREATE_USER.getValue());
  }

  @Override
  public void updateUser(ActorRef actorRef, Map<String, Object> userMap) {
    ProjectLogger.log("UserClientImpl: createOrg called", LoggerEnum.INFO);
    upsertUser(actorRef, userMap, ActorOperations.UPDATE_USER.getValue());
  }

  private String upsertUser(ActorRef actorRef, Map<String, Object> userMap, String operation) {
    String userId = null;

    Request request = new Request();
    request.setRequest(userMap);
    request.setOperation(operation);

    Object obj = interServiceCommunication.getResponse(actorRef, request);

    if (obj instanceof Response) {
      Response response = (Response) obj;
      userId = (String) response.get(JsonKey.USER_ID);
    } else if (obj instanceof ProjectCommonException) {
      throw (ProjectCommonException) obj;
    } else if (obj instanceof Exception) {
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }

    return userId;
  }
}
