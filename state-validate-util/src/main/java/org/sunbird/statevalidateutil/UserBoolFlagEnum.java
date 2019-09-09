package org.sunbird.statevalidateutil;

public enum UserBoolFlagEnum {
  PHONE_VERIFIED("phoneVerified", true, 1),
  EMAIL_VERIFIED("emailVerified", true, 2),
  IS_STATE_VALIDATED("isStateValidated", true, 4);

  private String userFlagType;
  private boolean flagEnabled;
  private int userFlagValue;

  UserBoolFlagEnum(String userFlagType, boolean flagEnabled, int userFlagValue) {
    this.userFlagType = userFlagType;
    this.flagEnabled = flagEnabled;
    this.userFlagValue = userFlagValue;
  }

  public int getUserFlagValue() {
    return userFlagValue;
  }

  public String getUserFlagType() {
    return userFlagType;
  }

  public boolean isFlagEnabled() {
    return flagEnabled;
  }

}
