package org.sunbird.migration.connection.factory;

import org.sunbird.migration.connection.Connection;

/** @author anmolgupta */
public interface ConnectionFactory {

  Connection getConnection(String hostName, String keyspaceName, String port);
}
