package org.sunbird.helper;

import com.datastax.driver.core.AtomicMonotonicTimestampGenerator;
import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.QueryLogger;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.sunbird.common.Constants;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * Created by arvind on 11/10/17.
 */
public class CassandraConnectionManagerImpl implements CassandraConnectionManager {

  private String mode;
  private static Map<String, Session> cassandraSessionMap = new HashMap<>();
  private static Map<String, Cluster> cassandraclusterMap = new HashMap<>();

  static{
    registerShutDownHook();
  }

  public CassandraConnectionManagerImpl(String mode) {
    this.mode = mode;
  }

  @Override
  public boolean createConnection(String ip, String port, String userName, String password,
      String keyspace) {
    boolean connectionCreated = false;
    if (mode.equalsIgnoreCase(JsonKey.EMBEDDED_MODE)) {
      return createEmbeddedConnection(ip, port, userName, password,
          keyspace);
    }
    if (mode.equalsIgnoreCase(JsonKey.STANDALONE_MODE)) {
      return createStandaloneConnection(ip, port, userName, password,
          keyspace);
    }
    return connectionCreated;
  }

  private boolean createStandaloneConnection(String ip, String port, String userName,
      String password,
      String keyspace) {

    Session cassandraSession = null;
    boolean connection = false;
    Cluster cluster = null;
    //Session session = null;
    try {
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
            .setHeartbeatIntervalSeconds(
                Integer.parseInt(cache.getProperty(Constants.HEARTBEAT_INTERVAL)));
        poolingOptions
            .setPoolTimeoutMillis(Integer.parseInt(cache.getProperty(Constants.POOL_TIMEOUT)));
        if (!ProjectUtil.isStringNullOREmpty(userName) && !ProjectUtil
            .isStringNullOREmpty(password)) {
          cluster = createCluster(ip, port, userName, password, poolingOptions);
        } else {
          cluster = createCluster(ip, port, poolingOptions);
        }
        QueryLogger queryLogger = QueryLogger.builder()
            .withConstantThreshold(
                Integer.parseInt(cache.getProperty(Constants.QUERY_LOGGER_THRESHOLD)))
            .build();
        cluster.register(queryLogger);
        cassandraSession = cluster.connect(keyspace);

        if (null != cassandraSession) {
          connection = true;
          cassandraSessionMap.put(keyspace, cassandraSession);
          cassandraclusterMap.put(keyspace, cluster);
        }
        final Metadata metadata = cluster.getMetadata();
        String msg = String.format("Connected to cluster: %s", metadata.getClusterName());
        ProjectLogger.log(msg);

        for (final Host host : metadata.getAllHosts()) {
          msg = String
              .format("Datacenter: %s; Host: %s; Rack: %s", host.getDatacenter(), host.getAddress(),
                  host.getRack());
          ProjectLogger.log(msg);
        }
      }
    } catch (Exception e) {
      ProjectLogger.log("Error occured while creating connection :", e);
      throw new ProjectCommonException(ResponseCode.internalError.getErrorCode(), e.getMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }

    if (null != cassandraSessionMap.get(keyspace)) {
      connection = true;
    }
    return connection;


  }

  /**
   * @param ip String
   * @param port String
   * @param userName String
   * @param password String
   * @param poolingOptions PoolingOptions
   * @return Cluster Cluster
   */
  private static Cluster createCluster(String ip, String port, String userName, String password,
      PoolingOptions poolingOptions) {
    return Cluster
        .builder()
        .addContactPoint(ip)
        .withPort(Integer.parseInt(port))
        .withProtocolVersion(ProtocolVersion.V3)
        .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
        .withTimestampGenerator(new AtomicMonotonicTimestampGenerator())
        .withPoolingOptions(poolingOptions).withCredentials(userName, password).build();
  }

  /**
   * @param ip String
   * @param port String
   * @param poolingOptions PoolingOptions
   * @return Cluster Cluster
   */
  private static Cluster createCluster(String ip, String port, PoolingOptions poolingOptions) {
    return Cluster
        .builder()
        .addContactPoint(ip)
        .withPort(Integer.parseInt(port))
        .withProtocolVersion(ProtocolVersion.V3)
        .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
        .withTimestampGenerator(new AtomicMonotonicTimestampGenerator())
        .withPoolingOptions(poolingOptions).build();
  }

  private boolean createEmbeddedConnection(String ip, String port, String userName, String password,
      String keyspace) {

    Session cassandraSession = null;

    PropertiesCache propertiesCache = PropertiesCache.getInstance();
    boolean connection = false;

    if (null == cassandraSessionMap.get(keyspace)) {
      try {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra(Long.parseLong(propertiesCache.getProperty("embeddedCassandra_TimeOut")));
        Cluster cluster = new Cluster.Builder().addContactPoints("127.0.0.1").withPort(9142)
            .build();
        cassandraSession = cluster.connect();
        CQLDataLoader dataLoader = new CQLDataLoader(cassandraSession);
        System.out.println("loading data .....");
        dataLoader.load(new ClassPathCQLDataSet("cassandra.cql", keyspace));
        //session = dataLoader.getSession();
        System.out.println("EMBEDDED SESSION IS " + cassandraSession);
        if (null != cassandraSession) {
          cassandraSessionMap.put(keyspace, cassandraSession);
          cassandraclusterMap.put(keyspace, cluster);
        }
      } catch (TTransportException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (null != cassandraSessionMap.get(keyspace)) {
      connection = true;
    }
    return connection;


  }

  @Override
  public Session getSession(String keyspaceName) {
    if (null == cassandraSessionMap.get(keyspaceName)) {
      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(), Constants.SESSION_IS_NULL + keyspaceName,
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    return cassandraSessionMap.get(keyspaceName);
  }

  /**
   * Register the hook for resource clean up.
   * this will be called when jvm shut down.
   */
  public static void registerShutDownHook() {
    Runtime runtime = Runtime.getRuntime();
    runtime.addShutdownHook(new ResourceCleanUp());
    ProjectLogger.log("Cassandra ShutDownHook registered.");
  }

  /**
   * This class will be called by registerShutDownHook to
   * register the call inside jvm , when jvm terminate it will call
   * the run method to clean up the resource.
   */
  static class ResourceCleanUp extends Thread {
    @Override
    public void run() {
      ProjectLogger.log("started resource cleanup Cassandra.");
      for(Map.Entry<String,Session> entry: cassandraSessionMap.entrySet()){
        cassandraSessionMap.get(entry.getKey()).close();
        cassandraclusterMap.get(entry.getKey()).close();
      }
      ProjectLogger.log("completed resource cleanup Cassandra.");
    }
  }
}
