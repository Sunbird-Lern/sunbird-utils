package org.sunbird.telemetry.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.telemetry.dto.Actor;
import org.sunbird.telemetry.dto.Context;
import org.sunbird.telemetry.dto.Producer;
import org.sunbird.telemetry.dto.Target;
import org.sunbird.telemetry.dto.Telemetry;

/**
 * class to generate the telemetry events and convert the final event oject to string ...
 */

public class TelemetryGenerator {

	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * To generate api_access LOG telemetry JSON string.
	 * @param context
	 * @param params
	 * @return
	 */
	public static String audit(Map<String, Object> context, Map<String, Object> params) {
		String actorId = (String)context.get(JsonKey.ACTOR_ID);
		String actorType = (String) context.get(JsonKey.ACTOR_TYPE);
		Actor actor = new Actor(actorId, actorType);
		Target targetObject = generateTargetObject((Map<String, Object>) params.get(JsonKey.TARGET_OBJECT));
		Context eventContext = getContext(context);
		// assign cdata into context from params correlated objects...
		if(params.containsKey(JsonKey.CORRELATED_OBJECTS)){
			setCorrelatedDataToContext(params.get(JsonKey.CORRELATED_OBJECTS), eventContext);
		}

		// assign request id into context cdata ...
		String reqId = (String) context.get(JsonKey.REQUEST_ID);
		if(!ProjectUtil.isStringNullOREmpty(reqId)) {
			Map<String, Object> map = new HashMap<>();
			map.put(JsonKey.ID, reqId);
			map.put(JsonKey.TYPE, JsonKey.REQUEST);
			eventContext.getCdata().add(map);
		}


		Map<String, Object> edata = new HashMap<String, Object>();
		edata = generateAuditEdata(params);

		setOjectStateToParams((Map<String, Object>) params.get(JsonKey.TARGET_OBJECT) , edata);
		Telemetry telemetry = new Telemetry("AUDIT", actor, eventContext, edata, targetObject);
		return getTelemetry(telemetry);
	}

	private static void setOjectStateToParams(Map<String, Object> target,
			Map<String, Object> edata) {

		if(target.get(JsonKey.CURRENT_STATE) != null){
			edata.put(JsonKey.STATE , target.get(JsonKey.CURRENT_STATE));
		}
	}

