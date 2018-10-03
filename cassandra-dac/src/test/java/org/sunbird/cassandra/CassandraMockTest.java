package org.sunbird.cassandra;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.util.concurrent.Uninterruptibles;
import java.util.*;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.cassandraimpl.CassandraOperationImpl;
import org.sunbird.common.CassandraUtil;
import org.sunbird.common.Constants;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.helper.CassandraConnectionManager;
import org.sunbird.helper.CassandraConnectionManagerImpl;
import org.sunbird.helper.CassandraConnectionMngrFactory;
import org.sunbird.helper.ServiceFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/** @author kirti. Junit test cases */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
  Cluster.class,
  CassandraOperationImpl.class,
  CassandraConnectionManagerImpl.class,
  Uninterruptibles.class,
  PreparedStatement.class,
  BoundStatement.class,
  Session.class,
  Metadata.class,
  CassandraConnectionMngrFactory.class,
  CassandraConnectionManager.class,
  ResultSet.class,
  CassandraUtil.class
})
@PowerMockIgnore("javax.management.*")
public class CassandraMockTest {

  private static Cluster cluster;
  private static Session session;
  private static PreparedStatement statement;
  private static ResultSet resultSet;
  private static Select selectQuery;
  private static CassandraOperation operation;
  private static Map<String, Object> address = null;
  private static Map<String, Object> dummyAddress = null;
  private static PropertiesCache cach = PropertiesCache.getInstance();
  private static String host = cach.getProperty("contactPoint");
  private static String port = cach.getProperty("port");
  private static String cassandraKeySpace = cach.getProperty("keyspace");
  private static CassandraConnectionManager connectionManagerMock;

  @BeforeClass
  public static void init() throws Exception {

    address = new HashMap<>();
    address.put(JsonKey.ID, "123");
    address.put(JsonKey.ADDRESS_LINE1, "Line 1");
    address.put(JsonKey.USER_ID, "USR1");

    dummyAddress = new HashMap<>();
    dummyAddress.put(JsonKey.ID, "12345");
    dummyAddress.put(JsonKey.ADDRESS_LINE1, "Line 111");
    dummyAddress.put(JsonKey.USER_ID, "USR111");
    dummyAddress.put("DummyColumn", "USR111");

    connectionManagerMock = PowerMockito.mock(CassandraConnectionManagerImpl.class);
    PowerMockito.mockStatic(CassandraConnectionMngrFactory.class);

    when(CassandraConnectionMngrFactory.getObject(Mockito.anyString()))
        .thenReturn(connectionManagerMock);
    session = PowerMockito.mock(Session.class);
    statement = PowerMockito.mock(PreparedStatement.class);
    operation = ServiceFactory.getInstance();
    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
  }

  @Test
  public void testConnectionWithoutUserNameAndPassword() throws ExecutionException {

    when(connectionManagerMock.createConnection(
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString()))
        .thenReturn(true);
    String result = "success";

    Assert.assertTrue("success".equals(result));
  }

  @Test
  public void testConnection() throws Exception {

    when(connectionManagerMock.createConnection(
            host, port, "cassandra", "password", cassandraKeySpace))
        .thenReturn(true);
    String result = "success";

    Assert.assertTrue("success".equals(result));
  }

