package org.sunbird.common.models.util;

/**
 * Created by arvind on 7/3/18.
 */
public enum BadgingActorOperations {

  CREATE_BADGE_CLASS("createBadgeClass"),
  GET_BADGE_CLASS("getBadgeClass"),
  LIST_BADGE_CLASS("listBadgeClass"),
  DELETE_BADGE_CLASS("deleteBadgeClass"),
  CREATE_BADGE_ISSUER("createBadgeIssuer"),
  ASSIGN_BADGE_MESSAGE("assignBadgeMessage"),
  REVOKE_BADGE_MESSAGE("revokeBadgeMessage"),
  CREATE_BADGE_ASSERTION("createBageAssertion"),
  GET_BADGE_ASSERTION("getBadgeAssertion"),
  GET_BADGE_ASSERTION_LIST("getBadgeAssertionList"),
  REVOKE_BADGE("revokeBadge");

  private String value;

  /**
   * constructor
   *
   * @param value String
   */
  BadgingActorOperations(String value) {
    this.value = value;
  }

  /**
   * returns the enum value
   *
   * @return String
   */
  public String getValue() {
    return this.value;
  }


}
