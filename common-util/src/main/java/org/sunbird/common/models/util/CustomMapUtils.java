package org.sunbird.common.models.util;

import java.util.List;
import java.util.Map;

/**
 * This class will provide utility methods for Map.
 *
 * @author Amit Kumar
 */
public class CustomMapUtils {

  private CustomMapUtils() {}

  /**
   * This method will convert these(userName,loginId,source,provider) keys value to lower case.
   *
   * @param map Requested Map
   * @param keyList List of keys
   */
  public static void convertValuesToLower(Map<String, Object> map, List<String> keyList) {
    map.entrySet()
        .stream()
        .forEach(
            s -> {
              if (keyList.contains(s.getKey())) {
                s.getValue().toString().toLowerCase();
              }
            });
  }
}
