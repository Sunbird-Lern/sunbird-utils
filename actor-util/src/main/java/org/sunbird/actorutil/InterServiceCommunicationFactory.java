package org.sunbird.actorutil;

import net.jcip.annotations.ThreadSafe;
import org.sunbird.actorutil.impl.InterServiceCommunicationImpl;

/** Created by arvind on 24/4/18. @Desc Factory class for InterServiceCommunication */
@ThreadSafe
public class InterServiceCommunicationFactory {

  private static InterServiceCommunication instance;

  private InterServiceCommunicationFactory() {}

  static {
    instance = new InterServiceCommunicationImpl();
  }

  public static InterServiceCommunication getInstance() {
    if (null == instance) {
      instance = new InterServiceCommunicationImpl();
    }
    return instance;
  }
}
