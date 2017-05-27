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
	  //  prepareCreate
	 PutMappingResponse response  = client.admin().indices().preparePutMapping(indexName.toLowerCase()).setType(typeName).setSource(createMapping()).get();
	 System.out.println(response.isAcknowledged());
	}
	
	
	public static void main(String[] args) {
		//createIndex("SunBird", "course");
		ElasticSearchUtil.createData("sunbird", "course", createMapData());
	}
	
	public static String createMapping () {
		String mapping = "{\"properties\": {\"courseId\": {\"type\": \"string\",\"index\": \"analyzed\",\"store\": \"yes\"},\"courseDuration\": {\"type\": \"integer\",\"index\": \"analyzed\",\"store\": \"yes\"},\"enrollementStartDate\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"},\"enrollementEndDate\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"},\"publishedDate\": {\"type\": \"date\",\"index\": \"analyzed\",\"store\": \"yes\"}}}";
		return mapping;
	}
	
	
	/**
	 * 
	 * @return
	 */
	private static Map<String,Object> createMapData() {
		Map<String,Object> map = new HashMap<>();
		map.put("courseId","NTP course id");
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
		return map;
	} 
	
	
	
}
