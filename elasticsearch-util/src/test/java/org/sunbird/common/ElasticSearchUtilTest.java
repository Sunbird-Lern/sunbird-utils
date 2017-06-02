package org.sunbird.common;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.helper.ConnectionManager;
import org.sunbird.helper.ElasticSearchQueryBuilder;
import org.sunbird.common.ElasticSearchUtil;
import static org.junit.Assert.*;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ElasticSearchUtilTest {
	private static Map<String,Object> map = null;
	private static TransportClient client = null;
	private static final String indexName = "sbtestindex";
	private static final String typeName = "sbtesttype";
	@BeforeClass 
	public static void init() {
		map = intitialiseMap(5);
		client = ConnectionManager.getClient();
	}
	
	@Test
	public void testConnectionSuccess() {
		client = ConnectionManager.getClient();
		assertNotNull(client);
	}
	
	@Test
	public void CreateIndex() {
		boolean response = ElasticSearchUtil.createIndex(indexName, typeName, ElasticSearchQueryBuilder.createMapping(),
				ElasticSearchQueryBuilder.createMapping());
		assertTrue(response);
	}
	
	@Test
	public void createDataTest() {
		 String responseId = ElasticSearchUtil.createData(indexName, typeName, (String)map.get("courseId"), map);
		assertEquals(responseId, map.get("courseId"));
	}
    
	@Test
	public void getByIdentifier() {
		 Map<String,Object> responseMap = ElasticSearchUtil.getDataByIdentifier(indexName, typeName, (String)map.get("courseId"));
		assertEquals(responseMap.get("courseId"), map.get("courseId"));
	}
	
	
	@Test
	public void updateByIdentifier() {
		Map<String,Object> innermap = new HashMap<>();
		innermap.put("courseName", "updatedCourese name");
		innermap.put(   "organisationId","updatedOrgId");
		 boolean response = ElasticSearchUtil.updateData(indexName, typeName, (String)map.get("courseId"), innermap);
		 assertTrue(response);
	}
	
	
	@Test
	public void updateByIdentifierTest() {
		Map<String, Object> responseMap = ElasticSearchUtil.getDataByIdentifier(indexName, typeName,
				(String) map.get("courseId"));
		assertEquals(responseMap.get("courseName"), "updatedCourese name");
	}
	
	@AfterClass
	public static void destroy(){
		  ElasticSearchUtil.deleteIndex(indexName);
		  map = null;
		 ConnectionManager.closeClient();
	}
	
	
	
	private static Map<String,Object> intitialiseMap(int val) {
		Map<String,Object> map = new HashMap<>();
		map.put("objectType","course");
		map.put("courseId","course id_"+val);
		map.put( "courseName","NTP course_"+val);
		map.put(  "courseDuration",val);
		map.put(  "noOfLecture",30+val);
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
		map.put(  "availableFor","[\"C.B.S.C\",\"I.C.S.C\",\"all\"]");
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
		map.put("collections","[{\"identifier\": \"do_1121912573615472641169\",\"name\": \"A\",\"objectType\": \"Content\",\"relation\": \"hasSequenceMember\",\"description\": \"A.\",\"index\": null}]");
		map.put( "name","Test Content 1");
		map.put("artifactUrl","https://ekstep-public-dev.s3-ap-south-1.amazonaws.com/content/do_112228048362078208130/artifact/advancedenglishassessment1_1533_1489654074_1489653812104_1492681721669.zip");
		map.put( "lastUpdatedOn", "2017-05-24T17:26:49.112+0000");
		map.put("contentType","Story");
		map.put("status","Live");
		return map;
	}
}
