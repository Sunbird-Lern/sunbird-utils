package org.sunbird.helper;

import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.cassandraimpl.CassandraDACImpl;
import org.sunbird.common.request.RequestContext;

/**
 * This class will provide cassandraOperationImpl instance.
 *
 * @author Manzarul
 */
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
      synchronized (ServiceFactory.class) {
        if (null == operation) {
          operation = new CassandraDACImpl();
        }
      }
    }
    return operation;
  }

  public static CassandraOperation getInstance(RequestContext context) {
    return new CassandraDACImpl(context);
  }

  public CassandraOperation readResolve() {
    return getInstance();
  }
}