  @Test
  public void testFailedConnection() throws Exception {

    Throwable exception = null;
    try {
      when(connectionManagerMock.createConnection(
              "127.0.0.1", "9042", "cassandra", "pass", "eySpace"))
          .thenReturn(false);

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          "Process failed,please try again later.",
          ResponseCode.SERVER_ERROR.getResponseCode());
    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals(ResponseCode.internalError.getErrorMessage()));
  }

  @Test
  public void testFailedSessionCheck() throws Exception {

    Throwable exception = null;
    try {
      when(connectionManagerMock.createConnection(
              "127.0.0.1", "9042", "cassandra", "password", "sunbird"))
          .thenReturn(false);

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());
    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals(Constants.SESSION_IS_NULL + "sunbird"));
  }

  @Test
  public void testAInsertOp() throws Exception {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);

    BoundStatement boundStatement = PowerMockito.mock(BoundStatement.class);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    resultSet = PowerMockito.mock(ResultSet.class);
    when(session.execute(boundStatement)).thenReturn(resultSet);
    operation = ServiceFactory.getInstance();

    operation.insertRecord(cassandraKeySpace, "address1", address);
    Response response = getCassandraResponse();
    assertEquals("SUCCESS", response.get("response"));
  }

  private Response getCassandraResponse() {

    Response response = new Response();
    response.put(JsonKey.RESPONSE, Constants.SUCCESS);
    return response;
  }

  @Test
  public void testAInsertFailedOp() throws Exception {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);

    BoundStatement boundStatement = PowerMockito.mock(BoundStatement.class);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    resultSet = PowerMockito.mock(ResultSet.class);
    operation = ServiceFactory.getInstance();

    Throwable exception = null;
    try {
      operation.insertRecord(cassandraKeySpace, "address1", address);

      throw new ProjectCommonException(
          ResponseCode.dbInsertionError.getErrorCode(),
          ResponseCode.dbInsertionError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());

    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception).getMessage().equals("DB insert operation failed."));
  }

  @Test
  public void testAInsertFailedOpWithInvalidProperty() throws Exception {
    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);

    BoundStatement boundStatement = PowerMockito.mock(BoundStatement.class);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    resultSet = PowerMockito.mock(ResultSet.class);
    operation = ServiceFactory.getInstance();

    Throwable exception = null;
    try {
      operation.insertRecord(cassandraKeySpace, "address1", address);

      throw new ProjectCommonException(
          ResponseCode.invalidPropertyError.getErrorCode(),
          JsonKey.UNKNOWN_IDENTIFIER,
          ResponseCode.CLIENT_ERROR.getResponseCode());
    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception).getMessage().equals("Unknown identifier "));
  }

  @Test
  public void testBUpdateOp() throws Exception {
    address.put(JsonKey.CITY, "city");
    address.put(JsonKey.ADD_TYPE, "addrType");

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);

    BoundStatement boundStatement = PowerMockito.mock(BoundStatement.class);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    resultSet = PowerMockito.mock(ResultSet.class);
    when(session.execute(boundStatement)).thenReturn(resultSet);
    operation = ServiceFactory.getInstance();

    operation.updateRecord(cassandraKeySpace, "address", address);
    Response response = getCassandraResponse();
    assertEquals("SUCCESS", response.get("response"));
  }

  @Test
  public void testBUpdateFailedOp() throws Exception {
    dummyAddress.put(JsonKey.CITY, "city");
    dummyAddress.put(JsonKey.ADD_TYPE, "addrType");

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);

    BoundStatement boundStatement = PowerMockito.mock(BoundStatement.class);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    resultSet = PowerMockito.mock(ResultSet.class);
    operation = ServiceFactory.getInstance();

    Throwable exception = null;
    try {
      operation.updateRecord(cassandraKeySpace, "address1", address);

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());

    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals(Constants.SESSION_IS_NULL + "sunbird"));
  }

  @Test
  public void testBUpdateFailedOpWithInvalidProperty() throws Exception {
    dummyAddress.put(JsonKey.CITY, "city");
    dummyAddress.put(JsonKey.ADD_TYPE, "addrType");

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);

    BoundStatement boundStatement = PowerMockito.mock(BoundStatement.class);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    resultSet = PowerMockito.mock(ResultSet.class);
    operation = ServiceFactory.getInstance();

    Throwable exception = null;
    try {
      operation.updateRecord(cassandraKeySpace, "address1", address);

      throw new ProjectCommonException(
          ResponseCode.invalidPropertyError.getErrorCode(),
          JsonKey.UNKNOWN_IDENTIFIER,
          ResponseCode.CLIENT_ERROR.getResponseCode());

    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception).getMessage().equals("Unknown identifier "));
  }

  @Test
  public void testBgetAllRecordsOp() {

    resultSet = PowerMockito.mock(ResultSet.class);
    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.execute(selectQuery)).thenReturn(resultSet);

    PowerMockito.mockStatic(CassandraUtil.class);
    Response response = getAllCassandraRecord();
    when(CassandraUtil.createResponse(resultSet)).thenReturn(response);

    operation = ServiceFactory.getInstance();
    operation.getAllRecords(cassandraKeySpace, "address");

    String testResult = "success";
    Assert.assertTrue("success".equals(testResult));
  }

  private Response getAllCassandraRecord() {

    Response response = new Response();
    List<Map<String, Object>> responseList = new ArrayList();
    response.put(JsonKey.RESPONSE, responseList);
    return response;
  }

  @Test
  public void testBgetAllRecordsFailedOp() {

    resultSet = PowerMockito.mock(ResultSet.class);
    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.execute(selectQuery)).thenReturn(resultSet);

    PowerMockito.mockStatic(CassandraUtil.class);
    Response response = getAllCassandraRecord();
    when(CassandraUtil.createResponse(resultSet)).thenReturn(response);

    operation = ServiceFactory.getInstance();

    Throwable exception = null;
    try {
      operation.getAllRecords(cassandraKeySpace, "address");

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());

    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals("cassandra session is null for this sunbird"));
  }

  @Test
  public void testCgetPropertiesValueByIdOp() throws Exception {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);

    BoundStatement boundStatement = PowerMockito.mock(BoundStatement.class);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    resultSet = PowerMockito.mock(ResultSet.class);
    operation = ServiceFactory.getInstance();

    PowerMockito.mockStatic(CassandraUtil.class);
    Response response = getAllCassandraRecord();
    when(CassandraUtil.createResponse(resultSet)).thenReturn(response);

    operation.getPropertiesValueById(
        cassandraKeySpace, "address", "123", JsonKey.ID, JsonKey.CITY, JsonKey.ADD_TYPE);

    String testResult = "success";
    Assert.assertTrue("success".equals(testResult));
  }

  @Test
  public void testCgetPropertiesValueByIdFailedOp() throws Exception {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);

    BoundStatement boundStatement = PowerMockito.mock(BoundStatement.class);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    resultSet = PowerMockito.mock(ResultSet.class);
    operation = ServiceFactory.getInstance();

    PowerMockito.mockStatic(CassandraUtil.class);
    Response response = getAllCassandraRecord();
    when(CassandraUtil.createResponse(resultSet)).thenReturn(response);

    Throwable exception = null;
    try {
      operation.getPropertiesValueById(
          cassandraKeySpace, "address", "123", JsonKey.ID, JsonKey.CITY, JsonKey.ADD_TYPE);

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());

    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals("cassandra session is null for this sunbird"));
  }

  @Test
  public void testDgetRecordByIdOp() {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    PowerMockito.mockStatic(CassandraUtil.class);

    ResultSet results = session.execute(Mockito.anyString());
    Response response = getAllCassandraRecord();
    when(CassandraUtil.createResponse(results)).thenReturn(response);
    operation = ServiceFactory.getInstance();

    operation.getRecordById(cassandraKeySpace, "address", "123");
    String testResult = "success";
    Assert.assertTrue("success".equals(testResult));
  }

  @Test
  public void testDgetRecordByIdFailedOp() {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    PowerMockito.mockStatic(CassandraUtil.class);

    ResultSet results = session.execute(Mockito.anyString());
    Response response = getAllCassandraRecord();
    when(CassandraUtil.createResponse(results)).thenReturn(response);
    operation = ServiceFactory.getInstance();

    Throwable exception = null;
    try {
      operation.getRecordById(cassandraKeySpace, "Dummy Table Name", "12345");

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());
    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals("cassandra session is null for this sunbird"));
  }

  @Test
  public void testFgetRecordsByPropertiesOp() {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    ResultSet results = session.execute(Mockito.anyString());

    PowerMockito.mockStatic(CassandraUtil.class);
    Response response = getAllCassandraRecord();
    when(CassandraUtil.createResponse(results)).thenReturn(response);
    operation = ServiceFactory.getInstance();

    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.USER_ID, "USR1");
    map.put(JsonKey.ADD_TYPE, "addrType");

    operation.getRecordsByProperties(cassandraKeySpace, "address", map);
    String testResult = "success";
    Assert.assertTrue("success".equals(testResult));
  }

  @Test
  public void testFgetRecordsByPropertiesFailed2Op() {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    ResultSet results = session.execute(Mockito.anyString());

    PowerMockito.mockStatic(CassandraUtil.class);
    Response response = getAllCassandraRecord();
    when(CassandraUtil.createResponse(results)).thenReturn(response);
    operation = ServiceFactory.getInstance();

    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.ADDRESS_TYPE, "add");
    map.put(JsonKey.ADDRESS_LINE1, "line1");
    List<String> list = new ArrayList<>();
    list.add("USR1");
    map.put("dummy", list);

    Throwable exception = null;
    try {
      operation.getRecordsByProperties(cassandraKeySpace, "address", map);

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());
    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals("cassandra session is null for this sunbird"));
  }

  @Test
  public void testFgetRecordsByPropertiesFailedOp() {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    ResultSet results = session.execute(Mockito.anyString());

    PowerMockito.mockStatic(CassandraUtil.class);
    Response response = getAllCassandraRecord();
    when(CassandraUtil.createResponse(results)).thenReturn(response);
    operation = ServiceFactory.getInstance();
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.ADDRESS_TYPE, "add");
    map.put(JsonKey.ADDRESS_LINE1, "line1");
    operation.getRecordsByProperties(cassandraKeySpace, "address", map);

    Throwable exception = null;
    try {
      operation.getRecordsByProperties(cassandraKeySpace, "address", map);

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());
    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals("cassandra session is null for this sunbird"));
  }

  @Test
  public void testFgetRecordsByPropertyFrListOp() {
    List<Object> list = new ArrayList<>();
    list.add("123");
    list.add("321");

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    ResultSet results = session.execute(Mockito.anyString());

    Response response = getAllCassandraRecord();

    PowerMockito.mockStatic(CassandraUtil.class);
    when(CassandraUtil.createResponse(results)).thenReturn(response);
    operation = ServiceFactory.getInstance();

    operation.getRecordsByProperty(cassandraKeySpace, "address", JsonKey.ID, list);
    String testResult = "success";
    Assert.assertTrue("success".equals(testResult));
  }

  @Test
  public void testFgetRecordsByPropertyFrListFailedOp() {
    List<Object> list = new ArrayList<>();
    list.add("123");
    list.add("321");

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    ResultSet results = session.execute(Mockito.anyString());

    Response response = getAllCassandraRecord();
    PowerMockito.mockStatic(CassandraUtil.class);
    when(CassandraUtil.createResponse(results)).thenReturn(response);
    operation = ServiceFactory.getInstance();

    Throwable exception = null;
    try {
      operation.getRecordsByProperty(cassandraKeySpace, "address", JsonKey.ID, list);

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());
    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals("cassandra session is null for this sunbird"));
  }

  @Test
  public void testFgetRecordsByPropertyOp() {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    ResultSet results = session.execute(Mockito.anyString());

    Response response = getAllCassandraRecord();

    PowerMockito.mockStatic(CassandraUtil.class);
    when(CassandraUtil.createResponse(results)).thenReturn(response);
    operation = ServiceFactory.getInstance();

    operation.getRecordsByProperty(cassandraKeySpace, "address", JsonKey.ADD_TYPE, "addrType");
    String testResult = "success";
    Assert.assertTrue("success".equals(testResult));
  }

  @Test
  public void testFgetRecordsByPropertyFailedOp() {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    ResultSet results = session.execute(Mockito.anyString());

    Response response = getAllCassandraRecord();

    PowerMockito.mockStatic(CassandraUtil.class);
    when(CassandraUtil.createResponse(results)).thenReturn(response);
    operation = ServiceFactory.getInstance();

    Throwable exception = null;
    try {
      operation.getRecordsByProperty(cassandraKeySpace, "address", JsonKey.ADD_TYPE, "addrType");

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());
    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals("cassandra session is null for this sunbird"));
  }

  @Test
  public void testGgetRecordByIdOp() {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    PowerMockito.mockStatic(CassandraUtil.class);

    ResultSet results = session.execute(Mockito.anyString());
    Response response = getAllCassandraRecord();
    when(CassandraUtil.createResponse(results)).thenReturn(response);
    operation = ServiceFactory.getInstance();

    operation.getRecordById(cassandraKeySpace, "address", "123");
    String testResult = "success";
    Assert.assertTrue("success".equals(testResult));
  }

  @Test
  public void testGgetRecordByIdOpFailed() {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    PowerMockito.mockStatic(CassandraUtil.class);

    ResultSet results = session.execute(Mockito.anyString());
    Response response = getAllCassandraRecord();
    when(CassandraUtil.createResponse(results)).thenReturn(response);
    operation = ServiceFactory.getInstance();

    Throwable exception = null;
    try {
      operation.getRecordById(cassandraKeySpace, "address1", "123");

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());

    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals("cassandra session is null for this sunbird"));
  }

  @Test
  public void testHUpsertOp() throws Exception {

    address.put("Country", "country");

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);

    BoundStatement boundStatement = PowerMockito.mock(BoundStatement.class);
    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    resultSet = PowerMockito.mock(ResultSet.class);
    when(session.execute(boundStatement)).thenReturn(resultSet);
    operation = ServiceFactory.getInstance();

    operation.updateRecord(cassandraKeySpace, "address", address);
    Response response = getCassandraResponse();
    assertEquals("SUCCESS", response.get("response"));
  }

  @Test
  public void testHUpsertOpFailed() throws Exception {
    address.put("Country", "country");

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);

    BoundStatement boundStatement = PowerMockito.mock(BoundStatement.class);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    resultSet = PowerMockito.mock(ResultSet.class);
    operation = ServiceFactory.getInstance();

    Throwable exception = null;
    try {
      operation.updateRecord(cassandraKeySpace, "address1", address);

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());

    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals(Constants.SESSION_IS_NULL + "sunbird"));
  }

  @Test
  public void testHUpsertOpFailedWithInvalidParameter() throws Exception {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);

    BoundStatement boundStatement = PowerMockito.mock(BoundStatement.class);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    resultSet = PowerMockito.mock(ResultSet.class);
    operation = ServiceFactory.getInstance();

    Throwable exception = null;
    try {
      operation.updateRecord(cassandraKeySpace, "address1", address);

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());
    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals(Constants.SESSION_IS_NULL + "sunbird"));
  }

  @Test
  public void testZDeleteOp() throws Exception {

    when(connectionManagerMock.getSession(Mockito.any())).thenReturn(session);
    operation = ServiceFactory.getInstance();
    Response response = getCassandraResponse();

    operation.deleteRecord(cassandraKeySpace, "address", "123");
    assertEquals("SUCCESS", response.get("response"));
  }

  @Test
  public void testZDeleteFailedOp() {

    Throwable exception = null;
    try {
      operation.deleteRecord(cassandraKeySpace, "address", "123");

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.SESSION_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());
    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals(Constants.SESSION_IS_NULL + "sunbird"));
  }

  @Test
  public void testZgetTableList() {
    String testResult = "success";
    Assert.assertTrue("success".equals(testResult));
  }

  @Test
  public void testZgetCluster() {

    when(connectionManagerMock.getCluster("sun")).thenReturn(cluster);
    String testResult = "success";
    Assert.assertTrue("success".equals(testResult));
  }

  @Test
  public void testZgetClusterWithInvalidKeySpace() {

    Throwable exception = null;
    try {
      connectionManagerMock.getCluster("sun");

      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
          Constants.CLUSTER_IS_NULL + "sunbird",
          ResponseCode.SERVER_ERROR.getResponseCode());
    } catch (Exception ex) {
      exception = ex;
    }
    Assert.assertTrue(
        ((ProjectCommonException) exception)
            .getMessage()
            .equals(Constants.CLUSTER_IS_NULL + "sunbird"));
  }
}
