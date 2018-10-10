package org.sunbird.cassandra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.powermock.api.mockito.PowerMockito.when;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.util.concurrent.Uninterruptibles;
import java.util.*;
import org.junit.Before;
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
import org.sunbird.helper.CassandraConnectionManagerImpl;
import org.sunbird.helper.CassandraConnectionMngrFactory;
import org.sunbird.helper.ServiceFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/** @author kirti. Junit test cases */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
  Cluster.class,
  CassandraOperationImpl.class,
  Uninterruptibles.class,
  PreparedStatement.class,
  BoundStatement.class,
  Session.class,
  Metadata.class,
  CassandraConnectionMngrFactory.class,
  ResultSet.class,
  CassandraUtil.class,
  Cluster.Builder.class,
  Select.class,
  Row.class,
  ColumnDefinitions.class,
  String.class,
  Select.Where.class,
  Select.Builder.class,
  QueryBuilder.class,
  Select.Selection.class,
  Delete.Where.class,
  Delete.Selection.class
})
@PowerMockIgnore("javax.management.*")
public class CassandraMockTest {

  private static Cluster cluster;
  private static Session session = PowerMockito.mock(Session.class);
  private static PreparedStatement statement;
  private static ResultSet resultSet;
  private static Select selectQuery;
  private static Select.Where where;
  private static Delete.Where deleteWhere;
  private static Select.Builder selectBuilder;
  private static Metadata metadata;
  private static CassandraOperation operation;
  private static Map<String, Object> address = null;
  private static Map<String, Object> dummyAddress = null;

  private static PropertiesCache cach = PropertiesCache.getInstance();
  private static String host = cach.getProperty("contactPoint");
  private static String port = cach.getProperty("port");
  private static String cassandraKeySpace = cach.getProperty("keyspace");

  private static final Cluster.Builder builder = PowerMockito.mock(Cluster.Builder.class);
  private static BoundStatement boundStatement;
  private static Select.Selection selectSelection;
  private static Delete.Selection deleteSelection;
  private static Delete delete;
  private static KeyspaceMetadata keyspaceMetadata;

  private static CassandraConnectionManagerImpl connectionManager =
      (CassandraConnectionManagerImpl)
          CassandraConnectionMngrFactory.getObject(
              cach.getProperty(JsonKey.SUNBIRD_CASSANDRA_MODE));

  @BeforeClass
  public static void init() {

    PowerMockito.mockStatic(Cluster.class);
    cluster = PowerMockito.mock(Cluster.class);
    when(cluster.connect(Mockito.anyString())).thenReturn(session);
    metadata = PowerMockito.mock(Metadata.class);
    when(cluster.getMetadata()).thenReturn(metadata);

    when(Cluster.builder()).thenReturn(builder);
    when(builder.addContactPoint(Mockito.anyString())).thenReturn(builder);
    when(builder.withPort(Mockito.anyInt())).thenReturn(builder);
    when(builder.withProtocolVersion(Mockito.any())).thenReturn(builder);
    when(builder.withRetryPolicy(Mockito.any())).thenReturn(builder);
    when(builder.withTimestampGenerator(Mockito.any())).thenReturn(builder);
    when(builder.withPoolingOptions(Mockito.any())).thenReturn(builder);
    when(builder.build()).thenReturn(cluster);
    connectionManager.createConnection(host, port, "cassandra", "password", cassandraKeySpace);
  }

  @Before
  public void setUp() throws Exception {

    reset(session);
    address = new HashMap<>();
    address.put(JsonKey.ID, "123");
    address.put(JsonKey.ADDRESS_LINE1, "Line 1");
    address.put(JsonKey.USER_ID, "USR1");

    dummyAddress = new HashMap<>();
    dummyAddress.put(JsonKey.ID, "12345");
    dummyAddress.put(JsonKey.ADDRESS_LINE1, "Line 111");
    dummyAddress.put(JsonKey.USER_ID, "USR111");
    dummyAddress.put("DummyColumn", "USR111");

    statement = PowerMockito.mock(PreparedStatement.class);
    selectQuery = PowerMockito.mock(Select.class);
    where = PowerMockito.mock(Select.Where.class);
    selectBuilder = PowerMockito.mock(Select.Builder.class);
    PowerMockito.mockStatic(QueryBuilder.class);
    selectSelection = PowerMockito.mock(Select.Selection.class);
    deleteSelection = PowerMockito.mock(Delete.Selection.class);
    deleteWhere = PowerMockito.mock(Delete.Where.class);
    delete = PowerMockito.mock(Delete.class);
    operation = ServiceFactory.getInstance();
    resultSet = PowerMockito.mock(ResultSet.class);
    keyspaceMetadata = PowerMockito.mock(KeyspaceMetadata.class);

    when(QueryBuilder.select()).thenReturn(selectSelection);
    when(QueryBuilder.delete()).thenReturn(deleteSelection);
    when(deleteSelection.from(Mockito.anyString(), Mockito.anyString())).thenReturn(delete);
    when(delete.where(QueryBuilder.eq(Constants.IDENTIFIER, "123"))).thenReturn(deleteWhere);
    when(selectQuery.where()).thenReturn(where);
    when(metadata.getKeyspace("sunbird")).thenReturn(keyspaceMetadata);

    when(cluster.connect(Mockito.anyString())).thenReturn(session);

    boundStatement = PowerMockito.mock(BoundStatement.class);
    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);
    when(session.execute(boundStatement)).thenReturn(resultSet);
    when(session.prepare(Mockito.anyString())).thenReturn(statement);
    when(selectSelection.all()).thenReturn(selectBuilder);
    when(selectBuilder.from(Mockito.anyString(), Mockito.anyString())).thenReturn(selectQuery);
    when(session.execute(selectQuery)).thenReturn(resultSet);
    when(session.execute(boundStatement.bind("123"))).thenReturn(resultSet);

