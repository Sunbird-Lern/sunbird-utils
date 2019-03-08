package org.sunbird.common.message.broker.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.sunbird.common.message.broker.inf.MessageBroker;
import org.sunbird.common.message.broker.model.EventMessage;

public class MessageBrokerImpl implements MessageBroker {

  private static BlockingQueue<EventMessage> queue = null;

  public MessageBrokerImpl() {
    if (queue == null) {
      queue = new ArrayBlockingQueue<>(50);
    }
  }

  @Override
  public void send(EventMessage eventMessage) {
    queue.add(eventMessage);
  }

  @Override
  public EventMessage recieve() {
    return queue.poll();
  }
}
