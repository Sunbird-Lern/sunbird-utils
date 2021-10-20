package org.sunbird.migration.constants;

/**
 * this class has constants which is required to set as env.
 *
 * @author anmolgupta
 */
public class EnvConstants {
  public static final String SUNBIRD_CASSANDRA_HOST = "sunbird_cassandra_host";
  public static final String SUNBIRD_CASSANDRA_LEARNER_KEYSPACENAME =
      "sunbird_cassandra_keyspace";
  public static final String SUNBIRD_CASSANDRA_PORT = "sunbird_cassandra_port";
  public static final String PRE_PROCESSED_RECORDS_FILE_USER_FEED =
      "preProcessedRecords.txt";

  public static final String FAILED_FEED_MIGRATION_OPERATION = "user_feed_migration_failed.txt";
  public static final String SUNBIRD_CASSANDRA_NOTIFICATION_KEYSPACENAME = "sunbird_cassandra_notification_keyspace";
}
