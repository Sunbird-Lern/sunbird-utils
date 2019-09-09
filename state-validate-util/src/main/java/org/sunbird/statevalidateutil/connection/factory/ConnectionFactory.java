package org.sunbird.statevalidateutil.connection.factory;


import org.sunbird.statevalidateutil.connection.Connection;

/** @author anmolgupta */
public interface ConnectionFactory {

  Connection getConnection(String hostName, String keyspaceName, String port);
}
