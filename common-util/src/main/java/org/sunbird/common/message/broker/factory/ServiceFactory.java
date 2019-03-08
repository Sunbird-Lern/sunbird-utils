package org.sunbird.common.message.broker.factory;

import org.sunbird.common.message.broker.impl.MessageBrokerImpl;
import org.sunbird.common.message.broker.inf.MessageBroker;

public class ServiceFactory {
  private static MessageBroker broker = null;

  public ServiceFactory() {
    // TODO Auto-generated constructor stub
  }

  public static MessageBroker getInstance() {
    if (null == broker) {
      broker = new MessageBrokerImpl();
    }
    return broker;
  }
}
