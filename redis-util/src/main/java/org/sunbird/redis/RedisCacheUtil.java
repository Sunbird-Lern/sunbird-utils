package org.sunbird.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.sunbird.cache.inf.CacheUtil;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

public class RedisCacheUtil implements CacheUtil {
  protected static final String REDIS_CONFIG_FILE = "cache.conf";
  private static final String CACHE_MAP_LIST = "cache.mapNames";
  private Map<String, String> properties = readConfig();
  private String mapNameListStr = properties.get(CACHE_MAP_LIST);
  private String[] mapNameList = mapNameListStr.split(",");
  private  RedissonClient client = null;

  public RedisCacheUtil() {
    client =  RedisConnectionManager.getClient();
    
  }

  @Override
  public Map<String, String> readConfig() {
    Map<String, String> props = new HashMap<>();
    com.typesafe.config.Config config = ConfigFactory.load(REDIS_CONFIG_FILE);
    Set<Entry<String, ConfigValue>> configSet = config.entrySet();
    for (Entry<String, ConfigValue> confEntry : configSet) {
      props.put(confEntry.getKey(), confEntry.getValue().unwrapped().toString());
    }
    return props;
  }

  @Override
  public String get(String mapName, String key) {
    try {
      RMap<String, String> map = client.getMap(mapName);
      String s = map.get(key);
      return s;
    } catch (Exception e) {
      ProjectLogger.log("RedisCacheUtil:get , error occured map :" + mapName + " , key : " + key,
          LoggerEnum.INFO.name());
    }
    return null;
  }

  @Override
  public boolean put(String mapName, String key, String value) {
    try {
      RMap<String, String> map = client.getMap(mapName);
      map.put(key, value);
      ProjectLogger.log("RedisCacheUtil:put = map :" + mapName + " , key : " + key + " , value : " + value,
          LoggerEnum.INFO.name());
      return true;
    } catch (Exception e) {
      ProjectLogger.log(
          "RedisCacheUtil:put , error occured map :" + mapName + " , key : " + key + " , value : " + value,
          LoggerEnum.INFO.name());
    }
    return false;
  }

  @Override
  public boolean clear(String mapName) {
    try {
      RMap<String, String> map = client.getMap(mapName);
      map.clear();
      ProjectLogger.log("RedisCacheUtil:clear : " + mapName + LoggerEnum.INFO.name());
      return true;
    } catch (Exception e) {
      ProjectLogger
          .log("RedisCacheUtil:clear error occured for map: " + mapName + " error : " + e + LoggerEnum.INFO.name());
    }
    return false;
  }

  @Override
  public void clearAll() {
    ProjectLogger.log("RedisCacheUtil:clearAll: ", LoggerEnum.INFO.name());
    for (int i = 0; i < mapNameList.length; i++) {
      clear(mapNameList[i]);
    }
  }
  @Override
  public  boolean setMapExpiry(String name, long seconds) {
    boolean result = client.getMap(name).expire(seconds, TimeUnit.SECONDS);
    ProjectLogger.log("RedisCacheUtil:setMapExpiry for map :" + name + " result : " + result, LoggerEnum.INFO.name());
    return result;

  }

}
