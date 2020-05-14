package org.sunbird.telemetry.util;

import org.sunbird.common.request.Request;

/**
 * Lmax Disruptor engine to receive the telemetry request and forward the request to event handler
 *
 * @author arvind
 */
public class TelemetryLmaxWriter {

  private static TelemetryLmaxWriter lmaxWriter;

  private TelemetryLmaxWriter() {

  }

  /**
   * Method to get the singleton object of TelemetryLmaxWriter
   *
   * @return TelemetryLmaxWriter singleton object
   */
  public static TelemetryLmaxWriter getInstance() {
    if (lmaxWriter != null) {
      return lmaxWriter;
    }
    synchronized (TelemetryLmaxWriter.class) {
      if (null == lmaxWriter) {
        lmaxWriter = new TelemetryLmaxWriter();
        lmaxWriter.setRingBufferSize(8);
      }
    }
    return lmaxWriter;
  }

  public void setRingBufferSize(int ringBufferSize) {

  }

  /**
   * Method to receive the message (represents telemetry event)
   *
   * @param message telemetry request which contains telemetry event
   */
  public void submitMessage(Request message) {

  }

}
