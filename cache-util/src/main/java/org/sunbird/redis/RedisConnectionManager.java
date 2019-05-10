package org.sunbird.redis;

import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import redis.clients.jedis.Jedis;

public class RedisConnectionManager {
  private static String host = ProjectUtil.getConfigValue("sunbird_redis_host");
  private static String port = ProjectUtil.getConfigValue("sunbird_redis_port");
  private static Boolean isRedisCluster = host.contains(",") ? true : false;
  private static String scanInterval = ProjectUtil.getConfigValue("sunbird_redis_scan_interval");
  private static Jedis client = null;

  public static Jedis getClient() {
    if (client == null) {
      ProjectLogger.log(
          "RedisConnectionManager:getClient: Redis client is null", LoggerEnum.INFO.name());
      boolean start = initialiseConnection();
      ProjectLogger.log(
          "RedisConnectionManager:getClient: Connection status = " + start, LoggerEnum.INFO.name());
    }
    return client;
  }

  private static boolean initialiseConnection() {
    try {
      if (isRedisCluster) {
        initialisingClusterServer(host, port);
      } else {
        initialiseSingleServer(host, port);
      }
    } catch (Exception e) {
      ProjectLogger.log(
          "RedisConnectionManager:initialiseConnection: Error occurred = " + e.getMessage(), e);
      return false;
    }
    return true;
  }

  private static void initialiseSingleServer(String host, String port) {
    ProjectLogger.log(
        "RedisConnectionManager: initialiseSingleServer called", LoggerEnum.INFO.name());
    client = new Jedis(host, Integer.parseInt(port));
  }

  private static void initialisingClusterServer(String host, String port) {
    //    ProjectLogger.log(
    //        "RedisConnectionManager: initialisingClusterServer called with host = "
    //            + host
    //            + " port = "
    //            + port,
    //        LoggerEnum.INFO.name());
    //
    //    String[] hosts = host.split(",");
    //    String[] ports = port.split(",");
    //
    //    Config config = new Config();
    //
    //    try {
    //      config.setCodec(new StringCodec());
    //      ClusterServersConfig clusterConfig = config.useClusterServers();
    //
    //      clusterConfig.setScanInterval(Integer.parseInt(scanInterval));
    //
    //      for (int i = 0; i < hosts.length && i < ports.length; i++) {
    //        clusterConfig.addNodeAddress("redis://" + hosts[i] + ":" + ports[i]);
    //      }
    //
    //      client = Redisson.create(config);
    //
    //      ProjectLogger.log(
    //          "RedisConnectionManager:initialisingClusterServer: Redis client is created",
    //          LoggerEnum.INFO.name());
    //    } catch (Exception e) {
    //      ProjectLogger.log(
    //          "RedisConnectionManager:initialisingClusterServer: Error occurred = " +
    // e.getMessage(),
    //          LoggerEnum.ERROR.name());
  }
}
