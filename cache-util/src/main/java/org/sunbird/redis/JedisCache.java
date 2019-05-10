package org.sunbird.redis;

import org.sunbird.cache.JedisFactory;
import org.sunbird.cache.interfaces.Cache;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.notification.utils.JsonUtil;
import redis.clients.jedis.Jedis;

public class JedisCache implements Cache {

  private Jedis jedisCache;

  public JedisCache() {
    jedisCache = JedisFactory.getJedisConncetion();
  }

  @Override
  public String get(String mapName, String key) {
    return null;
  }

  @Override
  public Object get(String mapName, String key, Class<?> cls) {
    ProjectLogger.log(
        "RedisCache:get: mapName = " + mapName + ", key = " + key, LoggerEnum.INFO.name());
    try {
      String response = jedisCache.hget(mapName, key);
      return JsonUtil.getAsObject(response, cls);
    } catch (Exception e) {
      ProjectLogger.log(
          "RedisCache:get: Error occurred mapName = " + mapName + ", key = " + key,
          LoggerEnum.ERROR.name());
    }
    return null;
  }

  @Override
  public boolean put(String mapName, String key, String value) {
    return false;
  }

  public boolean put(String mapName, String key, Object value) {
    ProjectLogger.log(
        "RedisCache:put: mapName = " + mapName + ", key = " + key + ", value = " + value,
        LoggerEnum.INFO.name());

    try {
      String res = JsonUtil.toJson(value);
      jedisCache.hsetnx(mapName, key, res);
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
  public boolean clear(String mapName) {
    return false;
  }

  @Override
  public void clearAll() {}

  @Override
  public boolean setMapExpiry(String name, long seconds) {
    return false;
  }
}
