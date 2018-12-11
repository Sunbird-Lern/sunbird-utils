package org.sunbird.common.request;

public enum SignupType {
  GOOGLE("GOOGLE");

  private String typeName;

  private SignupType(String name) {
    this.typeName = name;
  }

  public String getTypeName() {
    return typeName;
  }
}
