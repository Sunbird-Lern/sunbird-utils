package org.sunbird.telemetry.util.lmaxdisruptor;

/**
 * Created by arvind on 29/1/18.
 */
public enum TelemetryEvents {

  AUDIT("AUDIT"),SEARCH("SEARCH"),LOG("LOG"),ERROR("ERROR");
  private String name;

  TelemetryEvents(String name){
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
