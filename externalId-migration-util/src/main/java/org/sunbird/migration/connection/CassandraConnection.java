package org.sunbird.migration.connection;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.apache.log4j.Logger;
import org.sunbird.migration.LoggerFactory;
import org.sunbird.migration.constants.DbColumnConstants;
import java.util.Map;

public class CassandraConnection implements Connection {

    private static Cluster cluster;
    private static Session session;
    private String keyspaceName;
    private String host;
    private String port;
    static Logger logger = LoggerFactory.getLoggerInstance(CassandraConnection.class.getName());


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
        try {
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
        } catch (Exception e) {
            logger.error(String.format("exception occurred in deleting record %s with externalId: %s ", e.getMessage(), compositeKeyMap.get(DbColumnConstants.externalId)));
            return false;
        }
    }

    @Override
    public boolean insertRecord(String query) {
        try {
            ResultSet resultSet = session.execute(query);
            return resultSet.wasApplied();
        } catch (Exception e) {
            logger.error(String.format("exception occurred in inserting record %s with query %s ", e.getMessage(), query));
            return false;
        }
    }

    /**
     * this method will initialize the cassandra connection
     */
    public void initializeConnection() {
        String[] hostsArray=host.split(",");
        cluster = Cluster.builder().addContactPoints(hostsArray).withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();
        session = cluster.connect(keyspaceName);
        session.execute("USE ".concat(keyspaceName));
        logger.info(String.format("cassandra connection created %s", session));
    }

    @Override
    public Session getSession(){
        return  session;
    }
}
