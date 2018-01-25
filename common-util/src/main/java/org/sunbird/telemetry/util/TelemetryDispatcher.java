package org.sunbird.telemetry.util;

import java.util.List;

/**
 * Created by arvind on 9/1/18.
 */
public interface TelemetryDispatcher {

  public boolean dispatchTelemetryEvent(List<String> eventList);

}
