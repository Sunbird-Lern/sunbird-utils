package org.sunbird.models.user;

public enum UserType {
  TEACHER("TEACHER"),
  SELF_SIGNUP("SELF_SIGN_UP"),
  OTHER("OTHER");

  private String typeName;

  private UserType(String name) {
    this.typeName = name;
  }

  public String getTypeName() {
    return typeName;
  }
}
