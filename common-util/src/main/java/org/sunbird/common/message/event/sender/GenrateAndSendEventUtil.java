package org.sunbird.common.message.event.sender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sunbird.common.message.broker.factory.MessageBrokerFactory;
import org.sunbird.common.message.broker.inf.MessageBroker;
import org.sunbird.common.message.broker.model.EventMessage;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

public class GenrateAndSendEventUtil {
  private static MessageBroker messageBroker = MessageBrokerFactory.getInstance();

  public static void generateAndSendEvent(
      String operationType, String operationOn, Map<String, Object> requestData, String topic) {
    // topic = Database_operation
    ProjectLogger.log(
        " *GenrateAndSendEventUtil: Genrate and Send Called :" + topic, LoggerEnum.INFO);
    String eventType = JsonKey.INFORMATIONAL;
    if (topic.equals(JsonKey.DATABASE_OPERATION)) {
      eventType = JsonKey.TRANSACTIONAL;
    }
    EventMessage message = new EventMessage(operationType, eventType, requestData, operationOn);
    try {
      messageBroker.send(topic, message);
    } catch (Exception e) {
      ProjectLogger.log("Error while processing message", LoggerEnum.INFO);
    }
  }

  public static void generateAndSendEvent(
      String operationType, String operationOn, List<String> requestData, String topic) {
    ProjectLogger.log(
        "GenrateAndSendEventUtil : Genrate and Send Called :" + topic, LoggerEnum.INFO);
    // topic = Database_operation
    String eventType = JsonKey.INFORMATIONAL;
    if (topic.equals(JsonKey.DATABASE_OPERATION)) {
      eventType = JsonKey.TRANSACTIONAL;
    }

    Map<String, Object> data = new HashMap<>();
    if (operationType.equals(JsonKey.DELETE)) {
      data.put(JsonKey.IDENTIFIER, requestData);
    }
    EventMessage message = new EventMessage(operationType, eventType, data, operationOn);
    try {
      messageBroker.send(topic, message);
    } catch (Exception e) {
      ProjectLogger.log(
          "GenrateAndSendEventUtil : Error while processing message", LoggerEnum.INFO);
    }
  }
}
