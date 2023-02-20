package com.contrastsecurity.cassandra.migration.utils;

import com.contrastsecurity.cassandra.migration.CassandraMigration;
import com.contrastsecurity.cassandra.migration.config.Keyspace;
import com.contrastsecurity.cassandra.migration.logging.Log;
import com.contrastsecurity.cassandra.migration.logging.LogFactory;
import com.contrastsecurity.cassandra.migration.logging.console.ConsoleLog;
import com.contrastsecurity.cassandra.migration.logging.console.ConsoleLogCreator;

public class MigrationScriptEntryPoint {

    private static String CASSANDRA__KEYSPACE = "sunbird";
    private static int CASSANDRA_PORT = 9042;
    private static String[] CASSANDRA_HOST;
    private static String CASSANDRA_USER_NAME;
    private static String CASSANDRA_PASSWORD;
    private static final String SUNBIRD_CASSANDRA_PORT = "sunbird_cassandra_port";
    private static final String SUNBIRD_CASSANDRA_HOST = "sunbird_cassandra_host";
    private static final String SUNBIRD_CASSANDRA_USERNAME = "sunbird_cassandra_username";
    private static final String SUNBIRD_CASSANDRA_PASSWORD = "sunbird_cassandra_password";
    private static final String SUNBIRD_CASSANDRA_KEYSPACE = "sunbird_cassandra_keyspace";
    //  private static final String[] SCRIPT_LOCATIONS = {"db/migration/cassandra"};
    private static final String[] SCRIPT_LOCATIONS = {System.getenv("sunbird_cassandra_migration_location")};

    /**
     * logging support
     */
    private static Log LOG;

    /**
     * main method to run cassandra migration; |
     *
     * @param args
     */
    public static void main(String[] args) {
        initLogging(ConsoleLog.Level.INFO);
        LOG.info("Migration started at ==" + System.currentTimeMillis());
        try {
            init();
            Keyspace keyspace = createSpaces();
            CassandraMigration cm = new CassandraMigration();
            cm.getConfigs().setScriptsLocations(SCRIPT_LOCATIONS);
            cm.setKeyspace(keyspace);
            // cm.validate();
            cm.migrate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info("Migration Completed at ==" + System.currentTimeMillis());
    }

    /**
     * This method will initialize cassandra db connection configuration values.
     *
     * @throws Exception
     */
    public static void init() throws Exception {
        String host = System.getenv(SUNBIRD_CASSANDRA_HOST);
        String port = System.getenv(SUNBIRD_CASSANDRA_PORT);
        String userName = System.getenv(SUNBIRD_CASSANDRA_USERNAME);
        String password = System.getenv(SUNBIRD_CASSANDRA_PASSWORD);
        String keyspace = System.getenv(SUNBIRD_CASSANDRA_KEYSPACE);
        if (host == null || port == null || "".equals(host.trim()) || "".equals(port.trim())) {
            Exception e = new Exception("Cassandra configuration values are not set");
            throw e;
        } else {
            CASSANDRA_HOST = host.split(",");
            CASSANDRA_PORT = Integer.parseInt(port.split(",")[0]);
            CASSANDRA_USER_NAME = userName;
            CASSANDRA_PASSWORD = password;
            if (keyspace != null && !"".equals(keyspace.trim())) {
                CASSANDRA__KEYSPACE = keyspace;
            }
        }
    }

    /**
     * This method will create keyspace
     *
     * @return Keyspace
     */
    public static Keyspace createSpaces() {
        Keyspace keyspace = new Keyspace();
        keyspace.setName(CASSANDRA__KEYSPACE);
        keyspace.getCluster().setContactpoints(CASSANDRA_HOST);
        keyspace.getCluster().setPort(CASSANDRA_PORT);
        if (CASSANDRA_USER_NAME != null
                && !"".equals(CASSANDRA_USER_NAME.trim())
                && CASSANDRA_PASSWORD != null
                && !"".equals(CASSANDRA_PASSWORD.trim())) {
            keyspace.getCluster().setUsername(CASSANDRA_USER_NAME);
            keyspace.getCluster().setPassword(CASSANDRA_PASSWORD);
        }
        return keyspace;
    }

    static void initLogging(ConsoleLog.Level level) {
        LogFactory.setLogCreator(new ConsoleLogCreator(level));
        LOG = LogFactory.getLog(MigrationScriptEntryPoint.class);
    }
}
