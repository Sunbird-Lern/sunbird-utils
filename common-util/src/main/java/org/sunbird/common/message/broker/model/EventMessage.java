package org.sunbird.common.message.broker.model;

import java.util.Map;

public class EventMessage {

  private String opertaionType;
  private String eventType;
  private Map<String, Object> message;
  private String operationOn;

  public EventMessage(
      String opertaionType, String eventType, Map<String, Object> message, String operationOn) {
    super();
    this.opertaionType = opertaionType;
    this.eventType = eventType;
    this.message = message;
    this.operationOn = operationOn;
  }

  public String getOperationOn() {
    return operationOn;
  }

  public void setOperationOn(String operationOn) {
    this.operationOn = operationOn;
  }

  public String getOpertaionType() {
    return opertaionType;
  }

  public void setOpertaionType(String opertaionType) {
    this.opertaionType = opertaionType;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public Map<String, Object> getMessage() {
    return message;
  }

  public void setMessage(Map<String, Object> message) {
    this.message = message;
  }
}
