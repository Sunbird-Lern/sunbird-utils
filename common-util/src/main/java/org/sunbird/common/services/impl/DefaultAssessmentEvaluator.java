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
 *
 */
public class DefaultAssessmentEvaluator implements AssessmentEvaluator {

	@Override
	public Map<String, List<Map<String, Object>>> evaluateAssessment(Map<String, List<Map<String, Object>>> data) {
		return null;
	}

	@Override
	public List<Map<String, Object>> evaluateResult(Map<String, List<Map<String, Object>>> evaluatedData) {
		int score = 0;
		int maxScore = 0;
		String userId = "";
		String courseId = "";
		String contnetId = "";
		Map<String, Object> resultMap = new HashMap<>();
		List<Map<String, Object>> listMap = new ArrayList<>();
		if (evaluatedData != null && evaluatedData.size() > 0) {
			Iterator<Entry<String, List<Map<String, Object>>>> itr = evaluatedData.entrySet().iterator();
			while (itr.hasNext()) {
				userId = itr.next().getKey();
				List<Map<String, Object>> list = itr.next().getValue();
				for (Map<String, Object> map : list) {
					courseId = (String) map.get(JsonKey.COURSE_ID);
					contnetId = (String) map.get(JsonKey.CONTENT_ID);
					score = score
							+ (int) (map.get(JsonKey.ASSESSMENT_SCORE) != null ? map.get(JsonKey.ASSESSMENT_SCORE) : 0);
					maxScore = maxScore + (int) (map.get(JsonKey.ASSESSMENT_MAX_SCORE) != null
							? map.get(JsonKey.ASSESSMENT_MAX_SCORE) : 0);
				}
				int percentage = ProjectUtil.calculatePercentage(score, maxScore);
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
	
	

}
