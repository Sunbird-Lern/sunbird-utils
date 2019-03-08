package org.sunbird.common.message.broker.factory;

import org.sunbird.common.message.broker.impl.MessageBrokerImpl;
import org.sunbird.common.message.broker.inf.MessageBroker;

public class MessageBrokerFactory {
  private static MessageBroker broker = null;

  public MessageBrokerFactory() {
    // TODO Auto-generated constructor stub
  }

  public static MessageBroker getInstance() {
    if (null == broker) {
      broker = new MessageBrokerImpl();
    }
    return broker;
  }
}
