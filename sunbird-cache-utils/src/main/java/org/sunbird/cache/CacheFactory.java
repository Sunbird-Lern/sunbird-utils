package org.sunbird.cache;

import org.sunbird.cache.interfaces.Cache;
import org.sunbird.redis.RedisCache;

public class CacheFactory {

  private static Cache cache = null;

  private CacheFactory() {}

  public static Cache getInstance() {
    if (null == cache) {
      cache = new RedisCache();
    }
    return cache;
  }
}
