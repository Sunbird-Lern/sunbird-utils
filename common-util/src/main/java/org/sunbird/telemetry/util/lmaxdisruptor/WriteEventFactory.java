package org.sunbird.telemetry.util.lmaxdisruptor;

import org.sunbird.common.request.Request;

import com.lmax.disruptor.EventFactory;

/**
 * Created by arvind on 10/1/18.
 */
public class WriteEventFactory implements EventFactory<Request> {
  public Request newInstance() {
    return new Request();
  }
}
