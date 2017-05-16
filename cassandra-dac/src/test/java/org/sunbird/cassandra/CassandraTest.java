package org.sunbird.cassandra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.cassandraimpl.CassandraOperationImpl;
import org.sunbird.common.models.response.Response;
import org.sunbird.helper.CassandraConnectionManager;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CassandraTest {
	
	/*static CassandraOperation operation= new CassandraOperationImpl();
	static Map<String,Object> contentmap = null;
	static Map<String,Object> coursemap = null;
	static Map<String,Object> contentmap1 = null;
	static Map<String,Object> coursemap1 = null;
	static Map<String,Object> contentmap2 = null;
	static Map<String,Object> coursemap2 = null;
	@Before
	public void init(){
		contentmap = new HashMap<>();
		coursemap = new HashMap<>();
		coursemap1 = new HashMap<>();
		contentmap1 = new HashMap<>();
		contentmap2 = new HashMap<>();
		coursemap2 = new HashMap<>();
		
		contentmap.put("contentId", "contentId1");
   		contentmap.put("viewCount", "viewCount 1");
   		contentmap.put("completedCount", "completedCount1");
   		contentmap.put("status", 1);
   		contentmap.put("userId", "userId2");
   		contentmap.put("lastUpdatedTime", new Timestamp(System.currentTimeMillis()) );
   		contentmap.put("lastAccessTime", new Timestamp(System.currentTimeMillis()));
   		contentmap.put("lastCompletedTime", new Timestamp(System.currentTimeMillis()));
   		contentmap.put("viewPosition", "viewPosition 1");
   		contentmap.put("id", "contentId1##userId2");
   		
   		coursemap.put("courseName", "courseName1");
   		coursemap.put("userId", "userId2");
   		coursemap.put("courseId", "courseId2");
   		coursemap.put("enrolledDate", new Timestamp(System.currentTimeMillis()));
   		coursemap.put("description", "description");
   		coursemap.put("tocUrl", "tocUrl");
   		coursemap.put("status", "1");
   		coursemap.put("active", true);
   		coursemap.put("delta", "delta as json string");
   		coursemap.put("id", "courseId2##userId2");
   		
   		coursemap1.put("courseName", "courseName1");
   		coursemap1.put("courseId", "courseId2"); 
   		coursemap1.put("userId", "userId2");
   		
   		contentmap1.put("contentId", "contentId1");
   		contentmap1.put("status", 1);
   		contentmap1.put("userId", "userId2");
   		
   		coursemap2.put("delta", "delta as json string updated");
   		coursemap2.put("tocUrl", "tocUrl updated");
   		
   		contentmap2.put("viewPosition", "viewPosition 1 updated");
   		contentmap2.put("completedCount", "completedCount1 updated");
   		
   	}
	
	@Before
	public void testConnection() {
		boolean bool= CassandraConnectionManager.createConnection("127.0.0.1", "9042", "cassandra", "password", "cassandraKeySpace");
		assertEquals(true,bool);
	}
	@Test
	public void testACourseInsertion() {
		Response response=operation.insertRecord("cassandraKeySpace", "course_enrollment", coursemap);
    	assertEquals("SUCCESS", response.get("response"));
	}
	@Test
	public void testAContentInsertion() {
		Response response=operation.insertRecord("cassandraKeySpace", "content_consumption", contentmap);
    	assertEquals("SUCCESS", response.get("response"));
	}
	
	@Test
	public void testBGetRecordCourseById() {
		Response response=operation.getRecordById("cassandraKeySpace", "course_enrollment", "courseId2##userId2");
		assertEquals(1,((List<Map<String, Object>>)(response.get("response"))).size());
	}
	
	//@Test
	public void testBGetRecordContentById() {
		Response response=operation.getRecordById("cassandraKeySpace", "content_consumption", "contentId1##userId2");
		assertEquals(1,((List<Map<String, Object>>)(response.get("response"))).size());
	}
	
	@Test
	public void testCgetRecordCourseByProperty() {
		Response response=operation.getRecordsByProperty("cassandraKeySpace", "course_enrollment", "userId", "userId2");
		assertTrue(((List<Map<String, Object>>)(response.get("response"))).size()>0);
	}*/
/*	
	//@Test
	public void testCgetRecordContentByProperty() {
		Response response=operation.getRecordsByProperty("cassandraKeySpace", "content_consumption", "userId", "userId2");
		assertTrue(((List<Map<String, Object>>)(response.get("response"))).size()>0);
	}
	
	//@Test
	public void testCCgetRecordCourseByProperty() {
		List<Object> list = new ArrayList<>();
		list.add("courseId1");
		list.add("courseId2");
		list.add("courseId3");
		//Response response=operation.getRecordsByProperty("cassandraKeySpace", "course_enrollment", "userId", list);
		Response response=operation.getRecordsByProperty("cassandraKeySpace", "course_enrollment", "courseid", list);
		assertTrue(((List<Map<String, Object>>)(response.get("response"))).size()>0);
	}
	
	//@Test
	public void testCgetRecordContentByProperties() {
		Response response=operation.getRecordsByProperties("cassandraKeySpace", "content_consumption",contentmap1);
		assertTrue(((List<Map<String, Object>>)(response.get("response"))).size()>0);
	}
	//@Test
	public void testCgetRecordCourseByProperties() {
		Response response=operation.getRecordsByProperties("cassandraKeySpace", "course_enrollment", coursemap1);
		assertTrue(((List<Map<String, Object>>)(response.get("response"))).size()>0);
	}
	//@Test
	public void testCUpdateCourseById() {
		operation.updateRecord("cassandraKeySpace", "course_enrollment", coursemap2, "courseId2##userId2");
		Response response=operation.getRecordById("cassandraKeySpace", "course_enrollment", "courseId2##userId2");
		List<Map<String, Object>> result =  (List<Map<String, Object>>)response.get("response");
		Map<String, Object> map = result.get(0);
		assertTrue(((String)map.get("delta")).equalsIgnoreCase("delta as json string updated"));
	}
	//@Test
	public void testCUpdateContentById() {
		operation.updateRecord("cassandraKeySpace", "content_consumption", contentmap2, "contentId1##userId2");
		Response response=operation.getRecordById("cassandraKeySpace", "content_consumption", "contentId1##userId2");
		List<Map<String, Object>> result =  (List<Map<String, Object>>)response.get("response");
		Map<String, Object> map = result.get(0);
		assertTrue(((String)map.get("viewPosition")).equalsIgnoreCase("viewPosition 1 updated"));
	}
	//@Test
	public void testDeleteContent() {
		Response response=operation.deleteRecord("cassandraKeySpace", "content_consumption", "contentId1##userId2");
		assertEquals("SUCCESS", response.get("response"));
	}
	
	//@Test
	public void testDeleteCourse() {
		Response response=operation.deleteRecord("cassandraKeySpace", "course_enrollment", "courseId2##userId2");
		assertEquals("SUCCESS", response.get("response"));
	}
	
	//@AfterClass
	public static void shutdownhook() {
		CassandraConnectionManager.shutdownhook();
		contentmap = null;
		coursemap = null;
		contentmap1 = null;
		coursemap1 = null;
    }
*/
}

