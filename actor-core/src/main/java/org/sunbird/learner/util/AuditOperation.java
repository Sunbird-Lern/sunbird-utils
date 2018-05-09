package org.sunbird.learner.util;

/** @author Amit Kumar */
public class AuditOperation {

  private String objectType;
  private String operationType;

  public AuditOperation(String objectType, String operationType) {
    this.objectType = objectType;
    this.operationType = operationType;
  }

  /** @return the objectType */
  public String getObjectType() {
    return objectType;
  }

  /** @param objectType the objectType to set */
  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  /** @return the operationType */
  public String getOperationType() {
    return operationType;
  }

  /** @param operationType the operationType to set */
  public void setOperationType(String operationType) {
    this.operationType = operationType;
  }
}
