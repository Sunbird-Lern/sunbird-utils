package org.sunbird.common;

import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.core.querybuilder.Update.Assignments;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sunbird.cassandraannotation.ClusteringKey;
import org.sunbird.cassandraannotation.PartitioningKey;
import org.sunbird.common.models.util.JsonKey;

/** @author arvind. */
public class CassandraQueryUtil {

  private static final String SERIAL_VERSION_UID = "serialVersionUID";

  public static <T> Map<String, Map<String, Object>> batchUpdateQuery(T clazz)
      throws IllegalAccessException {
    Field[] fieldList = clazz.getClass().getDeclaredFields();

    Map<String, Object> primaryKeyMap = new HashMap<>();
    Map<String, Object> nonPKMap = new HashMap<>();
    for (Field field : fieldList) {
      String fieldName = null;
      Object fieldValue = null;
      Boolean isFieldPrimaryKeyPart = false;
      if (Modifier.isPrivate(field.getModifiers())) {
        field.setAccessible(true);
      }
      Annotation[] annotations = field.getDeclaredAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation instanceof PartitioningKey) {
          isFieldPrimaryKeyPart = true;
        } else if (annotation instanceof ClusteringKey) {
          isFieldPrimaryKeyPart = true;
        }
      }
      fieldName = field.getName();
      fieldValue = field.get(clazz);
      if (!(fieldName.equalsIgnoreCase(SERIAL_VERSION_UID))) {
        if (isFieldPrimaryKeyPart) {
          primaryKeyMap.put(fieldName, fieldValue);
        } else {
          nonPKMap.put(fieldName, fieldValue);
        }
      }
    }
    Map<String, Map<String, Object>> map = new HashMap<>();
    map.put(JsonKey.PRIMARY_KEY, primaryKeyMap);
    map.put(JsonKey.NON_PRIMARY_KEY, nonPKMap);
    return map;
  }

  public static <T> Map<String, Object> getPrimaryKey(T clazz) throws IllegalAccessException {
    Field[] fieldList = clazz.getClass().getDeclaredFields();
    Map<String, Object> primaryKeyMap = new HashMap<>();

    for (Field field : fieldList) {
      String fieldName = null;
      Object fieldValue = null;
      Boolean isFieldPrimaryKeyPart = false;
      if (Modifier.isPrivate(field.getModifiers())) {
        field.setAccessible(true);
      }
      Annotation[] annotations = field.getDeclaredAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation instanceof PartitioningKey) {
          isFieldPrimaryKeyPart = true;
        } else if (annotation instanceof ClusteringKey) {
          isFieldPrimaryKeyPart = true;
        }
      }
      fieldName = field.getName();
      fieldValue = field.get(clazz);
      if (!(fieldName.equalsIgnoreCase(SERIAL_VERSION_UID))) {
        if (isFieldPrimaryKeyPart) {
          primaryKeyMap.put(fieldName, fieldValue);
        }
      }
    }
    return primaryKeyMap;
  }

  public static void createQuery(String key, Object value, Where where) {
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

  public static RegularStatement createUpdateQuery(
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
