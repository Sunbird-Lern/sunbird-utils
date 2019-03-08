package org.sunbird.common.message.broker.impl;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import org.sunbird.common.message.broker.inf.MessageBroker;
import org.sunbird.common.message.broker.model.EventMessage;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

public class MessageBrokerImpl implements MessageBroker {

  private static Map<String, BlockingQueue<EventMessage>> queueMap = new ConcurrentHashMap<>();

  public MessageBrokerImpl() {
    queueMap.put(JsonKey.USER, new ArrayBlockingQueue<>(50));
    queueMap.put(JsonKey.ORGANISATION, new ArrayBlockingQueue<>(50));
  }

  @Override
  public void send(String topic, EventMessage eventMessage) {
    BlockingQueue<EventMessage> queue = queueMap.get(topic);
    if (queue == null) {
      ProjectLogger.log("NO topic Found For parameter :" + topic, LoggerEnum.INFO);
    } else {
      queue.add(eventMessage);
      ProjectLogger.log(
          "*** Queue size for Topic :" + topic + " : is **** : " + queue.size(), LoggerEnum.INFO);
    }
  }

  @Override
  public EventMessage recieve(String topic) {
    BlockingQueue<EventMessage> queue = queueMap.get(topic);
    if (queue != null) {
      ProjectLogger.log(
          "*** Queue size for Topic :" + topic + " : is before removing **** : " + queue.size(),
          LoggerEnum.INFO);
      return queue.poll();
    }
    ProjectLogger.log("NO topic Found For parameter :" + topic, LoggerEnum.INFO);
    return null;
  }
}
