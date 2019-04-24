package org.sunbird.actor.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletionStage;

import org.sunbird.actor.router.BackgroundRequestRouter;
import org.sunbird.actor.router.RequestRouter;
import org.sunbird.actor.service.BaseMWService;
import org.sunbird.actor.service.SunbirdMWService;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseParams;
import org.sunbird.common.models.response.ResponseParams.StatusType;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.StringFormatter;
import org.sunbird.common.request.ExecutionContext;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedAbstractActor;
import scala.concurrent.duration.Duration;

/** @author Vinaya & Mahesh Kumar Gangula */
public abstract class BaseActor extends UntypedAbstractActor {

  public abstract void onReceive(Request request) throws Throwable;

  private final static String confFile = "eventSync.conf";
  private final static String EVENT_SYNC = "eventSync";

  private static Config config = ConfigFactory.parseResources(confFile);
  private static Map<String, String> eventSyncProperties = new HashMap<>();

  @Override
  public void onReceive(Object message) throws Throwable {
    if (message instanceof Request) {
      Request request = (Request) message;
      String operation = request.getOperation();
      ProjectLogger.log("BaseActor: onReceive called for operation: " + operation, LoggerEnum.INFO);
      try {
        onReceive(request);
      } catch (Exception e) {
        onReceiveException(operation, e);
      }
    } else {
      // Do nothing !
    }
  }

  public void tellToAnother(Request request) {
    request
        .getContext()
        .put(JsonKey.TELEMETRY_CONTEXT, ExecutionContext.getCurrent().getRequestContext());
    SunbirdMWService.tellToBGRouter(request, self());
  }

  public void unSupportedMessage() throws Exception {
    ProjectCommonException exception =
        new ProjectCommonException(
            ResponseCode.invalidRequestData.getErrorCode(),
            ResponseCode.invalidRequestData.getErrorMessage(),
            ResponseCode.CLIENT_ERROR.getResponseCode());
    sender().tell(exception, self());
  }

  public void onReceiveUnsupportedOperation(String callerName) throws Exception {
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

  protected void onReceiveException(String callerName, Exception exception) throws Exception {
    ProjectLogger.log(
        "Exception in message processing for: "
            + callerName
            + " :: message: "
            + exception.getMessage(),
        exception);
    sender().tell(exception, self());
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

  protected ActorRef getActorRef(String operation) {
    int waitTime = 10;
    ActorSelection select = null;
    ActorRef actor = RequestRouter.getActor(operation);
    if (null != actor) {
      return actor;
    } else {
      select =
          (BaseMWService.getRemoteRouter(RequestRouter.class.getSimpleName()) == null
              ? (BaseMWService.getRemoteRouter(BackgroundRequestRouter.class.getSimpleName()))
              : BaseMWService.getRemoteRouter(RequestRouter.class.getSimpleName()));
      CompletionStage<ActorRef> futureActor =
          select.resolveOneCS(Duration.create(waitTime, "seconds"));
      try {
        actor = futureActor.toCompletableFuture().get();
      } catch (Exception e) {
        ProjectLogger.log(
            "InterServiceCommunicationImpl : getResponse - unable to get actorref from actorselection "
                + e.getMessage(),
            e);
      }
      return actor;
    }
  }

  protected String getEventSyncSetting(String actor) {
    if (eventSyncProperties.isEmpty()) {
      intiEventSyncProperties();
    }
    String key = StringFormatter.joinByDot(EVENT_SYNC, actor);
    if (eventSyncProperties.containsKey(key)) {
      return eventSyncProperties.get(key);
    }

    return null;
  }

  private void intiEventSyncProperties() {

    Set<Entry<String, ConfigValue>> confs = config.entrySet();
    for (Entry<String, ConfigValue> conf : confs) {
      eventSyncProperties.put(conf.getKey(), conf.getValue().unwrapped().toString());
    }
  }
}
