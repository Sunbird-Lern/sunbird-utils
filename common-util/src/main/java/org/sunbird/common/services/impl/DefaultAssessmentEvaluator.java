/**
 * 
 */
package org.sunbird.common.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.services.AssessmentEvaluator;

/**
 * @author Manzarul
 *
 */
public class DefaultAssessmentEvaluator implements AssessmentEvaluator {

	@Override
	public Map<String, List<Map<String, Object>>> evaluateAssessment(Map<String, List<Map<String, Object>>> data) {
		Map<String, List<Map<String, Object>>> response = new HashMap<String, List<Map<String,Object>>>();
		Map<String, List<Map<String, Object>>> request =data;
		if(request.size() > 0)
		for(Entry<String, List<Map<String, Object>>> entry : request.entrySet()){
			if(entry.getValue().size() > 0){
			  List<Map<String, Object>> assessmentList = getAssessmentListWithMaxScore(entry);
			  response.put(entry.getKey(), assessmentList);
			}
		}
		
		return response;
	}


	@Override
	public Map<String, Object> evaluateResult(Map<String, List<Map<String, Object>>> evaluatedData) {
		return null;
	}
	

	private List<Map<String, Object>> getAssessmentListWithMaxScore(Entry<String, List<Map<String, Object>>> entry) {
		List<Map<String, Object>> request = entry.getValue();
		Map<String, Map<String,Object>> assessmentWithMaxScore = new HashMap<>();
		assessmentWithMaxScore.put((String) request.get(0).get(JsonKey.ASSESSMENT_ITEM_ID), request.get(0));
		String assessmentId = null;
		Double score = null;
		if(request.size() > 1){
			for(int i=1;i<request.size();i++){
				assessmentId = (String) request.get(i).get(JsonKey.ASSESSMENT_ITEM_ID);
				score = Double.parseDouble((String) request.get(i).get("score"));
				if(assessmentWithMaxScore.containsKey(assessmentId)){
					Map<String,Object> tempMap = (Map<String, Object>) assessmentWithMaxScore.get(assessmentId);
					if(score.compareTo(Double.parseDouble((String)tempMap.get("score"))) > 0){
						assessmentWithMaxScore.put(assessmentId, request.get(i));
					}
				}else{
					assessmentWithMaxScore.put(assessmentId, request.get(i));
				}
			}
		}
		return (List<Map<String, Object>>) assessmentWithMaxScore.values();
	}
	

}
