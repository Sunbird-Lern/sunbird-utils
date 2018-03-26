package org.sunbird.telemetry.util.lmaxdisruptor;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.Request;
import org.sunbird.telemetry.collector.TelemetryAssemblerFactory;
import org.sunbird.telemetry.collector.TelemetryDataAssembler;
import org.sunbird.telemetry.util.TelemetryFlush;
import org.sunbird.telemetry.util.validator.TelemetryObjectValidator;
import org.sunbird.telemetry.util.validator.TelemetryObjectValidatorV3;

import com.lmax.disruptor.EventHandler;

/**
 * Created by arvind on 10/1/18.
 */
public class WriteEventHandler implements EventHandler<Request> {

	private TelemetryFlush telemetryFlush = TelemetryFlush.getInstance();
	private TelemetryDataAssembler telemetryDataAssembler = TelemetryAssemblerFactory.get();
	private TelemetryObjectValidator telemetryObjectValidator = new TelemetryObjectValidatorV3();

	@Override
	public void onEvent(Request request, long l, boolean b) throws Exception {
		String eventType = (String) request.getRequest().get(JsonKey.TELEMETRY_EVENT_TYPE);

		if (TelemetryEvents.AUDIT.getName().equalsIgnoreCase(eventType)) {
			processAuditEvent(request);
		} else if (TelemetryEvents.SEARCH.getName().equalsIgnoreCase(eventType)) {
			processSearchEvent(request);
		} else if (TelemetryEvents.ERROR.getName().equalsIgnoreCase(eventType)) {
			processErrorEvent(request);
		} else if (TelemetryEvents.LOG.getName().equalsIgnoreCase(eventType)) {
			processLogEvent(request);
		}
	}

	public boolean processLogEvent(Request request) {

		boolean success = false;
		Map<String, Object> context = (Map<String, Object>) request.getRequest().get(JsonKey.CONTEXT);
		Map<String, Object> params = (Map<String, Object>) request.getRequest().get(JsonKey.PARAMS);
		String telemetry = telemetryDataAssembler.log(context, params);
		if (StringUtils.isNotBlank(telemetry) && telemetryObjectValidator.validateLog(telemetry)) {
			telemetryFlush.flushTelemetry(telemetry);
			success = true;
		} else {
			ProjectLogger.log("Telemetry validation failed: LOG", request.getRequest(), LoggerEnum.WARN.name());
		}
		return success;
	}

	public boolean processErrorEvent(Request request) {

		boolean success = false;
		Map<String, Object> context = (Map<String, Object>) request.get(JsonKey.CONTEXT);
		Map<String, Object> params = (Map<String, Object>) request.get(JsonKey.PARAMS);
		String telemetry = telemetryDataAssembler.error(context, params);
		if (telemetryObjectValidator.validateError(telemetry)) {
			telemetryFlush.flushTelemetry(telemetry);
			success = true;
		}
		return success;
	}

	public boolean processSearchEvent(Request request) {

		boolean success = false;
		Map<String, Object> context = (Map<String, Object>) request.get(JsonKey.CONTEXT);
		Map<String, Object> params = (Map<String, Object>) request.get(JsonKey.PARAMS);
		String telemetry = telemetryDataAssembler.search(context, params);
		if (telemetryObjectValidator.validateSearch(telemetry)) {
			telemetryFlush.flushTelemetry(telemetry);
			success = true;
		}
		return success;

	}

	public boolean processAuditEvent(Request request) {

		boolean success = false;
		Map<String, Object> context = (Map<String, Object>) request.get(JsonKey.CONTEXT);
		Map<String, Object> targetObject = (Map<String, Object>) request.get(JsonKey.TARGET_OBJECT);
		List<Map<String, Object>> correlatedObjects = (List<Map<String, Object>>) request
				.get(JsonKey.CORRELATED_OBJECTS);
		Map<String, Object> params = (Map<String, Object>) request.get(JsonKey.PARAMS);
		params.put(JsonKey.TARGET_OBJECT, targetObject);
		params.put(JsonKey.CORRELATED_OBJECTS, correlatedObjects);
		String telemetry = telemetryDataAssembler.audit(context, params);
		ProjectLogger.log("Audit event: "+ telemetry, LoggerEnum.INFO.name());
		if (StringUtils.isNotBlank(telemetry)) {
			telemetryFlush.flushTelemetry(telemetry);
			success = true;
		} else {
			ProjectLogger.log("Telemetry validation failed: AUDIT", request.getRequest(), LoggerEnum.WARN.name());
		}
		return success;
	}
}
