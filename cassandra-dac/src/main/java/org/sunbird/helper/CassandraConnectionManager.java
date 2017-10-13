package org.sunbird.helper;

import com.datastax.driver.core.Session;

/**
 * Created by arvind on 11/10/17.
 */
public interface CassandraConnectionManager {

  boolean createConnection(String ip,String port,String userName,String password,String keyspace);
  Session getSession(String keyspaceName);

}
