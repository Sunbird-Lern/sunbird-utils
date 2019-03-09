package org.sunbird.common.message.broker.impl;

import com.google.common.eventbus.EventBus;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import org.sunbird.common.eventbus.factory.EventBusfactory;
import org.sunbird.common.eventbus.subscriber.EventSubsciber;
import org.sunbird.common.message.broker.inf.MessageBroker;
import org.sunbird.common.message.broker.model.EventMessage;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

public class MessageBrokerImpl implements MessageBroker {

  private static Map<String, BlockingQueue<EventMessage>> queueMap = new ConcurrentHashMap<>();
  private static EventBus eventBus = EventBusfactory.getInstance();

  static {
    queueMap.put(JsonKey.DATABASE_OPERATION, new ArrayBlockingQueue<>(50));
    queueMap.put(JsonKey.INFORMATIONAL, new ArrayBlockingQueue<>(50));
  }

  public MessageBrokerImpl() {
    EventSubsciber sub = new EventSubsciber();
    eventBus.register(sub);
    ProjectLogger.log("Messagebrokerimpl :Constructor called ", LoggerEnum.INFO);
  }

  @Override
  public void send(String topic, EventMessage eventMessage) {
    BlockingQueue<EventMessage> queue = queueMap.get(topic);
    if (queue == null) {
      ProjectLogger.log(
          "Messagebrokerimpl :Send :NO topic Found For parameter :" + topic, LoggerEnum.INFO);
    } else {
      queue.add(eventMessage);
      eventBus.post(topic);
      ProjectLogger.log(
          "Messagebrokerimpl: Send : Queue size for Topic :"
              + topic
              + " : is **** : "
              + queue.size(),
          LoggerEnum.INFO);
    }
  }

  @Override
  public EventMessage recieve(String topic) {
    BlockingQueue<EventMessage> queue = queueMap.get(topic);
    if (queue != null) {
      ProjectLogger.log(
          "Messagebrokerimpl: Send : Queue size for Topic :"
              + topic
              + " : is before removing **** : "
              + queue.size(),
          LoggerEnum.INFO);
      return queue.poll();
    }
    ProjectLogger.log(
        "Messagebrokerimpl: Send : NO topic Found For parameter :" + topic, LoggerEnum.INFO);
    return null;
  }
}
