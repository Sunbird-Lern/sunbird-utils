package org.sunbird.common.models.util;

/**
 * enum to represent the bulk upload operations.
 *
 * @author arvind.
 */
public enum BulkUploadActorOperation {
  LOCATION_BULK_UPLOAD("locationBulkUpload"),
  LOCATION_BULK_UPLOAD_BACKGROUND_JOB("locationBulkUploadBackground"),
  ORG_BULK_UPLOAD_BACKGROUND_JOB("orgBulkUploadBackground"),
  ORG_BULK_UPLOAD("orgBulkUpload");

  private String value;

  BulkUploadActorOperation(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
