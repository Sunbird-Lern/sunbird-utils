package org.sunbird.telemetry.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by arvind on 11/1/18.
 */
public class TelemetryFlush {

  /*TODO: move to the util class so all threads will access the same queue only bcoz this queue can have multiple instances ...*/
  Queue<String> queue = new ConcurrentLinkedQueue<String>();
  int thresholdSize = 2;

  private static TelemetryFlush telemetryFlush;

  TelemetryDispatcher telemetryDispatcher = TelemetryDispatcherFactory.get("EK-STEP");

  public static TelemetryFlush getInstance(){

    if(telemetryFlush == null){
      synchronized (TelemetryFlush.class){
        if(telemetryFlush == null){
          telemetryFlush = new TelemetryFlush();
        }
      }
    }
    return telemetryFlush;
  }

  public void flushTelemetry(String message) {
    writeToQueue(message);
  }

  private void writeToQueue(String message) {
    System.out.println("INSIDE TELEMETRY FLUSH ");
    queue.offer(message);
    System.out.println("MESSAGE WRITE TO QUEUE IS "+message+ " QUEUE SIZE IS "+queue.size());

    if(queue.size()>=thresholdSize){
      List<String> list = new ArrayList();
      for(int i=1;i<=thresholdSize;i++){
        String obj = queue.poll();
        if(obj == null){
          break;
        }else {
          list.add(obj);
        }
      }
      telemetryDispatcher.dispatchTelemetryEvent(list);
    }

  }

}
