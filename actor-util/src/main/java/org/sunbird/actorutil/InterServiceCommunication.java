package org.sunbird.actorutil;

import akka.actor.ActorRef;
import org.sunbird.common.request.Request;
import scala.concurrent.Future;

/**
 * @Desc This interface will provide utility method to communicate between two actor
 *
 * @author Arvind
 */
public interface InterServiceCommunication {

  /**
   * @param actorRef Actor reference
   * @param Request Request object
   * @return Object Response Object
   */
  public Object getResponse(ActorRef actorRef, Request request);

  /*
   * @param actorRef Actor reference
   * @param Request Request object
   * @return CompletableFuture<Object> Future Object
   */
  public Future<Object> getFuture(ActorRef actorRef, Request request);
}
