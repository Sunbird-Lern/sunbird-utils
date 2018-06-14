package org.sunbird.telemetry.util;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.dsl.Disruptor;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.Request;

/** @author Manzarul */
public class WriteEventProducer {

  private final Disruptor<Request> disruptor;

  public WriteEventProducer(Disruptor<Request> disruptor) {
    this.disruptor = disruptor;
  }

  private static final EventTranslatorOneArg<Request, Request> TRANSLATOR_ONE_ARG =
      new EventTranslatorOneArg<Request, Request>() {
        public void translateTo(Request writeEvent, long sequence, Request request) {
          writeEvent.setRequest(request.getRequest());
        }
      };

  public void onData(Request request) {
    ProjectLogger.log("Publishing " + request.toString(), LoggerEnum.INFO.name());
    // publish the message to disruptor
    disruptor.publishEvent(TRANSLATOR_ONE_ARG, request);
  }
}
