package org.sunbird.actorutil.user;

import akka.actor.ActorRef;
import java.util.Map;

public interface UserClient {

  /**
   * Create user.
   *
   * @param actorRef Actor reference
   * @param userMap User details
   * @return User ID
   */
  String createUser(ActorRef actorRef, Map<String, Object> userMap);

  /**
   * Update user details.
   *
   * @param actorRef Actor reference
   * @param userMap User details
   */
  void updateUser(ActorRef actorRef, Map<String, Object> userMap);

  /**
   * Check phone uniqueness.
   *
   * @param existingValue existing db value
   * @param requestedValue requested value
   */
  void esIsPhoneUnique(boolean existingValue, boolean requestedValue);

  /**
   * Check email uniqueness.
   *
   * @param existingValue existing db value
   * @param requestedValue requested value
   */
  void esIsEmailUnique(boolean existingValue, boolean requestedValue);
}
