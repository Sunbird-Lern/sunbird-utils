package org.sunbird.actorUtil;

import java.util.HashMap;
import java.util.Map;
import org.sunbird.actorUtil.impl.InterServiceCommunicationImpl;

/** Created by arvind on 24/4/18. */
public class InterServiceCommunicationFactory {

  private static InterServiceCommunicationFactory factory;
  private static Map<String, InterServiceCommunication> modes = new HashMap<>();

  private InterServiceCommunicationFactory() {}

  public static InterServiceCommunicationFactory getInstance() {
    if (null == factory) {
      synchronized (InterServiceCommunicationFactory.class) {
        if (null == factory) {
          factory = new InterServiceCommunicationFactory();
        }
      }
    }
    return factory;
  }

  public InterServiceCommunication getCommunicationPath(String mode) {
    if ("actorCommunication".equalsIgnoreCase(mode)) {
      return getActorCommunicationMode();
    }
    return null;
  }

  public InterServiceCommunication getActorCommunicationMode() {

    if (modes.get("actorCommunication") != null) {
      return modes.get("actorCommunication");
    } else {
      synchronized (InterServiceCommunicationFactory.class) {
        if (modes.get("actorCommunication") == null) {
          InterServiceCommunication communication = new InterServiceCommunicationImpl();
          modes.put("actorCommunication", communication);
        }
      }
    }
    return modes.get("actorCommunication");
  }
}
