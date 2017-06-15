/**
 * 
 */
package org.sunbird.common.services;

import java.util.List;
import java.util.Map;

/**
 * @author Manzarul
 * This will have method to evaluate the assessment.
 *
 */
public interface AssessmentEvaluator {
	
    /**
     * This method will take user assessment as a request map. 
     * map will contains uid , and corresponding list of assessment , 
     * list will contains assessment details. Default implementation is
     * to collect the best among all.
     * @param data Map<String, List<Map<String, Object>>>
     * @return Map<String, Map<String, Object>>
     */
	public Map<String, Map<String, Object>> evaluateAssessment(Map<String, List<Map<String, Object>>> data);

}
