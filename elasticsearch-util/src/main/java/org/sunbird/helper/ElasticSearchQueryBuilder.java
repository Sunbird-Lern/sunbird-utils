/**
 * 
 */
package org.sunbird.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.Build;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.sunbird.common.ElasticSearchUtil;

import io.netty.handler.codec.json.JsonObjectDecoder;

/**
 * This class will create elastic search query
 * from requested search data. 
 * @author Manzarul
 *
 */
public class ElasticSearchQueryBuilder {
	
	public static void createIndex(String indexName,String typeName) {
	  TransportClient client	= ConnectionManager.getClient();
	  CreateIndexResponse response = client.admin().indices().prepareCreate(indexName).setSettings(createSettingsForIndex()).addMapping(typeName, createMapping()).get();
	 
	  //PutMappingResponse response  = client.admin().indices().preparePutMapping(indexName).setType(typeName).setSource(createMapping()).get();
	 System.out.println(response.isAcknowledged());
	}
	
	
	public static void main(String[] args) {
		createIndex("sunbird-inx2", "course");
		//ElasticSearchUtil.createData("sunbird", "course", "NTP course id_2",createMapData());
	}
	
	public static String createMapping () {
		String mapping = 
				//"{ \"cs\" : { \"dynamic_templates\": [{\"longs\": { \"match_mapping_type\": \"long\",\"mapping\": {\"type\": \"long\",\"fields\": { \"raw\": {\"type\": \"long\" }}}} },{\"booleans\": {\"match_mapping_type\": \"boolean\",\"mapping\": {\"type\": \"boolean\",\"fields\": {\"raw\": {\"type\": \"boolean\"}}}}},{\"doubles\": {\"match_mapping_type\": \"double\",\"mapping\": {\"type\": \"double\",\"fields\": {\"raw\": { \"type\": \"double\"}}}}},{\"dates\": {\"match_mapping_type\": \"date\",\"mapping\": {\"type\": \"date\",\"fields\": {\"raw\": {\"type\": \"date\"}}}} },{\"strings\": {\"match_mapping_type\": \"string\",\"mapping\": {\"type\": \"string\",\"copy_to\": \"all_fields\",\"analyzer\": \"cs_index_analyzer\",\"search_analyzer\": \"cs_search_analyzer\",\"fields\": {\"raw\": {\"type\": \"string\",\"analyzer\": \"keylower\"}}}}}],\"properties\": {\"all_fields\": {\"type\": \"string\",\"analyzer\": \"cs_index_analyzer\",\"search_analyzer\": \"cs_search_analyzer\",\"fields\": {\"raw\": {\"type\": \"string\",\"analyzer\": \"keylower\"}}}}}}";
				
				"{\"properties\": {\"courseId\": {\"type\": \"string\",\"index\": \"analyzed\",\"store\": \"yes\"},\"courseDuration\": {\"type\": \"integer\",\"index\": \"analyzed\",\"store\": \"yes\"},\"enrollementStartDate\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"},\"enrollementEndDate\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"},\"publishedDate\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"},\"createdOn\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"},\"lastUpdatedOn\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"}}}";
		return mapping;
	}
	
	
	public static String createSettingsForIndex () {
		String settings = "{\"settings\": {\"analysis\": {\"analyzer\": {\"sb_index_analyzer\": {\"tokenizer\": \"my_tokenizer\"}},\"tokenizer\": {\"my_tokenizer\": {\"type\": \"edge_ngram\",\"min_gram\": 2,\"max_gram\": 10,\"token_chars\": [\"letter\",\"digit\",\"whitespace\",\"punctuation\",\"symbol\"]}}}}}";
				
		//"{\"settings\": {\"index\": {\"index\": \"composite_search_index_type\",\"type\": \"composite_search_index_type\",\"analysis\": {\"analyzer\": {\"cs_index_analyzer\": {\"type\": \"custom\",\"tokenizer\": \"standard\",\"filter\": [\"lowercase\",\"mynGram\"]},\"cs_search_analyzer\": {\"type\": \"custom\",\"tokenizer\": \"standard\",\"filter\": [\"standard\",\"lowercase\"]},\"keylower\": {\"tokenizer\": \"keyword\",\"filter\": \"lowercase\"}},\"filter\": {\"mynGram\": {\"type\": \"ngram\",\"min_gram\": 1,\"max_gram\": 20,\"token_chars\": [\"letter\", \"digit\",\"whitespace\",\"punctuation\",\"symbol\"]} }}} }}";
		return settings;
	}
	
	/**
	 * 
	 * @return
	 */
	private static Map<String,Object> createMapData() {
		Map<String,Object> map = new HashMap<>();
		map.put("courseId","NTP course id_2");
		map.put( "courseName","Name of the course added in NTP");
		map.put(  "courseDuration",50);
		map.put(  "noOfLecture",30);
		map.put(   "organisationId","org id");
		map.put(   "orgName","Name of the organisation");
		map.put("courseAddedById","who added the course in NTP");
		map.put( "courseAddedByName","Name of the person who added the course under sunbird");
		map.put(  "coursePublishedById","who published the course");
		map.put(  "coursePublishedByName","who published the course");
		map.put(  "enrollementStartDate",new Date());  
		map.put(  "publishedDate",new Date());
		map.put(  "updatedDate",new Date());
		map.put(   "updatedById","last updated by id");
		map.put("updatedByName","last updated person name");
		map.put("courseType","type of the course. all , private");
		map.put("facultyId","faculty for this course");
		map.put( "facultyName","name of the faculty");
		map.put( "CoursecontentType","list of course content type as comma separated , pdf, video, wordDoc");  
		map.put(  "createdFor","[\"C.B.S.C\",\"I.C.S.C\",\"all\"]");
		map.put(  "tutor","[{\"id\":\"name\"},{\"id\":\"name\"}]");
		map.put(   "operationType","add/updated/delete");
		map.put(    "owner","EkStep");
		map.put("identifier","do_112228048362078208130");
		map.put( "visibility","Default");
		map.put(  "downloadUrl","https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/ecar_files/do_112228048362078208130/test-content-1_1493905653021_do_112228048362078208130_5.0.ecar");
		map.put("description", "Test Content 1");
		map.put("language", "[\"Hindi\"]");
		map.put("mediaType","content");
		map.put("variants",  "{\"spine\": {\"ecarUrl\": \"https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/ecar_files/do_112228048362078208130/test-content-1_1493905655272_do_112228048362078208130_5.0_spine.ecar\",\"size\": 863}}");
		map.put("mimeType", "application/vnd.ekstep.html-archive");
		map.put("osId", "org.ekstep.quiz.app");
		map.put("languageCode","hi");
		map.put("createdOn","2017-05-04T13:47:32.676+0000");
		map.put("pkgVersion", 5);
		map.put("versionKey","1495646809112");
		map.put( "size",2699766);
		map.put( "lastPublishedOn","2017-05-04T13:47:33.000+0000");
		//map.put("collections","[{\"identifier\": \"do_1121912573615472641169\",\"name\": \"A\",\"objectType\": \"Content\",\"relation\": \"hasSequenceMember\",\"description\": \"A.\",\"index\": null}]");
		map.put( "name","Test Content 1");
		map.put("artifactUrl","https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/content/do_112228048362078208130/artifact/advancedenglishassessment1_1533_1489654074_1489653812104_1492681721669.zip");
		map.put( "lastUpdatedOn", "2017-05-24T17:26:49.112+0000");
		map.put("contentType","Story");
		map.put("status","Live");
		return map;
	} 
	
	
	
}
