package org.sunbird.actorutil.org;

import akka.actor.ActorRef;
import java.util.Map;
import org.sunbird.models.organization.Organization;

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
   * @desc This method will update org.
   * @param actorRef Actor reference.
   * @param orgId id for organization
   * @return Organization.
   */
  Organization getOrgById(ActorRef actorRef, String orgId);
}
