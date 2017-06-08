package org.sunbird.common.models;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sunbird.common.models.util.HttpUtil;

/**
 * Unit test for simple App.
 */
public class AppTest
{
	private static final String data = "{\"request\": { \"search\": {\"contentType\": [\"Story\"] }}}";
	private static Map<String,String> headers = new HashMap<String, String>();
	@BeforeClass
	public static void init(){
		headers.put("content-type", "application/json");
		headers.put("accept", "application/json");
		headers.put("user-id", "mahesh");	
	} 
    
	
	@Test
	public void testGetResourceMethod() throws Exception{
		String response = HttpUtil.sendGetRequest("https://dev.ekstep.in/api/learning/v2/content/numeracy_377", headers);
		Assert.assertNotNull(response);
	}
	
	@Test
	public void testPostResourceMethod() throws Exception {
		String response = HttpUtil.sendPostRequest("https://dev.ekstep.in/api/learning/v2/content/list", data, headers);
		Assert.assertNotNull(response);
	}
	
	@Test
	public void testPostFailureResourceMethod() {
		//passing wrong url
		String response=null;
		try {
			response = HttpUtil.sendPostRequest("https://dev.ekstep.in/api/learning/v/content/list", data, headers);
		} catch (IOException e) {
		}
		System.out.println(response);
		Assert.assertEquals("", response);
	}
}
