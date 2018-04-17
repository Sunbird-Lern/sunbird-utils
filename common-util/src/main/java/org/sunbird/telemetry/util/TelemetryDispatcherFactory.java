package org.sunbird.telemetry.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

/** Created by arvind on 9/1/18. */
public class TelemetryDispatcherFactory {

  private static Map<String, TelemetryDispatcher> dispatcher = new HashMap<>();

  public static TelemetryDispatcher get(String dispatcherName) {

    // validate the dispatcher name , it should be some predefined value ...
    TelemetryDispatcher obj = dispatcher.get(dispatcherName);
    if (null == obj) {
      synchronized (TelemetryDispatcherFactory.class) {
        obj = dispatcher.get(dispatcherName);
        if (null == obj) {
          TelemetryDispatcher telemetryDispatcher = getDispatcherObject(dispatcherName);
          dispatcher.put(dispatcherName, telemetryDispatcher);
        }
      }
    }
    return dispatcher.get(dispatcherName);
  }

  private static TelemetryDispatcher getDispatcherObject(String dispatcherName) {

    TelemetryDispatcher dispatcher = null;
    if (dispatcherName.equalsIgnoreCase(JsonKey.EK_STEP)) {
      dispatcher = new TelemetryDispatcherEkstep();
    } else if (dispatcherName.equalsIgnoreCase(JsonKey.SUNBIRD_LMS_TELEMETRY)) {
      dispatcher = new TelemetryDispatcherSunbirdLMS();
    }
    return dispatcher;
  }

  public static List<TelemetryDispatcher> getInstanceList(String... dispatcherName) {
    if (dispatcherName == null || dispatcherName.length == 0) {
      ProjectLogger.log("Please provide the instance name ", LoggerEnum.INFO.name());
      return null;
    }
    List<TelemetryDispatcher> objectList = new ArrayList<>();
    for (String name : dispatcherName) {
      objectList.add(get(name));
    }
    return objectList;
  }
}
