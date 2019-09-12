package org.sunbird.statevalidateutil;

public enum UserBoolFlagEnum {
  PHONE_VERIFIED("phoneVerified", 1),
  EMAIL_VERIFIED("emailVerified",  2),
  STATE_VALIDATED("stateValidated", 4);

  private String userFlagType;
  private int userFlagValue;

  UserBoolFlagEnum(String userFlagType, int userFlagValue) {
    this.userFlagType = userFlagType;
    this.userFlagValue = userFlagValue;
  }

  public int getUserFlagValue() {
    return userFlagValue;
  }

  public String getUserFlagType() {
    return userFlagType;
  }

}
