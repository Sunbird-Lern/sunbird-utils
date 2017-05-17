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
	 * @param ip  String, ES Ip
	 * @param port String ES port
	 * @param index String  ES index name
	 * @param type  String  ES type name
	 * @param data Map<String,Object> 
	 */
	public static void createData(String ip,String port, String index,String type, Map<String,Object> data) {
		
	}
    
	/**
	 * This method will provide data form ES based on incoming identifier.
	 * @param ip  String, ES Ip
	 * @param port String ES port
	 * @param type String
	 * @param identifier String
	 * @return Map<String,Object>
	 */
	public static Map<String,Object> getData(String ip,String port,String index,String type,String identifier) {
		return null;
	}
	/**
	 * This method will do the data search inside ES. based on incoming search data.
	 * @param ip  String, ES Ip
	 * @param port String ES port
	 * @param index String
	 * @param type String
	 * @param searchData  Map<String,Object>
	 * @return Map<String,Object>
	 */
	public static Map<String,Object> searchData (String ip,String port,String type, Map<String ,Object> searchData) {
		return null;
	}
    
	/**
	 * This method will update data based on identifier.take the data based on identifier and merge with 
	 * incoming data then update it.
	 * @param ip  String, ES Ip
	 * @param port String ES port
	 * @param index String 
	 * @param type String
	 * @param data Map<String,Object>
	 */
	public static void updateData (String ip,String port,String type, Map<String,Object> data) {
		
	}
	
	/**
	 * This method will remove data from ES based on identifier.
	 * @param ip  String, ES Ip
	 * @param port String ES port
	 * @param index String
	 * @param type String
	 * @param identifier String
	 */
	public static void removeData(String ip,String port,String type,String identifier) {
		
	}
}
