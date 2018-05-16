package org.sunbird.cassandraimpl;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Builder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.core.querybuilder.Update.Assignments;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections.CollectionUtils;
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
        "Cassandra Service insertRecord method started at ==" + startTime, LoggerEnum.INFO);
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
    logQueryElapseTime("insertRecord", startTime);
    return response;
  }

  @Override
  public Response updateRecord(String keyspaceName, String tableName, Map<String, Object> request) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service updateRecord method started at ==" + startTime, LoggerEnum.INFO);
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
    logQueryElapseTime("updateRecord", startTime);
    return response;
  }

  @Override
  public Response deleteRecord(String keyspaceName, String tableName, String identifier) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service deleteRecord method started at ==" + startTime, LoggerEnum.INFO);
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
    logQueryElapseTime("deleteRecord", startTime);
    return response;
  }

  @Override
  public Response getRecordsByProperty(
      String keyspaceName, String tableName, String propertyName, Object propertyValue) {
    return getRecordsByProperty(keyspaceName, tableName, propertyName, propertyValue, null);
  }

  @Override
  public Response getRecordsByProperty(
      String keyspaceName,
      String tableName,
      String propertyName,
      Object propertyValue,
      List<String> fields) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getRecordsByProperty method started at ==" + startTime, LoggerEnum.INFO);
    Response response = new Response();
    Session session = connectionManager.getSession(keyspaceName);
    try {
      Builder selectBuilder;
      if (CollectionUtils.isNotEmpty(fields)) {
        selectBuilder = QueryBuilder.select((String[]) fields.toArray());
      } else {
        selectBuilder = QueryBuilder.select().all();
      }
      Statement selectStatement =
          selectBuilder
              .from(keyspaceName, tableName)
              .where(QueryBuilder.eq(propertyName, propertyValue));
      ResultSet results = null;
      results = session.execute(selectStatement);
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    logQueryElapseTime("getRecordsByProperty", startTime);
    return response;
  }

  @Override
  public Response getRecordsByProperty(
      String keyspaceName, String tableName, String propertyName, List<Object> propertyValueList) {
    return getRecordsByProperty(keyspaceName, tableName, propertyName, propertyValueList, null);
  }

  @Override
  public Response getRecordsByProperty(
      String keyspaceName,
      String tableName,
      String propertyName,
      List<Object> propertyValueList,
      List<String> fields) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getRecordsByProperty method started at ==" + startTime, LoggerEnum.INFO);
    Response response = new Response();
    try {
      Builder selectBuilder;
      if (CollectionUtils.isNotEmpty(fields)) {
        selectBuilder = QueryBuilder.select((String[]) fields.toArray());
      } else {
        selectBuilder = QueryBuilder.select().all();
      }
      Statement selectStatement =
          selectBuilder
              .from(keyspaceName, tableName)
              .where(QueryBuilder.in(propertyName, propertyValueList));
      ResultSet results = connectionManager.getSession(keyspaceName).execute(selectStatement);
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    logQueryElapseTime("getRecordsByProperty", startTime);
    return response;
  }

  @Override
  public Response getRecordsByProperties(
      String keyspaceName, String tableName, Map<String, Object> propertyMap) {
    return getRecordsByProperties(keyspaceName, tableName, propertyMap, null);
  }

  @Override
  public Response getRecordsByProperties(
      String keyspaceName, String tableName, Map<String, Object> propertyMap, List<String> fields) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getRecordsByProperties method started at ==" + startTime,
        LoggerEnum.INFO);
    Response response = new Response();
    try {
      Builder selectBuilder;
      if (CollectionUtils.isNotEmpty(fields)) {
        selectBuilder = QueryBuilder.select((String[]) fields.toArray());
      } else {
        selectBuilder = QueryBuilder.select().all();
      }
      Select selectQuery = selectBuilder.from(keyspaceName, tableName);
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
    logQueryElapseTime("getRecordsByProperties", startTime);
    return response;
  }

  @Override
  public Response getPropertiesValueById(
      String keyspaceName, String tableName, String id, String... properties) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getPropertiesValueById method started at ==" + startTime,
        LoggerEnum.INFO);
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
    logQueryElapseTime("getPropertiesValueById", startTime);
    return response;
  }

  @Override
  public Response getAllRecords(String keyspaceName, String tableName) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getAllRecords method started at ==" + startTime, LoggerEnum.INFO);
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
    logQueryElapseTime("getAllRecords", startTime);
    return response;
  }

  @Override
  public Response upsertRecord(String keyspaceName, String tableName, Map<String, Object> request) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service upsertRecord method started at ==" + startTime, LoggerEnum.INFO);
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
    logQueryElapseTime("upsertRecord", startTime);
    return response;
  }

  @Override
  public Response updateRecord(
      String keyspaceName,
      String tableName,
      Map<String, Object> request,
      Map<String, Object> compositeKey) {

    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service updateRecord method started at ==" + startTime, LoggerEnum.INFO);
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
      Statement updateQuery = where;
      session.execute(updateQuery);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_UPDATE + tableName + " : " + e.getMessage(), e);
      if (e.getMessage().contains(JsonKey.UNKNOWN_IDENTIFIER)) {
        throw new ProjectCommonException(
            ResponseCode.invalidPropertyError.getErrorCode(),
            CassandraUtil.processExceptionForUnknownIdentifier(e),
            ResponseCode.CLIENT_ERROR.getResponseCode());
      }
      throw new ProjectCommonException(
          ResponseCode.dbUpdateError.getErrorCode(),
          ResponseCode.dbUpdateError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    logQueryElapseTime("updateRecord", startTime);
    return response;
  }

  private Response getRecordByIdentifier(
      String keyspaceName, String tableName, Object key, List<String> fields) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service getRecordBy key method started at ==" + startTime, LoggerEnum.INFO);
    Response response = new Response();
    try {
      Session session = connectionManager.getSession(keyspaceName);
      Builder selectBuilder;
      if (CollectionUtils.isNotEmpty(fields)) {
        selectBuilder = QueryBuilder.select((String[]) fields.toArray());
      } else {
        selectBuilder = QueryBuilder.select().all();
      }
      Select selectQuery = selectBuilder.from(keyspaceName, tableName);
      Where selectWhere = selectQuery.where();
      if (key instanceof String) {
        selectWhere.and(QueryBuilder.eq(Constants.IDENTIFIER, (String) key));
      } else if (key instanceof Map) {
        Map<String, Object> compositeKey = (Map<String, Object>) key;
        compositeKey
            .entrySet()
            .stream()
            .forEach(
                x -> {
                  CassandraUtil.createQuery(x.getKey(), x.getValue(), selectWhere);
                });
      }
      ResultSet results = session.execute(selectWhere);
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    logQueryElapseTime("getRecordByIdentifier", startTime);
    return response;
  }

  @Override
  public Response getRecordById(String keyspaceName, String tableName, String key) {
    return getRecordByIdentifier(keyspaceName, tableName, key, null);
  }

  @Override
  public Response getRecordById(String keyspaceName, String tableName, Map<String, Object> key) {
    return getRecordByIdentifier(keyspaceName, tableName, key, null);
  }

  @Override
  public Response getRecordById(
      String keyspaceName, String tableName, String key, List<String> fields) {
    return getRecordByIdentifier(keyspaceName, tableName, key, fields);
  }

  @Override
  public Response getRecordById(
      String keyspaceName, String tableName, Map<String, Object> key, List<String> fields) {
    return getRecordByIdentifier(keyspaceName, tableName, key, fields);
  }

  @Override
  public Response batchInsert(
      String keyspaceName, String tableName, List<Map<String, Object>> records) {

    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service batchInsert method started at ==" + startTime, LoggerEnum.INFO);

    Session session = connectionManager.getSession(keyspaceName);
    Response response = new Response();
    BatchStatement batchStatement = new BatchStatement();
    ResultSet resultSet = null;

    try {
      for (Map<String, Object> map : records) {
        Insert insert = QueryBuilder.insertInto(keyspaceName, tableName);
        map.entrySet()
            .stream()
            .forEach(
                x -> {
                  insert.value(x.getKey(), x.getValue());
                });
        batchStatement.add(insert);
      }
      resultSet = session.execute(batchStatement);
      response.put(Constants.RESPONSE, Constants.SUCCESS);
    } catch (QueryExecutionException
        | QueryValidationException
        | NoHostAvailableException
        | IllegalStateException e) {
      ProjectLogger.log("Cassandra Batch Insert Failed." + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    logQueryElapseTime("batchInsert", startTime);
    return response;
  }

  @Override
  public Response batchUpdate(
      String keyspaceName, String tableName, List<Map<String, Map<String, Object>>> list) {

    Session session = connectionManager.getSession(keyspaceName);
    BatchStatement batchStatement = new BatchStatement();
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "Cassandra Service batchUpdate method started at ==" + startTime, LoggerEnum.INFO);
    Response response = new Response();
    ResultSet resultSet = null;
    try {
      for (Map<String, Map<String, Object>> record : list) {
        Map<String, Object> primaryKey = record.get(JsonKey.PRIMARY_KEY);
        Map<String, Object> nonPKRecord = record.get(JsonKey.NON_PRIMARY_KEY);
        batchStatement.add(
            CassandraUtil.createUpdateQuery(primaryKey, nonPKRecord, keyspaceName, tableName));
      }
      resultSet = session.execute(batchStatement);
      response.put(Constants.RESPONSE, Constants.SUCCESS);
    } catch (Exception ex) {
      ProjectLogger.log("Cassandra Batch Update failed " + ex.getMessage(), ex);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    logQueryElapseTime("batchUpdate", startTime);
    return response;
  }

  private void logQueryElapseTime(String operation, long startTime) {

    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    String message =
        "Cassandra operation {0} started at {1} and completed at {2}. Total time elapsed is {3}.";
    MessageFormat mf = new MessageFormat(message);
    ProjectLogger.log(
        mf.format(new Object[] {operation, startTime, stopTime, elapsedTime}), LoggerEnum.PERF_LOG);
  }
}
