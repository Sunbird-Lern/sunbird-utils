package org.sunbird.actorutil;

import akka.actor.ActorRef;
import org.sunbird.common.request.Request;

/** Created by arvind on 24/4/18. */
public interface InterServiceCommunication {

  public Object getResponse(ActorRef actor, Request request);
}
