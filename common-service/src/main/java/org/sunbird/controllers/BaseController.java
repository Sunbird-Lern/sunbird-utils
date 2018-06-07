package org.sunbird.controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.sunbird.telemetry.util.TelemetryConstant;
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
    return actorResponseHandler(actorRef, request, timeout, responseKey, httpReq, true);
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
   * @param generateTelemetry If true, generate telemetry for the API call
   * @return Return a promise for API request
   */
  public Promise<Result> actorResponseHandler(
      Object actorRef,
      org.sunbird.common.request.Request request,
      Timeout timeout,
      String responseKey,
      Request httpReq,
      boolean generateTelemetry) {
    Function<Object, Result> function =
        new Function<Object, Result>() {
          public Result apply(Object result) {
            if (result instanceof Response) {
              Response response = (Response) result;
              return createSuccessResponse(response, responseKey, httpReq, generateTelemetry);
            } else if (result instanceof ProjectCommonException) {
              return createCommonExceptionResponse(
                  (ProjectCommonException) result, httpReq, generateTelemetry);
            } else {
              ProjectLogger.log(
                  "BaseController:actorResponseHandler: Unsupported actor response format",
                  LoggerEnum.INFO.name());
              return createCommonExceptionResponse(new Exception(), httpReq, generateTelemetry);
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
   * @param request represents the play context request.
   * @param code Failure code used to set response params (i.e. err, errmsg)
   * @param headerCode Failure code used to set response code (e.g. CLIENT_ERROR, SERVER_ERROR etc.)
   * @return Response
   */
  public static Response createFailureResponse(
      Request request, ResponseCode code, ResponseCode headerCode) {

    Response response = new Response();
    response.setVer(getApiVersion(request.path()));
    response.setId(ResponseIdUtil.getApiResponseId(request.path(), request.method()));
    response.setTs(ProjectUtil.getFormattedDate());
    response.setResponseCode(headerCode);
    response.setParams(createResponseParamObj(code));
    return response;
  }

  /**
   * This method will create success response for all controller method
   *
   * @param response represents the response from service layer.
   * @param key Response key used to update result within success response
   * @param request Represents the play context request
   * @return Return play action success result
   */
  public Result createSuccessResponse(
      Response response, String key, Request request, boolean generateTelemetry) {

    if (generateTelemetry) {
      generateTelemetryForSuccessResponse(response, request);
    }

    if (!StringUtils.isBlank(key)) {
      Object value = response.getResult().get(JsonKey.RESPONSE);
      response.getResult().remove(JsonKey.RESPONSE);
      response.getResult().put(key, value);
    }
    return Results.ok(
        Json.toJson(BaseController.createSuccessResponseBody(request, (Response) response)));
  }

  /**
   * Helper method for creating and initialising a request for given operation and request body.
   *
   * @param operation A defined actor operation
   * @param requestBodyJson Optional information received in request body (JSON)
   * @return Created and initialised Request (@see {@link org.sunbird.common.request.Request})
   *     instance.
   */
  public org.sunbird.common.request.Request createAndInitRequest(
      String operation, JsonNode requestBodyJson) {
    org.sunbird.common.request.Request request;

    if (requestBodyJson != null) {
      request =
          (org.sunbird.common.request.Request)
              org.sunbird.controllers.mapper.RequestMapper.mapRequest(
                  requestBodyJson, org.sunbird.common.request.Request.class);
    } else {
      request = new org.sunbird.common.request.Request();
    }

    request.setOperation(operation);
    request.setRequestId(ExecutionContext.getRequestId());
    request.setEnv(getEnvironment());
    request.getContext().put(JsonKey.REQUESTED_BY, ctx().flash().get(JsonKey.USER_ID));
    return request;
  }

  /**
   * Helper method for creating and initialising a request for given operation.
   *
   * @param operation A defined actor operation
   * @return Created and initialised Request (@see {@link org.sunbird.common.request.Request})
   *     instance.
   */
  public org.sunbird.common.request.Request createAndInitRequest(String operation) {
    return createAndInitRequest(operation, null);
  }

  /**
   * Method to create data for success response.
   *
   * @param request play.mvc.Http.Request
   * @param response represents the response object from service layer.
   * @return Response
   */
  public static Response createSuccessResponseBody(Request request, Response response) {

    if (request != null) {
      response.setVer(getApiVersion(request.path()));
    } else {
      response.setVer("");
    }
    response.setId(ResponseIdUtil.getApiResponseId(request.path(), request.method()));
    response.setTs(ProjectUtil.getFormattedDate());
    ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
    code.setResponseCode(ResponseCode.OK.getResponseCode());
    response.setParams(createResponseParamObj(code));
    return response;
  }

  /**
   * Common exception response handler method.
   *
   * @param e Exception represents the exception occurred while processing the request.
   * @param request play.mvc.Http.Request
   * @return Result
   */
  public Result createCommonExceptionResponse(
      Exception e, Request request, boolean generateTelemetry) {

    ProjectCommonException exception = null;
    if (e instanceof ProjectCommonException) {
      exception = (ProjectCommonException) e;
    } else {
      exception =
          new ProjectCommonException(
              ResponseCode.internalError.getErrorCode(),
              ResponseCode.internalError.getErrorMessage(),
              ResponseCode.SERVER_ERROR.getResponseCode());
    }

    Request req = request;
    if (req == null) {
      req = request();
    }

    if (generateTelemetry) {
      generateTelemetryForExceptionResponse(exception, req);
    }
    return createCommonExceptionResult(req.path(), exception, req.method());
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
   * This method will provide environment id.
   *
   * @return int
   */
  private int getEnvironment() {

    if (ServiceBaseGlobal.env != null) {
      return ServiceBaseGlobal.env.getValue();
    }
    return ProjectUtil.Environment.dev.getValue();
  }

  private void generateTelemetryForSuccessResponse(Object response, Request request) {
    generateTelemetry(
        request,
        JsonKey.SUCCESS,
        String.valueOf(((Response) response).getResponseCode().getResponseCode()),
        JsonKey.INFO,
        null);
  }

  private void generateTelemetryForExceptionResponse(
      ProjectCommonException exception, Request request) {
    // Generate a telemetry event of type ERROR using project logger for exception
    ProjectLogger.log(exception.getMessage(), exception, generateTelemetryInfoForError());
    // Generate a telemetry event of type API_ACCESS
    generateTelemetry(
        request,
        exception.getMessage(),
        String.valueOf(exception.getResponseCode()),
        TelemetryConstant.LOG_LEVEL_ERROR,
        generateStackTrace(exception.getStackTrace()));
  }

  private void generateTelemetry(
      Request request, String message, String status, String logLevel, String stackTrace) {
    Map<String, Object> requestInfo =
        ServiceBaseGlobal.requestInfo.get(ctx().flash().get(JsonKey.REQUEST_ID));

    Map<String, Object> params = (Map<String, Object>) requestInfo.get(JsonKey.ADDITIONAL_INFO);

    params.put(JsonKey.LOG_TYPE, JsonKey.API_ACCESS);
    params.put(JsonKey.MESSAGE, message);

    params.put(JsonKey.LOG_TYPE, JsonKey.API_ACCESS);
    params.put(JsonKey.METHOD, request.method());
    // calculate  the total time consume
    Long startTime = (Long) params.get(JsonKey.START_TIME);
    params.put(JsonKey.DURATION, calculateApiTimeTaken(startTime));
    removeFields(params, JsonKey.START_TIME);
    params.put(JsonKey.STATUS, status);
    params.put(JsonKey.LOG_LEVEL, logLevel);
    if (TelemetryConstant.LOG_LEVEL_ERROR.equalsIgnoreCase(logLevel)) {
      params.put(JsonKey.STACKTRACE, stackTrace);
    }
    org.sunbird.common.request.Request req = new org.sunbird.common.request.Request();
    req.setRequest(
        generateTelemetryRequestForController(
            TelemetryEvents.LOG.getName(),
            params,
            (Map<String, Object>) requestInfo.get(JsonKey.CONTEXT)));
    lmaxWriter.submitMessage(req);
    // remove request info from map
    ServiceBaseGlobal.requestInfo.remove(ctx().flash().get(JsonKey.REQUEST_ID));
  }

  private Map<String, Object> generateTelemetryRequestForController(
      String eventType, Map<String, Object> params, Map<String, Object> context) {

    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.TELEMETRY_EVENT_TYPE, eventType);
    map.put(JsonKey.CONTEXT, context);
    map.put(JsonKey.PARAMS, params);
    return map;
  }

  private String generateStackTrace(StackTraceElement[] elements) {
    StringBuilder builder = new StringBuilder("");
    for (StackTraceElement element : elements) {

      builder.append(element.toString());
      builder.append("\n");
    }
    return builder.toString();
  }

  private static Map<String, Object> generateTelemetryInfoForError() {

    Map<String, Object> map = new HashMap<>();
    Map<String, Object> requestInfo =
        ServiceBaseGlobal.requestInfo.get(ctx().flash().get(JsonKey.REQUEST_ID));
    Map<String, Object> contextInfo = (Map<String, Object>) requestInfo.get(JsonKey.CONTEXT);
    Map<String, Object> params = new HashMap<>();
    params.put(JsonKey.ERR_TYPE, JsonKey.API_ACCESS);

    map.put(JsonKey.CONTEXT, contextInfo);
    map.put(JsonKey.PARAMS, params);
    return map;
  }

  private Long calculateApiTimeTaken(Long startTime) {

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
}
