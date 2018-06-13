package org.sunbird.common.models.util;

import java.util.Arrays;
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
   */
  public static void convertValuesToLower(Map<String, Object> map) {
    List<String> keyList =
        Arrays.asList(JsonKey.USERNAME, JsonKey.LOGIN_ID, JsonKey.SOURCE, JsonKey.PROVIDER);
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
