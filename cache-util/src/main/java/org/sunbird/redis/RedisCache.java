package org.sunbird.redis;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.sunbird.cache.interfaces.Cache;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.notification.utils.JsonUtil;

public class RedisCache implements Cache {
  private static final String CACHE_MAP_LIST = "cache.mapNames";
  private static final String CACHE_MAP_TTL = "cache.ttl";
  private static final String DEFAULT_ENTRY = "default_entry";
  private static final String DEFAULT_VALUE = "default_value";
  private static boolean initTTL = false;
  private Map<String, String> properties = readConfig();
  private String[] mapNameList = properties.get(CACHE_MAP_LIST).split(",");

  private RedissonClient client;

  public RedisCache() {
    client = RedisConnectionManager.getClient();
    if (!initTTL) {
      setDefaultTTL();
      initTTL = !initTTL;
    }
  }

  @Override
  public boolean put(String mapName, String key, Object value) {
    ProjectLogger.log(
        "RedisCache:put: mapName = " + mapName + ", key = " + key + ", value = " + value,
        LoggerEnum.INFO.name());

    try {
      String res = JsonUtil.toJson(value);
      RMap<String, String> map = client.getMap(mapName);
      map.put(key, res);
      return true;
    } catch (Exception e) {
      ProjectLogger.log(
          "RedisCache:put: Error occurred mapName = "
              + mapName
              + ", key = "
              + key
              + ", value = "
              + value,
          LoggerEnum.ERROR.name());
    }
    return false;
  }

  @Override
  public Object get(String mapName, String key, Class<?> clazz) {
    ProjectLogger.log(
        "RedisCache:get: mapName = " + mapName + ", key = " + key, LoggerEnum.INFO.name());
    try {
      RMap<String, String> map = client.getMap(mapName);
      String s = map.get(key);
      return JsonUtil.getAsObject(s, clazz);
    } catch (Exception e) {
      ProjectLogger.log(
          "RedisCache:get: Error occurred mapName = " + mapName + ", key = " + key,
          LoggerEnum.ERROR.name());
    }
    return null;
  }

  @Override
  public void clearAll() {
    ProjectLogger.log("RedisCache: clearAll called", LoggerEnum.INFO.name());
    for (int i = 0; i < mapNameList.length; i++) {
      clear(mapNameList[i]);
    }
  }

  @Override
  public boolean clear(String mapName) {
    ProjectLogger.log("RedisCache:clear: mapName = " + mapName, LoggerEnum.INFO.name());
    try {
      RMap<String, String> map = client.getMap(mapName);
      map.clear();
      return true;
    } catch (Exception e) {
      ProjectLogger.log(
          "RedisCache:clear: Error occurred mapName = " + mapName + " error = " + e,
          LoggerEnum.ERROR.name());
    }
    return false;
  }

  @Override
  public boolean setMapExpiry(String name, long seconds) {
    boolean result = client.getMap(name).expire(seconds, TimeUnit.SECONDS);
    ProjectLogger.log(
        "RedisCache:setMapExpiry: name = " + name + " seconds = " + seconds + " result = " + result,
        LoggerEnum.INFO.name());

    return result;
  }

  private void setDefaultTTL() {
    ProjectLogger.log("RedisCache:setDefaultTTL setting default ttl all the maps");
    for (int i = 0; i < mapNameList.length; i++) {
      put(mapNameList[i], DEFAULT_ENTRY, DEFAULT_VALUE);
      ProjectLogger.log("****** : cache.ttl = " + properties.get(CACHE_MAP_TTL));
      long time = Long.valueOf(properties.get(CACHE_MAP_TTL + "." + mapNameList[i]));
      setMapExpiry(mapNameList[i], time);
    }
  }
}
