/**
 * 
 */
package org.sunbird.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sunbird.cassandraimpl.CassandraOperationImpl;
import org.sunbird.common.Constants;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;

import com.datastax.driver.core.AtomicMonotonicTimestampGenerator;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.QueryLogger;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;

/**
 * This class will handle cassandra database 
 * connection and session.
 * @author Manzarul
 * @author Amit Kumar
 */
public final class CassandraConnectionManager {
	private final static Logger LOGGER = Logger.getLogger(CassandraOperationImpl.class.getName());
	//Map<keySpaceName,Session>
    private static Map<String,Session> cassandraSessionMap = new HashMap<>();
    private static Map<String,Cluster> cassandraclusterMap = new HashMap<>();
    static{
    	registerShutDownHook();
    }
    /**
     * This method create connection for given keyspace 
     * @param ip String
     * @param port String
     * @param userName String
     * @param password String
     * @return boolean Boolean
     * 
     */
	public static boolean createConnection(String ip,String port,String userName,String password,String keyspace){
		boolean connection = false;
		Cluster cluster= null;
		Session session = null;
		try{
			if (null == cassandraSessionMap.get(keyspace)) {
				PropertiesCache cache = PropertiesCache.getInstance();
				PoolingOptions poolingOptions = new PoolingOptions();
				poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL,
						Integer.parseInt(cache.getProperty(Constants.CORE_CONNECTIONS_PER_HOST_FOR_LOCAL)));
				poolingOptions.setMaxConnectionsPerHost(HostDistance.LOCAL,
						Integer.parseInt(cache.getProperty(Constants.MAX_CONNECTIONS_PER_HOST_FOR_LOCAl)));
				poolingOptions.setCoreConnectionsPerHost(HostDistance.REMOTE,
						Integer.parseInt(cache.getProperty(Constants.CORE_CONNECTIONS_PER_HOST_FOR_REMOTE)));
				poolingOptions.setMaxConnectionsPerHost(HostDistance.REMOTE,
						Integer.parseInt(cache.getProperty(Constants.MAX_CONNECTIONS_PER_HOST_FOR_REMOTE)));
				poolingOptions.setMaxRequestsPerConnection(HostDistance.LOCAL,
						Integer.parseInt(cache.getProperty(Constants.MAX_REQUEST_PER_CONNECTION)));
				poolingOptions
						.setHeartbeatIntervalSeconds(Integer.parseInt(cache.getProperty(Constants.HEARTBEAT_INTERVAL)));
				poolingOptions.setPoolTimeoutMillis(Integer.parseInt(cache.getProperty(Constants.POOL_TIMEOUT)));
				if (!ProjectUtil.isStringNullOREmpty(userName) && !ProjectUtil.isStringNullOREmpty(password)) {
					cluster = createCluster(ip, port, userName, password, poolingOptions);
				} else {
					cluster = createCluster(ip, port, poolingOptions);
				}
				QueryLogger queryLogger = QueryLogger.builder()
						.withConstantThreshold(Integer.parseInt(cache.getProperty(Constants.QUERY_LOGGER_THRESHOLD)))
						.build();
				cluster.register(queryLogger);
				session = cluster.connect(keyspace);

				if (null != session) {
					connection = true;
					cassandraSessionMap.put(keyspace, session);
					cassandraclusterMap.put(keyspace, cluster);
				}
				final Metadata metadata = cluster.getMetadata();
				String msg = String.format("Connected to cluster: %s", metadata.getClusterName());
				LOGGER.debug(msg);

				for (final Host host : metadata.getAllHosts()) {
					msg = String.format("Datacenter: %s; Host: %s; Rack: %s", host.getDatacenter(), host.getAddress(),
							host.getRack());
					LOGGER.debug(msg);
				}
			}
			   }catch(Exception e){
				   LOGGER.error(e);
				   throw new ProjectCommonException(ResponseCode.internalError.getErrorCode(), e.getMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
			   }
		        
		        if(null != cassandraSessionMap.get(keyspace)){
		        	connection = true;
		        }
		        return connection;
	}
	
	/**
	 * This method provide session for given keyspace
	 * @param  keyspaceName String
	 * @return Session Session
	 */
	public static Session getSession(String keyspaceName) {
		if(null == cassandraSessionMap.get(keyspaceName)){
			throw new ProjectCommonException(ResponseCode.internalError.getErrorCode(), Constants.SESSION_IS_NULL+keyspaceName, ResponseCode.SERVER_ERROR.getResponseCode());
		}
        return cassandraSessionMap.get(keyspaceName);
    }
 
   
	/**
	 * 
	 * @param ip String
	 * @param port String
	 * @param userName String
	 * @param password String
	 * @param poolingOptions PoolingOptions
	 * @return Cluster Cluster
	 */
	private static Cluster createCluster(String ip,String port,String userName, String password,PoolingOptions poolingOptions){
		return Cluster
				.builder()
				.addContactPoint(ip)
				.withPort(Integer.parseInt(port))
				.withProtocolVersion(ProtocolVersion.V3)
				.withRetryPolicy(DefaultRetryPolicy.INSTANCE)
				.withTimestampGenerator(new AtomicMonotonicTimestampGenerator())
				.withPoolingOptions(poolingOptions).withCredentials(userName,password).build();	
	}
	
	/**
	 * 
	 * @param ip String
	 * @param port String
	 * @param poolingOptions PoolingOptions
	 * @return Cluster Cluster
	 */
	private static Cluster createCluster(String ip,String port, PoolingOptions poolingOptions){
		return Cluster
		.builder()
		.addContactPoint(ip)
		.withPort(Integer.parseInt(port))
		.withProtocolVersion(ProtocolVersion.V3)
		.withRetryPolicy(DefaultRetryPolicy.INSTANCE)
		.withTimestampGenerator(new AtomicMonotonicTimestampGenerator())
		.withPoolingOptions(poolingOptions).build();
	}
	
	/**
	 * This class will be called by registerShutDownHook to 
	 * register the call inside jvm , when jvm terminate it will call
	 * the run method to clean up the resource.
	 */
	static class ResourceCleanUp extends Thread {
		  public void run() {
			  LOGGER.info("started resource cleanup Cassandra.");
			  for(String key: cassandraSessionMap.keySet()){
					cassandraSessionMap.get(key).close();
					cassandraclusterMap.get(key).close();
				}
				cassandraSessionMap=null;
				cassandraclusterMap=null;
			  LOGGER.info("completed resource cleanup Cassandra.");
		  }
	}

	/**
	 * Register the hook for resource clean up.
	 * this will be called when jvm shut down.
	 */
	public static void registerShutDownHook() {
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new ResourceCleanUp());
		LOGGER.info("Cassandra ShutDownHook registered.");
	}
	
}
