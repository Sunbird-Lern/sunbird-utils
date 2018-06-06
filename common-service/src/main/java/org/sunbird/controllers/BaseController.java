package org.sunbird.controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.pattern.Patterns;
import akka.util.Timeout;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.actor.service.SunbirdMWService;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseParams;
import org.sunbird.common.models.response.ResponseParams.StatusType;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.request.ExecutionContext;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.global.ServiceBaseGlobal;
import org.sunbird.telemetry.util.TelemetryLmaxWriter;
import org.sunbird.telemetry.util.lmaxdisruptor.TelemetryEvents;
import org.sunbird.util.ResponseIdUtil;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Base controller with common functionality which can be inherited by other controller classes for
 * reuse.
 *
 * @author Mahesh Kumar Gangula
 */
public class BaseController extends Controller {

  private static final int AKKA_WAIT_TIME = 10;
  protected Timeout timeout = new Timeout(AKKA_WAIT_TIME, TimeUnit.SECONDS);
  private static Object actorRef = null;
  private static ResponseIdUtil util = new ResponseIdUtil();
  private TelemetryLmaxWriter lmaxWriter = TelemetryLmaxWriter.getInstance();

  static {
    try {
      actorRef = SunbirdMWService.getRequestRouter();
    } catch (Exception ex) {
      ProjectLogger.log("BaseController: Exception occurred while getting actor reference: " + ex);
    }
  }

  /**
   * Common method to handle async response from actor service. Based on the result type, a uniform
   * response object is constructed and returned to caller.
   *
   * @param actorRef Actor reference or selection object
   * @param request Request to be sent to actor
   * @param timeout Request timeout
   * @param responseKey Response key used to update result within success response
   * @param httpReq Play HTTP request
   * @return Return a promise for API request
   */
  public Promise<Result> actorResponseHandler(
      Object actorRef,
      org.sunbird.common.request.Request request,
      Timeout timeout,
      String responseKey,
      Request httpReq) {
    Function<Object, Result> function =
        new Function<Object, Result>() {
          public Result apply(Object result) {
            if (result instanceof Response) {
              Response response = (Response) result;
              return createCommonResponse(
                  request().path(), responseKey, response, httpReq.method());
            } else if (result instanceof ProjectCommonException) {
              return createCommonExceptionResult(
                  request().path(), (ProjectCommonException) result, httpReq.method());
            } else {
              ProjectLogger.log(
                  "BaseController:actorResponseHandler: Unsupported actor response format",
                  LoggerEnum.INFO.name());
              return createCommonExceptionResult(
                  request().path(), new Exception(), httpReq.method());
            }
          }
        };

    if (actorRef instanceof ActorRef) {
      return Promise.wrap(Patterns.ask((ActorRef) actorRef, request, timeout)).map(function);
    } else {
      return Promise.wrap(Patterns.ask((ActorSelection) actorRef, request, timeout)).map(function);
    }
  }

  /**
   * Creates play mvc result in case of an exception.
   *
   * @param path Request URI path
   * @param exception Exception which occurred
   * @param method Request method (e.g. GET)
   * @return Play mvc result created for given exception
   */
  public Result createCommonExceptionResult(String path, Exception exception, String method) {
    Response reponse = createResponseOnException(path, exception, method);
    int status = ResponseCode.SERVER_ERROR.getResponseCode();
    if (exception instanceof ProjectCommonException) {
      ProjectCommonException me = (ProjectCommonException) exception;
      status = me.getResponseCode();
    }
    return Results.status(status, Json.toJson(reponse));
  }

  /**
   * Creates a response object in case of an exception.
   *
   * @param path Request URI path
   * @param exception Exception which occurred
   * @param method Request method (e.g. GET)
   * @return Response object created
   */
  public static Response createResponseOnException(
      String path, Exception exception, String method) {
    Response response = getErrorResponse(exception);
    response.setVer("");
    if (path != null) {
      response.setVer(getApiVersion(path));
    }
    response.setId(util.getApiResponseId(path, method));
    response.setTs(ProjectUtil.getFormattedDate());
    return response;
  }

