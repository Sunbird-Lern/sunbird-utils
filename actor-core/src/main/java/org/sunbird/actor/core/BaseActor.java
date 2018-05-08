package org.sunbird.actor.core;

import akka.actor.UntypedAbstractActor;
import org.sunbird.actor.service.SunbirdMWService;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseParams;
import org.sunbird.common.models.response.ResponseParams.StatusType;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.ExecutionContext;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

/** @author Vinaya & Mahesh Kumar Gangula */
public abstract class BaseActor extends UntypedAbstractActor {

  public abstract void onReceive(Request request) throws Throwable;

  @Override
  public void onReceive(Object message) throws Throwable {
    if (message instanceof Request) {
      Request request = (Request) message;
      String callerName = request.getOperation();
      ProjectLogger.log("BaseActor onReceive called for operation : " + callerName);
      try {
        onReceive(request);
      } catch (Exception e) {
        onReceiveException(callerName, e);
      }
    } else if (message instanceof Response) {
      sender().tell(message, self());
    } else {
      unSupportedMessage();
    }
  }

  public void tellToAnother(Request request) {
    request
        .getContext()
        .put(JsonKey.TELEMETRY_CONTEXT, ExecutionContext.getCurrent().getRequestContext());
    SunbirdMWService.tellToBGRouter(request, self());
  }

  public void unSupportedMessage() {
    ProjectCommonException exception =
        new ProjectCommonException(
            ResponseCode.invalidRequestData.getErrorCode(),
            ResponseCode.invalidRequestData.getErrorMessage(),
            ResponseCode.CLIENT_ERROR.getResponseCode());
    sender().tell(exception, self());
  }

  public void onReceiveUnsupportedOperation(String callerName) {
    ProjectLogger.log(callerName + ": unsupported message");
    unSupportedMessage();
  }

  public void onReceiveUnsupportedMessage(String callerName) {
    ProjectLogger.log(callerName + ": unsupported operation");
    ProjectCommonException exception =
        new ProjectCommonException(
            ResponseCode.invalidOperationName.getErrorCode(),
            ResponseCode.invalidOperationName.getErrorMessage(),
            ResponseCode.CLIENT_ERROR.getResponseCode());
    sender().tell(exception, self());
  }

  protected void onReceiveException(String callerName, Exception e) {
    ProjectLogger.log(
        "Exception in message processing for: " + callerName + " :: message: " + e.getMessage(), e);
    sender().tell(e, self());
  }

  protected Response getErrorResponse(Exception e) {
    Response response = new Response();
    ResponseParams resStatus = new ResponseParams();
    String message = e.getMessage();
    resStatus.setErrmsg(message);
    resStatus.setStatus(StatusType.FAILED.name());
    if (e instanceof ProjectCommonException) {
      ProjectCommonException me = (ProjectCommonException) e;
      resStatus.setErr(me.getCode());
      response.setResponseCode(ResponseCode.SERVER_ERROR);
    } else {
      resStatus.setErr(e.getMessage());
      response.setResponseCode(ResponseCode.SERVER_ERROR);
    }
    response.setParams(resStatus);
    return response;
  }
}
