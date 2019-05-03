package org.sunbird.cache;

import org.sunbird.cache.inf.CacheUtil;
import org.sunbird.redis.RedisCacheUtil;

public class CacheServiceFactory {

  private static CacheUtil cacheUtil = null;

  private CacheServiceFactory() {
  }

  public static CacheUtil getInstance() {
    if (null == cacheUtil) {

      cacheUtil = new RedisCacheUtil();
    }
    return cacheUtil;
  }

}
