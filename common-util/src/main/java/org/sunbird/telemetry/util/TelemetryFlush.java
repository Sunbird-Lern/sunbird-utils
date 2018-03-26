package org.sunbird.telemetry.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.StringUtils;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.PropertiesCache;

/**
 * Created by arvind on 11/1/18.
 */
public class TelemetryFlush {

	private Queue<String> queue = new ConcurrentLinkedQueue<>();

	private int thresholdSize = 20;

	private static TelemetryFlush telemetryFlush;

	private List<TelemetryDispatcher> telemetryDispatcher = TelemetryDispatcherFactory
			.getInstanceList(JsonKey.EK_STEP);

	public static TelemetryFlush getInstance() {
		if (telemetryFlush == null) {
			synchronized (TelemetryFlush.class) {
				if (telemetryFlush == null) {
					telemetryFlush = new TelemetryFlush();
				}
			}
		}
		return telemetryFlush;
	}

	public TelemetryFlush() {
//		String queueThreshold = PropertiesCache.getInstance().getProperty(JsonKey.TELEMETRY_QUEUE_THRESHOLD_VALUE);
//		if (!StringUtils.isBlank(queueThreshold)
//				&& !queueThreshold.equalsIgnoreCase(JsonKey.TELEMETRY_QUEUE_THRESHOLD_VALUE)) {
//			try {
//				this.thresholdSize = Integer.parseInt(queueThreshold.trim());
//			} catch (Exception ex) {
//				ProjectLogger.log("Threshold size from config is not integer", ex);
//			}
//		}
		this.thresholdSize = 1;
	}

	public void flushTelemetry(String message) {
		writeToQueue(message);
	}
	
	public void flushTelemetry(String message, boolean audit) {
		ProjectLogger.log("Audit event pushed to queue.", LoggerEnum.INFO.name());
		writeToQueue(message);
	}

	private void writeToQueue(String message) {
		queue.offer(message);
		if (queue.size() >= thresholdSize) {
			List<String> list = new ArrayList<>();
			int size = queue.size();
			for (int i = 1; i <= size; i++) {
				String obj = queue.poll(); 
				if (obj == null) {
					break;
				} else {
					list.add(obj);
				}
			}
			for (TelemetryDispatcher dispatch : telemetryDispatcher) {
				if (dispatch != null) {
					dispatch.dispatchTelemetryEvent(list);
				} else {
					ProjectLogger.log("TelemetryDispatcher instance is coming as null " + dispatch);
				}
			}
		}

	}

}
