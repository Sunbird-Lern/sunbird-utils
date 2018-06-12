package org.sunbird.scheduler;

import akka.actor.ActorRef;
import org.quartz.Job;
import org.sunbird.actor.service.SunbirdMWService;
import org.sunbird.common.request.Request;

/**
 * Abstract base implementation for a Sunbird scheduler job
 *
 * @author Mahesh Kumar Gangula
 */
public abstract class BaseJob implements Job {

  public void tellToBGRouter(Request request) {
    SunbirdMWService.tellToBGRouter(request, ActorRef.noSender());
  }
}
