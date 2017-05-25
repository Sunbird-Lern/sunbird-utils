package org.sunbird.cassandra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.cassandraimpl.CassandraOperationImpl;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.helper.CassandraConnectionManager;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CassandraTest {
	
	static CassandraOperation operation= new CassandraOperationImpl();
	static Map<String,Object> contentmap = null;
	static Map<String,Object> coursemap = null;
	static Map<String,Object> contentmap1 = null;
	static Map<String,Object> coursemap1 = null;
	
	@BeforeClass
	public static void init(){
		
		CassandraConnectionManager.createConnection("127.0.0.1", "9042", "cassandra", "password", "cassandraKeySpace");
		
		contentmap = new HashMap<>();
		coursemap = new HashMap<>();
		coursemap1 = new HashMap<>();
		contentmap1 = new HashMap<>();
		
		contentmap.put("contentId", "contentId1");
   		contentmap.put("viewCount", "viewCount 1");
   		contentmap.put("completedCount", "completedCount1");
   		contentmap.put("status", 1);
   		contentmap.put("userId", "userId2");
   		contentmap.put("lastUpdatedTime", "2017-05-15 10:58:07:509+0530" );
   		contentmap.put("lastAccessTime", "2017-05-15 10:58:07:509+0530");
   		contentmap.put("lastCompletedTime", "2017-05-15 10:58:07:509+0530");
   		contentmap.put("viewPosition", "viewPosition 1");
   		contentmap.put("id", "contentId1##userId2");
   		
   		coursemap.put("courseName", "courseName1");
   		coursemap.put("userId", "userId2");
   		coursemap.put("courseId", "courseId2");
   		coursemap.put("enrolledDate", "2017-05-15 10:58:07:509+0530");
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
   		contentmap1.put("status", "1");
   		contentmap1.put("userId", "userId2");
   		
   	}
	
	@Test
	public void testConnection() {
		boolean bool= CassandraConnectionManager.createConnection("127.0.0.1", "9042", "cassandra", "password", "cassandraKeySpace");
		assertEquals(true,bool);
	}
	
	@Test(expected=ProjectCommonException.class)
	public void testAFailedConnection() {
		CassandraConnectionManager.createConnection("127.0.0.1", "9042", "cassandra", "pass", "eySpace");
	}
	
	@Test(expected=ProjectCommonException.class)
	public void testFailedSessionCheck() {
		CassandraConnectionManager.getSession("Keyspace");
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
	
	@Test(expected=ProjectCommonException.class)
	public void testFailedInsertion() {
		operation.insertRecord("keySpace", "content_consumption", contentmap);
	}
	@Test(expected=ProjectCommonException.class)
	public void testFailedGetRecordById() {
		operation.getRecordById("keySpace", "course_enrollment", "courseId2##userId2");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testBGetCourseById() {
		Response response=operation.getRecordById("cassandraKeySpace", "course_enrollment", "courseId2##userId2");
		assertEquals(1,((List<Map<String, Object>>)(response.getResult().get("response"))).size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testBGetContentById() {
		Response response=operation.getRecordById("cassandraKeySpace", "content_consumption", "contentId1##userId2");
		assertEquals(1,((List<Map<String, Object>>)(response.getResult().get("response"))).size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCgetCourseByProperty() {
		Response response=operation.getRecordsByProperty("cassandraKeySpace", "course_enrollment", "userId", "userId2");
		assertTrue(((List<Map<String, Object>>)(response.getResult().get("response"))).size()>0);
	}
	
	@Test(expected=ProjectCommonException.class)
	public void testFailedgetRecordByProperty() {
		operation.getRecordsByProperty("keySpace", "course", "userId", "userId2");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCgetContentByProperty() {
		Response response=operation.getRecordsByProperty("cassandraKeySpace", "content_consumption", "userId", "userId2");
		assertTrue(((List<Map<String, Object>>)(response.getResult().get("response"))).size()>0);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCgetCourseByPropertyFrInClause() {
		List<Object> list = new ArrayList<>();
		list.add("courseId2##userId2");
		list.add("courseId2##userId2");
		list.add("courseId2##userId2");
		Response response=operation.getRecordsByProperty("cassandraKeySpace", "course_enrollment", "id", list);
		assertTrue(((List<Map<String, Object>>)(response.getResult().get("response"))).size()>0);
	}
	
	@Test(expected=ProjectCommonException.class)
	public void testCFailedgetRecordByPropertyFrInClause() {
		List<Object> list = new ArrayList<>();
		list.add("courseId2##userId2");
		list.add("courseId2##userId2");
		list.add("courseId2##userId2");
		operation.getRecordsByProperty("eySpace", "course_enrollment", "id", list);
	}
	
	@Test
	public void testCUpdateCourseById() {
		coursemap.put("delta", "delta as json string updated");
		operation.updateRecord("cassandraKeySpace", "course_enrollment", coursemap);
		Response response=operation.getRecordById("cassandraKeySpace", "course_enrollment", "courseId2##userId2");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result =  (List<Map<String, Object>>)response.getResult().get("response");
		Map<String, Object> map = result.get(0);
		assertTrue(((String)map.get("delta")).equalsIgnoreCase("delta as json string updated"));
	}
	
	@Test(expected=ProjectCommonException.class)
	public void testCFailedUpdateById() {
		coursemap.put("delta", "delta as json string updated");
		operation.updateRecord("eySpace", "course_enrollment", coursemap);
	}
	
	@Test
	public void testCUpdateContentById() {
		contentmap.put("viewPosition", "viewPosition 1 updated");
		operation.updateRecord("cassandraKeySpace", "content_consumption", contentmap);
		Response response=operation.getRecordById("cassandraKeySpace", "content_consumption", "contentId1##userId2");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result =  (List<Map<String, Object>>)response.getResult().get("response");
		Map<String, Object> map = result.get(0);
		assertTrue(((String)map.get("viewPosition")).equalsIgnoreCase("viewPosition 1 updated"));
	}
	@Test
	public void testDeleteContent() {
		Response response=operation.deleteRecord("cassandraKeySpace", "content_consumption", "contentId1##userId2");
		assertEquals("SUCCESS", response.get("response"));
	}
	@Test(expected=ProjectCommonException.class)
	public void testFailedDeleteContent() {
		Response response=operation.deleteRecord("keySpace", "content_consumption", "contentId1##userId2");
		assertEquals("SUCCESS", response.get("response"));
	}
	
	@Test
	public void testDeleteCourse() {
		Response response=operation.deleteRecord("cassandraKeySpace", "course_enrollment", "courseId2##userId2");
		assertEquals("SUCCESS", response.get("response"));
	}

	/*//@Test
	@SuppressWarnings("unchecked")
	public void testCgetRecordContentByProperties() {
		Response response=operation.getRecordsByProperties("cassandraKeySpace", "content_consumption",contentmap1);
		assertTrue(((List<Map<String, Object>>)(response.get("response"))).size()>0);
	}
	//@Test
	@SuppressWarnings("unchecked")
	public void testCgetRecordCourseByProperties() {
		Response response=operation.getRecordsByProperties("cassandraKeySpace", "course_enrollment", coursemap1);
		assertTrue(((List<Map<String, Object>>)(response.get("response"))).size()>0);
	}*/
	
	@AfterClass
	public static void shutdownhook() {
		CassandraConnectionManager.shutdownhook();
		contentmap = null;
		coursemap = null;
		contentmap1 = null;
		coursemap1 = null;
    }

}

