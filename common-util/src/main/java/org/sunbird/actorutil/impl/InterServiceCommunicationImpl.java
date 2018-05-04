package org.sunbird.actorutil.impl;

import static akka.pattern.PatternsCS.ask;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.util.Timeout;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import org.sunbird.actorutil.InterServiceCommunication;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.Request;
import scala.concurrent.duration.Duration;

/** Created by arvind on 24/4/18. */
public class InterServiceCommunicationImpl implements InterServiceCommunication {

  private Integer WAIT_TIME = 10;

  @Override
  public Object getResponse(Request request, Object actorRef) {
    ActorRef actor = null;
    ActorSelection select = null;
    if (actorRef instanceof ActorRef) {
      actor = (ActorRef) actorRef;
    } else if (actorRef instanceof ActorSelection) {
      select = (ActorSelection) actorRef;
    }
    Timeout t = new Timeout(Duration.create(WAIT_TIME, TimeUnit.SECONDS));
    Object obj = null;
    if (null == actor) {
      CompletionStage<ActorRef> futureActor =
          select.resolveOneCS(Duration.create(WAIT_TIME, "seconds"));
      try {
        actor = futureActor.toCompletableFuture().get();
      } catch (Exception e) {
        ProjectLogger.log(
            "InterServiceCommunicationImpl : getResponse - unable to get actorref from actorselection "
                + e.getMessage(),
            e);
      }
    }
    if (null == actor) {
      ProjectLogger.log(
          "InterServiceCommunicationImpl : getResponse - actorRef is null ", LoggerEnum.INFO);
      return obj;
    }
    ProjectLogger.log(
        "Operation "
            + request.getOperation()
            + " with request type "
            + (request instanceof Request));
    CompletableFuture<Object> future = ask(actor, request, t).toCompletableFuture();
    try {
      obj = future.get(WAIT_TIME + 2, TimeUnit.SECONDS);
    } catch (Exception e) {
      ProjectLogger.log("Interservice communication error " + e.getMessage(), e);
    }
    return obj;
  }
}
