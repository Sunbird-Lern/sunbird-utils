package org.sunbird.cassandraimpl;


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

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Where;

/**
 * 
 * @author Amit Kumar
 * 
 * @desc this class will hold functions for cassandra db interaction
 * 
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
    ProjectLogger.log("Cassandra Service insertRecord method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
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
        throw new ProjectCommonException(ResponseCode.invalidPropertyError.getErrorCode(),
            CassandraUtil.processExceptionForUnknownIdentifier(e),
            ResponseCode.CLIENT_ERROR.getResponseCode());
      }
      ProjectLogger.log(
          "Exception occured while inserting record to " + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(ResponseCode.dbInsertionError.getErrorCode(),
          ResponseCode.dbInsertionError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("Cassandra Service insertRecord method end at ==" + stopTime
        + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return response;
  }


  @Override
  public Response updateRecord(String keyspaceName, String tableName, Map<String, Object> request) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("Cassandra Service updateRecord method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
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
        throw new ProjectCommonException(ResponseCode.invalidPropertyError.getErrorCode(),
            CassandraUtil.processExceptionForUnknownIdentifier(e),
            ResponseCode.CLIENT_ERROR.getResponseCode());
      }
      ProjectLogger.log(Constants.EXCEPTION_MSG_UPDATE + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(ResponseCode.dbUpdateError.getErrorCode(),
          ResponseCode.dbUpdateError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("Cassandra Service updateRecord method end at ==" + stopTime
        + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return response;
  }


  @Override
  public Response deleteRecord(String keyspaceName, String tableName, String identifier) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("Cassandra Service deleteRecord method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      Delete.Where delete = QueryBuilder.delete().from(keyspaceName, tableName)
          .where(QueryBuilder.eq(Constants.IDENTIFIER, identifier));
      connectionManager.getSession(keyspaceName).execute(delete);
      response.put(Constants.RESPONSE, Constants.SUCCESS);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_DELETE + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("Cassandra Service deleteRecord method end at ==" + stopTime
        + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return response;
  }


  @Override
  public Response getRecordById(String keyspaceName, String tableName, String identifier) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("Cassandra Service getRecordById method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      Select selectQuery = QueryBuilder.select().all().from(keyspaceName, tableName);
      Where selectWhere = selectQuery.where();
      Clause clause = QueryBuilder.eq(Constants.IDENTIFIER, identifier);
      selectWhere.and(clause);
      ResultSet results = connectionManager.getSession(keyspaceName).execute(selectQuery);
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("Cassandra Service getRecordById method end at ==" + stopTime
        + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return response;
  }


  @Override
  public Response getRecordsByProperty(String keyspaceName, String tableName, String propertyName,
      Object propertyValue) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("Cassandra Service getRecordsByProperty method started at ==" + startTime,
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
      throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("Cassandra Service getRecordsByProperty method end at ==" + stopTime
        + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return response;
  }

  @Override
  public Response getRecordsByProperty(String keyspaceName, String tableName, String propertyName,
      List<Object> propertyValueList) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("Cassandra Service getRecordsByProperty method started at ==" + startTime,
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
      throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("Cassandra Service getRecordsByProperty method end at ==" + stopTime
        + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return response;
  }


  @Override
  public Response getRecordsByProperties(String keyspaceName, String tableName,
      Map<String, Object> propertyMap) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("Cassandra Service getRecordsByProperties method started at ==" + startTime,
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
      throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("Cassandra Service getRecordsByProperties method end at ==" + stopTime
        + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return response;
  }


  @Override
  public Response getPropertiesValueById(String keyspaceName, String tableName, String id,
      String... properties) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("Cassandra Service getPropertiesValueById method started at ==" + startTime,
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
      throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("Cassandra Service getPropertiesValueById method end at ==" + stopTime
        + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return response;
  }


  @Override
  public Response getAllRecords(String keyspaceName, String tableName) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("Cassandra Service getAllRecords method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    Response response = new Response();
    try {
      Select selectQuery = QueryBuilder.select().all().from(keyspaceName, tableName);
      ResultSet results = connectionManager.getSession(keyspaceName).execute(selectQuery);
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("Cassandra Service getAllRecords method end at ==" + stopTime
        + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return response;
  }



  @Override
  public Response upsertRecord(String keyspaceName, String tableName, Map<String, Object> request) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("Cassandra Service upsertRecord method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
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
        throw new ProjectCommonException(ResponseCode.invalidPropertyError.getErrorCode(),
            CassandraUtil.processExceptionForUnknownIdentifier(e),
            ResponseCode.CLIENT_ERROR.getResponseCode());
      }
      ProjectLogger.log(Constants.EXCEPTION_MSG_UPSERT + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log("Cassandra Service upsertRecord method end at ==" + stopTime
        + " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
    return response;

  }

}

