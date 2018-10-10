package org.sunbird.actorutil.email;

import akka.actor.ActorRef;
import java.util.Map;
import org.sunbird.common.models.response.Response;

public interface EmailServiceClient {
  /**
   * Send mail user from course.
   *
   * @param actorRef Actor reference
   * @param request Request containing unenroll information
   * @return Response containing unenroll request status
   */
  Response sendMail(ActorRef actorRef, Map<String, Object> request);
}
