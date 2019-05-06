package org.sunbird.cache;

import org.sunbird.cache.interfaces.Cache;
import org.sunbird.redis.RedisCache;

public class CacheServiceFactory {

  private static Cache cache = null;

  private CacheServiceFactory() {}

  public static Cache getInstance() {
    if (null == cache) {

      cache = new RedisCache();
    }
    return cache;
  }
}
