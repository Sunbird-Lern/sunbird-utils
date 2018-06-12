/** */
package org.sunbird.helper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.PropertiesCache;

/**
 * This class will manage connection.
 *
 * @author Manzarul
 */
public class ConnectionManager {

  private static TransportClient client = null;
  private static List<String> host = new ArrayList<>();
  private static List<Integer> ports = new ArrayList<>();
  private static PropertiesCache propertiesCache = PropertiesCache.getInstance();
  private static String cluster = propertiesCache.getProperty("es.cluster.name");
  private static String hostName = propertiesCache.getProperty("es.host.name");
  private static String port = propertiesCache.getProperty("es.host.port");

  static {
    initialiseConnection();
    registerShutDownHook();
  }

  private ConnectionManager() {}

  /**
   * This method will provide ES transport client.
   *
   * @return TransportClient
   */
  public static TransportClient getClient() {
    if (client == null) {
      ProjectLogger.log("ELastic search clinet is null " + client, LoggerEnum.INFO.name());
      initialiseConnection();
      ProjectLogger.log(
          "After calling initialiseConnection ES client value " + client, LoggerEnum.INFO.name());
    }
    return client;
  }

  /**
   * This method will create the client instance for elastic search.
   *
   * @param clusterName String
   * @param host List<String>
   * @param port List<Integer>
   * @return boolean
   * @throws UnknownHostException
   */
  private static boolean createClient(String clusterName, List<String> host)
      throws UnknownHostException {
    Builder builder = Settings.builder();
    if (clusterName != null && !"".equals(clusterName)) {
      builder = builder.put("cluster.name", clusterName);
    }
    builder = builder.put("client.transport.sniff", false);
    builder = builder.put("client.transport.ignore_cluster_name", true);
    client = new PreBuiltTransportClient(builder.build());
    for (int i = 0; i < host.size(); i++) {
      client.addTransportAddress(
          new InetSocketTransportAddress(InetAddress.getByName(host.get(i)), ports.get(i)));
      ProjectLogger.log(
          "ES Client is adding hsot and Port  " + host.get(i) + " ," + ports.get(i),
          LoggerEnum.INFO.name());
    }
    return true;
  }

  /**
   * This method will read configuration data form properties file and update the list.
   *
   * @return boolean
   */
  private static boolean initialiseConnection() {
    try {
      if (initialiseConnectionFromEnv()) {
        ProjectLogger.log("value found under system variable.", LoggerEnum.INFO.name());
        return true;
      }
      return initialiseConnectionFromPropertiesFile(cluster, hostName, port);
    } catch (Exception e) {
      ProjectLogger.log("Error while initialising elastic search connection", e);
      return false;
    }
  }

  /**
   * This method will initialize the connection from Resource properties file.
   *
   * @param cluster String cluster name
   * @param hostName String host name
   * @param port String port
   * @return boolean
   */
  public static boolean initialiseConnectionFromPropertiesFile(
      String cluster, String hostName, String port) {
    try {
      String[] splitedHost = hostName.split(",");
      for (String val : splitedHost) {
        host.add(val);
      }
      String[] splitedPort = port.split(",");
      for (String val : splitedPort) {
        ports.add(Integer.parseInt(val));
      }
      boolean response = createClient(cluster, host);
      ProjectLogger.log(
          "ES Connection Established from Properties file Cluster  "
              + cluster
              + " host "
              + hostName
              + " port "
              + port
              + " Response "
              + response,
          LoggerEnum.INFO.name());
    } catch (Exception e) {
      ProjectLogger.log("Error while initialising connection From Properties File", e);
      return false;
    }
    return true;
  }

  /**
   * This method will read configuration data form System environment variable.
   *
   * @return boolean
   */
  private static boolean initialiseConnectionFromEnv() {
    boolean response = false;
    try {
      String cluster = System.getenv(JsonKey.SUNBIRD_ES_CLUSTER);
      String hostName = System.getenv(JsonKey.SUNBIRD_ES_IP);
      String port = System.getenv(JsonKey.SUNBIRD_ES_PORT);
      if (StringUtils.isBlank(hostName) || StringUtils.isBlank(port)) {
        return false;
      }
      String[] splitedHost = hostName.split(",");
      for (String val : splitedHost) {
        host.add(val);
      }
      String[] splitedPort = port.split(",");
      for (String val : splitedPort) {
        ports.add(Integer.parseInt(val));
      }
      response = createClient(cluster, host);
      ProjectLogger.log(
          "ELASTIC SEARCH CONNECTION ESTABLISHED from EVN with Following Details cluster "
              + cluster
              + "  hostName"
              + hostName
              + " port "
              + port
              + response,
          LoggerEnum.INFO.name());
    } catch (Exception e) {
      ProjectLogger.log("Error while initialising connection from the Env", e);
      return false;
    }
    return response;
  }

  public static void closeClient() {
    client.close();
  }

  /**
   * This class will be called by registerShutDownHook to register the call inside jvm , when jvm
   * terminate it will call the run method to clean up the resource.
   *
   * @author Manzarul
   */
  public static class ResourceCleanUp extends Thread {
    @Override
    public void run() {
      client.close();
    }
  }

  /** Register the hook for resource clean up. this will be called when jvm shut down. */
  public static void registerShutDownHook() {
    Runtime runtime = Runtime.getRuntime();
    runtime.addShutdownHook(new ResourceCleanUp());
    ProjectLogger.log("ShutDownHook registered.");
  }
}
