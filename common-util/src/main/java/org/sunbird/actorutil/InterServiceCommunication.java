package org.sunbird.actorutil;

import akka.actor.ActorRef;
import org.sunbird.common.request.Request;

/**
 * Created by arvind on 24/4/18. @Desc This Interface will provide utility method to communicate
 * between two actor
 */
public interface InterServiceCommunication {

  /**
   * @param Actor reference
   * @param Request object
   * @return Object
   */
  public Object getResponse(ActorRef actor, Request request);
}
