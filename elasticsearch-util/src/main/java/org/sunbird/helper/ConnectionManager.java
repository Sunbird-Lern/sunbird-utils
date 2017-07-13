/**
 * 
 */
package org.sunbird.helper;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LogHelper;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;

/**
 * This class will manage connection.
 * @author Manzarul
 */
public class ConnectionManager {
	private static final LogHelper LOGGER = LogHelper.getInstance(ConnectionManager.class.getName());
	private static TransportClient client = null;
	private static List<String> host = new ArrayList<>();
	private static List<Integer> ports = new ArrayList<>();
	static {
		initialiseConnection();
		registerShutDownHook();
	}
	
	public static TransportClient getClient() {
		if (client == null) {
			initialiseConnection();
		}
		return client;
	}
   
   /**
    * This method will create the client instance for elastic search.
    * @param clusterName String
    * @param host  List<String>
    * @param port List<Integer>
    * @return boolean
    * @throws Exception
    */
	private static boolean createClient(String clusterName, List<String> host, List<Integer> port) throws Exception {
		Builder builder = Settings.builder();
		if (clusterName != null && !"".equals(clusterName)) {
			builder = builder.put("cluster.name", clusterName);
		}
		builder = builder.put("client.transport.sniff", true);
		builder = builder.put("client.transport.ignore_cluster_name", true);
		client = new PreBuiltTransportClient(builder.build());
		for (int i = 0; i < host.size(); i++) {
			client.addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName(host.get(i)), ports.get(i)));
		}
		return true;
	}
  
   /**
    * This method will read configuration data form properties file and update the list.
    * @return boolean
    */
	private static boolean initialiseConnection() {
		try {
			if(initialiseConnectionFromEnv()) {
				LOGGER.info("value found under system variable.");
				ProjectLogger.log("value found under system variable.");
				return true;
			}
			PropertiesCache propertiesCache = PropertiesCache.getInstance();
			String cluster = propertiesCache.getProperty("es.cluster.name");
			String hostName = propertiesCache.getProperty("es.host.name");
			String port = propertiesCache.getProperty("es.host.port");
			String splitedHost[] = hostName.split(",");
			for (String val : splitedHost) {
				host.add(val);
			}
			String splitedPort[] = port.split(",");
			for (String val : splitedPort) {
				ports.add(Integer.parseInt(val));
			}
			boolean response = createClient(cluster, host, ports);
			LOGGER.info("ELASTIC SEARCH CONNECTION ESTABLISHED " + response);
			ProjectLogger.log("ELASTIC SEARCH CONNECTION ESTABLISHED " + response);
		} catch (Exception e) {
			LOGGER.error(e);
			ProjectLogger.log("Error while initialising connection"+e);
			return false;
		}
		return true;
	}
	
	
	/**
	    * This method will read configuration data form System environment variable.
	    * @return boolean
	    */
	private static boolean initialiseConnectionFromEnv() {
		try {
			String cluster = System.getenv(JsonKey.SUNBIRD_ES_CLUSTER);
			String hostName = System.getenv(JsonKey.SUNBIRD_ES_IP);
			String port = System.getenv(JsonKey.SUNBIRD_ES_PORT);
			if(ProjectUtil.isStringNullOREmpty(hostName) || ProjectUtil.isStringNullOREmpty(port)) {
				return false;
			}
			String splitedHost[] = hostName.split(",");
			for (String val : splitedHost) {
				host.add(val);
			}
			String splitedPort[] = port.split(",");
			for (String val : splitedPort) {
				ports.add(Integer.parseInt(val));
			}
			boolean response = createClient(cluster, host, ports);
			LOGGER.info("ELASTIC SEARCH CONNECTION ESTABLISHED " + response);
			ProjectLogger.log("ELASTIC SEARCH CONNECTION ESTABLISHED " + response);
		} catch (Exception e) {
			LOGGER.error(e);
		    ProjectLogger.log("Error while initialising connection from the Env",e);
			return false;
		}
		return true;
	}
	
	public static void closeClient() {
		client.close();
	}
	
	/**
	 * This class will be called by registerShutDownHook to 
	 * register the call inside jvm , when jvm terminate it will call
	 * the run method to clean up the resource.
	 * @author Manzarul
	 *
	 */
	static class ResourceCleanUp extends Thread {
		  public void run() {
			  LOGGER.info("started resource cleanup.");
			  ProjectLogger.log("started resource cleanup.");
			  client.close(); 
			  LOGGER.info("completed resource cleanup.");
			  ProjectLogger.log("completed resource cleanup.");
		  }
	}
	
	/**
	 * Register the hook for resource clean up.
	 * this will be called when jvm shut down.
	 */
	public static void registerShutDownHook() {
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new ResourceCleanUp());
		LOGGER.info("ShutDownHook registered.");
		ProjectLogger.log("ShutDownHook registered.");
	}

}
