package org.sunbird.common.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.services.AssessmentEvaluator;
import org.sunbird.common.services.impl.DefaultAssessmentEvaluator;

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
	
	@Test()
	public void testPostFailureResourceMethod() {
		//passing wrong url
		String response=null;
		try {
			response = HttpUtil.sendPostRequest("https://dev.ekstep.in/api/learning/v/content/list", data, headers);
		} catch (IOException e) {
			
		}
		Assert.assertEquals("", response);
	}
	
	@Test
	public void testEvaluateAssessment(){
		Map<String, List<Map<String, Object>>> data = createEvaluateAssessmentRequest();
		AssessmentEvaluator evaluator = new  DefaultAssessmentEvaluator();
		Map<String, List<Map<String, Object>>> response = evaluator.evaluateAssessment(data);
		List<Map<String, Object>> list = response.get("USR1");
		Assert.assertEquals(list.size(),6);
	}

	@Test
	public void testEvaluateResult(){
		Map<String, List<Map<String, Object>>> data1 = createEvaluateAssessmentRequest();
		AssessmentEvaluator evaluator = new  DefaultAssessmentEvaluator();
		Map<String, List<Map<String, Object>>> response1 = evaluator.evaluateAssessment(data1);
		Map<String, List<Map<String, Object>>> req= new HashMap<>();
		req.put("USR1", response1.get("USR1"));
		List<Map<String, Object>> response = evaluator.evaluateResult(req);
		Assert.assertEquals(response.size(),2);
	}
	
	@Test
	public void testEmailValidation(){
		boolean bool = ProjectUtil.isEmailvalid("amit.kumar@tarento.com");
		Assert.assertTrue(bool);
	}
	
	@Test
	public void testEmailFailureValidation(){
		boolean bool = ProjectUtil.isEmailvalid("amit.kumartarento.com");
		Assert.assertFalse(bool);
	}
	
	private Map<String, List<Map<String, Object>>> createEvaluateAssessmentRequest() {
		
		Map<String,Object> assmntMap1 = new HashMap<>();
		assmntMap1.put(JsonKey.ASSESSMENT_SCORE, "4");
		assmntMap1.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap1.put(JsonKey.ASSESSMENT_ITEM_ID, "1");
		assmntMap1.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap1.put(JsonKey.CONTENT_ID, "CON1");
		assmntMap1.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap2 = new HashMap<>();
		assmntMap2.put(JsonKey.ASSESSMENT_SCORE, "8");
		assmntMap2.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap2.put(JsonKey.ASSESSMENT_ITEM_ID, "1");
		assmntMap2.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap2.put(JsonKey.CONTENT_ID, "CON1");
		assmntMap2.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap3 = new HashMap<>();
		assmntMap3.put(JsonKey.ASSESSMENT_SCORE, "8");
		assmntMap3.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap3.put(JsonKey.ASSESSMENT_ITEM_ID, "2");
		assmntMap3.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap3.put(JsonKey.CONTENT_ID, "CON1");
		assmntMap3.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap4 = new HashMap<>();
		assmntMap4.put(JsonKey.ASSESSMENT_SCORE, "5");
		assmntMap4.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap4.put(JsonKey.ASSESSMENT_ITEM_ID, "3");
		assmntMap4.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap4.put(JsonKey.CONTENT_ID, "CON1");
		assmntMap4.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap5 = new HashMap<>();
		assmntMap5.put(JsonKey.ASSESSMENT_SCORE, "5");
		assmntMap5.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap5.put(JsonKey.ASSESSMENT_ITEM_ID, "1");
		assmntMap5.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap5.put(JsonKey.CONTENT_ID, "CON2");
		assmntMap5.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap6 = new HashMap<>();
		assmntMap6.put(JsonKey.ASSESSMENT_SCORE, "6");
		assmntMap6.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap6.put(JsonKey.ASSESSMENT_ITEM_ID, "1");
		assmntMap6.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap6.put(JsonKey.CONTENT_ID, "CON2");
		assmntMap6.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap7 = new HashMap<>();
		assmntMap7.put(JsonKey.ASSESSMENT_SCORE, "8");
		assmntMap7.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap7.put(JsonKey.ASSESSMENT_ITEM_ID, "2");
		assmntMap7.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap7.put(JsonKey.CONTENT_ID, "CON2");
		assmntMap7.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap8 = new HashMap<>();
		assmntMap8.put(JsonKey.ASSESSMENT_SCORE, "9");
		assmntMap8.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap8.put(JsonKey.ASSESSMENT_ITEM_ID, "3");
		assmntMap8.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap8.put(JsonKey.CONTENT_ID, "CON2");
		assmntMap8.put(JsonKey.USER_ID, "USR1");
		
		Map<String,List<Map<String,Object>>> map = new HashMap<>();
		
		List<Map<String,Object>> list = new ArrayList<>();
		list.add(assmntMap1);
		list.add(assmntMap2);
		list.add(assmntMap3);
		list.add(assmntMap4);
		list.add(assmntMap5);
		list.add(assmntMap6);
		list.add(assmntMap7);
		list.add(assmntMap8);
		
		map.put("USR1", list);
		
		return map;
		
	}
	
	private Map<String, List<Map<String, Object>>> createEvaluateResultRequest() {
		Map<String, List<Map<String, Object>>> map = new HashMap<>();
		List<Map<String, Object>> list3 = new ArrayList<Map<String, Object>>();
		Map<String, Object> assmntItem2 = new HashMap<>();
		Map<String, Object> assmntItem4 = new HashMap<>();
		Map<String, Object> assmntItem6 = new HashMap<>();
		Map<String, Object> assmntItem8 = new HashMap<>();
		
		list3.add(assmntItem2);
		list3.add(assmntItem4);
		list3.add(assmntItem6);
		list3.add(assmntItem8);
		
		
		assmntItem2.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId1");
		assmntItem2.put(JsonKey.ASSESSMENT_SCORE, "3");
		assmntItem2.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		
		assmntItem4.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId2");
		assmntItem4.put(JsonKey.ASSESSMENT_SCORE, "5");
		assmntItem4.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		
		assmntItem6.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId3");
		assmntItem6.put(JsonKey.ASSESSMENT_SCORE, "7");
		assmntItem6.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		
		assmntItem8.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId4");
		assmntItem8.put(JsonKey.ASSESSMENT_SCORE, "9");
		assmntItem8.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		
		map.put("USR3", list3);
		return map;
	}
}
