package org.sunbird.notification.utils;

import org.jboss.logging.Logger;

import com.google.gson.Gson;

/**
 * 
 * @author Manzarul
 *
 */
public class JsonUtil {
  
  private static Logger logger = Logger.getLogger(JsonUtil.class);
   
  /**
   * This method will convert Object to Json String
   * @param object  Object
   * @return String
   */
  public static String toJson(Object object) {
        Gson gsonObj = new Gson();

        return gsonObj.toJson(object);
    }
  
    /**
     * This method will check incoming value is null or empty it will do empty check by doing trim
     * method. in case of null or empty it will return true else false.
     *
     * @param value
     * @return
     */
    public static boolean isStringNullOREmpty(String value) {
      if (value == null || "".equals(value.trim())) {
    	  logger.debug("String is either null or empty.");
        return true;
      }
      return false;
    }
}
