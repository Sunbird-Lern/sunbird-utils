package org.sunbird.common.models.util;

public enum UserType {
  TEACHER("TEACHER"),
  SELF_SIGNUP("SELF_SIGNUP"),
  OTHER("OTHER");

  private String typeName;

  private UserType(String name) {
    this.typeName = name;
  }

  public String getTypeName() {
    return typeName;
  }
}
