package org.sunbird.telemetry.util.lmaxdisruptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * Created by arvind on 29/1/18.
 */
public class WriteEventHandlerTest {

	private WriteEventHandler writeEventHandler = new WriteEventHandler();

	@Test
	public void testAuditSuccess() {

		Request request = new Request();
		Map<String, Object> context = new HashMap<>();
		Map<String, Object> params = new HashMap<>();

		Map<String, Object> targetObject = new HashMap<>();
		targetObject.put(JsonKey.ID, "org123");
		targetObject.put(JsonKey.TYPE, JsonKey.ORGANISATION);

		context.put(JsonKey.ACTOR_ID, "123");
		context.put(JsonKey.ACTOR_TYPE, JsonKey.USER);
		context.put(JsonKey.CHANNEL, "channel001");
		context.put(JsonKey.ENV, "orgEnv");

		context.put(JsonKey.PDATA_ID, "PID 01");
		context.put(JsonKey.PDATA_PID, "INSTANCE 01");
		context.put(JsonKey.PDATA_VERSION, "1.4");

		Map<String, Object> props = new HashMap<String, Object>();
		props.put(JsonKey.ID, "4849");
		props.put(JsonKey.STATE, "active");
		params.put(JsonKey.PROPS, props);

		request.getRequest().put(JsonKey.TARGET_OBJECT, targetObject);
		request.getRequest().put(JsonKey.PARAMS, params);
		request.getRequest().put(JsonKey.CONTEXT, context);

		boolean result = writeEventHandler.processAuditEvent(request);
		Assert.assertTrue(result);

	}

	@Test
	public void testAuditSuccessWithoutChannelEnv() {

		Request request = new Request();
		Map<String, Object> context = new HashMap<>();
		Map<String, Object> params = new HashMap<>();

		Map<String, Object> targetObject = new HashMap<>();
		targetObject.put(JsonKey.ID, "org123");
		targetObject.put(JsonKey.TYPE, JsonKey.ORGANISATION);

		context.put(JsonKey.ACTOR_ID, "123");
		context.put(JsonKey.ACTOR_TYPE, JsonKey.USER);

		context.put(JsonKey.PDATA_ID, "PID 01");
		context.put(JsonKey.PDATA_PID, "INSTANCE 01");
		context.put(JsonKey.PDATA_VERSION, "1.4");

		Map<String, Object> props = new HashMap<String, Object>();
		props.put(JsonKey.ID, "4849");
		props.put(JsonKey.STATE, "active");
		params.put(JsonKey.PROPS, props);

		request.getRequest().put(JsonKey.TARGET_OBJECT, targetObject);
		request.getRequest().put(JsonKey.PARAMS, params);
		request.getRequest().put(JsonKey.CONTEXT, context);

		boolean result = writeEventHandler.processAuditEvent(request);
		Assert.assertFalse(result);

	}

	@Test
	public void testAuditSuccessWithoutActorInfo() {

		Request request = new Request();
		Map<String, Object> context = new HashMap<>();
		Map<String, Object> params = new HashMap<>();

		Map<String, Object> targetObject = new HashMap<>();
		targetObject.put(JsonKey.ID, "org123");
		targetObject.put(JsonKey.TYPE, JsonKey.ORGANISATION);

		context.put(JsonKey.CHANNEL, "channel001");
		context.put(JsonKey.ENV, "orgEnv");

		context.put(JsonKey.PDATA_ID, "PID 01");
		context.put(JsonKey.PDATA_PID, "INSTANCE 01");
		context.put(JsonKey.PDATA_VERSION, "1.4");

		Map<String, Object> props = new HashMap<String, Object>();
		props.put(JsonKey.ID, "4849");
		props.put(JsonKey.STATE, "active");
		params.put(JsonKey.PROPS, props);

		request.getRequest().put(JsonKey.TARGET_OBJECT, targetObject);
		request.getRequest().put(JsonKey.PARAMS, params);
		request.getRequest().put(JsonKey.CONTEXT, context);

		boolean result = writeEventHandler.processAuditEvent(request);
		Assert.assertFalse(result);

	}

