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
		System.out.println(response);
		Assert.assertEquals("", response);
	}
	
	@Test
	public void testEvaluateAssessment(){
		Map<String, List<Map<String, Object>>> data = createEvaluateAssessmentRequest();
		AssessmentEvaluator evaluator = new  DefaultAssessmentEvaluator();
		Map<String, List<Map<String, Object>>> response = evaluator.evaluateAssessment(data);
		List<Map<String, Object>> list = response.get("USR1");
		String score = null;
		for(int i =0 ; i< list.size();i++){
			System.out.println(list.get(i).get(JsonKey.ASSESSMENT_ITEM_ID)+"  "+list.get(i).get(JsonKey.ASSESSMENT_SCORE));
			if(((String)list.get(i).get(JsonKey.ASSESSMENT_ITEM_ID)).equalsIgnoreCase("assmntItemId1")){
				score = ((String)list.get(i).get(JsonKey.ASSESSMENT_SCORE));
			}
		}
		Assert.assertEquals(score,"3");
	}

	@Test
	public void testEvaluateResult(){
		Map<String, List<Map<String, Object>>> data = createEvaluateResultRequest();
		AssessmentEvaluator evaluator = new  DefaultAssessmentEvaluator();
		List<Map<String, Object>> response = evaluator.evaluateResult(data);
		Assert.assertEquals(response.size(),"3");
	}
	
	@Test
	public void testEmailValidation(){
		boolean bool = ProjectUtil.isEmailvalid("amit.kumar@tarento.com");
		Assert.assertTrue(bool);
	}
	
	private Map<String, List<Map<String, Object>>> createEvaluateAssessmentRequest() {
		Map<String, List<Map<String, Object>>> map = new HashMap<>();
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list3 = new ArrayList<Map<String, Object>>();
		Map<String, Object> assmntItem1 = new HashMap<>();
		Map<String, Object> assmntItem2 = new HashMap<>();
		Map<String, Object> assmntItem3 = new HashMap<>();
		Map<String, Object> assmntItem4 = new HashMap<>();
		Map<String, Object> assmntItem5 = new HashMap<>();
		Map<String, Object> assmntItem6 = new HashMap<>();
		Map<String, Object> assmntItem7 = new HashMap<>();
		Map<String, Object> assmntItem8 = new HashMap<>();
		
		list1.add(assmntItem1);
		list1.add(assmntItem2);
		list1.add(assmntItem3);
		list1.add(assmntItem4);
		list1.add(assmntItem5);
		list1.add(assmntItem6);
		list1.add(assmntItem7);
		list1.add(assmntItem8);
		list2.add(assmntItem1);
		list2.add(assmntItem2);
		list2.add(assmntItem3);
		list2.add(assmntItem4);
		list2.add(assmntItem5);
		list2.add(assmntItem6);
		list2.add(assmntItem7);
		list2.add(assmntItem8);
		
		list3.add(assmntItem2);
		list3.add(assmntItem4);
		list3.add(assmntItem6);
		list3.add(assmntItem8);
		
		assmntItem1.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId1");
		assmntItem1.put(JsonKey.ASSESSMENT_SCORE, "2");
		assmntItem1.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntItem2.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId1");
		assmntItem2.put(JsonKey.ASSESSMENT_SCORE, "3");
		assmntItem2.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntItem3.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId2");
		assmntItem3.put(JsonKey.ASSESSMENT_SCORE, "4");
		assmntItem3.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntItem4.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId2");
		assmntItem4.put(JsonKey.ASSESSMENT_SCORE, "5");
		assmntItem4.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntItem5.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId3");
		assmntItem5.put(JsonKey.ASSESSMENT_SCORE, "6");
		assmntItem5.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntItem6.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId3");
		assmntItem6.put(JsonKey.ASSESSMENT_SCORE, "7");
		assmntItem6.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntItem7.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId4");
		assmntItem7.put(JsonKey.ASSESSMENT_SCORE, "8");
		assmntItem7.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		assmntItem8.put(JsonKey.ASSESSMENT_ITEM_ID, "assmntItemId4");
		assmntItem8.put(JsonKey.ASSESSMENT_SCORE, "9");
		assmntItem8.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
		
		map.put("USR1", list1);
		map.put("USR2", list2);
		map.put("USR3", list3);
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
