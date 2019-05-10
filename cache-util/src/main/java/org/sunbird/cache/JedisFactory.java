package org.sunbird.cache;

import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisFactory {

  private static JedisPool jedisPool;

  private static int maxConnections = 128;
  private static int index = 0;
  private static String host = ProjectUtil.getConfigValue("sunbird_redis_host");
  private static String port = ProjectUtil.getConfigValue("sunbird_redis_port");

  static {
    JedisPoolConfig config = new JedisPoolConfig();
    config.setMaxTotal(maxConnections);
    config.setBlockWhenExhausted(true);
    jedisPool = new JedisPool(config, host, Integer.parseInt(port));
  }

  public static Jedis getJedisConncetion() {
    try {
      Jedis jedis = jedisPool.getResource();
      if (index > 0) jedis.select(index);
      ProjectLogger.log(
          "RedisConnectionManager:getClient: Redis connected client value is " + jedis.toString(),
          LoggerEnum.INFO.name());
      return jedis;
    } catch (Exception e) {
      ProjectLogger.log(
          "RedisConnectionManager:getClient: Redis client is null", LoggerEnum.ERROR.name());
      return null;
    }
  }
}

//    public static void returnConnection(Jedis jedis) {
//        try {
//            if (null != jedis)
//                jedisPool.returnResource(jedis);
//        } catch (Exception e) {
//            throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_CONNECTION_ERROR.name(),
// e.getMessage());
//        }
