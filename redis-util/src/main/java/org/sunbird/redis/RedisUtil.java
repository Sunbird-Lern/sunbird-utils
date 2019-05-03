package org.sunbird.redis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedisUtil {

  private static ObjectMapper mapper = new ObjectMapper();

  public static void main(String []args) throws IOException {
    /*CacheUtil util = CacheServiceFactory.getInstance();
    util.put("isco", "22", "cm");
    */
    
    Map<String,Object> a = new HashMap<>();
    Map<String,Object> b = new HashMap<>();
    Map<String,Object> c = new HashMap<>();
    Map<String,Object> d = new HashMap<>();
    a.put("this is a", "value for a");
    a.put("this is a-1", "value for a-1");
    a.put("this is a-2", "value for a-2");
    b.put("this is b", "value for b");
    b.put("this is b-1", "value for b-1");
    b.put("this is b-2", "value for b-2");
    c.put("this is c", "value for c");
    c.put("this is x", "value for x");
    c.put("this is c-1", "value for c-1");
    c.put("this is c-2", "value for c-2");
    
    a.put("b", b);
    a.put("c", c);
    d.put("a", a);
    d.put("b", b);
    d.put("c", c);
    List<String> list = new ArrayList<>();
    list.add("abc");
    list.add("xyz");
    list.add("pqr");
    d.put("list", list);
    String s = mapper.writeValueAsString(d);
    System.out.println(s);
    JsonNode node = mapper.readTree(s);
    System.out.println(node.hashCode());
    
    Map<String,Object> e = new HashMap<>();
    a.put("c", c);
    e.put("b", b);
    a.put("b", b);
    e.put("b", c);
    e.put("a", a);
    List<String> list1 = new ArrayList<>();
    list1.add("xyz");
    list1.add("abc");
    list1.add("pqr");
    e.put("list", list1);
    String s1 = mapper.writeValueAsString(d);
    System.out.println(s1);
    JsonNode node1 = mapper.readTree(s1);
    System.out.println(node1.hashCode());
    
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
