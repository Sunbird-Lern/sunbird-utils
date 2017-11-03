/**
 * 
 */
package org.sunbird.common.models.util.fcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Manzarul
 *
 */
public class FCMNotification {
  public static void main(String[] args) {
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
    Notification.sendNotification("/topics/nameOFTopic", map,"");
  }

}
