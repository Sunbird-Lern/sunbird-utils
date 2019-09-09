package org.sunbird.statevalidateutil.connection.factory;


import org.sunbird.statevalidateutil.connection.CassandraConnection;
import org.sunbird.statevalidateutil.connection.Connection;

public class CassandraConnectionFactory implements ConnectionFactory {
  @Override
  public Connection getConnection(String hostName, String keyspaceName, String port) {
    return new CassandraConnection(hostName, keyspaceName, port);
  }
}
