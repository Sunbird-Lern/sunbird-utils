package org.sunbird.actorutil.impl;

import static akka.pattern.PatternsCS.ask;

import akka.actor.ActorRef;
import akka.util.Timeout;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.sunbird.actorutil.InterServiceCommunication;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;
import scala.concurrent.duration.Duration;

public class InterServiceCommunicationImpl implements InterServiceCommunication {

  private static final Integer WAIT_TIME = 10;

  @Override
  public Object getResponse(ActorRef actorRef, Request request) {
    Timeout t = new Timeout(Duration.create(WAIT_TIME, TimeUnit.SECONDS));
    if (null == actorRef) {
      ProjectLogger.log(
          "InterServiceCommunicationImpl : getResponse - actorRef is null ", LoggerEnum.INFO);
      throw new ProjectCommonException(
          ResponseCode.unableToCommunicateWithActor.getErrorCode(),
          ResponseCode.unableToCommunicateWithActor.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    CompletableFuture<Object> future = ask(actorRef, request, t).toCompletableFuture();
    try {
      return future.get(WAIT_TIME + 2, TimeUnit.SECONDS);
    } catch (Exception e) {
      ProjectLogger.log(
          "InterServiceCommunicationImpl : Interservice communication error " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.unableToCommunicateWithActor.getErrorCode(),
          ResponseCode.unableToCommunicateWithActor.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
  }
}
