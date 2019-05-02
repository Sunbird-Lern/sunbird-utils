package org.sunbird.redis;

import java.io.IOException;

import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedisUtil {

  private static ObjectMapper mapper = new ObjectMapper();

  public String getJsonString(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      ProjectLogger.log("RedisUtil:getJsonString error occured : " + e, LoggerEnum.INFO);

    }
    return null;
  }

  public Response getAsResponse(String res) {
    Response result = null;
    try {
      JsonNode node = mapper.readTree(res);
      result = mapper.convertValue(node, Response.class);
    } catch (IOException e) {
      ProjectLogger.log("RedisUtil:getAsResponse error occured : " + e, LoggerEnum.INFO);
      e.printStackTrace();
    }
    return result;
  }

}
