package org.sunbird.telemetry.telemetryrouter;

import com.lmax.disruptor.dsl.Disruptor;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.Request;

/**
 * @author Manzarul A class to control the disruptor engine. It initialize the disruptor, submits
 *     messages to event producers and closes the disruptor.
 */
public class LMAXWriter {
  private static Disruptor<Request> disruptor;
  private WriteEventProducer writeEventProducer;
  private static final int BUFFER_SIZE = 1024;
  private int ringBufferSize;
  private static LMAXWriter lmaxWriter;

  private LMAXWriter() {
    init();
    registerShutDownHook();
  }

  public static LMAXWriter getInstance() {
    if (lmaxWriter != null) {
      return lmaxWriter;
    }
    lmaxWriter = new LMAXWriter();
    lmaxWriter.setRingBufferSize(BUFFER_SIZE);
    return lmaxWriter;
  }

  public void setRingBufferSize(int ringBufferSize) {
    this.ringBufferSize = ringBufferSize;
  }

  /** Initialize the disruptor engine. */
  @SuppressWarnings("unchecked")
  private void init() {
    // create a thread pool executor to be used by disruptor
    Executor executor = Executors.newCachedThreadPool();

    // initialize our event factory
    WriteEventFactory factory = new WriteEventFactory();

    if (ringBufferSize == 0) {
      ringBufferSize = BUFFER_SIZE;
    }

    // ring buffer size always has to be the power of 2.
    // so if it is not, make it equal to the nearest integer.
    double power = Math.log(ringBufferSize) / Math.log(2);
    if (power % 1 != 0) {
      power = Math.ceil(power);
      ringBufferSize = (int) Math.pow(2, power);
      ProjectLogger.log(
          "LMAXWriter:init: New ring buffer size = " + ringBufferSize, LoggerEnum.INFO.name());
    }

    // initialize our event handler.
    SunbirdTelemetryEventConsumer sunbirdTelemetryHandler = new SunbirdTelemetryEventConsumer();
    // initialize the disruptor
    disruptor = new Disruptor<>(factory, ringBufferSize, executor);
    disruptor.handleEventsWith(sunbirdTelemetryHandler);

    // start the disruptor and get the generated ring buffer instance
    disruptor.start();

    // initialize the event producer to submit messages
    writeEventProducer = new WriteEventProducer(disruptor);
  }

  public void submitMessage(Request message) {
    if (writeEventProducer != null) {
      // publish the messages via event producer
      writeEventProducer.onData(message);
    }
  }

  /**
   * This class will be called by registerShutDownHook to register the call inside jvm , when jvm
   * terminate it will call the run method to clean up the resource.
   *
   * @author Manzarul
   */
  static class ResourceCleanUp extends Thread {
    @Override
    public void run() {
      ProjectLogger.log("started resource cleanup.");
      if (disruptor != null) {
        disruptor.halt();
        disruptor.shutdown();
      }
      ProjectLogger.log("completed resource cleanup.");
    }
  }

  /** Register the hook for resource clean up. this will be called when jvm shut down. */
  public static void registerShutDownHook() {
    Runtime runtime = Runtime.getRuntime();
    runtime.addShutdownHook(new ResourceCleanUp());
    ProjectLogger.log("ShutDownHook registered.");
  }
}
