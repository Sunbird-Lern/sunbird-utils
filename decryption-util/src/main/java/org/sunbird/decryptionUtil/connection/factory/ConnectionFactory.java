package org.sunbird.decryptionUtil.connection.factory;


import org.sunbird.decryptionUtil.connection.Connection;

/** @author anmolgupta */
public interface ConnectionFactory {

  Connection getConnection(String hostName, String keyspaceName, String port);
}
