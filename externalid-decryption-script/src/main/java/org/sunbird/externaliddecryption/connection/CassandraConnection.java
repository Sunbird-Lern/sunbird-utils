package org.sunbird.externaliddecryption.connection;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import java.util.Map;

public class CassandraConnection implements Connection {

  private static Cluster cluster;
  private static Session session;
  private String keyspaceName;
  private String host;
  private String port;

  public CassandraConnection(String host, String keyspaceName, String port) {
    this.keyspaceName = keyspaceName;
    this.host = host;
    this.port = port;
    initializeConnection();
  }

  @Override
  public ResultSet getRecords(String query) {
    ResultSet resultSet = session.execute(query);
    return resultSet;
  }

  @Override
  public void closeConnection() {
    cluster.close();
  }

  @Override
  public boolean deleteRecord(Map<String, String> compositeKeyMap) {
    Delete delete = QueryBuilder.delete().from(keyspaceName, "usr_external_identity");
    Delete.Where deleteWhere = delete.where();
    compositeKeyMap
        .entrySet()
        .stream()
        .forEach(
            x -> {
              Clause clause = QueryBuilder.eq(x.getKey(), x.getValue());
              deleteWhere.and(clause);
            });
    ResultSet resultSet = session.execute(delete);
    return resultSet.wasApplied();
  }

  @Override
  public ResultSet insertRecord(String query) {
    ResultSet resultSet = session.execute(query);
    return resultSet;
  }

  /** this method will initialize the cassandra connection */
  public void initializeConnection() {

    cluster = Cluster.builder().addContactPoint(host).build();
    session = cluster.connect(keyspaceName);
    session.execute("USE ".concat(keyspaceName));
  }
}
