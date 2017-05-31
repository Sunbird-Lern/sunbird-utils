/**
 * 
 */
package org.sunbird.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.transport.TransportClient;
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
	  Map<String,Object> mapping = new HashMap<>();
	  mapping.put("courseId", String.class);mapping.put("courseDuration", Integer.class);
	  mapping.put("noOfLecture", Integer.class);mapping.put("enrollementStartDate", Date.class);
	 PutMappingResponse response  = client.admin().indices().preparePutMapping(indexName.toLowerCase()).setType(typeName).setSource(createMapping()).get();
	 System.out.println(response.isAcknowledged());
	}
	
	
	public static void main(String[] args) {
		createIndex("SunBird", "course");
		ElasticSearchUtil.createData("sunbird", "course", "NTP course id_2",createMapData());
	}
	public static String createMapping () {
		String mapping = "{\"properties\": {\"courseId\": {\"type\": \"string\",\"index\": \"analyzed\",\"store\": \"yes\"},\"courseDuration\": {\"type\": \"integer\",\"index\": \"analyzed\",\"store\": \"yes\"},\"enrollementStartDate\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"},\"enrollementEndDate\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"},\"publishedDate\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"},\"createdOn\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"},\"lastUpdatedOn\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"}}}";
		return mapping;
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
