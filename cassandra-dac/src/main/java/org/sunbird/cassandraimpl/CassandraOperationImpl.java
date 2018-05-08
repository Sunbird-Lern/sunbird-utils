package org.sunbird.cassandraimpl;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.datastax.driver.core.querybuilder.Batch;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.core.querybuilder.Update.Assignments;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.common.CassandraUtil;
import org.sunbird.common.Constants;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.helper.CassandraConnectionManager;
import org.sunbird.helper.CassandraConnectionMngrFactory;

/**
 * @author Amit Kumar
 * @desc this class will hold functions for cassandra db interaction
 */
public class CassandraOperationImpl implements CassandraOperation {

  private CassandraConnectionManager connectionManager;

  public CassandraOperationImpl() {
    PropertiesCache propertiesCache = PropertiesCache.getInstance();
    String cassandraMode = propertiesCache.getProperty(JsonKey.SUNBIRD_CASSANDRA_MODE);
    connectionManager = CassandraConnectionMngrFactory.getObject(cassandraMode);
  }

  @Override
  public Response insertRecord(String keyspaceName, String tableName, Map<String, Object> request) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service insertRecord method started at ==" + startTime, LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      String query = CassandraUtil.getPreparedStatement(keyspaceName, tableName, request);
      PreparedStatement statement = connectionManager.getSession(keyspaceName).prepare(query);
      BoundStatement boundStatement = new BoundStatement(statement);
      Iterator<Object> iterator = request.values().iterator();
      Object[] array = new Object[request.keySet().size()];
      int i = 0;
      while (iterator.hasNext()) {
        array[i++] = iterator.next();
      }
      connectionManager.getSession(keyspaceName).execute(boundStatement.bind(array));
      response.put(Constants.RESPONSE, Constants.SUCCESS);
    } catch (Exception e) {
      if (e.getMessage().contains(JsonKey.UNKNOWN_IDENTIFIER)
          || e.getMessage().contains(JsonKey.UNDEFINED_IDENTIFIER)) {
        ProjectLogger.log(
            "Exception occured while inserting record to " + tableName + " : " + e.getMessage(), e);
        throw new ProjectCommonException(
            ResponseCode.invalidPropertyError.getErrorCode(),
            CassandraUtil.processExceptionForUnknownIdentifier(e),
            ResponseCode.CLIENT_ERROR.getResponseCode());
      }
      ProjectLogger.log(
          "Exception occured while inserting record to " + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.dbInsertionError.getErrorCode(),
          ResponseCode.dbInsertionError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "Cassandra Service insertRecord method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  @Override
  public Response updateRecord(String keyspaceName, String tableName, Map<String, Object> request) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service updateRecord method started at ==" + startTime, LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      String query = CassandraUtil.getUpdateQueryStatement(keyspaceName, tableName, request);
      String updateQuery = query + Constants.IF_EXISTS;
      PreparedStatement statement = connectionManager.getSession(keyspaceName).prepare(updateQuery);
      Object[] array = new Object[request.size()];
      int i = 0;
      String str = "";
      int index = query.lastIndexOf(Constants.SET.trim());
      str = query.substring(index + 4);
      str = str.replace(Constants.EQUAL_WITH_QUE_MARK, "");
      str = str.replace(Constants.WHERE_ID, "");
      str = str.replace(Constants.SEMICOLON, "");
      String[] arr = str.split(",");
      for (String key : arr) {
        array[i++] = request.get(key.trim());
      }
      array[i] = request.get(Constants.IDENTIFIER);
      BoundStatement boundStatement = statement.bind(array);
      connectionManager.getSession(keyspaceName).execute(boundStatement);
      response.put(Constants.RESPONSE, Constants.SUCCESS);
    } catch (Exception e) {
      if (e.getMessage().contains(JsonKey.UNKNOWN_IDENTIFIER)) {
        ProjectLogger.log(Constants.EXCEPTION_MSG_UPDATE + tableName + " : " + e.getMessage(), e);
        throw new ProjectCommonException(
            ResponseCode.invalidPropertyError.getErrorCode(),
            CassandraUtil.processExceptionForUnknownIdentifier(e),
            ResponseCode.CLIENT_ERROR.getResponseCode());
      }
      ProjectLogger.log(Constants.EXCEPTION_MSG_UPDATE + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.dbUpdateError.getErrorCode(),
          ResponseCode.dbUpdateError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "Cassandra Service updateRecord method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  @Override
  public Response deleteRecord(String keyspaceName, String tableName, String identifier) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service deleteRecord method started at ==" + startTime, LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      Delete.Where delete =
          QueryBuilder.delete()
              .from(keyspaceName, tableName)
              .where(QueryBuilder.eq(Constants.IDENTIFIER, identifier));
      connectionManager.getSession(keyspaceName).execute(delete);
      response.put(Constants.RESPONSE, Constants.SUCCESS);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_DELETE + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "Cassandra Service deleteRecord method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  @Override
  public Response getRecordsByProperty(
      String keyspaceName, String tableName, String propertyName, Object propertyValue) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getRecordsByProperty method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      Select selectQuery = QueryBuilder.select().all().from(keyspaceName, tableName);
      Where selectWhere = selectQuery.where();
      Clause clause = QueryBuilder.eq(propertyName, propertyValue);
      selectWhere.and(clause);
      ResultSet results = null;
      Session session = connectionManager.getSession(keyspaceName);
      results = session.execute(selectQuery);
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "Cassandra Service getRecordsByProperty method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  @Override
  public Response getRecordsByProperty(
      String keyspaceName, String tableName, String propertyName, List<Object> propertyValueList) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getRecordsByProperty method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      Select selectQuery = QueryBuilder.select().all().from(keyspaceName, tableName);
      Where selectWhere = selectQuery.where();
      Clause clause = QueryBuilder.in(propertyName, propertyValueList);
      selectWhere.and(clause);
      ResultSet results = connectionManager.getSession(keyspaceName).execute(selectQuery);
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "Cassandra Service getRecordsByProperty method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  @Override
  public Response getRecordsByProperties(
      String keyspaceName, String tableName, Map<String, Object> propertyMap) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getRecordsByProperties method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      Select selectQuery = QueryBuilder.select().all().from(keyspaceName, tableName);
      Where selectWhere = selectQuery.where();
      for (Entry<String, Object> entry : propertyMap.entrySet()) {
        if (entry.getValue() instanceof List) {
          Clause clause = QueryBuilder.in(entry.getKey(), entry.getValue());
          selectWhere.and(clause);
        } else {
          Clause clause = QueryBuilder.eq(entry.getKey(), entry.getValue());
          selectWhere.and(clause);
        }
      }
      ResultSet results =
          connectionManager.getSession(keyspaceName).execute(selectQuery.allowFiltering());
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "Cassandra Service getRecordsByProperties method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  @Override
  public Response getPropertiesValueById(
      String keyspaceName, String tableName, String id, String... properties) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getPropertiesValueById method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      String selectQuery = CassandraUtil.getSelectStatement(keyspaceName, tableName, properties);
      PreparedStatement statement = connectionManager.getSession(keyspaceName).prepare(selectQuery);
      BoundStatement boundStatement = new BoundStatement(statement);
      ResultSet results =
          connectionManager.getSession(keyspaceName).execute(boundStatement.bind(id));
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "Cassandra Service getPropertiesValueById method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  @Override
  public Response getAllRecords(String keyspaceName, String tableName) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getAllRecords method started at ==" + startTime, LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      Select selectQuery = QueryBuilder.select().all().from(keyspaceName, tableName);
      ResultSet results = connectionManager.getSession(keyspaceName).execute(selectQuery);
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "Cassandra Service getAllRecords method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  @Override
  public Response upsertRecord(String keyspaceName, String tableName, Map<String, Object> request) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service upsertRecord method started at ==" + startTime, LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      String query = CassandraUtil.getPreparedStatementFrUpsert(keyspaceName, tableName, request);
      PreparedStatement statement = connectionManager.getSession(keyspaceName).prepare(query);
      BoundStatement boundStatement = new BoundStatement(statement);
      Iterator<Object> iterator = request.values().iterator();
      Object[] array = new Object[request.keySet().size()];
      int i = 0;
      while (iterator.hasNext()) {
        array[i++] = iterator.next();
      }
      connectionManager.getSession(keyspaceName).execute(boundStatement.bind(array));
      response.put(Constants.RESPONSE, Constants.SUCCESS);

    } catch (Exception e) {
      if (e.getMessage().contains(JsonKey.UNKNOWN_IDENTIFIER)) {
        ProjectLogger.log(Constants.EXCEPTION_MSG_UPSERT + tableName + " : " + e.getMessage(), e);
        throw new ProjectCommonException(
            ResponseCode.invalidPropertyError.getErrorCode(),
            CassandraUtil.processExceptionForUnknownIdentifier(e),
            ResponseCode.CLIENT_ERROR.getResponseCode());
      }
      ProjectLogger.log(Constants.EXCEPTION_MSG_UPSERT + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "Cassandra Service upsertRecord method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  /**
   * @param keyspaceName
   * @param tableName
   * @param request
   * @param compositeKey
   * @return Response
   */
  @Override
  public Response updateRecord(
      String keyspaceName,
      String tableName,
      Map<String, Object> request,
      Map<String, Object> compositeKey) {

    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service updateRecord method started at ==" + startTime, LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      Session session = connectionManager.getSession(keyspaceName);
      Update update = QueryBuilder.update(keyspaceName, tableName);
      Assignments assignments = update.with();
      Update.Where where = update.where();
      request
          .entrySet()
          .stream()
          .forEach(
              x -> {
                assignments.and(QueryBuilder.set(x.getKey(), x.getValue()));
              });
      compositeKey
          .entrySet()
          .stream()
          .forEach(
              x -> {
                where.and(QueryBuilder.eq(x.getKey(), x.getValue()));
              });
      Statement exampleQuery = where;
      session.execute(exampleQuery);
    } catch (Exception e) {
      if (e.getMessage().contains(JsonKey.UNKNOWN_IDENTIFIER)) {
        ProjectLogger.log(Constants.EXCEPTION_MSG_UPDATE + tableName + " : " + e.getMessage(), e);
        throw new ProjectCommonException(
            ResponseCode.invalidPropertyError.getErrorCode(),
            CassandraUtil.processExceptionForUnknownIdentifier(e),
            ResponseCode.CLIENT_ERROR.getResponseCode());
      }
      ProjectLogger.log(Constants.EXCEPTION_MSG_UPDATE + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.dbUpdateError.getErrorCode(),
          ResponseCode.dbUpdateError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "Cassandra Service updateRecord method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  /**
   * @param keyspaceName
   * @param tableName
   * @param key key represents the identifier it could be singe string or in case of composite key
   *     it will be map of composite keys name and their corresponding values.
   * @return Response.
   */
  @Override
  public Response getRecordById(String keyspaceName, String tableName, Object key) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getRecordBy key method started at ==" + startTime, LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      Session session = connectionManager.getSession(keyspaceName);
      Select.Where where = QueryBuilder.select().from(keyspaceName, tableName).where();
      if (key instanceof String) {
        where.and(QueryBuilder.eq(JsonKey.ID, (String) key));
      } else if (key instanceof Map) {
        Map<String, Object> compositeKey = (Map<String, Object>) key;
        compositeKey
            .entrySet()
            .stream()
            .forEach(
                x -> {
                  createQuery(x.getKey(), x.getValue(), where);
                });
      }
      ResultSet results = session.execute(where);
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "Cassandra Service getRecordBy key method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  @Override
  public Response batchInsert(
      String keyspaceName, String tableName, List<Map<String, Object>> records) {

    Session session = connectionManager.getSession(keyspaceName);
    Batch batch = QueryBuilder.batch();
    Response response = new Response();

    try {
      for (Map<String, Object> map : records) {
        Insert insert = QueryBuilder.insertInto(keyspaceName, tableName);
        map.entrySet()
            .stream()
            .forEach(
                x -> {
                  insert.value(x.getKey(), x.getValue());
                });
        insert.setConsistencyLevel(ConsistencyLevel.QUORUM);
        batch.add(insert);
      }
      // now execute the batch
      session.execute(batch);
      response.put(Constants.RESPONSE, Constants.SUCCESS);
    } catch (QueryExecutionException
        | QueryValidationException
        | NoHostAvailableException
        | IllegalStateException e) {
      // log an exception
      throw e;
    }
    return response;
  }

  private void createQuery(String key, Object value, Where where) {
    QueryBuilder.eq(key, value);
    if (value instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) value;
      map.entrySet()
          .stream()
          .forEach(
              x -> {
                if (JsonKey.LTE.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.lte(key, x.getValue()));
                } else if (JsonKey.LT.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.lt(key, x.getValue()));
                } else if (JsonKey.GTE.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.gte(key, x.getValue()));
                } else if (JsonKey.GT.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.gt(key, x.getValue()));
                }
              });
    } else if (value instanceof List) {
      where.and(QueryBuilder.in(key, (List) value));
    } else {
      where.and(QueryBuilder.eq(key, value));
    }
  }

  @Override
  public Response batchUpdate(
      String keyspaceName, String tableName, List<Map<String, Map<String, Object>>> list) {

    Session session = connectionManager.getSession(keyspaceName);
    Batch batch = QueryBuilder.batch();
    Response response = new Response();
    try {
      for (Map<String, Map<String, Object>> record : list) {
        Map<String, Object> primaryKey = record.get(JsonKey.PRIMARY_KEY);
        Map<String, Object> nonPKRecord = record.get(JsonKey.NON_PRIMARY_KEY);
        batch.add(createUpdateQuery(primaryKey, nonPKRecord, keyspaceName, tableName));
      }
      session.execute(batch);
      response.put(Constants.RESPONSE, Constants.SUCCESS);
    } catch (Exception ex) {
      ProjectLogger.log("Batch Update failed " + ex.getMessage(), ex);
      throw ex;
    }
    return response;
  }

  private RegularStatement createUpdateQuery(
      Map<String, Object> primaryKey,
      Map<String, Object> nonPKRecord,
      String keyspaceName,
      String tableName) {

    Update update = QueryBuilder.update(keyspaceName, tableName);
    Assignments assignments = update.with();
    Update.Where where = update.where();
    nonPKRecord
        .entrySet()
        .stream()
        .forEach(
            x -> {
              assignments.and(QueryBuilder.set(x.getKey(), x.getValue()));
            });
    primaryKey
        .entrySet()
        .stream()
        .forEach(
            x -> {
              where.and(QueryBuilder.eq(x.getKey(), x.getValue()));
            });
    return where;
  }
}