	private static void setCorrelatedDataToContext(Object correlatedObjects,
			Context eventContext) {
		ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) correlatedObjects;
		ArrayList<Map<String, Object>> targetList = new ArrayList<>();
		if(null != list && !list.isEmpty()){

			for(Map<String, Object> m : list){
				Map<String, Object> map = new HashMap<>();
				map.put(JsonKey.ID, m.get(JsonKey.ID));
				map.put(JsonKey.TYPE, m.get(JsonKey.TYPE));
				targetList.add(map);
			}
		}
		eventContext.setCdata(targetList);
	}

	private static Target generateTargetObject(Map<String, Object> targetObject) {

		Target target = new Target((String)targetObject.get(JsonKey.ID), (String)targetObject.get(JsonKey.TYPE));
		if(targetObject.get(JsonKey.ROLLUP) != null){
			target.setRollup((Map<String, String>) targetObject.get(JsonKey.ROLLUP));
		}
		return target;
	}

	private static Map<String,Object> generateAuditEdata(Map<String, Object> params) {

		Map<String, Object> edata = new HashMap<>();
		Map<String, Object> props = (Map<String, Object>) params.get(JsonKey.PROPS);
		edata.put(JsonKey.PROPS, props.entrySet().stream().map(entry -> entry.getKey()).collect(
				Collectors.toList()));
		return edata;

	}

	
	public static String search(Map<String, String> context) {
		return null;
	}
	
	public static String audit(Map<String, String> context) {
		return null;
	}

	private static Context getContext(Map<String, Object> context) {
		String channel = (String) context.get(JsonKey.CHANNEL);
		String env = (String)context.get(JsonKey.ENV);
		Producer producer = getProducer(context);
		Context eventContext = new Context(channel, env, producer);
		String did = (String)context.get("did");
		if (StringUtils.isNotBlank(did)) {
			eventContext.setDid(did);
		}
		if(context.get(JsonKey.ROLLUP) != null && !((Map<String, String>) context.get(JsonKey.ROLLUP)).isEmpty()){
			eventContext.setRollup((Map<String, String>) context.get(JsonKey.ROLLUP));
		}
		return eventContext;
	}

	private static Producer getProducer(Map<String, Object> context) {

		String id = (String) context.get(JsonKey.PRODUCER_ID);
		String pid = (String) context.get(JsonKey.PRODUCER_INSTTANCE_ID);
		String ver = (String) context.get(JsonKey.PRODUCER_VERSION);
		return new Producer(id, pid, ver);
	}

	private static List<Map<String, Object>> getParamsList(Map<String, Object> params) {
		List<Map<String, Object>> paramsList = new ArrayList<Map<String, Object>>();
		if (null != params && !params.isEmpty()) {
			for (Entry<String, Object> entry : params.entrySet()) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put(entry.getKey(), entry.getValue());
				paramsList.add(param);
			}
		}
		return paramsList;
	}

	private static String getTelemetry(Telemetry telemetry) {
		String event = "";
		try {
			event = mapper.writeValueAsString(telemetry);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return event;
	}

	public static String search(Map<String, Object> context, Map<String, Object> params) {

		String actorId = (String)context.get(JsonKey.ACTOR_ID);
		String actorType = (String) context.get(JsonKey.ACTOR_TYPE);
		Actor actor = new Actor(actorId, actorType);

		Context eventContext = getContext(context);

		// assign request id into context cdata ...
		String reqId = (String) context.get(JsonKey.REQUEST_ID);
		//String reqType = (String)context.get(JsonKey.REQUEST_TYPE);
		if(!ProjectUtil.isStringNullOREmpty(reqId)) {
			Map<String, Object> map = new HashMap<>();
			map.put(JsonKey.ID, reqId);
			map.put(JsonKey.TYPE, JsonKey.REQUEST);
			eventContext.getCdata().add(map);
		}
		Map<String, Object> edata = new HashMap<String, Object>();
		edata = generateSearchEdata(params);
		Telemetry telemetry = new Telemetry("SEARCH", actor, eventContext, edata);
		return getTelemetry(telemetry);
	}

	private static Map<String,Object> generateSearchEdata(Map<String, Object> params) {

		Map<String, Object> edata = new HashMap<>();
		String type = (String) params.get(JsonKey.TYPE);
		String query = (String) params.get(JsonKey.QUERY);
		Map filters = (Map) params.get(JsonKey.FILTERS);
		Map sort = (Map) params.get(JsonKey.SORT);
		Long size = (Long) params.get(JsonKey.SIZE);
		List<Map> topn = (List<Map>) params.get(JsonKey.TOPN);

		edata.put(JsonKey.TYPE , type);
		edata.put(JsonKey.QUERY , query);
		edata.put(JsonKey.FILTERS, filters);
		edata.put(JsonKey.SORT , sort);
		edata.put(JsonKey.SIZE, size);
		edata.put(JsonKey.TOPN , topn);
		return edata;

	}

	public static String log(Map<String, Object> context, Map<String, Object> params) {

		String actorId = (String)context.get(JsonKey.ACTOR_ID);
		String actorType = (String) context.get(JsonKey.ACTOR_TYPE);
		Actor actor = new Actor(actorId, actorType);

		Context eventContext = getContext(context);

		// assign request id into context cdata ...
		String reqId = (String) context.get(JsonKey.REQUEST_ID);
		//String reqType = (String)context.get(JsonKey.REQUEST_TYPE);
		if(!ProjectUtil.isStringNullOREmpty(reqId)) {
			Map<String, Object> map = new HashMap<>();
			map.put(JsonKey.ID, reqId);
			map.put(JsonKey.TYPE, JsonKey.REQUEST);
			eventContext.getCdata().add(map);
		}

		Map<String, Object> edata = new HashMap<String, Object>();
		edata = generateLogEdata(params);

		Telemetry telemetry = new Telemetry("LOG", actor, eventContext, edata);
		return getTelemetry(telemetry);

	}

	private static Map<String,Object> generateLogEdata(Map<String, Object> params) {

		Map<String, Object> edata = new HashMap<>();
		String logType = (String) params.get(JsonKey.LOG_TYPE);
		String logLevel = (String) params.get(JsonKey.LOG_LEVEL);


		long startTime = (long) params.get(JsonKey.START_TIME);
		long endTime = (long) params.get(JsonKey.END_TIME);
		String method = (String) params.get(JsonKey.METHOD);
		String status = (String) params.get(JsonKey.STATUS);
		String message = (String) params.get(JsonKey.MESSAGE);
		String stackTrace = (String) params.get(JsonKey.STACKTRACE);

		edata.put(JsonKey.TYPE, logType);
		edata.put(JsonKey.LEVEL, logLevel);
		edata.put(JsonKey.MESSAGE, message);

		Map<String, Object> additionalParams = new HashMap<>();
		additionalParams.put(JsonKey.START_TIME, startTime);
		additionalParams.put(JsonKey.END_TIME, endTime);
		if(!ProjectUtil.isStringNullOREmpty(method)) {
			additionalParams.put(JsonKey.METHOD, method);
		}
		if(!ProjectUtil.isStringNullOREmpty(status)) {
			additionalParams.put(JsonKey.STATUS, status);
		}
		if(!ProjectUtil.isStringNullOREmpty(stackTrace)){
			additionalParams.put(JsonKey.STACKTRACE , stackTrace);
		}
		List list = new ArrayList();
		list.add(additionalParams);
		edata.put(JsonKey.PARAMS , list);
		return edata;

	}

	public static String error(Map<String, Object> context, Map<String, Object> params) {

		String actorId = (String)context.get(JsonKey.ACTOR_ID);
		String actorType = (String) context.get(JsonKey.ACTOR_TYPE);
		Actor actor = new Actor(actorId, actorType);

		Context eventContext = getContext(context);

		// assign request id into context cdata ...
		String reqId = (String) context.get(JsonKey.REQUEST_ID);
		//String reqType = (String)context.get(JsonKey.REQUEST_TYPE);
		if(!ProjectUtil.isStringNullOREmpty(reqId)) {
			Map<String, Object> map = new HashMap<>();
			map.put(JsonKey.ID, reqId);
			map.put(JsonKey.TYPE, JsonKey.REQUEST);
			eventContext.getCdata().add(map);
		}

		Map<String, Object> edata = new HashMap<String, Object>();
		edata = generateErrorEdata(params);

		Telemetry telemetry = new Telemetry("ERROR", actor, eventContext, edata);
		return getTelemetry(telemetry);

	}

	private static Map<String,Object> generateErrorEdata(Map<String, Object> params) {

		Map<String, Object> edata = new HashMap<>();
		String error = (String) params.get("err");
		String errorType = (String) params.get("errtype");
		String stackTrace = (String) params.get(JsonKey.STACKTRACE);

		String object = (String) params.get(JsonKey.OBJECT_TYPE);

		edata.put("err", error);
		edata.put("errtype", errorType);
		edata.put(JsonKey.STACKTRACE, stackTrace);
		if(!ProjectUtil.isStringNullOREmpty(object)){
			//TODO:  "error": "Invalid type. Expected: object, given: string" from telemetry api ..
			//edata.put("object", object);
		}

		return edata;

	}
}
