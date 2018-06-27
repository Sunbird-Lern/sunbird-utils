package org.sunbird.global;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.actor.service.SunbirdMWService;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.BadgingJsonKey;
import org.sunbird.common.models.util.JsonKey;
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
 * Base class for sunbird services which require Cassandra connection and authentication of API
 * requests to perform specific tasks at application start up or shut down.
 *
 * @author arvind.
 */
public class ServiceBaseGlobal extends BaseGlobal {

  public static ProjectUtil.Environment env;

  public static Map<String, Map<String, Object>> requestInfo = new HashMap<>();

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
      // Verify request data like authentication
      String userId = RequestInterceptor.verifyRequestData(ctx);
      // Set required parameters for telemetry event
      intializeRequestInfo(ctx, userId);
      if (!USER_UNAUTH_STATES.contains(userId)) {
        ctx.flash().put(JsonKey.USER_ID, userId);
        ctx.flash().put(JsonKey.IS_AUTH_REQ, "false");
        for (String uri : RequestInterceptor.getRestrictedUrlList()) {
          if (ctx.request().path().contains(uri)) {
            ctx.flash().put(JsonKey.IS_AUTH_REQ, "true");
            break;
          }
        }
        result = delegate.call(ctx);
      } else if (JsonKey.UNAUTHORIZED.equals(userId)) {
        result =
            createValidationErrorResult(
                ctx.request(), userId, ResponseCode.UNAUTHORIZED.getResponseCode());
      } else {
        result = delegate.call(ctx);
      }
      return result;
    }
  }

  /**
   * Called on application startup.
   *
   * @param app Play application
   */
  @Override
  public void onStart(Application app) {
    setEnvironment();
    SunbirdMWService.init();
    CassandraStartUpUtil.createCassandraConnection(JsonKey.SUNBIRD);
    CassandraStartUpUtil.createCassandraConnection(JsonKey.SUNBIRD_PLUGIN);
  }

  /**
   * Called to create root action for each request.
   *
   * @param request HTTP request
   * @param actionMethod Action method
   * @return Root action created for received request
   */
  @Override
  @SuppressWarnings("rawtypes")
  public Action onRequest(Request request, Method actionMethod) {

    String messageId = request.getHeader(JsonKey.MESSAGE_ID);
    if (StringUtils.isBlank(messageId)) {
      messageId = UUID.randomUUID().toString();
    }
    ExecutionContext.setRequestId(messageId);
    return new ActionWrapper(super.onRequest(request, actionMethod));
  }

  private void intializeRequestInfo(Context ctx, String userId) {
    Request request = ctx.request();
    String actionMethod = ctx.request().method();
    String messageId = ExecutionContext.getRequestId();
    String url = request.uri();
    long startTime = System.currentTimeMillis();

    ExecutionContext context = ExecutionContext.getCurrent();
    Map<String, Object> reqContext = new HashMap<>();
    // set env and channel to the request context
    String channel = request.getHeader(JsonKey.CHANNEL_ID);
    if (StringUtils.isBlank(channel)) {
      channel = ProjectUtil.getConfigValue(JsonKey.SUNBIRD_DEFAULT_CHANNEL);
    }
    reqContext.put(JsonKey.CHANNEL, channel);
    ctx.flash().put(JsonKey.CHANNEL, channel);
    reqContext.put(JsonKey.ENV, getEnv(request));
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
    additionalInfo.put(JsonKey.METHOD, actionMethod);
    additionalInfo.put(JsonKey.START_TIME, startTime);

    // additional info contains info other than context info ...
    map.put(JsonKey.ADDITIONAL_INFO, additionalInfo);
    ctx.flash().put(JsonKey.REQUEST_ID, messageId);
    requestInfo.put(messageId, map);
  }

  private String getEnv(Request request) {

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
    } else if (uri.startsWith("/v1/content")) {
      env = JsonKey.BATCH;
    } else if (uri.startsWith("/v1/role")) {
      env = JsonKey.ROLE;
    } else if (uri.startsWith("/v1/note")) {
      env = JsonKey.NOTE;
    } else if (uri.startsWith("/v1/location")) {
      env = JsonKey.LOCATION;
    } else {
      env = "miscellaneous";
    }
    return env;
  }

  /**
   * Returns a promise with error result in case of validation error
   *
   * @param request HTTP request in play context
   * @param errorMessage Validation error message
   * @return Promise with error result
   */
  private Promise<Result> createValidationErrorResult(
      Request request, String errorMessage, int responseCode) {
    ProjectLogger.log(
        "ServiceBaseGlobal:createValidationErrorResult: Validation error result: " + errorMessage);
    ResponseCode code = ResponseCode.getResponse(errorMessage);
    ResponseCode headerCode = ResponseCode.CLIENT_ERROR;
    Response resp = BaseController.createFailureResponse(request, code, headerCode);
    return Promise.<Result>pure(Results.status(responseCode, Json.toJson(resp)));
  }

  /**
   * This method will identify the environment and update with enum.
   *
   * @return Environment
   */
  public Environment setEnvironment() {

    if (play.Play.isDev()) {
      return env = Environment.dev;
    } else if (play.Play.isTest()) {
      return env = Environment.qa;
    } else {
      return env = Environment.prod;
    }
  }
}
