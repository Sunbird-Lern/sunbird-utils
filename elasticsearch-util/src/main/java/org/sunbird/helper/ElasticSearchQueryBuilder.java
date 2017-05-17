/**
 * 
 */
package org.sunbird.helper;

import java.net.InetAddress;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * This class will create elasticsearch query
 * from requested search data. 
 * @author Manzarul
 *
 */
public class ElasticSearchQueryBuilder {
	Settings settings = Settings.builder()
	        .put("cluster.name", "myClusterName").build();
	
	/*TransportClient client = new PreBuiltTransportClient(settings)
	        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getLocalHost(), 9300))
	         .addTransportAddress(new InetSocketTransportAddress(InetAddress.getLocalHost(), 9300));*/
}
