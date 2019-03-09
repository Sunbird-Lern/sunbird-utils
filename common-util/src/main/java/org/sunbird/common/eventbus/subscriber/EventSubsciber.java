package org.sunbird.common.eventbus.subscriber;

import com.google.common.eventbus.Subscribe;
import org.sunbird.common.message.broker.factory.MessageBrokerFactory;
import org.sunbird.common.message.broker.inf.MessageBroker;
import org.sunbird.common.message.broker.model.EventMessage;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

public class EventSubsciber {

  private String lastMessage = null;
  private static MessageBroker messageBroker = MessageBrokerFactory.getInstance();

  @Subscribe
  public void listen(String topic) {
    lastMessage = topic;
    EventMessage message = messageBroker.recieve(topic);

    ProjectLogger.log(
        "****EventSubsciber : message recived : "
            + message.getMessage()
            + " ### + "
            + message.getOperationOn(),
        LoggerEnum.INFO);

    // TODO : you will get message whenever a message is added to queue.
    // event Message will be like : operation in cassandra(insert ,update,delete),
    // opeationOn (to determine which attributes have to be updated in elastic
    // search)

  }

  public String getLastMessage() {
    return lastMessage;
  }
}
