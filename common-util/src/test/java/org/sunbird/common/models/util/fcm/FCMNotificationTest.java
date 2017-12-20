/**
 * 
 */
package org.sunbird.common.models.util.fcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.common.models.util.JsonKey;

/**
 * Test cases for FCM notification service.
 * @author Manzarul
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FCMNotificationTest {
  
  @Test
  public void successNotificationWithStringAndObjectData(){
    Map<String,Object> map = new HashMap<>();
    map.put("title", "some title");
    map.put("summary", "some value");
    List<Object> list = new ArrayList<>();
    list.add("test12");
    list.add("test45");
    map.put("extra",list);
    Map<String,Object> innerMap = new HashMap<>();
    innerMap.put("title", "some value");
    innerMap.put("link", "https://google.com");
    map.put("map", innerMap);
    String val = Notification.sendNotification("nameOFTopic", map,Notification.FCM_URL);
    Assert.assertNotEquals(JsonKey.FAILURE, val);
  }

  
  @Test
  public void successNotificationWithStringAndStringData(){
    Map<String,Object> map = new HashMap<>();
    map.put("title", "some title");
    map.put("summary", "some value");
    String val = Notification.sendNotification("nameOFTopic", map,Notification.FCM_URL);
    Assert.assertNotEquals(JsonKey.FAILURE, val);
  }
  
  
  @Test
  public void failureNotificationWithEmptyFCMURL(){
    Map<String,Object> map = new HashMap<>();
    map.put("title", "some title");
    map.put("summary", "some value");
    String val = Notification.sendNotification("nameOFTopic", map,"");
    Assert.assertEquals(JsonKey.FAILURE, val);
  }
  
  @Test
  public void failureNotificationWithWrongJson(){
    Map<String,Object> map = null;
    String val = Notification.sendNotification("nameOFTopic", map,"");
    Assert.assertEquals(JsonKey.FAILURE, val);
  }
  
  @Test
  public void failureNotificationWithEmptyTopic(){
    Map<String,Object> map = new HashMap<>();
    map.put("title", "some title");
    map.put("summary", "some value");
    String val = Notification.sendNotification("", map,"");
    Assert.assertEquals(JsonKey.FAILURE, val);
  }
}
