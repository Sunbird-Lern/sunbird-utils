/**
 * 
 */
package org.sunbird.common;

import java.util.Date;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.sunbird.helper.ConnectionManager;

/**
 * This class will provide all required operation
 * for elastic search. 
 * @author Manzarul
 */
public class ElasticSearchUtil{
	
	/**
	 * This method will put a new data entry inside Elastic search.
	 * @param index String  ES index name
	 * @param type  String  ES type name
	 * @param data Map<String,Object> 
	 */
	public static void createData(String index,String type, Map<String,Object> data) {
		IndexResponse response = ConnectionManager.getClient().prepareIndex(index, type, (String)data.get("courseId")).setSource(data).get();
		System.out.println(response.getId() +" "+ response.status());
		
	}
    
	/**
	 * This method will provide data form ES based on incoming identifier.
	 * @param type String
	 * @param identifier String
	 * @return Map<String,Object>
	 */
	public static Map<String,Object> getData(String index,String type,String identifier) {
		return null;
	}
	/**
	 * This method will do the data search inside ES. based on incoming search data.
	 * @param index String
	 * @param type String
	 * @param searchData  Map<String,Object>
	 * @return Map<String,Object>
	 */
	public static Map<String,Object> searchData (String index,String type, Map<String ,Object> searchData) {
		return null;
	}
    
	/**
	 * This method will update data based on identifier.take the data based on identifier and merge with 
	 * incoming data then update it.
	 * @param index String 
	 * @param type String
	 * @param data Map<String,Object>
	 */
	public static void updateData (String index,String type, Map<String,Object> data) {
		
	}
	
	/**
	 * This method will remove data from ES based on identifier.
	 * @param index String
	 * @param type String
	 * @param identifier String
	 */
	public static void removeData(String index,String type,String identifier) {
		
	}
}
