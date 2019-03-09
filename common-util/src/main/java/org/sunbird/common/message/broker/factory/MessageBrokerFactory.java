package org.sunbird.common.message.broker.factory;

import org.sunbird.common.message.broker.impl.MessageBrokerImpl;
import org.sunbird.common.message.broker.inf.MessageBroker;

public class MessageBrokerFactory {
  private static MessageBroker broker = null;

  private MessageBrokerFactory() {
    // TODO Auto-generated constructor stub
  }

  public static MessageBroker getInstance() {
    if (broker == null) {
      broker = new MessageBrokerImpl();
    }
    return broker;
  }
}