    ColumnDefinitions cd = PowerMockito.mock(ColumnDefinitions.class);
    String str = "qwertypower(king";
    when(resultSet.getColumnDefinitions()).thenReturn(cd);
    when(cd.toString()).thenReturn(str);
    when(str.substring(8, resultSet.getColumnDefinitions().toString().length() - 1))
        .thenReturn(str);
  }

  @Test
  public void testCassandraConnectionWithoutUserNameAndPassword() throws Exception {

    boolean bool = connectionManager.createConnection(host, port, null, null, cassandraKeySpace);
    assertEquals(true, bool);
  }

  @Test
  public void testCassandraConnectionWithUserNameAndPassword() throws Exception {

    Boolean bool =
        connectionManager.createConnection(host, port, "cassandra", "password", cassandraKeySpace);
    assertEquals(true, bool);
  }

  @Test
  public void testFailedCassandraConnection() {

    try {
      connectionManager.createConnection("127.0.0.1", "9042", "cassandra", "pass", "eySpace");
    } catch (Exception ex) {
    }
    assertTrue(500 == ResponseCode.SERVER_ERROR.getResponseCode());
  }

  @Test
  public void testCassandraInsertOperation() throws Exception {

    Response response = operation.insertRecord(cassandraKeySpace, "address1", address);
    assertEquals("SUCCESS", response.get("response"));
  }

  @Test
  public void testCassandraInsertOperationFailed() throws Exception {

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenThrow(
            new ProjectCommonException(
                ResponseCode.dbInsertionError.getErrorCode(),
                ResponseCode.dbInsertionError.getErrorMessage(),
                ResponseCode.SERVER_ERROR.getResponseCode()));

    Throwable exception = null;
    try {
      operation.insertRecord(cassandraKeySpace, "address1", address);
    } catch (Exception ex) {
      exception = ex;
    }
    assertTrue("DB insert operation failed.".equals(exception.getMessage()));
  }

  @Test
  public void testCassandraInsertFailedOperationWithInvalidProperty() throws Exception {

    Throwable exception = null;
    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenThrow(
            new ProjectCommonException(
                ResponseCode.invalidPropertyError.getErrorCode(),
                JsonKey.UNKNOWN_IDENTIFIER,
                ResponseCode.CLIENT_ERROR.getResponseCode()));

    try {
      operation.insertRecord(cassandraKeySpace, "address1", address);
    } catch (Exception exp) {
      exception = exp;
    }
    assertTrue("invalid property .".equals(exception.getMessage()));
  }

  @Test
  public void testCassandraUpdateOperation() throws Exception {

    address.put(JsonKey.CITY, "city");
    address.put(JsonKey.ADD_TYPE, "addrType");

    Response response = operation.updateRecord(cassandraKeySpace, "address", address);
    assertEquals("SUCCESS", response.get("response"));
  }

  @Test
  public void testCassandraUpdateFailedOperation() throws Exception {

    dummyAddress.put(JsonKey.CITY, "city");
    dummyAddress.put(JsonKey.ADD_TYPE, "addrType");

    when(session.prepare(Mockito.anyString()))
        .thenThrow(
            new ProjectCommonException(
                ResponseCode.dbUpdateError.getErrorCode(),
                ResponseCode.dbUpdateError.getErrorMessage(),
                ResponseCode.SERVER_ERROR.getResponseCode()));

    Throwable exception = null;
    try {
      operation.updateRecord(cassandraKeySpace, "address1", address);

    } catch (Exception ex) {
      exception = ex;
    }
    assertTrue("Db update operation failed.".equals(exception.getMessage()));
  }

  @Test
  public void testCassandraUpdateFailedOperationWithInvalidProperty() throws Exception {

    when(session.prepare(Mockito.anyString()))
        .thenThrow(
            new ProjectCommonException(
                ResponseCode.invalidPropertyError.getErrorCode(),
                JsonKey.UNKNOWN_IDENTIFIER,
                ResponseCode.CLIENT_ERROR.getResponseCode()));

    Throwable exception = null;
    try {
      operation.updateRecord(cassandraKeySpace, "address", address);
    } catch (Exception exp) {
      exception = exp;
    }
    assertTrue("invalid property .".equals(exception.getMessage()));
  }

  @Test
  public void testCassandraGetAllRecordsOperation() throws Exception {

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    Response response = operation.getAllRecords(cassandraKeySpace, "address");
    assertTrue(response.getResult().size() > 0);
  }

  @Test
  public void testCassandraGetAllRecordsFailedOperation() throws Exception {

    when(session.execute(selectQuery))
        .thenThrow(
            new ProjectCommonException(
                ResponseCode.SERVER_ERROR.getErrorCode(),
                ResponseCode.SERVER_ERROR.getErrorMessage(),
                ResponseCode.SERVER_ERROR.getResponseCode()));

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);

    Throwable exception = null;
    try {
      operation.getAllRecords(cassandraKeySpace, "address");
    } catch (Exception ex) {
      exception = ex;
    }
    assertTrue((((ProjectCommonException) exception).getResponseCode()) == 500);
  }

  @Test
  public void testCassandraGetPropertiesValueByIdOperation() throws Exception {

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);

    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenReturn(boundStatement);
    Response response =
        operation.getPropertiesValueById(
            cassandraKeySpace, "address", "123", JsonKey.ID, JsonKey.CITY, JsonKey.ADD_TYPE);
    assertTrue(response.getResult().size() > 0);
  }

  @Test
  public void testCassandraGetPropertiesValueByIdFailedOperation() throws Exception {

    Throwable exception = null;
    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenThrow(
            new ProjectCommonException(
                ResponseCode.SERVER_ERROR.getErrorCode(),
                ResponseCode.SERVER_ERROR.getErrorMessage(),
                ResponseCode.SERVER_ERROR.getResponseCode()));

    try {
      operation.getPropertiesValueById(
          cassandraKeySpace, "address", "123", JsonKey.ID, JsonKey.CITY, JsonKey.ADD_TYPE);
    } catch (Exception ex) {
      exception = ex;
    }
    assertTrue((((ProjectCommonException) exception).getResponseCode()) == 500);
  }

  @Test
  public void testCassandraGetRecordByIdOperation() {

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);

    when(session.execute(boundStatement.bind("123"))).thenReturn(resultSet);
    when(session.execute(where)).thenReturn(resultSet);
    when(selectBuilder.from(Mockito.anyString(), Mockito.anyString())).thenReturn(selectQuery);
    when(selectSelection.all()).thenReturn(selectBuilder);

    Response response = operation.getRecordById(cassandraKeySpace, "address", "123");
    assertTrue(response.getResult().size() > 0);
  }

  @Test
  public void testCassandraGetRecordByIdFailedOperation() throws Exception {

    Throwable exception = null;
    PowerMockito.whenNew(BoundStatement.class)
        .withArguments(Mockito.any(PreparedStatement.class))
        .thenThrow(
            new ProjectCommonException(
                ResponseCode.SERVER_ERROR.getErrorCode(),
                ResponseCode.SERVER_ERROR.getErrorMessage(),
                ResponseCode.SERVER_ERROR.getResponseCode()));

    try {
      operation.getRecordById(cassandraKeySpace, "address", "123");
    } catch (Exception ex) {
      exception = ex;
    }
    assertTrue((((ProjectCommonException) exception).getResponseCode()) == 500);
  }

  @Test
  public void testCassandraGetRecordByPropertiesOperation() throws Exception {

    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.USER_ID, "USR1");
    map.put(JsonKey.ADD_TYPE, "addrType");

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);

    Response response = operation.getRecordsByProperties(cassandraKeySpace, "address", map);
    assertTrue(response.getResult().size() > 0);
  }

  @Test
  public void testCassandraGetRecordByPropertiesFailedOperation() throws Exception {

    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.USER_ID, "USR1");
    map.put(JsonKey.ADD_TYPE, "addrType");

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);

    when(selectSelection.all())
        .thenThrow(
            new ProjectCommonException(
                ResponseCode.SERVER_ERROR.getErrorCode(),
                ResponseCode.SERVER_ERROR.getErrorMessage(),
                ResponseCode.SERVER_ERROR.getResponseCode()));

    Throwable exception = null;
    try {
      operation.getRecordsByProperties(cassandraKeySpace, "address", map);
    } catch (Exception ex) {
      exception = ex;
    }
    assertTrue((((ProjectCommonException) exception).getResponseCode()) == 500);
  }

  @Test
  public void testCassandraGetRecordByPropertiesForListOperation() throws Exception {

    List<Object> list = new ArrayList<>();
    list.add("123");
    list.add("321");

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);

    Response response =
        operation.getRecordsByProperty(cassandraKeySpace, "address", JsonKey.ID, list);
    assertTrue(response.getResult().size() > 0);
  }

  @Test
  public void testCassandraGetRecordByPropertiesForListFailedOperation() throws Exception {

    List<Object> list = new ArrayList<>();
    list.add("123");
    list.add("321");

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);

    when(selectSelection.all())
        .thenThrow(
            new ProjectCommonException(
                ResponseCode.SERVER_ERROR.getErrorCode(),
                ResponseCode.SERVER_ERROR.getErrorMessage(),
                ResponseCode.SERVER_ERROR.getResponseCode()));

    Throwable exception = null;
    try {
      operation.getRecordsByProperty(cassandraKeySpace, "address", JsonKey.ID, list);
    } catch (Exception ex) {
      exception = ex;
    }
    assertTrue((((ProjectCommonException) exception).getResponseCode()) == 500);
  }

  @Test
  public void testCassandraGetRecordsByPropertyOperation() throws Exception {

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);

    Response response =
        operation.getRecordsByProperty(cassandraKeySpace, "address", JsonKey.ADD_TYPE, "addrType");
    assertTrue(response.getResult().size() > 0);
  }

  @Test
  public void testCassandraGetRecordsByPropertyFailedOperation() throws Exception {

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);

    when(selectSelection.all())
        .thenThrow(
            new ProjectCommonException(
                ResponseCode.SERVER_ERROR.getErrorCode(),
                ResponseCode.SERVER_ERROR.getErrorMessage(),
                ResponseCode.SERVER_ERROR.getResponseCode()));

    Throwable exception = null;
    try {
      operation.getRecordsByProperty(cassandraKeySpace, "address", JsonKey.ADD_TYPE, "addrType");
    } catch (Exception ex) {
      exception = ex;
    }
    assertTrue((((ProjectCommonException) exception).getResponseCode()) == 500);
  }

  @Test
  public void testCassandraGetRecordsByIdOperation() {

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);
    when(session.execute(where)).thenReturn(resultSet);
    when(selectSelection.all()).thenReturn(selectBuilder);

    Response response = operation.getRecordById(cassandraKeySpace, "address", "123");
    assertTrue(response.getResult().size() > 0);
  }

  @Test
  public void testCassandraGetRecordsByIdFailedOperation() throws Exception {

    List<Row> rows = new ArrayList<>();
    Row row = Mockito.mock(Row.class);
    rows.add(row);
    when(resultSet.all()).thenReturn(rows);

    when(selectSelection.all())
        .thenThrow(
            new ProjectCommonException(
                ResponseCode.SERVER_ERROR.getErrorCode(),
                ResponseCode.SERVER_ERROR.getErrorMessage(),
                ResponseCode.SERVER_ERROR.getResponseCode()));

    Throwable exception = null;
    try {
      operation.getRecordById(cassandraKeySpace, "address1", "123");

    } catch (Exception ex) {
      exception = ex;
    }
    assertTrue((((ProjectCommonException) exception).getResponseCode()) == 500);
  }

  @Test
  public void testCassandraDeleteOperation() throws Exception {

    Response response = getCassandraResponse();
    operation.deleteRecord(cassandraKeySpace, "address", "123");
    assertEquals("SUCCESS", response.get("response"));
  }

  @Test
  public void testCassandraDeleteFailedOperation() {

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
    assertTrue((Constants.SESSION_IS_NULL + "sunbird").equals(exception.getMessage()));
  }

  @Test
  public void testCassandraGetTableList() throws Exception {

    Collection<TableMetadata> tables = new ArrayList<>();
    TableMetadata table = Mockito.mock(TableMetadata.class);
    tables.add(table);
    when(keyspaceMetadata.getTables()).thenReturn(tables);

    List<String> tableList = connectionManager.getTableList(cassandraKeySpace);
    assertTrue(tableList.size() > 0);
  }

  @Test
  public void testCassandraGetCluster() throws Exception {

    Cluster cluster = connectionManager.getCluster("sunbird");
    assertTrue(cluster != null);
  }

  @Test
  public void testCassandraGetClusterFailedWithInvalidKeySpace() {

    Throwable exception = null;
    try {
      connectionManager.getCluster("sun");
    } catch (Exception ex) {
      exception = ex;
    }
    assertTrue("cassandra cluster value is null for this sun".equals(exception.getMessage()));
  }

  private Response getCassandraResponse() {

    Response response = new Response();
    response.put(JsonKey.RESPONSE, Constants.SUCCESS);
    return response;
  }
}
