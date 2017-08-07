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
	static Map<String,Object> address = null;
	static Map<String,Object> dummyAddress = null;
	static PropertiesCache cach = PropertiesCache.getInstance();
	
	@BeforeClass
	public static void init(){
	  
	  address = new HashMap<>();
	  address.put(JsonKey.ID,"123");
	  address.put(JsonKey.ADDRESS_LINE1, "Line 1");
	  address.put(JsonKey.USER_ID, "USR1");
	  
	  dummyAddress = new HashMap<>();
	  dummyAddress.put(JsonKey.ID,"12345");
	  dummyAddress.put(JsonKey.ADDRESS_LINE1, "Line 111");
	  dummyAddress.put(JsonKey.USER_ID, "USR111");
	  dummyAddress.put("DummyColumn", "USR111");
	  
	  CassandraConnectionManager.createConnection(cach.getProperty("contactPoint"), cach.getProperty("port"), "cassandra", "password", cach.getProperty("keyspace"));
   	}
	
	@Test
	public void testConnectionWithoutUserNameAndPassword() {
		boolean bool= CassandraConnectionManager.createConnection(cach.getProperty("contactPoint"), cach.getProperty("port"), null, null, cach.getProperty("keyspace"));
		assertEquals(true,bool);
	}
	
	@Test
    public void testConnection() {
        boolean bool= CassandraConnectionManager.createConnection(cach.getProperty("contactPoint"), cach.getProperty("port"), "cassandra", "password", cach.getProperty("keyspace"));
        assertEquals(true,bool);
    }
	
	@Test(expected=ProjectCommonException.class)
    public void testFailedConnection() {
        CassandraConnectionManager.createConnection("127.0.0.1", "9042", "cassandra", "pass", "eySpace");
    }
	
    
    @Test(expected=ProjectCommonException.class)
    public void testFailedSessionCheck() {
        CassandraConnectionManager.getSession("Keyspace");
    }
    
    @Test
    public void testAInsertOp() {
      Response response=operation.insertRecord(cach.getProperty("keyspace"), "address", address);
      assertEquals("SUCCESS", response.get("response"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testAInsertFailedOp() {
      operation.insertRecord(cach.getProperty("keyspace"), "address1", address);
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testAInsertFailedOpWithInvalidProperty() {
      operation.insertRecord(cach.getProperty("keyspace"), "address", dummyAddress);
    }
       
    @Test
    public void testBUpdateOp() {
      address.put(JsonKey.CITY, "city");
      address.put(JsonKey.ADD_TYPE, "addrType");
      Response response=operation.updateRecord(cach.getProperty("keyspace"), "address", address);
      assertEquals("SUCCESS", response.get("response"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testBUpdateFailedOp() {
      dummyAddress.put(JsonKey.CITY, "city");
      dummyAddress.put(JsonKey.ADD_TYPE, "addrType");
      operation.updateRecord(cach.getProperty("keyspace"), "address1", address);
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testBUpdateFailedOpWithInvalidProperty() {
      dummyAddress.put(JsonKey.CITY, "city");
      dummyAddress.put(JsonKey.ADD_TYPE, "addrType");
      operation.updateRecord(cach.getProperty("keyspace"), "address", dummyAddress);
    }
    
    @Test
    public void testBgetAllRecordsOp() {
      Response response=operation.getAllRecords(cach.getProperty("keyspace"), "address");
      assertTrue(((List<?>)response.get("response")).size()>0);
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testBgetAllRecordsFailedOp() {
      operation.getAllRecords(cach.getProperty("keyspace"), "Dummy Table Name");
    }
    
    @Test
    public void testCgetPropertiesValueByIdOp() {
      Response response=operation.getPropertiesValueById(cach.getProperty("keyspace"), "address", "123", JsonKey.ID,JsonKey.CITY,JsonKey.ADD_TYPE);
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.ID)).equals("123"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testCgetPropertiesValueByIdFailedOp() {
      operation.getPropertiesValueById(cach.getProperty("keyspace"), "address", "123", "Dummy Column");
    }
    
    @Test
    public void testDgetRecordByIdOp() {
      Response response=operation.getRecordById(cach.getProperty("keyspace"), "address", "123");
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.CITY)).equals("city"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testDgetRecordByIdFailedOp() {
      operation.getRecordById(cach.getProperty("keyspace"), "Dummy Table Name", "12345");
    }
    
    @Test
    public void testFgetRecordsByPropertiesOp() {
      Map<String,Object> map = new HashMap<>();
      map.put(JsonKey.USER_ID, "USR1");
      map.put(JsonKey.ADD_TYPE, "addrType");
      Response response=operation.getRecordsByProperties(cach.getProperty("keyspace"), "address", map);
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.ID)).equals("123"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testFgetRecordsByPropertiesFailed2Op() {
      Map<String,Object> map = new HashMap<>();
      map.put(JsonKey.ADDRESS_TYPE, "add");
      map.put(JsonKey.ADDRESS_LINE1, "line1");
      List<String> list= new ArrayList<>();
      list.add("USR1");
      map.put("dummy", list);
      operation.getRecordsByProperties(cach.getProperty("keyspace"), "address", map);
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testFgetRecordsByPropertiesFailedOp() {
      Map<String,Object> map = new HashMap<>();
      map.put(JsonKey.ADDRESS_TYPE, "add");
      map.put(JsonKey.ADDRESS_LINE1, "line1");
      operation.getRecordsByProperties(cach.getProperty("keyspace"), "address", map);
    }
    
    @Test
    public void testFgetRecordsByPropertyFrListOp() {
      List<Object> list = new ArrayList<>();
      list.add("123");
      list.add("321");
      Response response=operation.getRecordsByProperty(cach.getProperty("keyspace"), "address", JsonKey.ID, list);
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.ID)).equals("123"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testFgetRecordsByPropertyFrListFailedOp() {
      List<Object> list = new ArrayList<>();
      list.add("123");
      list.add("321");
      operation.getRecordsByProperty(cach.getProperty("keyspace"), "address", JsonKey.ADD_TYPE, list);
    }
    
    @Test
    public void testFgetRecordsByPropertyOp() {
      Response response=operation.getRecordsByProperty(cach.getProperty("keyspace"), "address", JsonKey.ADD_TYPE, "addrType");
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.ID)).equals("123"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testFgetRecordsByPropertyFailedOp() {
      operation.getRecordsByProperty(cach.getProperty("keyspace"), "address", JsonKey.ADDRESS_LINE1, "Line1");
    }
    
    @Test
    public void testGgetRecordByIdOp() {
      Response response=operation.getRecordById(cach.getProperty("keyspace"), "address", "123");
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.CITY)).equals("city"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testGgetRecordByIdOpFailed() {
      operation.getRecordById(cach.getProperty("keyspace"), "address1", "123");
    }
    
    @Test
    public void testHUpsertOp() {
      address.put("Country", "country");
      Response response=operation.upsertRecord(cach.getProperty("keyspace"), "address", address);
      assertEquals("SUCCESS", response.get("response"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testHUpsertOpFailed() {
      address.put("Country", "country");
      Response response=operation.upsertRecord(cach.getProperty("keyspace"), "address1", address);
      assertEquals("SUCCESS", response.get("response"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testHUpsertOpFailedWithInvalidParameter() {
      //address.put("Country", "country");
      Response response=operation.upsertRecord(cach.getProperty("keyspace"), "address", dummyAddress);
      //assertEquals("SUCCESS", response.get("response"));
    }
    
    @Test
    public void testZDeleteOp() {
      Response response=operation.deleteRecord(cach.getProperty("keyspace"), "address", "123");
      assertEquals("SUCCESS", response.get("response"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testZDeleteFailedOp() {
      operation.deleteRecord(cach.getProperty("keyspace"), "address1", "123");
    }
    
    @Test
    public void testZaDeleteFailedOp() {
      CassandraConnectionManager.createConnection(cach.getProperty("contactPoint"), cach.getProperty("port"), null, null, cach.getProperty("keyspace"));
    }
	
	@AfterClass
	public static void shutdownhook() {
		CassandraConnectionManager.registerShutDownHook();
		address = null;
    }

}

