package org.sunbird.actorutil.org;

import akka.actor.ActorRef;
import java.util.Map;
import org.sunbird.models.organization.Organisation;

public interface OrganisationClient {

  /**
   * Create organisation.
   *
   * @param actorRef Actor reference
   * @param orgMap Organisation details
   * @return Organisation ID
   */
  String createOrg(ActorRef actorRef, Map<String, Object> orgMap);

  /**
   * Update organisation details.
   *
   * @param actorRef Actor reference
   * @param orgMap Organisation details
   */
  void updateOrg(ActorRef actorRef, Map<String, Object> orgMap);

  /**
   * Get details of organisation for given ID.
   *
   * @param actorRef Actor reference
   * @param orgId Organisation ID
   * @return Organisation details
   */
  Organisation getOrgById(ActorRef actorRef, String orgId);
}
