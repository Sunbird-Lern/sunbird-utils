package org.sunbird.common.models.util;

/**
 * Enum contains the database related constants
 *
 * @author arvind
 */
public enum DbConstant {
  SUNBIRD_KEYSPACE_NAME("sunbird"),
  USER_TABLE_NAME("user");

  DbConstant(String value) {
    this.value = value;
  }

  String value;

  public String getValue() {
    return this.value;
  }
}
