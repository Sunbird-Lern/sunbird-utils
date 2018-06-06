package org.sunbird.telemetry.telemetryrouter;

import com.lmax.disruptor.EventFactory;
import org.sunbird.common.request.Request;

/** @author Manzarul */
public class WriteEventFactory implements EventFactory<Request> {
  public Request newInstance() {
    return new Request();
  }
}
