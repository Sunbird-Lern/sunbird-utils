package org.sunbird.helper;

import com.datastax.driver.core.Session;

/**
 * Interface for cassandra connection manager , implementation would be Standalone and Embedde cassandra connection manager .
 * Created by arvind on 11/10/17.
 */
public interface CassandraConnectionManager {

  /**
   * Method to create the cassandra connection .
   * @param ip
   * @param port
   * @param userName
   * @param password
   * @param keyspace
   * @return
   */
  boolean createConnection(String ip,String port,String userName,String password,String keyspace);

  /**
   * Method to get the cassandra session oject on basis of keyspace name provided .
   * @param keyspaceName
   * @return
   */
  Session getSession(String keyspaceName);

}
