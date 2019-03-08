package org.sunbird.common.message.broker.model;

import java.util.Map;

public class EventMessage {

  private String opertaionType;
  private String eventType;
  private Map<String, Object> message;

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
