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
      ProjectLogger.log("RedisConnectionManager:getClient: Redis client is null", LoggerEnum.INFO.name());
      boolean start = initialiseConnection();
      ProjectLogger.log("RedisConnectionManager:getClient: Connection status = " + start, LoggerEnum.INFO.name());
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
      ProjectLogger.log("RedisConnectionManager:initialiseConnection: Error occurred = " + e.getMessage(), e);
      return false;
    }
    return true;
  }

  private static void initialiseSingleServer(String host, String port) {
    ProjectLogger.log("RedisConnectionManager: initialiseSingleServer called", LoggerEnum.INFO.name());
    
    Config config = new Config();
    config.useSingleServer().setAddress("127.0.0.1" + ":" + "6379");
    config.setCodec(new StringCodec());
    client = Redisson.create(config);
  }

  private static void initialisingClusterServer(String host, String port) {
    ProjectLogger.log("RedisConnectionManager: initialisingClusterServer called with host = " + host + " port = " + port, LoggerEnum.INFO.name());
    
    String[] hosts = host.split(",");
    String[] ports = port.split(",");

    Config config = new Config();

    try {
      config.setCodec(new StringCodec());
      ClusterServersConfig clusterConfig = config.useClusterServers();
      
      clusterConfig.setScanInterval(2000);
      
      for (int i = 0; i < hosts.length && i < ports.length; i++) {
        clusterConfig.addNodeAddress("redis://" + hosts[i] + ":" + ports[i]);
      }

      client = Redisson.create(config);

      ProjectLogger.log("RedisConnectionManager:initialisingClusterServer: Redis client is created", LoggerEnum.INFO.name());
    } catch (Exception e) {
      ProjectLogger.log("RedisConnectionManager:initialisingClusterServer: Error occurred = " + e.getMessage(), LoggerEnum.ERROR.name());
    }
  }
}
