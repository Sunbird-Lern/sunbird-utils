package org.sunbird.controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.pattern.Patterns;
import akka.util.Timeout;
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
import org.sunbird.util.ResponseIdUtil;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Results;

/**
 * This controller we can use for writing some common method. it should be extended by each
 * controller class.
 *
 * @author Mahesh Kumar Gangula
 */
public class BaseController extends Controller {

  private static final int AKKA_WAIT_TIME = 10;
  protected Timeout timeout = new Timeout(AKKA_WAIT_TIME, TimeUnit.SECONDS);
  private static Object actorRef = null;
  static ResponseIdUtil util = new ResponseIdUtil();

  static {
    try {
      actorRef = SunbirdMWService.getRequestRouter();
    } catch (Exception ex) {
      ProjectLogger.log("Exception occured while getting actor ref in base controller " + ex);
    }
  }

  /**
   * This is a common method which will handle Asyn response came from Actor service. This method
   * has the internal logic to identify the response object. add some more attribute on top of
   * provided response and then return the response to caller.
   *
   * @param actorRef Object Reference of the actor selection
   * @param request org.sunbird.common.request.Request
   * @param timeout Timeout
   * @param responseKey String
   * @param httpReq Request Play http request
   * @param method name of the api method (GET,POST etc)
   * @return Promise<Result>
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
              ProjectLogger.log("Unsupported Actor Response format", LoggerEnum.INFO.name());
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
   * This method will create play mvc Result object from uri and exception param.
   *
   * @param path String (uri)
   * @param exception Exception
   * @param method name of the api method (GET,POST)
   * @return Result
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
   * Common method to create Response object from path and exception. This response object is used
   * by createCommonExceptionResult method.
   *
   * @param path String
   * @param exception Exception
   * @return Response
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
   * This method will create Response object based on passed exception.
   *
   * @param e
   * @return
   */
  private static Response getErrorResponse(Exception e) {
    Response response = new Response();
    ResponseParams resStatus = new ResponseParams();
    String message = setMessage(e);
    resStatus.setErrmsg(message);
    resStatus.setStatus(StatusType.FAILED.name());
    if (e instanceof ProjectCommonException) {
      ProjectCommonException me = (ProjectCommonException) e;
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
   * This method will return message based on exception type. this method has the assumption if
   * exception is instance of ProjectCommonException then it will come with message. in other case
   * we are checking is it timeout exception then return "Request processing taking too long time.
   * Please try again later." else it will return "Something went wrong in server while processing
   * the reques"
   *
   * @param e Exception
   * @return String
   */
  protected static String setMessage(Exception e) {
    if (e != null) {
      if (e instanceof ProjectCommonException) {
        return e.getMessage();
      } else if (e instanceof akka.pattern.AskTimeoutException) {
        return "Request processing taking too long time. Please try again later.";
      }
    }
    return "Something went wrong in server while processing the request";
  }

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

  public Result createCommonResponse(String path, Response response, String method) {
    return Results.ok(Json.toJson(BaseController.createSuccessResponse(path, response, method)));
  }

  public Result createCommonResponse(String path, String key, Response response, String method) {
    if (!StringUtils.isBlank(key)) {
      Object value = response.getResult().get(JsonKey.RESPONSE);
      response.getResult().remove(JsonKey.RESPONSE);
      response.getResult().put(key, value);
    }
    return Results.ok(Json.toJson(BaseController.createSuccessResponse(path, response, method)));
  }

  /**
   * This method will create success response object.
   *
   * @param path api uri example : /v1/user/create
   * @param response Response object return from controller or actor
   * @param method name of the api method (GET,POST etc)
   * @return Response
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
   * This method will return api version. Assumption is version will always come first in url :EX
   * v1/user/create
   *
   * @param path api uri path value.
   * @return version of api
   */
  public static String getApiVersion(String path) {
    if (StringUtils.isBlank(path)) {
      ProjectLogger.log("Path is coming as null or empty==", LoggerEnum.INFO);
      return "v1";
    }
    return path.split("[/]")[1];
  }

  public static void setActorRef(Object obj) {
    actorRef = obj;
  }

  /**
   * This method will provide remote Actor selection
   *
   * @return Object
   */
  public Object getActorRef() {

    return actorRef;
  }
}
