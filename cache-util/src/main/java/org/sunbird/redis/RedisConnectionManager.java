package org.sunbird.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;

public class RedisConnectionManager {

  private static Boolean isRedisCluster =
      Boolean.parseBoolean(ProjectUtil.getConfigValue("sunbird_redis_cluster"));
  private static String host = ProjectUtil.getConfigValue("sunbird_redis_host");
  private static String port = ProjectUtil.getConfigValue("sunbird_redis_port");
  private static RedissonClient client = null;

  public static RedissonClient getClient() {
    if (client == null) {
      ProjectLogger.log("Redis  clinet is null ", LoggerEnum.INFO.name());
      boolean start = initialiseConnection();
      if (start) {
        ProjectLogger.log(
            "After calling initialiseConnection Redis client value " + client,
            LoggerEnum.INFO.name());
      }
    }
    return client;
  }

  private static boolean initialiseConnection() {
    try {
      if (isRedisCluster) {
        ProjectLogger.log(
            "RedisConnectionManager:initialiseConnection initalising Cluster.",
            LoggerEnum.INFO.name());
        initialisingClusterServer(host, port);
        return true;
      }
      ProjectLogger.log(
          "RedisConnectionManager:initialiseConnection initalising Single Server.",
          LoggerEnum.INFO.name());
      initialiseSingleServer(host, port);
      return true;
    } catch (Exception e) {
      ProjectLogger.log("Error while initialising redis connection", e);
      return false;
    }
  }

  private static void initialiseSingleServer(String host, String port) {
    Config config = new Config();
    config.useSingleServer().setAddress("127.0.0.1" + ":" + "6379");
    config.setCodec(new StringCodec());
    client = Redisson.create(config);
  }

  // cluster state scan interval in milliseconds
  // use "rediss://" for SSL connection
  private static void initialisingClusterServer(String host, String port) {
    String[] hosts = host.split(",");
    String[] ports = port.split(",");
    Config config = new Config();
    try {
      config.setCodec(new StringCodec());
      ClusterServersConfig clusterConfig = config.useClusterServers();
      ProjectLogger.log(
          "RedisConnectionManager:initialisingClusterServer with ip :" + hosts.toString(),
          LoggerEnum.INFO.name());

      clusterConfig.setScanInterval(2000);
      for (int i = 0; i < hosts.length && i < ports.length; i++) {
        clusterConfig.addNodeAddress("redis://" + hosts[i] + ":" + ports[i]);
      }

      client = Redisson.create(config);
      ProjectLogger.log(
          "RedisConnectionManager:initialisingClusterServer client created ",
          LoggerEnum.INFO.name());
    } catch (Exception e) {
      ProjectLogger.log(
          "RedisConnectionManager:initialisingClusterServer Exception " + e,
          LoggerEnum.INFO.name());
    }
  }
}
