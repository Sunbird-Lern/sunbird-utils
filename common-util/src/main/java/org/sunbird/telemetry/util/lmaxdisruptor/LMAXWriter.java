package org.sunbird.telemetry.util.lmaxdisruptor;

import com.lmax.disruptor.dsl.Disruptor;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.Request;

/**
 * Created by arvind on 10/1/18.
 */
public class LMAXWriter {

  static private Disruptor<Request> disruptor;
  private WriteEventProducer writeEventProducer;
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
    synchronized (LMAXWriter.class){
      if(null == lmaxWriter){
        lmaxWriter = new LMAXWriter();
        lmaxWriter.setRingBufferSize(8);
      }
    }
    return lmaxWriter;
  }


  public void setRingBufferSize(int ringBufferSize) {
    this.ringBufferSize = ringBufferSize;
  }

  /**
   * Initialize the disruptor engine.
   */
  @SuppressWarnings("unchecked")
  private void init() {
    // create a thread pool executor to be used by disruptor
    Executor executor = Executors.newCachedThreadPool();

    // initialize our event factory
    WriteEventFactory factory = new WriteEventFactory();

    // TODO: make it configurable from env properties file or environment variable ...
    if (ringBufferSize == 0) {
      ringBufferSize = 1024;
    }

    // ring buffer size always has to be the power of 2.
    // so if it is not, make it equal to the nearest integer.
    double power = Math.log(ringBufferSize) / Math.log(2);
    if (power % 1 != 0) {
      power = Math.ceil(power);
      ringBufferSize = (int) Math.pow(2, power);
      ProjectLogger.log("New ring buffer size = " + ringBufferSize);
    }

    // initialize our event handler.
    WriteEventHandler handler = new WriteEventHandler();

    // initialize the disruptor
    disruptor = new Disruptor<Request>(factory, ringBufferSize, executor);
    disruptor.handleEventsWith(handler);

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
   *
   */
  static class ResourceCleanUp extends Thread {
    public void run() {
      ProjectLogger.log("started resource cleanup.");
      if (disruptor != null) {
        disruptor.halt();
        disruptor.shutdown();
      }
      ProjectLogger.log("completed resource cleanup.");
    }
  }

  /**
   * Register the hook for resource clean up. this will be called when jvm shut down.
   */
  public static void registerShutDownHook() {
    Runtime runtime = Runtime.getRuntime();
    runtime.addShutdownHook(new ResourceCleanUp());
    ProjectLogger.log("ShutDownHook registered.");
  }

}
