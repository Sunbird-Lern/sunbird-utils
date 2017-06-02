/**
 * 
 */
package org.sunbird.common;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.sunbird.common.models.util.LogHelper;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.helper.ConnectionManager;

/**
 * This class will provide all required operation
 * for elastic search. 
 * @author Manzarul
 */
public class ElasticSearchUtil{
	private static final LogHelper LOGGER = LogHelper.getInstance(ElasticSearchUtil.class.getName());
	
	/**
	 * This method will put a new data entry inside Elastic search. identifier 
	 * value becomes _id inside ES, so every time provide a unique value while
	 * saving it.
	 * @param index String  ES index name
	 * @param type  String  ES type name
	 * @param identifier ES column identifier as an String
	 * @param data Map<String,Object> 
	 */
	public static void createData(String index, String type, String identifier, Map<String, Object> data) {
		if (ProjectUtil.isStringNullOREmpty(identifier) || ProjectUtil.isStringNullOREmpty(type)
				|| ProjectUtil.isStringNullOREmpty(index)) {
			LOGGER.info("Identifier value is null or empty ,not able to save data.");
			return;
		}
		data.put("identifier", identifier);
		IndexResponse response = ConnectionManager.getClient().prepareIndex(index, type, identifier).setSource(data)
				.get();
		LOGGER.info("Save value==" + response.getId() + " " + response.status());
	}
    
	/**
	 * This method will provide data form ES based on incoming identifier.
	 * we can get data by passing index and  identifier values , or all the three
	 * @param type String
	 * @param identifier String
	 * @return Map<String,Object>
	 */
	public static Map<String, Object> getDataByIdentifier(String index, String type, String identifier) {
		GetResponse response = null;
		if (ProjectUtil.isStringNullOREmpty(index) && ProjectUtil.isStringNullOREmpty(type)
				&& ProjectUtil.isStringNullOREmpty(identifier)) {
			LOGGER.info("Invalid request is coming.");
		} else if (ProjectUtil.isStringNullOREmpty(index)) {
			LOGGER.info("Please provide index value.");
		} else if (ProjectUtil.isStringNullOREmpty(type)) {
			response = ConnectionManager.getClient().prepareGet().setIndex(index).setId(identifier).get();
		} else {
			response = ConnectionManager.getClient().prepareGet(index, type, identifier).get();
		}
		if (response == null) {
			return null;
		}
		return response.getSource();
	}
	/**
	 * This method will do the data search inside ES. based on incoming search data.
	 * @param index String
	 * @param type String
	 * @param searchData  Map<String,Object>
	 * @return Map<String,Object>
	 */
	public static Map<String, Object> searchData(String index, String type, Map<String, Object> searchData) {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		 Iterator<Entry<String, Object>> itr = searchData.entrySet().iterator();
		 while (itr.hasNext()) {
			  Entry<String,Object> entry =itr.next();
			 sourceBuilder.query(QueryBuilders.commonTermsQuery(entry.getKey(),entry.getValue()));
		 }
		 SearchResponse sr = null;
		try {
			sr = ConnectionManager.getClient().search(new SearchRequest(index).types(type).source(sourceBuilder)).get();
		} catch (InterruptedException e) {
			LOGGER.error(e);
		} catch (ExecutionException e) {
			LOGGER.error(e);
		}
		
		return  sr.getAggregations().asList().get(0).getMetaData();
	}
    
	/**
	 * This method will update data based on identifier.take the data based on identifier and merge with 
	 * incoming data then update it.
	 * @param index String 
	 * @param type String
	 * @param identifier String
	 * @param data Map<String,Object>
	 * @return boolean
	 */
	public static boolean updateData(String index, String type, String identifier, Map<String, Object> data) {
		if (!ProjectUtil.isStringNullOREmpty(index) && !ProjectUtil.isStringNullOREmpty(type)
				&& !ProjectUtil.isStringNullOREmpty(identifier) && data != null) {
			UpdateResponse response = ConnectionManager.getClient().prepareUpdate(index, type, identifier).setDoc(data)
					.get();
			LOGGER.info("updated response==" + response.getResult().name());
			if (response.getResult().name().equals("UPDATED")) {
				return true;
			}
		} else {
			LOGGER.info("Requested data is invalid.");
		}
		return false;
	}
	
	/**
	 * This method will remove data from ES based on identifier.
	 * @param index String
	 * @param type String
	 * @param identifier String
	 */
	public static void removeData(String index, String type, String identifier) {
		if (!ProjectUtil.isStringNullOREmpty(index) && !ProjectUtil.isStringNullOREmpty(type)
				&& !ProjectUtil.isStringNullOREmpty(identifier)) {
			DeleteResponse response = ConnectionManager.getClient().prepareDelete(index, type, identifier).get();
			LOGGER.info("delete info ==" + response.getResult().name() + " " + response.getId());
		} else {
			LOGGER.info("Data can not be deleted due to invalid input.");
		}
	}
}
