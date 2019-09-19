package org.sunbird.cassandraimpl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.sunbird.common.CassandraUtil;
import org.sunbird.common.Constants;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.responsecode.ResponseCode;

public class CassandraDACImpl extends CassandraOperationImpl {

  public Response getRecords(
      String keySpace, String table, Map<String, Object> filters, List<String> fields) {
    Response response = new Response();
    Session session = connectionManager.getSession(keySpace);
    try {
      Select select;
      if (CollectionUtils.isNotEmpty(fields)) {
        select = QueryBuilder.select((String[]) fields.toArray()).from(keySpace, table);
      } else {
        select = QueryBuilder.select().all().from(keySpace, table);
      }

      if (MapUtils.isNotEmpty(filters)) {
        Select.Where where = select.where();
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
          Object value = filter.getValue();
          if (value instanceof List) {
            where = where.and(QueryBuilder.in(filter.getKey(), ((List) filter.getValue())));
          } else {
            where = where.and(QueryBuilder.eq(filter.getKey(), filter.getValue()));
          }
        }
      }

      ResultSet results = null;
      results = session.execute(select);
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + table + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    return response;
  }

  public void applyOperationOnRecordsAsync(
      String keySpace,
      String table,
      Map<String, Object> filters,
      List<String> fields,
      FutureCallback<ResultSet> callback) {
    Session session = connectionManager.getSession(keySpace);
    try {
      Select select;
      if (CollectionUtils.isNotEmpty(fields)) {
        select = QueryBuilder.select((String[]) fields.toArray()).from(keySpace, table);
      } else {
        select = QueryBuilder.select().all().from(keySpace, table).allowFiltering();
      }

      if (MapUtils.isNotEmpty(filters)) {
        Select.Where where = select.where();
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
          Object value = filter.getValue();
          if (value instanceof List) {
            where = where.and(QueryBuilder.in(filter.getKey(), ((List) filter.getValue())));
          } else {
            where = where.and(QueryBuilder.eq(filter.getKey(), filter.getValue()));
          }
        }
      }
      ResultSetFuture future = session.executeAsync(select);
      Futures.addCallback(future, callback, Executors.newFixedThreadPool(1));
    } catch (Exception e) {
      ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + table + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
  }
}
