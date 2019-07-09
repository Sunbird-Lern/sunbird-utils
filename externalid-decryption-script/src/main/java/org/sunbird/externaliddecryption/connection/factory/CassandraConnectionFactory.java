package org.sunbird.externaliddecryption.connection.factory;

import org.sunbird.externaliddecryption.connection.CassandraConnection;
import org.sunbird.externaliddecryption.connection.Connection;

public class CassandraConnectionFactory implements ConnectionFactory {
  @Override
  public Connection getConnection(String hostName, String keyspaceName, String port) {
    return new CassandraConnection(hostName, keyspaceName, port);
  }
}
