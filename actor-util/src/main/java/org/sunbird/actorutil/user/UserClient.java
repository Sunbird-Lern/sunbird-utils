package org.sunbird.actorutil.user;

import akka.actor.ActorRef;
import java.util.Map;

public interface UserClient {

  /**
   * Create user.
   *
   * @param actorRef Actor reference
   * @param userMap user details
   * @return User ID
   */
  String createUser(ActorRef actorRef, Map<String, Object> userMap);

  /**
   * Update user details.
   *
   * @param actorRef Actor reference
   * @param userMap user details
   */
  void updateUser(ActorRef actorRef, Map<String, Object> userMap);
}
