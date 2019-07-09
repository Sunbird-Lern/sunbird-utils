package org.sunbird.externaliddecryption.connection.factory;

import org.sunbird.externaliddecryption.connection.Connection;

/** @author anmolgupta */
public interface ConnectionFactory {

  Connection getConnection(String hostName, String keyspaceName, String port);
}
