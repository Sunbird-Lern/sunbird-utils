package org.sunbird.common.message.broker.inf;

import org.sunbird.common.message.broker.model.EventMessage;

public interface MessageBroker {
  public void send(String topic, EventMessage eventMessage);

  public EventMessage recieve(String topic);
}