	@Test
	public void testSearchSuccess() {

		Request request = new Request();
		Map<String, Object> context = new HashMap<>();
		Map<String, Object> params = new HashMap<>();

		context.put(JsonKey.ACTOR_ID, "123");
		context.put(JsonKey.ACTOR_TYPE, JsonKey.USER);
		context.put(JsonKey.CHANNEL, "channel001");
		context.put(JsonKey.ENV, "orgEnv");

		context.put(JsonKey.PDATA_ID, "PID 01");
		context.put(JsonKey.PDATA_PID, "INSTANCE 01");
		context.put(JsonKey.PDATA_VERSION, "1.4");

		params.put(JsonKey.TYPE, JsonKey.USER);
		params.put(JsonKey.QUERY, "hello");
		Map<String, Object> sort = new HashMap<>();
		sort.put("date", "desc");
		params.put(JsonKey.SORT, sort);
		params.put(JsonKey.SIZE, new Long(10));

		List<Map> topn = new ArrayList<>();
		params.put(JsonKey.TOPN, topn);
		request.getRequest().put(JsonKey.PARAMS, params);
		request.getRequest().put(JsonKey.CONTEXT, context);

		boolean result = writeEventHandler.processSearchEvent(request);
		Assert.assertTrue(result);

	}

	@Test
	public void testSearchWithoutRequiredEdata() {

		Request request = new Request();
		Map<String, Object> context = new HashMap<>();
		Map<String, Object> params = new HashMap<>();

		context.put(JsonKey.ACTOR_ID, "123");
		context.put(JsonKey.ACTOR_TYPE, JsonKey.USER);
		context.put(JsonKey.CHANNEL, "channel001");
		context.put(JsonKey.ENV, "orgEnv");

		context.put(JsonKey.PDATA_ID, "PID 01");
		context.put(JsonKey.PDATA_PID, "INSTANCE 01");
		context.put(JsonKey.PDATA_VERSION, "1.4");

		Map<String, Object> sort = new HashMap<>();
		sort.put("date", "desc");
		params.put(JsonKey.SORT, sort);

		request.getRequest().put(JsonKey.PARAMS, params);
		request.getRequest().put(JsonKey.CONTEXT, context);

		boolean result = writeEventHandler.processSearchEvent(request);
		Assert.assertFalse(result);

	}

	@Test
	public void testLogSuccess() {

		Request request = new Request();
		Map<String, Object> context = new HashMap<>();
		Map<String, Object> params = new HashMap<>();

		context.put(JsonKey.ACTOR_ID, "123");
		context.put(JsonKey.ACTOR_TYPE, JsonKey.USER);
		context.put(JsonKey.CHANNEL, "channel001");
		context.put(JsonKey.ENV, "orgEnv");

		context.put(JsonKey.PDATA_ID, "PID 01");
		context.put(JsonKey.PDATA_PID, "INSTANCE 01");
		context.put(JsonKey.PDATA_VERSION, "1.4");

		params.put(JsonKey.LOG_TYPE, JsonKey.API_ACCESS);
		params.put(JsonKey.LOG_LEVEL, JsonKey.INFO);
		params.put(JsonKey.MESSAGE, " log message");

		request.getRequest().put(JsonKey.PARAMS, params);
		request.getRequest().put(JsonKey.CONTEXT, context);

		boolean result = writeEventHandler.processLogEvent(request);
		Assert.assertTrue(result);

	}

