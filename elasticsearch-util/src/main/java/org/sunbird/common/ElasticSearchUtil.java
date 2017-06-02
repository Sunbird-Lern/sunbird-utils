/**
 * 
 */
package org.sunbird.common;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
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
	 * @param 
	 */
	public static String createData(String index, String type, String identifier, Map<String, Object> data) {
		if (ProjectUtil.isStringNullOREmpty(identifier) || ProjectUtil.isStringNullOREmpty(type)
				|| ProjectUtil.isStringNullOREmpty(index)) {
			LOGGER.info("Identifier value is null or empty ,not able to save data.");
			return "ERROR";
		}
		data.put("identifier", identifier);
		IndexResponse response = ConnectionManager.getClient().prepareIndex(index, type, identifier).setSource(data)
				.get();
		LOGGER.info("Save value==" + response.getId() + " " + response.status());
		return response.getId();
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
		sr.getHits().getAt(0).getSource();
		//{courseType=type of the course. all , private, organisationId=org id, downloadUrl=https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/ecar_files/do_112228048362078208130/test-content-1_1493905653021_do_112228048362078208130_5.0.ecar, description=Test Content 1, language=["Hindi"], updatedDate=2017-06-01T10:45:17.270Z, variants={"spine": {"ecarUrl": "https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/ecar_files/do_112228048362078208130/test-content-1_1493905655272_do_112228048362078208130_5.0_spine.ecar","size": 863}}, mimeType=application/vnd.ekstep.html-archive, createdOn=2017-05-04T13:47:32.676+0000, objectType=course, courseAddedByName=Name of the person who added the course under sunbird, artifactUrl=https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/content/do_112228048362078208130/artifact/advancedenglishassessment1_1533_1489654074_1489653812104_1492681721669.zip, lastUpdatedOn=2017-05-24T17:26:49.112+0000, courseId=course id_0, contentType=Story, courseAddedById=who added the course in NTP, owner=EkStep, identifier=NTP course id_0, orgName=Name of the organisation, visibility=Default, enrollementStartDate=2017-06-01T10:45:17.270Z, updatedByName=last updated person name, noOfLecture=30, mediaType=content, osId=org.ekstep.quiz.app, updatedById=last updated by id, languageCode=hi, coursePublishedByName=who published the course, pkgVersion=5, tutor=[{"id":"name"},{"id":"name"}], versionKey=1495646809112, facultyId=faculty for this course, courseName=Name of the course added in NTP, size=2699766, lastPublishedOn=2017-05-04T13:47:33.000+0000, courseDuration=50, coursePublishedById=who published the course, availableFor=["C.B.S.C","I.C.S.C","all"], CoursecontentType=list of course content type as comma separated , pdf, video, wordDoc, name=Test Content 1, operationType=add/updated/delete, publishedDate=2017-06-01T10:45:17.270Z, facultyName=name of the faculty, status=Live}
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
	
	/**
	 * This method will create index  , type ,setting and mapping.
	 * @param index String index name
	 * @param type String type name
	 * @param mappings String 
	 * @param settings String
	 * @return boolean
	 */
	public static boolean createIndex(String index, String type, String mappings, String settings) {
		boolean response = false;
		if(ProjectUtil.isStringNullOREmpty(index)) {
			return response;
		}
		CreateIndexResponse createIndexResponse = null;
		TransportClient client = ConnectionManager.getClient();
		@SuppressWarnings("deprecation")
		CreateIndexRequestBuilder createIndexBuilder = client.admin().indices().prepareCreate(index);
		if (!ProjectUtil.isStringNullOREmpty(settings)) {
			//createIndexResponse = createIndexBuilder.setSettings(Settings.builder().put(settings).build()).get();
			createIndexResponse = createIndexBuilder.get();
		} else {
			createIndexResponse = createIndexBuilder.get();
		}
		if (createIndexResponse != null && createIndexResponse.isAcknowledged()) {
			response = true;
			if (!ProjectUtil.isStringNullOREmpty(mappings) && !ProjectUtil.isStringNullOREmpty(type)) {
				PutMappingResponse mappingResponse = client.admin().indices().preparePutMapping(index).setType(type)
						.setSource(mappings).get();
				if (mappingResponse.isAcknowledged()) {
					response = true;
				} else {
					response = false;
				}
			} else if (!ProjectUtil.isStringNullOREmpty(type)) {
				PutMappingResponse mappingResponse = client.admin().indices().preparePutMapping(index).setType(type)
						.get();
				if (mappingResponse.isAcknowledged()) {
					response = true;
				} else {
					response = false;
				}
			}
		}
		LOGGER.info("Index creation status==" + response);
		return response;
	}
	
	
	/**
	 * This method will type and mapping under already created index.
	 * @param indexName String
	 * @param typeName String
	 * @param mapping String
	 * @return boolean
	 */
	public static boolean addOrUpdateMapping(String indexName, String typeName, String mapping) {
		@SuppressWarnings("deprecation")
		PutMappingResponse response = ConnectionManager.getClient().admin().indices().preparePutMapping(indexName)
				.setType(typeName).setSource(mapping).get();
		if (response.isAcknowledged()) {
			return true;
		}
		return false;

	 }
	
	/**
	 * This method will delete the index  
	 * @param index String name of the index which we need to delete.
	 * @return boolean
	 */
	public static boolean deleteIndex (String index) {
		boolean response = false;
		 DeleteIndexResponse deleteResponse = ConnectionManager.getClient().admin().indices().prepareDelete(index).get();
		 if(deleteResponse !=null && deleteResponse.isAcknowledged()) {
			 response = true;
		 }
		return response;
	}
}


