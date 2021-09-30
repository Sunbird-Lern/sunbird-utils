package org.sunbird.migration.connection;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

/**
 * this is an interface class for connection with db..
 *
 * @author anmolgupta
 */
public interface Connection {

  /**
   * this method will return the resultSet of the queried data
   *
   * @param query
   * @return ResultSet
   */
  public ResultSet getRecords(String query);

  /** this method will be responsible to close the db connection */
  public void closeConnection();

  /**
   * this method is used to insert the user record into db
   *
   * @return boolean
   */
  public boolean insertRecord(String query);

  public Session getSession();
}
