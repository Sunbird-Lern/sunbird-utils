package org.sunbird.actorutil.org;

import akka.actor.ActorRef;
import java.util.Map;

public interface OrganisationClient {

  /**
   * @desc This method will create org.
   * @param actorRef Actor reference.
   * @param orgMap List of location code.
   * @return Response.
   */
  String createOrg(ActorRef actorRef, Map<String, Object> orgMap);

  /**
   * @desc This method will update org.
   * @param actorRef Actor reference.
   * @param orgMap List of location code.
   * @return Response.
   */
  String updateOrg(ActorRef actorRef, Map<String, Object> orgMap);
}
