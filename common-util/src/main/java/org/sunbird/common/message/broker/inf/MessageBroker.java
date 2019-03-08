package org.sunbird.common.message.broker.inf;

import org.sunbird.models.event.message.EventMessage;

public interface MessageBroker {

  public void send(EventMessage eventMessage);

  public EventMessage recieve();
}
