package org.sunbird.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

public class RedisCacheUtil {
  protected static final String REDIS_CONFIG_FILE = "sunbird-redis.conf";
  private static final String REDIS_CONF_MAP_LIST = "redis.map.list";
  private static Map<String, String> properties = getProperties();
  private static String mapNameListStr = properties.get(REDIS_CONF_MAP_LIST);
  private static String[] mapNameList = mapNameListStr.split(",");
  

  private RedisCacheUtil() {
  }
  
  private static Map<String, String> getProperties() {
    Map<String, String> props = new HashMap<>();
    com.typesafe.config.Config config = ConfigFactory.load(REDIS_CONFIG_FILE);
    Set<Entry<String, ConfigValue>> configSet = config.entrySet();
    for (Entry<String, ConfigValue> confEntry : configSet) {
      props.put(confEntry.getKey(), confEntry.getValue().unwrapped().toString());
    }
    return props;
  }

  public static String getRedisValueFromMap(String mapName, String key) {
    RedissonClient client = RedisConnectionManager.getClient();
    RMap<String, String> map = client.getMap(mapName);
    String s = map.get(key);
    return s;
  }

  public static boolean setMapExpiry(String name, long seconds) {
    RedissonClient client = RedisConnectionManager.getClient();
    boolean result = client.getMap(name).expire(seconds, TimeUnit.SECONDS);
    ProjectLogger.log(
        "RedisCacheUtil:setMapExpiry for map :" + name + " result : "+result,
        LoggerEnum.INFO.name());
    return result;

  }

  public static void addDataToRedisMap(String name, String key, String value) {
    RedissonClient client = RedisConnectionManager.getClient();
    RMap<String, String> map = client.getMap(name);
    map.put(key, value);
    ProjectLogger.log(
        "RedisCacheUtil:addDataToRedisMap addDataToRedisMap map :" + name + " , key : " + key + " , value : " + value,
        LoggerEnum.INFO.name());
  }

  public static void flushCache(String name) {
    RedissonClient client = RedisConnectionManager.getClient();
    RMap<String, String> map = client.getMap(name);
    map.clear();
    ProjectLogger.log("RedisCacheUtil:flushCache mapflused : " + name + LoggerEnum.INFO.name());
  }

  public static void flushAll() {
    for (int i = 0; i < mapNameList.length; i++) {
      flushCache(mapNameList[i]);
    }
  }
}
