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
import org.sunbird.common.models.util.LogHelper;

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
			builder.put("cluster.name", clusterName);
		}
		builder.put("client.transport.sniff", true);
		client = new PreBuiltTransportClient(Settings.builder().build());
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
		} catch (Exception e) {
			LOGGER.error(e);
			return false;
		}
		return true;
	}
	
	public static void closeClient() {
		client.close();
	}
}
