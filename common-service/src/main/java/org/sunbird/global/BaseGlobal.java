package org.sunbird.global;

import java.lang.reflect.Method;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.sunbird.actor.service.SunbirdMWService;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.ExecutionContext;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.controllers.BaseController;
import play.Application;
import play.GlobalSettings;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Base class with common functionality to perform specific tasks at application start up or shut
 * down.
 *
 * @author Manzarul
 */
public class BaseGlobal extends GlobalSettings {

  private class ActionWrapper extends Action.Simple {
    public ActionWrapper(Action<?> action) {
      this.delegate = action;
    }

    @Override
    public Promise<Result> call(Http.Context ctx) throws Throwable {
      Http.Response response = ctx.response();
      response.setHeader("Access-Control-Allow-Origin", "*");
      return delegate.call(ctx);
    }
  }

  /**
   * Called on application startup.
   *
   * @param app Play application
   */
  public void onStart(Application app) {
    SunbirdMWService.init();
    ProjectLogger.log("Server started.");
  }

  /**
   * Called to create root action for each request.
   *
   * @param request HTTP request
   * @param actionMethod Action method
   * @return Root action created for received request
   */
  @SuppressWarnings("rawtypes")
  public Action onRequest(Request request, Method actionMethod) {

    String messageId = request.getHeader(JsonKey.MESSAGE_ID);
    if (StringUtils.isEmpty(messageId)) {
      UUID uuid = UUID.randomUUID();
      messageId = uuid.toString();
    }
    ExecutionContext.setRequestId(messageId);
    return new ActionWrapper(super.onRequest(request, actionMethod));
  }

  /**
   * Called when an exception occurred such as request header missing error message.
   *
   * @param request HTTP request header
   * @param t Throwable error or exception
   * @return Return a promise for API error
   */
  @Override
  public Promise<Result> onError(Http.RequestHeader request, Throwable t) {
    Response response;
    ProjectCommonException commonException;
    if (t instanceof ProjectCommonException) {
      commonException = (ProjectCommonException) t;
      response =
          BaseController.createResponseOnException(
              request.path(), (ProjectCommonException) t, request.method());
    } else if (t instanceof akka.pattern.AskTimeoutException) {
      commonException =
          new ProjectCommonException(
              ResponseCode.operationTimeout.getErrorCode(),
              ResponseCode.operationTimeout.getErrorMessage(),
              ResponseCode.SERVER_ERROR.getResponseCode());
    } else {
      commonException =
          new ProjectCommonException(
              ResponseCode.internalError.getErrorCode(),
              ResponseCode.internalError.getErrorMessage(),
              ResponseCode.SERVER_ERROR.getResponseCode());
    }
    response =
        BaseController.createResponseOnException(request.path(), commonException, request.method());
    return Promise.<Result>pure(
        Results.status(ResponseCode.SERVER_ERROR.getResponseCode(), Json.toJson(response)));
  }
}
