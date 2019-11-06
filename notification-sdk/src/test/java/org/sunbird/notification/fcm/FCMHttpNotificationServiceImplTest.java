package org.sunbird.notification.fcm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sunbird.notification.fcm.provider.IFCMNotificationService;
import org.sunbird.notification.fcm.provider.NotificationFactory;
import org.sunbird.notification.fcm.providerImpl.FCMHttpNotificationServiceImpl;
import org.sunbird.notification.utils.FCMResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FCMHttpNotificationServiceImplTest{
	
	IFCMNotificationService factory = NotificationFactory.getInstance(NotificationFactory.instanceType.httpClinet.name());
	
	private static Map<String,String> data = new HashMap<String, String>();
	private static final String DEVICE_ID = "fudHZ8SJEKM:APA91bF5tJLaq5YTiq1BDPOYkpz9mXjtOxXm7nDr6hY7GtQkZsX7kp_a7Oh_XMqxqHY7SBo5TvxCrDmgVMj84bgcCoxo-FMv-0xchTsvatywNREDhPh14vwt3ROMM-jy-HE3-cwNV61K";
	@BeforeClass
	public static void init () throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Map <String,String> map = new HashMap<String, String>();
		map.put("id", "123");
		map.put("type", "1");
	    Map<String,String> innerMap = new HashMap<String, String>();
	    innerMap.put("actionType", "courseUpdate");
	    innerMap.put("title", "Local notification test");
	    map.put("actionData", mapper.writeValueAsString(innerMap));
		data.put("rawData", mapper.writeValueAsString(map));
		
	}
	
	@Test
	public void testInstanceCreationSuccess() {
		assertNotNull(factory);
	}

	@Test
	public void sendSingleDeviceNotificationWithincorrectKey () {
		FCMHttpNotificationServiceImpl.setAccountKey("key=someKey");
		FCMResponse response = factory.sendSingleDeviceNotification(DEVICE_ID, data, true);
		assertNull(response);
	}
	
	@Test
	public void sendMultipleDeviceNotificationWithincorrectKey () {
		FCMHttpNotificationServiceImpl.setAccountKey("key=someKey");
		List<String> deviceIds = new ArrayList<String>();
		deviceIds.add(DEVICE_ID); deviceIds.add("second deviceId");
		FCMResponse response = factory.sendMultiDeviceNotification(deviceIds, data, true);
		assertNull(response);
	}
	
	@Test
	public void sendTopicNotificationWithincorrectKey () {
		FCMHttpNotificationServiceImpl.setAccountKey("key=someKey");
		List<String> deviceIds = new ArrayList<String>();
		deviceIds.add(DEVICE_ID); deviceIds.add("second deviceId");
		FCMResponse response = factory.sendTopicNotification("topic", data, true);
		assertNull(response);
	}	
	
	
}
