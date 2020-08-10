package org.sunbird.migration.connection.factory;


import org.sunbird.migration.connection.CassandraConnection;
import org.sunbird.migration.connection.Connection;

public class CassandraConnectionFactory implements ConnectionFactory {
  @Override
  public Connection getConnection(String hostName, String keyspaceName, String port) {
    return new CassandraConnection(hostName, keyspaceName, port);
  }
}
