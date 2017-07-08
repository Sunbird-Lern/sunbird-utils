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
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.helper.CassandraConnectionManager;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CassandraTest {
	
	static CassandraOperation operation= new CassandraOperationImpl();
	static Map<String,Object> contentmap = null;
	static Map<String,Object> coursemap = null;
	static Map<String,Object> contentmap1 = null;
	static Map<String,Object> coursemap1 = null;
	static PropertiesCache cach = PropertiesCache.getInstance();
	@BeforeClass
	public static void init(){
		
		CassandraConnectionManager.createConnection(cach.getProperty("contactPoint"), cach.getProperty("port"), cach.getProperty("userName"), cach.getProperty("password"), cach.getProperty("keyspace"));
		
		contentmap = new HashMap<>();
		coursemap = new HashMap<>();
		coursemap1 = new HashMap<>();
		contentmap1 = new HashMap<>();
		
		contentmap.put("contentId", "contentId1");
   		contentmap.put("viewCount", 1);
   		contentmap.put("completedCount", 1);
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
   		coursemap.put("batchId", "1");
   		coursemap.put("enrolledDate", "2017-05-15 10:58:07:509+0530");
   		coursemap.put("description", "description");
   		coursemap.put("tocUrl", "tocUrl");
   		coursemap.put("status", 1);
   		coursemap.put("active", true);
   		coursemap.put("delta", "delta as json string");
   		coursemap.put("id", "courseId2##userId2");
   		
   		coursemap1.put("courseName", "courseName1");
   		coursemap1.put("courseId", "courseId2"); 
   		coursemap1.put("userId", "userId2");
   		coursemap1.put("batchId", "1");
   		
   		contentmap1.put("contentId", "contentId1");
   		contentmap1.put("status", 1);
   		contentmap1.put("userId", "userId2");
   		
   	}
	
	@Test
	public void testConnection() {
		boolean bool= CassandraConnectionManager.createConnection(cach.getProperty("contactPoint"), cach.getProperty("port"), cach.getProperty("userName"), cach.getProperty("password"), cach.getProperty("keyspace"));
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
		Response response=operation.insertRecord(cach.getProperty("keyspace"), "user_courses", coursemap);
    	assertEquals("SUCCESS", response.get("response"));
	}
	
	@Test(expected=ProjectCommonException.class)
	public void testACourseInsertionWithSameId() {
		operation.insertRecord(cach.getProperty("keyspace"), "user_courses", coursemap);
	}
	
	
	@Test
	public void testAContentInsertion() {
		Response response=operation.insertRecord(cach.getProperty("keyspace"), "content_consumption", contentmap);
    	assertEquals("SUCCESS", response.get("response"));
	}
	
	@Test(expected=ProjectCommonException.class)
	public void testFailedInsertion() {
		operation.insertRecord("keySpace", "content_consumption", contentmap);
	}
	@Test(expected=ProjectCommonException.class)
	public void testFailedGetRecordById() {
		operation.getRecordById("keySpace", "user_courses", "courseId2##userId2");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testBGetCourseById() {
		Response response=operation.getRecordById(cach.getProperty("keyspace"), "user_courses", "courseId2##userId2");
		assertEquals(1,((List<Map<String, Object>>)(response.getResult().get("response"))).size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testBGetAllCourse() {
		Response response=operation.getAllRecords(cach.getProperty("keyspace"), "user_courses");
		assertTrue(((List<Map<String, Object>>)(response.getResult().get("response"))).size() > 0);
	}
	@Test(expected=ProjectCommonException.class)
	public void testBGetAllCourseWithException() {
		operation.getAllRecords(cach.getProperty("keyspace"), "course");
	}
	
	@Test
	public void testBgetPropertiesValueById() {
		Response response=operation.getPropertiesValueById(cach.getProperty("keyspace"), "user_courses", "courseId2##userId2","courseId","delta");
		assertTrue(response!=null);
	}
	@Test(expected=ProjectCommonException.class)
	public void testBgetPropertiesValueByIdWithException() {
		operation.getPropertiesValueById(cach.getProperty("keyspace"), "user_courses", "courseId2##userId2","unknownColumn");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testBGetContentById() {
		Response response=operation.getRecordById(cach.getProperty("keyspace"), "content_consumption", "contentId1##userId2");
		assertEquals(1,((List<Map<String, Object>>)(response.getResult().get("response"))).size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCgetCourseByProperty() {
		Response response=operation.getRecordsByProperty(cach.getProperty("keyspace"), "user_courses", "userId", "userId2");
		assertTrue(((List<Map<String, Object>>)(response.getResult().get("response"))).size()>0);
	}
	
	@Test(expected=ProjectCommonException.class)
	public void testFailedgetRecordByProperty() {
		operation.getRecordsByProperty("keySpace", "course", "userId", "userId2");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCgetContentByProperty() {
		Response response=operation.getRecordsByProperty(cach.getProperty("keyspace"), "content_consumption", "userId", "userId2");
		assertTrue(((List<Map<String, Object>>)(response.getResult().get("response"))).size()>0);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCgetCourseByPropertyFrInClause() {
		List<Object> list = new ArrayList<>();
		list.add("courseId2##userId2");
		list.add("courseId2##userId2");
		list.add("courseId2##userId2");
		Response response=operation.getRecordsByProperty(cach.getProperty("keyspace"), "user_courses", "id", list);
		assertTrue(((List<Map<String, Object>>)(response.getResult().get("response"))).size()>0);
	}
	
	@Test(expected=ProjectCommonException.class)
	public void testCFailedgetRecordByPropertyFrInClause() {
		List<Object> list = new ArrayList<>();
		list.add("courseId2##userId2");
		list.add("courseId2##userId2");
		list.add("courseId2##userId2");
		operation.getRecordsByProperty("eySpace", "user_courses", "id", list);
	}
	
	@Test
	public void testCUpdateCourseById() {
		coursemap.put("delta", "delta as json string updated");
		operation.upsertRecord(cach.getProperty("keyspace"), "user_courses", coursemap);
		Response response=operation.getRecordById(cach.getProperty("keyspace"), "user_courses", "courseId2##userId2");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result =  (List<Map<String, Object>>)response.getResult().get("response");
		Map<String, Object> map = result.get(0);
		assertTrue(((String)map.get("delta")).equalsIgnoreCase("delta as json string updated"));
	}
	
	
	@Test(expected=ProjectCommonException.class)
	public void testCUpdateCourseByIdWithException() {
		coursemap.put("delta", "delta as json string updated");
		operation.upsertRecord(cach.getProperty("keyspace"), "course", coursemap);
	}

	@Test(expected=ProjectCommonException.class)
	public void testCUpdateCourseByIdWithIncorrectId() {
		coursemap.put("delta", "delta as json string updated");
		HashMap<String, Object> map = new HashMap<>();
		map.putAll(coursemap);
		map.put("id", "courseId2");
		operation.updateRecord(cach.getProperty("keyspace"), "user_courses", map);
	}
	
	@Test(expected=ProjectCommonException.class)
	public void testCFailedUpdateById() {
		coursemap.put("delta", "delta as json string updated");
		operation.updateRecord("eySpace", "user_courses", coursemap);
	}
	
	@Test
	public void testCUpdateContentById() {
		contentmap.put("viewPosition", "viewPosition 1 updated");
		operation.updateRecord(cach.getProperty("keyspace"), "content_consumption", contentmap);
		Response response=operation.getRecordById(cach.getProperty("keyspace"), "content_consumption", "contentId1##userId2");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result =  (List<Map<String, Object>>)response.getResult().get("response");
		Map<String, Object> map = result.get(0);
		assertTrue(((String)map.get("viewPosition")).equalsIgnoreCase("viewPosition 1 updated"));
	}
	@Test
	public void testZDeleteContent() {
		Response response=operation.deleteRecord(cach.getProperty("keyspace"), "content_consumption", "contentId1##userId2");
		assertEquals("SUCCESS", response.get("response"));
	}
	@Test(expected=ProjectCommonException.class)
	public void testFailedDeleteContent() {
		Response response=operation.deleteRecord("keySpace", "content_consumption", "contentId1##userId2");
		assertEquals("SUCCESS", response.get("response"));
	}
	
	@Test
	public void testZDeleteCourse() {
		Response response=operation.deleteRecord(cach.getProperty("keyspace"), "user_courses", "courseId2##userId2");
		assertEquals("SUCCESS", response.get("response"));
	}

	//@Test
	@SuppressWarnings("unchecked")
	public void testCgetRecordContentByProperties() {
		Map<String,Object> map = new HashMap<>();
		map.put(JsonKey.USER_ID, "f660ab912ec121d1b1e928a0bb4bc61b15f5ad44d5efdc4e1c92a25e99b8e44a");
		map.put(JsonKey.COURSE_ID, "0122739033854443521");
		Response response=operation.getRecordsByProperties(cach.getProperty("keyspace"), "content_consumption",map);
		assertTrue(((List<Map<String, Object>>)(response.get("response"))).size()>0);
	}
	@Test
	@SuppressWarnings("unchecked")
	public void testCgetRecordCourseByProperties() {
		Response response=operation.getRecordsByProperties(cach.getProperty("keyspace"), "user_courses", coursemap1);
		assertTrue(((List<Map<String, Object>>)(response.get("response"))).size()>0);
	}
	@Test(expected=ProjectCommonException.class)
	@SuppressWarnings("unchecked")
	public void testCgetRecordCourseByPropertiesFailureCase() {
		Response response=operation.getRecordsByProperties("keySpace", "user_courses", coursemap1);
		assertTrue(((List<Map<String, Object>>)(response.get("response"))).size()>0);
	}
	
	@AfterClass
	public static void shutdownhook() {
		CassandraConnectionManager.registerShutDownHook();
		contentmap = null;
		coursemap = null;
		contentmap1 = null;
		coursemap1 = null;
    }

}

