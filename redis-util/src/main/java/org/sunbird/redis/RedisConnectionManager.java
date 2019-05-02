package org.sunbird.redis;

import java.util.List;
import java.util.Map;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ActorOperations;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.helper.ServiceFactory;

public class RedisConnectionManager {

  private static PropertiesCache propertiesCache = PropertiesCache.getInstance();
  private static Boolean isRedisCluster = Boolean.parseBoolean(propertiesCache.getProperty("sunbird_redis_cluster"));
  private static String host = propertiesCache.getProperty("sunbird_redis_host");
  private static String port = propertiesCache.getProperty("sunbird_redis_port");
  private static RedissonClient client = null;
  private static final String KEY_SPACE_NAME = "sunbird";
  private static final String PAGE_MANAGEMENT_TABLE_NAME = "page_management";
  private static final String PAGE_SECTION_TABLE_NAME = "page_section";
  private static RedisUtil redisUtil = new RedisUtil();
  private static CassandraOperation cassandraOperation = ServiceFactory.getInstance();

  static {
    initialiseConnection();
    initializePageCache();
    initializeSectionCache();
  }

  public static RedissonClient getClient() {
    if (client == null) {
      ProjectLogger.log("Redis  clinet is null ", LoggerEnum.INFO.name());
      initialiseConnection();
      ProjectLogger.log("After calling initialiseConnection Redis client value " + client, LoggerEnum.INFO.name());
    }
    return client;
  }

  private static boolean initialiseConnection() {
    try {
      if (isRedisCluster) {
        ProjectLogger.log("RedisConnectionManager:initialiseConnection initalising Cluster.", LoggerEnum.INFO.name());
        initialisingClusterServer(host, port);
        return true;
      }
      ProjectLogger.log("RedisConnectionManager:initialiseConnection initalising Single Server.",
          LoggerEnum.INFO.name());
      initialiseSingleServer(host, port);
      return true;
    } catch (Exception e) {
      ProjectLogger.log("Error while initialising elastic search connection", e);
      return false;
    }
  }

  private static void initialiseSingleServer(String host, String port) {
    Config config = new Config();
    config.useSingleServer().setAddress(host + ":" + port);
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
      ProjectLogger.log("RedisConnectionManager:initialisingClusterServer with ip :" + hosts.toString(),
          LoggerEnum.INFO.name());

      clusterConfig.setScanInterval(2000);
      for (int i = 0; i < hosts.length && i < ports.length; i++) {
        clusterConfig.addNodeAddress("redis://" + hosts[i] + ":" + ports[i]);
      }

      client = Redisson.create(config);
      ProjectLogger.log("RedisConnectionManager:initialisingClusterServer client created ", LoggerEnum.INFO.name());
    } catch (Exception e) {
      ProjectLogger.log("RedisConnectionManager:initialisingClusterServer Exception " + e, LoggerEnum.INFO.name());
    }
  }

  @SuppressWarnings("unchecked")
  public static void initializeSectionCache() {
    try {
      RedisCacheUtil.flushCache(ActorOperations.GET_SECTION.getValue());
      Response response = cassandraOperation.getAllRecords(KEY_SPACE_NAME, PAGE_SECTION_TABLE_NAME);
      List<Map<String, Object>> responseList = (List<Map<String, Object>>) response.get(JsonKey.RESPONSE);
      if (null != responseList && !responseList.isEmpty()) {
        for (Map<String, Object> resultMap : responseList) {

          RedisCacheUtil.addDataToRedisMap(ActorOperations.GET_SECTION.getValue(), (String) resultMap.get(JsonKey.ID),
              redisUtil.getJsonString(resultMap));
        }
      }
      ProjectLogger.log("RedisCacheInitialiser:initializeSectionCache " + PAGE_SECTION_TABLE_NAME + " cache size: "
          + responseList.size(), LoggerEnum.INFO.name());

    } catch (Exception e) {
      ProjectLogger.log(
          "RedisCacheInitialiser:initializeSectionCache: Exception in retrieving page section " + e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  private static void initializePageCache() {
    try {
      RedisCacheUtil.flushCache(ActorOperations.GET_PAGE_DATA.getValue());
      Response response = cassandraOperation.getAllRecords(KEY_SPACE_NAME, PAGE_MANAGEMENT_TABLE_NAME);
      List<Map<String, Object>> responseList = (List<Map<String, Object>>) response.get(JsonKey.RESPONSE);
      if (null != responseList && !responseList.isEmpty()) {
        for (Map<String, Object> resultMap : responseList) {
          String orgId = (((String) resultMap.get(JsonKey.ORGANISATION_ID)) == null ? "NA"
              : (String) resultMap.get(JsonKey.ORGANISATION_ID));

          RedisCacheUtil.addDataToRedisMap(ActorOperations.GET_PAGE_DATA.getValue(),
              orgId + ":" + ((String) resultMap.get(JsonKey.PAGE_NAME)), redisUtil.getJsonString(resultMap));
        }
      }
      ProjectLogger.log("RedisCacheInitialiser:initializePageCache: " + PAGE_MANAGEMENT_TABLE_NAME + " cache size: "
          + responseList.size(), LoggerEnum.INFO.name());

    } catch (Exception e) {
      ProjectLogger
          .log("RedisCacheInitialiser:initializePageCache: Exception in retrieving page section " + e.getMessage(), e);
    }

  }
}