  /**
   * Creates a response object in case of an exception.
   *
   * @param exception Exception which occurred
   * @return Response object created
   */
  private static Response getErrorResponse(Exception exception) {
    Response response = new Response();
    ResponseParams resStatus = new ResponseParams();
    String message = setMessage(exception);
    resStatus.setErrmsg(message);
    resStatus.setStatus(StatusType.FAILED.name());
    if (exception instanceof ProjectCommonException) {
      ProjectCommonException me = (ProjectCommonException) exception;
      resStatus.setErr(me.getCode());
      response.setResponseCode(ResponseCode.getHeaderResponseCode(me.getResponseCode()));
    } else {
      resStatus.setErr(ResponseCode.SERVER_ERROR.name());
      response.setResponseCode(ResponseCode.SERVER_ERROR);
    }
    response.setParams(resStatus);
    return response;
  }

  /**
   * Return message based on exception.
   *
   * @param exception Exception which occurred
   * @return Message corresponding to exception
   */
  protected static String setMessage(Exception exception) {
    if (exception != null) {
      if (exception instanceof ProjectCommonException) {
        return exception.getMessage();
      } else if (exception instanceof akka.pattern.AskTimeoutException) {
        return "Request timed out. Please try again.";
      }
    }
    return "Something went wrong while processing the request. Please try again.";
  }

  /**
   * Creates response params based on response code.
   *
   * @param code Response code
   * @return Response params created based on response code
   */
  public static ResponseParams createResponseParamObj(ResponseCode code) {
    ResponseParams params = new ResponseParams();
    if (code.getResponseCode() != 200) {
      params.setErr(code.getErrorCode());
      params.setErrmsg(code.getErrorMessage());
    }
    params.setMsgid(ExecutionContext.getRequestId());
    params.setStatus(ResponseCode.getHeaderResponseCode(code.getResponseCode()).name());
    return params;
  }

  /**
   * Creates a success play mvc result.
   *
   * @param path Request URI path
   * @param response Success response which is received
   * @param method Request method (e.g. GET)
   * @return Play mvc result created from success response
   */
  public Result createCommonResponse(String path, Response response, String method) {
    return Results.ok(Json.toJson(BaseController.createSuccessResponse(path, response, method)));
  }

  /**
   * Creates a success play mvc result.
   *
   * @param path Request URI path
   * @param key Response key used to update result within success response
   * @param response Success response which is received
   * @param method Request method (e.g. GET)
   * @return Play mvc result created from success response
   */
  public Result createCommonResponse(String path, String key, Response response, String method) {
    if (!StringUtils.isBlank(key)) {
      Object value = response.getResult().get(JsonKey.RESPONSE);
      response.getResult().remove(JsonKey.RESPONSE);
      response.getResult().put(key, value);
    }
    return Results.ok(Json.toJson(BaseController.createSuccessResponse(path, response, method)));
  }

  /**
   * Updates success response with common parameters.
   *
   * @param path Request URI path
   * @param response Success response which is received
   * @param method Request method (e.g. GET)
   * @return Updated response
   */
  public static Response createSuccessResponse(String path, Response response, String method) {
    if (StringUtils.isNotBlank(path)) {
      response.setVer(getApiVersion(path));
    } else {
      response.setVer("");
    }
    response.setId(util.getApiResponseId(path, method));
    response.setTs(ProjectUtil.getFormattedDate());
    ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
    code.setResponseCode(ResponseCode.OK.getResponseCode());
    response.setParams(createResponseParamObj(code));
    return response;
  }

  /**
   * Determine version of API request based on URI.
   *
   * @param path Request URI path
   * @return Version of API request
   */
  public static String getApiVersion(String path) {
    if (StringUtils.isBlank(path)) {
      ProjectLogger.log("BaseController:getApiVersion: Path is blank.", LoggerEnum.INFO);
      return "v1";
    }
    return path.split("[/]")[1];
  }

  /**
   * Setter method for actor reference or selection object.
   *
   * @param actorRefObj Actor reference or selection object
   */
  public static void setActorRef(Object actorRefObj) {
    actorRef = actorRefObj;
  }

  /**
   * Getter method for actor reference or selection object.
   *
   * @return Actor reference or selection object
   */
  public Object getActorRef() {
    return actorRef;
  }

  /**
   * This method will create failure response
   *
   * @param request Request
   * @param code ResponseCode
   * @param headerCode ResponseCode
   * @return Response
   */
  public static Response createFailureResponse(
      Request request, ResponseCode code, ResponseCode headerCode) {

    Response response = new Response();
    response.setVer(getApiVersion(request.path()));
    response.setId(getApiResponseId(request));
    response.setTs(ProjectUtil.getFormattedDate());
    response.setResponseCode(headerCode);
    response.setParams(createResponseParamObj(code));
    return response;
  }

