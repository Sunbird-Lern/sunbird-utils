package org.sunbird.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
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
}
