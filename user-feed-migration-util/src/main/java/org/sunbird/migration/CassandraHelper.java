package org.sunbird.migration;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import java.sql.Timestamp;
import java.util.*;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.sunbird.migration.constants.DbColumnConstants;

public class CassandraHelper {

  /** this variable is initialized so that list can easily handle the size of user.. */
  public static final int initialCapacity = 50000;

  /**
   * these methods will be used to convert resultSet into List of Group entity Object.
   *
   * @param resultSet
   * @return
   */
  public static List<Feed> getFeedFromResultSet(ResultSet resultSet) {

    List<Feed> feedList = new ArrayList<>(initialCapacity);
    Iterator<Row> iterator = resultSet.iterator();
    while (iterator.hasNext()) {
      Row row = iterator.next();
      Feed feed =
          new Feed(
              row.getString(DbColumnConstants.id),
              row.getString(DbColumnConstants.category),
              row.getString(DbColumnConstants.createdBy),
              row.getTimestamp(DbColumnConstants.createdOn) != null ? new Timestamp(row.getTimestamp(DbColumnConstants.createdOn).getTime()): null,
              row.getString(DbColumnConstants.data),
              row.getTimestamp(DbColumnConstants.expireOn) != null ? new Timestamp(row.getTimestamp(DbColumnConstants.expireOn).getTime()):null,
              row.getInt(DbColumnConstants.priority),
              row.getString(DbColumnConstants.status),
              row.getString(DbColumnConstants.updatedBy),
              row.getTimestamp(DbColumnConstants.updatedOn) != null ? new Timestamp(row.getTimestamp(DbColumnConstants.updatedOn).getTime()): null,
              row.getString(DbColumnConstants.userId)) ;
      feedList.add(feed);
    }
    return feedList;
  }

  public static  BatchStatement createBatchStatement(List<Map<String,Object>> records){
    BatchStatement batchStatement = new BatchStatement();
      for (Map<String, Object> map : records) {
        Insert insert = QueryBuilder.insertInto("sunbird_notifications", "notification_feed");
        map.entrySet()
                .stream()
                .forEach(
                        x -> {
                          insert.value(x.getKey(), x.getValue());
                        });
        batchStatement.add(insert);
      }
      return batchStatement;
  }



}
