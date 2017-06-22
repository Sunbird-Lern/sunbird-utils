/**
 * 
 */
package org.sunbird.common.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.services.AssessmentEvaluator;

/**
 * @author Manzarul
 * @author Amit Kumar
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
	public List<Map<String, Object>> evaluateResult(Map<String, List<Map<String, Object>>> evaluatedData) {
		double score = 0;
		double maxScore = 0;
		String userId = "";
		String courseId = "";
		String contnetId = "";
		Map<String, Object> resultMap = new HashMap<>();
		List<Map<String, Object>> listMap = new ArrayList<>();
		if (evaluatedData != null && evaluatedData.size() > 0) {
			Iterator<Entry<String, List<Map<String, Object>>>> itr = evaluatedData.entrySet().iterator();
			while (itr.hasNext()) {
				Entry<String, List<Map<String, Object>>> entry = itr.next(); 
				userId = entry.getKey();
				List<Map<String, Object>> list = entry.getValue();
				for (Map<String, Object> map : list) {
					courseId = (String) map.get(JsonKey.COURSE_ID);
					contnetId = (String) map.get(JsonKey.CONTENT_ID);
					score = score
							+ ((String)map.get(JsonKey.ASSESSMENT_SCORE)) != null ? Double.parseDouble((String) map.get(JsonKey.ASSESSMENT_SCORE)) : 0 ;
					maxScore = maxScore + ((String)map.get(JsonKey.ASSESSMENT_MAX_SCORE)) != null
							? Double.parseDouble((String) map.get(JsonKey.ASSESSMENT_MAX_SCORE)) : 0 ;
				}
				double percentage = ProjectUtil.calculatePercentage(score, maxScore);
				ProjectUtil.AssessmentResult result = ProjectUtil.calcualteAssessmentResult(percentage);
				resultMap.put(JsonKey.USER_ID, userId);
				resultMap.put(JsonKey.COURSE_ID, courseId);
				resultMap.put(JsonKey.CONTENT_ID, contnetId);
				resultMap.put(JsonKey.RESULT, result.getResult());
				resultMap.put(JsonKey.ASSESSMENT_GRADE, result.getGrade());
				listMap.add(resultMap);
				contnetId = "";
				userId = "";
				contnetId = "";
				score = 0;
				maxScore = 0; 
			}
		}

		return listMap;
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
				score = (request.get(i).get(JsonKey.ASSESSMENT_SCORE)) != null ? Double.parseDouble((String) request.get(i).get(JsonKey.ASSESSMENT_SCORE)) : 0 ;
				if(assessmentWithMaxScore.containsKey(assessmentId)){
					Map<String,Object> tempMap = (Map<String, Object>) assessmentWithMaxScore.get(assessmentId);
					if(null!= tempMap.get(JsonKey.ASSESSMENT_SCORE) && score.compareTo(Double.parseDouble((String)tempMap.get(JsonKey.ASSESSMENT_SCORE))) > 0){
						assessmentWithMaxScore.put(assessmentId, request.get(i));
					}
				}else{
					assessmentWithMaxScore.put(assessmentId, request.get(i));
				}
			}
		}
		return new ArrayList<Map<String,Object>> (assessmentWithMaxScore.values());
	}
	

}
