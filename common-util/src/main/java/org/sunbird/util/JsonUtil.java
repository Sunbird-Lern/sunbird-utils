package org.sunbird.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.jboss.logging.Logger;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

public class JsonUtil {

  private static Logger logger = Logger.getLogger(JsonUtil.class);

  public static String toJson(Object object) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(object);
    } catch (Exception e) {
      ProjectLogger.log("JsonUtil:getJsonString: Error occurred = " + e.getMessage(), LoggerEnum.ERROR);
    }
    return null;
  }

  public static boolean isStringNullOREmpty(String value) {
    if (value == null || "".equals(value.trim())) {
      return true;
    }
    return false;
  }

  public static <T> T getAsObject(String res, Class<T> clazz) {
    ObjectMapper mapper = new ObjectMapper();

    T result = null;
    try {
      JsonNode node = mapper.readTree(res);
      result = mapper.convertValue(node, clazz);
    } catch (IOException e) {
      ProjectLogger.log("JsonUtil:getAsObject: Error occurred = " + e.getMessage(), LoggerEnum.ERROR);
      e.printStackTrace();
    }
    return result;
  }

  public static int getHashCode(Map<String, Object> map) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = null;
    try {
      String json = mapper.writeValueAsString(map);
      node = mapper.readTree(json);
    } catch (IOException e) {
      ProjectLogger.log("JsonUtil:getHashCode: Error occurred = " + e.getMessage(), LoggerEnum.ERROR);
    }
    return node.hashCode();
  }
}
