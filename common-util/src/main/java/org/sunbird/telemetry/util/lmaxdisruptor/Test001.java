package org.sunbird.telemetry.util.lmaxdisruptor;

/**
 * Created by arvind on 11/1/18.
 */
public class Test001 {

  public static void main(String[] args) {
    LMAXWriter lmaxWriter = LMAXWriter.getInstance();

    for (int i=0;i<20;i++){
      //lmaxWriter.submitMessage("message "+i);
    }
  }

}
