package org.sunbird.global;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.BadgingJsonKey;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.ProjectUtil.Environment;
import org.sunbird.common.request.ExecutionContext;
import org.sunbird.common.request.HeaderParam;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.controllers.BaseController;
import org.sunbird.telemetry.util.TelemetryUtil;
import org.sunbird.util.CassandraStartUpUtil;
import org.sunbird.util.authentication.RequestInterceptor;
import play.Application;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Results;

/**
 * This class will work as a filter. All play request pass through this filter onCall method. Class
 * also contains onStart method that will be call only once when application will be started.
 *
 * @author arvind.
 */
public class ServiceBaseGlobal extends BaseGlobal {

  public static ProjectUtil.Environment env;

  public static Map<String, Map<String, Object>> requestInfo = new HashMap<>();

  public static String ssoPublicKey = "";
  private static final String version = "v1";
  private static final List<String> USER_UNAUTH_STATES =
      Arrays.asList(JsonKey.UNAUTHORIZED, JsonKey.ANONYMOUS);

  private class ActionWrapper extends Action.Simple {
    public ActionWrapper(Action<?> action) {
      this.delegate = action;
    }

    @Override
    public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
      Promise<Result> result = null;
      ctx.response().setHeader("Access-Control-Allow-Origin", "*");
      // Verify the request data
      String requesterId = RequestInterceptor.verifyRequestData(ctx);
      // Set required parameters for telemetry event
      intializeRequestInfo(ctx, requesterId);
      if (!USER_UNAUTH_STATES.contains(requesterId)) {
        ctx.flash().put(JsonKey.USER_ID, requesterId);
        ctx.flash().put(JsonKey.IS_AUTH_REQ, "false");
        for (String uri : RequestInterceptor.getRestrictedUriList()) {
          if (ctx.request().path().contains(uri)) {
            ctx.flash().put(JsonKey.IS_AUTH_REQ, "true");
            break;
          }
        }
        result = delegate.call(ctx);
      } else if (JsonKey.UNAUTHORIZED.equals(requesterId)) {
        result =
            onDataValidationError(
                ctx.request(), requesterId, ResponseCode.UNAUTHORIZED.getResponseCode());
      } else {
        result = delegate.call(ctx);
      }
      return result;
    }
  }

  /**
   * This method will be called on application start up , method to perform the tasks performed at
   * application start up time.
   *
   * @param app play Application
   */
  public void onStart(Application app) {
    setEnvironment();
    ProjectLogger.log("Server started.. with environment: " + env.name(), LoggerEnum.INFO.name());
    ssoPublicKey = System.getenv(JsonKey.SSO_PUBLIC_KEY);
    CassandraStartUpUtil.checkCassandraDbConnections(JsonKey.SUNBIRD);
    CassandraStartUpUtil.checkCassandraDbConnections(JsonKey.SUNBIRD_PLUGIN);
  }

  /**
   * This method will be called on each request.
   *
   * @param request represents the Play request object.
   * @param actionMethod Method type , example - GET , POST etc.
   * @return Action
   */
  @SuppressWarnings("rawtypes")
  public Action onRequest(Request request, Method actionMethod) {

    String messageId = request.getHeader(JsonKey.MESSAGE_ID);
    if (StringUtils.isBlank(messageId)) {
      UUID uuid = UUID.randomUUID();
      messageId = uuid.toString();
    }
    ExecutionContext.setRequestId(messageId);
    return new ActionWrapper(super.onRequest(request, actionMethod));
  }

  private String getOperationEnv(Request request) {

    String uri = request.uri();
    String env;
    if (uri.startsWith("/v1/user")) {
      env = JsonKey.USER;
    } else if (uri.startsWith("/v1/org")) {
      env = JsonKey.ORGANISATION;
    } else if (uri.startsWith("/v1/object")) {
      env = JsonKey.ANNOUNCEMENT;
    } else if (uri.startsWith("/v1/page")) {
      env = JsonKey.PAGE;
    } else if (uri.startsWith("/v1/course/batch")) {
      env = JsonKey.BATCH;
    } else if (uri.startsWith("/v1/notification")) {
      env = JsonKey.NOTIFICATION;
    } else if (uri.startsWith("/v1/dashboard")) {
      env = JsonKey.DASHBOARD;
    } else if (uri.startsWith("/v1/badges")) {
      env = JsonKey.BADGES;
    } else if (uri.startsWith("/v1/issuer")) {
      env = BadgingJsonKey.BADGES;
    } else {
      env = "miscellaneous";
    }
    return env;
  }

  /**
   * Method to perform action on occurrences of data validation error
   *
   * @param request Request represents the play request object
   * @param errorMessage represents the validation error message
   * @return Promise<Result>
   */
  private Promise<Result> onDataValidationError(
      Request request, String errorMessage, int responseCode) {
    ProjectLogger.log("Data error found--" + errorMessage);
    ResponseCode code = ResponseCode.getResponse(errorMessage);
    ResponseCode headerCode = ResponseCode.CLIENT_ERROR;
    Response resp = BaseController.createFailureResponse(request, code, headerCode);
    return Promise.<Result>pure(Results.status(responseCode, Json.toJson(resp)));
  }

  /**
   * This method will be called by play in case error occur.
   *
   * @param request Http.RequestHeader
   * @param t Throwable
   * @return Promise<Result>
   */
  @Override
  public Promise<Result> onError(Http.RequestHeader request, Throwable t) {

    Response response = null;
    ProjectCommonException commonException = null;
    if (t instanceof ProjectCommonException) {
      commonException = (ProjectCommonException) t;
      response =
          BaseController.createResponseOnException(
              request.path(), (ProjectCommonException) t, request.method());
    } else if (t instanceof akka.pattern.AskTimeoutException) {
      commonException =
          new ProjectCommonException(
              ResponseCode.actorConnectionError.getErrorCode(),
              ResponseCode.actorConnectionError.getErrorMessage(),
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
    return Promise.<Result>pure(Results.internalServerError(Json.toJson(response)));
  }

  private Environment setEnvironment() {

    if (play.Play.isDev()) {
      return env = Environment.dev;
    } else if (play.Play.isTest()) {
      return env = Environment.qa;
    } else {
      return env = Environment.prod;
    }
  }

  /**
   * Method to save the request related information inorder to use while telemetry generation.
   *
   * @param ctx play context
   * @param userId identifier of actor(triggered the request)
   */
  private void intializeRequestInfo(Context ctx, String userId) {
    Request request = ctx.request();
    String actionMethod = request.method();
    String messageId = ExecutionContext.getRequestId();
    String url = request.uri();
    String methodName = actionMethod;
    long startTime = System.currentTimeMillis();

    ExecutionContext context = ExecutionContext.getCurrent();
    Map<String, Object> reqContext = new HashMap<>();
    // set env and channel to the
    String channel = request.getHeader(JsonKey.CHANNEL_ID);
    if (StringUtils.isBlank(channel)) {
      channel = JsonKey.DEFAULT_ROOT_ORG_ID;
    }
    reqContext.put(JsonKey.CHANNEL, channel);
    // we need channel in case
    ctx.flash().put(JsonKey.CHANNEL, channel);
    reqContext.put(JsonKey.ENV, getOperationEnv(request));
    reqContext.put(JsonKey.REQUEST_ID, ExecutionContext.getRequestId());

    if (!USER_UNAUTH_STATES.contains(userId)) {
      reqContext.put(JsonKey.ACTOR_ID, userId);
      reqContext.put(JsonKey.ACTOR_TYPE, JsonKey.USER);
      ctx.flash().put(JsonKey.ACTOR_ID, userId);
      ctx.flash().put(JsonKey.ACTOR_TYPE, JsonKey.USER);
    } else {
      String consumerId = request.getHeader(HeaderParam.X_Consumer_ID.getName());
      if (StringUtils.isBlank(consumerId)) {
        consumerId = JsonKey.DEFAULT_CONSUMER_ID;
      }
      reqContext.put(JsonKey.ACTOR_ID, consumerId);
      reqContext.put(JsonKey.ACTOR_TYPE, JsonKey.CONSUMER);
      ctx.flash().put(JsonKey.ACTOR_ID, consumerId);
      ctx.flash().put(JsonKey.ACTOR_TYPE, JsonKey.CONSUMER);
    }

    context.setRequestContext(reqContext);
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.CONTEXT, TelemetryUtil.getTelemetryContext());
    Map<String, Object> additionalInfo = new HashMap<>();
    additionalInfo.put(JsonKey.URL, url);
    additionalInfo.put(JsonKey.METHOD, methodName);
    additionalInfo.put(JsonKey.START_TIME, startTime);

    // additional info contains info other than context info ...
    map.put(JsonKey.ADDITIONAL_INFO, additionalInfo);
    if (StringUtils.isBlank(messageId)) {
      messageId = JsonKey.DEFAULT_CONSUMER_ID;
    }
    ctx.flash().put(JsonKey.REQUEST_ID, messageId);
    requestInfo.put(messageId, map);
  }
}
