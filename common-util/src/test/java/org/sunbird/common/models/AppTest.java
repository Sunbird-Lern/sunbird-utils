package org.sunbird.common.models;

import java.io.FileNotFoundException;
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
import org.sunbird.common.models.util.PropertiesCache;
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
		 String header = System.getenv(JsonKey.EKSTEP_AUTHORIZATION);
	        if (ProjectUtil.isStringNullOREmpty(header)) {
	          header = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_AUTHORIZATION);
	        }
		headers.put("authorization", "Bearer "+ header);
	} 
    
	
	@Test
	public void testGetResourceMethod() throws Exception{
		String response = HttpUtil.sendGetRequest("https://qa.ekstep.in/api/content/v3/read/test", headers);
		Assert.assertNotNull(response);
	}
	
	@Test
	public void testPostResourceMethod() throws Exception {
		String response = HttpUtil.sendPostRequest("https://qa.ekstep.in/api/content/v3/list", data, headers);
		Assert.assertNotNull(response);
	}
	
	@Test()
	public void testPostFailureResourceMethod() {
		//passing wrong url
		String response=null;
		try {
			Map<String,String> data = new HashMap<>();
			data.put("search", "\"contentType\": [\"Story\"]");
			response = HttpUtil.sendPostRequest("https://qa.ekstep.in/api/content/wrong/v3/list", data, headers);
		} catch (Exception e) {
			Assert.assertTrue(e instanceof FileNotFoundException);
		}
		Assert.assertNull(response);
	}
	
	@Test()
	public void testPatchMatch() {
		String response = null;
		try {
			String ekStepBaseUrl = System.getenv(JsonKey.EKSTEP_BASE_URL);
			if (ProjectUtil.isStringNullOREmpty(ekStepBaseUrl)) {
				ekStepBaseUrl = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_BASE_URL);
			}
			response = HttpUtil.sendPatchRequest(ekStepBaseUrl
					+ PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_TAG_API_URL) + "/" + "testt123", "{}",
					headers);
		} catch (Exception e) {
		}
		Assert.assertNotNull(response);
	}
	
	@Test
	public void testEvaluateAssessment(){
		Map<String, List<Map<String, Object>>> data = createEvaluateAssessmentRequest();
		AssessmentEvaluator evaluator = new  DefaultAssessmentEvaluator();
		Map<String, List<Map<String, Object>>> response = evaluator.evaluateAssessment(data);
		List<Map<String, Object>> list = response.get("USR1");
		Assert.assertEquals(list.size(),8);
	}

	@Test
	public void testEvaluateResult(){
		//Map<String, List<Map<String, Object>>> data1 = createEvaluateAssessmentRequest();
		AssessmentEvaluator evaluator = new  DefaultAssessmentEvaluator();
		//Map<String, List<Map<String, Object>>> response1 = evaluator.evaluateAssessment(data1);
		//Map<String, List<Map<String, Object>>> req= new HashMap<>();
		//req.put("USR1", response1.get("USR1"));
		List<Map<String, Object>> response = evaluator.evaluateResult(createEvaluateAssessmentRequest());
		Assert.assertEquals(response.size(),1);
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
		assmntMap2.put(JsonKey.ASSESSMENT_ITEM_ID, "2");
		assmntMap2.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap2.put(JsonKey.CONTENT_ID, "CON1");
		assmntMap2.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap3 = new HashMap<>();
		assmntMap3.put(JsonKey.ASSESSMENT_SCORE, "8");
		assmntMap3.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap3.put(JsonKey.ASSESSMENT_ITEM_ID, "3");
		assmntMap3.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap3.put(JsonKey.CONTENT_ID, "CON1");
		assmntMap3.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap4 = new HashMap<>();
		assmntMap4.put(JsonKey.ASSESSMENT_SCORE, "5");
		assmntMap4.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap4.put(JsonKey.ASSESSMENT_ITEM_ID, "4");
		assmntMap4.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap4.put(JsonKey.CONTENT_ID, "CON1");
		assmntMap4.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap5 = new HashMap<>();
		assmntMap5.put(JsonKey.ASSESSMENT_SCORE, "5");
		assmntMap5.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap5.put(JsonKey.ASSESSMENT_ITEM_ID, "5");
		assmntMap5.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap5.put(JsonKey.CONTENT_ID, "CON1");
		assmntMap5.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap6 = new HashMap<>();
		assmntMap6.put(JsonKey.ASSESSMENT_SCORE, "6");
		assmntMap6.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap6.put(JsonKey.ASSESSMENT_ITEM_ID, "6");
		assmntMap6.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap6.put(JsonKey.CONTENT_ID, "CON1");
		assmntMap6.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap7 = new HashMap<>();
		assmntMap7.put(JsonKey.ASSESSMENT_SCORE, "8");
		assmntMap7.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap7.put(JsonKey.ASSESSMENT_ITEM_ID, "7");
		assmntMap7.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap7.put(JsonKey.CONTENT_ID, "CON1");
		assmntMap7.put(JsonKey.USER_ID, "USR1");
		
		Map<String,Object> assmntMap8 = new HashMap<>();
		assmntMap8.put(JsonKey.ASSESSMENT_SCORE, "9");
		assmntMap8.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntMap8.put(JsonKey.ASSESSMENT_ITEM_ID, "8");
		assmntMap8.put(JsonKey.COURSE_ID, "CSR1");
		assmntMap8.put(JsonKey.CONTENT_ID, "CON1");
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
