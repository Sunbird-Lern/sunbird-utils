/**
 * 
 */
package org.sunbird.common;

import java.util.Map;

/**
 * This class will provide all required operation
 * for elastic search. 
 * @author Manzarul
 */
public class ElasticSearchUtil{
	
	/**
	 * This method will put a new data entry inside Elastic search.
	 * @param url String , url of running elastic search
	 * @param index String  ES index name
	 * @param type  String  ES type name
	 * @param data Map<String,Object> 
	 */
	public static void createData(String url, String index,String type, Map<String,Object> data) {
		
	}
    
	/**
	 * This method will provide data form ES based on incoming key value pair.
	 * @param url String, url of running ES
	 * @param type String
	 * @param data  Map<String,Object>
	 * @return Map<String,Object>
	 */
	public static Map<String,Object> getData(String url,String type,Map<String,Object> data ) {
		return null;
	}
	/**
	 * This method will do the data search inside ES. based on incoming search data.
	 * @param url String, url of running ES
	 * @param index String
	 * @param type String
	 * @param searchData  Map<String,Object>
	 * @return Map<String,Object>
	 */
	public static Map<String,Object> searchData (String url,String index,String type, Map<String ,Object> searchData) {
		//TODO need to discuss 
		return null;
	}
    
	/**
	 * This method will update data based on identifier.take the data based on identifier and merge with 
	 * incoming data then update it.
	 * @param url  String, url of running ES
	 * @param index String 
	 * @param type String
	 * @param data Map<String,Object>
	 */
	public static void updateData (String url, String index,String type, Map<String,Object> data) {
		
	}
	
	/**
	 * This method will remove data from ES based on identifier.
	 * @param url  String, url of running ES
	 * @param index String
	 * @param type String
	 * @param identifier String
	 */
	public static void removeData(String url, String index,String type,String identifier) {
		
	}
}
