package org.sunbird.common.eventbus.factory;

import com.google.common.eventbus.EventBus;

public class EventBusfactory {

  private static EventBus eventBus = null;

  private EventBusfactory() {}

  public static EventBus getInstance() {
    if (null == eventBus) {
      eventBus = new EventBus("TEST");
    }
    return eventBus;
  }
}
