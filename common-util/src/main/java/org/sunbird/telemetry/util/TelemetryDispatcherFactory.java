package org.sunbird.telemetry.util;

import java.util.HashMap;
import java.util.Map;
import org.sunbird.common.models.util.JsonKey;

/**
 * Created by arvind on 9/1/18.
 */
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
		if (dispatcherName.equalsIgnoreCase("EK-STEP")) {
			dispatcher = new TelemetryDispatcherEkstep();
		} else if(dispatcherName.equalsIgnoreCase(JsonKey.SUNBIRD_LMS_TELEMETRY)) {
			dispatcher = new TelemetryDispatcherSunbirdLMS();
		}
		return dispatcher;
	}

}
