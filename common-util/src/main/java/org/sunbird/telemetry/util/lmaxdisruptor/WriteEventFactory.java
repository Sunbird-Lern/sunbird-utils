package org.sunbird.telemetry.util.lmaxdisruptor;

import com.lmax.disruptor.EventFactory;
import org.sunbird.common.request.Request;

/** Created by arvind on 10/1/18. */
public class WriteEventFactory implements EventFactory<Request> {
  public Request newInstance() {
    return new Request();
  }
}
