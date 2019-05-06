package org.sunbird.notification.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Created by rajatgupta on 06/05/19.
 */
public class JsonUtil {
    private static Logger logger = Logger.getLogger(JsonUtil.class);

    /**
     * This method will convert Object to Json String
     *
     * @param object Object
     * @return String
     */
    public static String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
//            ProjectLogger.log("JsonUtil:getJsonString error occured : " + e, LoggerEnum.INFO);
        }
        return null;
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

    public static <T> T getAsObject(String res, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();

        T result = null;
        try {
            JsonNode node = mapper.readTree(res);
            result = mapper.convertValue(node, clazz);
        } catch (IOException e) {
//            ProjectLogger.log("JsonUtil:getAsObject error occured : " + e, LoggerEnum.INFO);
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
//            ProjectLogger.log("JsonUtil:getHashOfString error occured : " + e, LoggerEnum.INFO);
        }
        return node.hashCode();
    }
}
