package org.sunbird.common.models.util;

/** Created by arvind on 24/4/18. */
public enum BulkUploadActorOperation {
  LOCATION_BULK_UPLOAD("locationBulkUpload"),
  LOCATION_BULK_UPLOAD_BACKGROUND_JOB("locationBulkUploadBackGround");

  private String value;

  /**
   * constructor
   *
   * @param value String
   */
  BulkUploadActorOperation(String value) {
    this.value = value;
  }

  /**
   * returns enum value
   *
   * @return
   */
  public String getValue() {
    return this.value;
  }
}
