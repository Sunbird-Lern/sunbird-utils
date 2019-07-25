/** */
package org.sunbird.helper;

import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.cassandraimpl.CassandraOperationImpl;

import javax.inject.Singleton;

/**
 * This class will provide cassandraOperationImpl instance.
 *
 * @author Manzarul
 */
@Singleton
public class ServiceFactory {
  private static CassandraOperation operation = null;

  private ServiceFactory() {}

  /**
   * On call of this method , it will provide a new CassandraOperationImpl instance on each call.
   *
   * @return
   */
  public static CassandraOperation getInstance() {
    if (null == operation) {
      operation = new CassandraOperationImpl();
    }
    return operation;
  }
}
