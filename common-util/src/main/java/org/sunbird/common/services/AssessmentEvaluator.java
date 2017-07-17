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
     * @return Map<String, List<Map<String, Object>>>
     */
	public Map<String, List<Map<String, Object>>>evaluateAssessment(Map<String, List<Map<String, Object>>> data);
	/**
	 * This  method will take user assessment item id as a key and value as score , max score and some other
	 * details for that assessment item id. 
	 * @param evaluatedData Map<String, List<Map<String, Object>>>
	 * @return List<Map<String,Object>> 
	 */
	public List<Map<String,Object>> evaluateResult (Map<String, List<Map<String, Object>>> evaluatedData);

}