  /**
   * Method to get API response Id
   *
   * @param request play.mvc.Http.Request
   * @return String
   */
  private static String getApiResponseId(Request request) {

    String val = "";
    if (request != null) {
      String path = request.path();
      if (request.method().equalsIgnoreCase(ProjectUtil.Method.GET.name())) {
        val = ResponseIdUtil.getResponseId(path);
        if (StringUtils.isBlank(val)) {
          String[] splitedpath = path.split("[/]");
          path = ResponseIdUtil.removeLastValue(splitedpath);
          val = ResponseIdUtil.getResponseId(path);
        }
      } else {
        val = ResponseIdUtil.getResponseId(path);
      }
      if (StringUtils.isBlank(val)) {
        val = ResponseIdUtil.getResponseId(path);
        if (StringUtils.isBlank(val)) {
          String[] splitedpath = path.split("[/]");
          path = ResponseIdUtil.removeLastValue(splitedpath);
          val = ResponseIdUtil.getResponseId(path);
        }
      }
    }
    return val;
  }

  /**
   * This method will create common response for all controller method
   *
   * @param response Object
   * @param key String
   * @param request play.mvc.Http.Request
   * @return Result
   */
  public Result createCommonResponse(Object response, String key, Request request) {

    Map<String, Object> requestInfo =
        ServiceBaseGlobal.requestInfo.get(ctx().flash().get(JsonKey.REQUEST_ID));
    org.sunbird.common.request.Request req = new org.sunbird.common.request.Request();

    Map<String, Object> params = (Map<String, Object>) requestInfo.get(JsonKey.ADDITIONAL_INFO);

    params.put(JsonKey.LOG_TYPE, JsonKey.API_ACCESS);
    params.put(JsonKey.MESSAGE, "");
    params.put(JsonKey.METHOD, request.method());
    // calculate  the total time consume
    long startTime = (Long) params.get(JsonKey.START_TIME);
    params.put(JsonKey.DURATION, calculateApiTimeTaken(startTime));
    removeFields(params, JsonKey.START_TIME);
    params.put(
        JsonKey.STATUS, String.valueOf(((Response) response).getResponseCode().getResponseCode()));
    params.put(JsonKey.LOG_LEVEL, JsonKey.INFO);
    req.setRequest(
        generateTelemetryRequestForController(
            TelemetryEvents.LOG.getName(),
            params,
            (Map<String, Object>) requestInfo.get(JsonKey.CONTEXT)));
    // if any request is coming form /v1/telemetry/save then don't generate the telemetry log
    // for it.
    lmaxWriter.submitMessage(req);

    Response courseResponse = (Response) response;
    if (!StringUtils.isBlank(key)) {
      Object value = courseResponse.getResult().get(JsonKey.RESPONSE);
      courseResponse.getResult().remove(JsonKey.RESPONSE);
      courseResponse.getResult().put(key, value);
    }

    // remove request info from map
    ServiceBaseGlobal.requestInfo.remove(ctx().flash().get(JsonKey.REQUEST_ID));
    return Results.ok(
        Json.toJson(BaseController.createSuccessResponse(request, (Response) courseResponse)));
    // }
    /*
     * else {
     *
     * ProjectCommonException exception = (ProjectCommonException) response; return
     * Results.status(exception.getResponseCode(),
     * Json.toJson(BaseController.createResponseOnException(request, exception))); }
     */
  }

  private long calculateApiTimeTaken(Long startTime) {

    Long timeConsumed = null;
    if (null != startTime) {
      timeConsumed = System.currentTimeMillis() - startTime;
    }
    return timeConsumed;
  }

  private void removeFields(Map<String, Object> params, String... properties) {
    for (String property : properties) {
      params.remove(property);
    }
  }

  private Map<String, Object> generateTelemetryRequestForController(
      String eventType, Map<String, Object> params, Map<String, Object> context) {

    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.TELEMETRY_EVENT_TYPE, eventType);
    map.put(JsonKey.CONTEXT, context);
    map.put(JsonKey.PARAMS, params);
    return map;
  }

  /**
   * This method will create data for success response.
   *
   * @param request play.mvc.Http.Request
   * @param response Response
   * @return Response
   */
  public static Response createSuccessResponse(Request request, Response response) {

    if (request != null) {
      response.setVer(getApiVersion(request.path()));
    } else {
      response.setVer("");
    }
    response.setId(getApiResponseId(request));
    response.setTs(ProjectUtil.getFormattedDate());
    ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
    code.setResponseCode(ResponseCode.OK.getResponseCode());
    response.setParams(createResponseParamObj(code));
    return response;
  }
}