	@Test
	public void testLogWithoutEdataRequiredFields() {

		Request request = new Request();
		Map<String, Object> context = new HashMap<>();
		Map<String, Object> params = new HashMap<>();

		context.put(JsonKey.ACTOR_ID, "123");
		context.put(JsonKey.ACTOR_TYPE, JsonKey.USER);
		context.put(JsonKey.CHANNEL, "channel001");
		context.put(JsonKey.ENV, "orgEnv");

		context.put(JsonKey.PDATA_ID, "PID 01");
		context.put(JsonKey.PDATA_PID, "INSTANCE 01");
		context.put(JsonKey.PDATA_VERSION, "1.4");

		request.getRequest().put(JsonKey.PARAMS, params);
		request.getRequest().put(JsonKey.CONTEXT, context);

		boolean result = writeEventHandler.processLogEvent(request);
		Assert.assertFalse(result);

	}

	@Test
	public void testErrorSuccess() {

		Request request = new Request();
		Map<String, Object> context = new HashMap<>();
		Map<String, Object> params = new HashMap<>();

		context.put(JsonKey.ACTOR_ID, "123");
		context.put(JsonKey.ACTOR_TYPE, JsonKey.USER);
		context.put(JsonKey.CHANNEL, "channel001");
		context.put(JsonKey.ENV, "orgEnv");

		context.put(JsonKey.PDATA_ID, "PID 01");
		context.put(JsonKey.PDATA_PID, "INSTANCE 01");
		context.put(JsonKey.PDATA_VERSION, "1.4");

		params.put(JsonKey.ERROR, ResponseCode.invalidTypeValue.getErrorCode());
		params.put(JsonKey.ERR_TYPE, JsonKey.API_ACCESS);
		params.put(JsonKey.STACKTRACE, " stacktrace");

		request.getRequest().put(JsonKey.PARAMS, params);
		request.getRequest().put(JsonKey.CONTEXT, context);

		boolean result = writeEventHandler.processErrorEvent(request);
		Assert.assertTrue(result);

	}

	@Test
	public void testErrorWithoutEdataRequiredFields() {

		Request request = new Request();
		Map<String, Object> context = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		context.put(JsonKey.ACTOR_ID, "123");
		context.put(JsonKey.ACTOR_TYPE, JsonKey.USER);
		context.put(JsonKey.CHANNEL, "channel001");
		context.put(JsonKey.ENV, "orgEnv");

		context.put(JsonKey.PDATA_ID, "PID 01");
		context.put(JsonKey.PDATA_PID, "INSTANCE 01");
		context.put(JsonKey.PDATA_VERSION, "1.4");

		request.getRequest().put(JsonKey.PARAMS, params);
		request.getRequest().put(JsonKey.CONTEXT, context);

		boolean result = writeEventHandler.processErrorEvent(request);
		Assert.assertFalse(result);
	}

	@Test
	public void testErrorWithoutParams() {

		Request request = new Request();
		Map<String, Object> context = new HashMap<>();
		context.put(JsonKey.ACTOR_ID, "123");
		context.put(JsonKey.ACTOR_TYPE, JsonKey.USER);
		context.put(JsonKey.CHANNEL, "channel001");
		context.put(JsonKey.ENV, "orgEnv");

		context.put(JsonKey.PDATA_ID, "PID 01");
		context.put(JsonKey.PDATA_PID, "INSTANCE 01");
		context.put(JsonKey.PDATA_VERSION, "1.4");

		request.getRequest().put(JsonKey.CONTEXT, context);

		boolean result = writeEventHandler.processErrorEvent(request);
		Assert.assertFalse(result);
	}

	@Test
	public void testErrorWithoutContext() {

		Request request = new Request();
		Map<String, Object> context = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		context.put(JsonKey.ACTOR_ID, "123");
		context.put(JsonKey.ACTOR_TYPE, JsonKey.USER);
		context.put(JsonKey.CHANNEL, "channel001");
		context.put(JsonKey.ENV, "orgEnv");

		context.put(JsonKey.PDATA_ID, "PID 01");
		context.put(JsonKey.PDATA_PID, "INSTANCE 01");
		context.put(JsonKey.PDATA_VERSION, "1.4");

		request.getRequest().put(JsonKey.PARAMS, params);
		boolean result = writeEventHandler.processErrorEvent(request);
		Assert.assertFalse(result);
	}

}
