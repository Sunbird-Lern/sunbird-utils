package org.sunbird.telemetry.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by arvind on 9/1/18.
 */
public class TelemetryDispatcherEkstep implements TelemetryDispatcher {

	private static ObjectMapper mapper = new ObjectMapper();

	@Override
	public boolean dispatchTelemetryEvent(List<String> eventList) {

		try {
			List<Map<String, Object>> jsonList = mapper.readValue(eventList.toString(),
					new TypeReference<List<Map<String, Object>>>() {
					});

			Map<String, Object> map = new HashMap<>();
			map.put("ets", System.currentTimeMillis());

			map.put(JsonKey.EVENTS, jsonList);

			String baseSearchUrl = System.getenv(JsonKey.EKSTEP_BASE_URL);
			if (ProjectUtil.isStringNullOREmpty(baseSearchUrl)) {
				baseSearchUrl = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_BASE_URL);
			}

			String event = getTelemetryEvent(map);
			ProjectLogger.log("EVEVTS TO FLUSH : " + event);

			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json");
			headers.put("accept", "application/json");
			headers.put(JsonKey.AUTHORIZATION, JsonKey.BEARER + System.getenv(JsonKey.EKSTEP_AUTHORIZATION));
			if (ProjectUtil.isStringNullOREmpty((String) headers.get(JsonKey.AUTHORIZATION))) {
				headers.put(JsonKey.AUTHORIZATION,
						PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_AUTHORIZATION));
			}
			String response = HttpUtil.sendPostRequest(
					baseSearchUrl + PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_TELEMETRY_API_URL), event,
					headers);
			ProjectLogger.log("FLUSH RESPONSE : " + response);

		} catch (Exception ex) {
			ProjectLogger.log(ex.getMessage(), ex);
		}
		return false;
	}

	private static String getTelemetryEvent(Map<String, Object> map) {
		String event = "";
		try {
			event = mapper.writeValueAsString(map);
		} catch (Exception e) {
			ProjectLogger.log(e.getMessage(), e);
		}
		return event;
	}
}
