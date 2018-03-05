package org.sunbird.telemetry.util.lmaxdisruptor;

import org.sunbird.common.request.Request;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * Created by arvind on 10/1/18.
 */
public class WriteEventProducer {

  //private static Logger logger = LoggerFactory.getLogger(WriteEventProducer.class);

  private final Disruptor<Request> disruptor;

  public WriteEventProducer(Disruptor<Request> disruptor) {
    this.disruptor = disruptor;
  }

  private static final EventTranslatorOneArg<Request, Request> TRANSLATOR_ONE_ARG =
      new EventTranslatorOneArg<Request, Request>() {
        @Override
        public void translateTo(Request writeEvent, long sequence, Request message) {
          writeEvent.setRequest(message.getRequest());
        }
      };

  public void onData(Request message) {
    disruptor.publishEvent(TRANSLATOR_ONE_ARG, message);
  }

}
