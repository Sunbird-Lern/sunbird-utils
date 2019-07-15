package org.sunbird.decryptionUtil.connection.factory;


import org.sunbird.decryptionUtil.connection.CassandraConnection;
import org.sunbird.decryptionUtil.connection.Connection;

public class CassandraConnectionFactory implements ConnectionFactory {
  @Override
  public Connection getConnection(String hostName, String keyspaceName, String port) {
    return new CassandraConnection(hostName, keyspaceName, port);
  }
}
