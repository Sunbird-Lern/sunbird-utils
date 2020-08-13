package org.sunbird.migration.constants;

/**
 * this class has constants which is required to set as env.
 *
 * @author anmolgupta
 */
public class EnvConstants {
  public static final String SUNBIRD_CASSANDRA_HOST = "sunbird_cassandra_host";
  public static final String SUNBIRD_CASSANDRA_KEYSPACENAME = "sunbird_cassandra_keyspace";
  public static final String SUNBIRD_CASSANDRA_PORT = "sunbird_cassandra_port";
  public static final String PRE_PROCESSED_RECORDS_FILE_SELF_DECLARED =
      "preProcessedRecords_selfdeclared.txt";
  public static final String PRE_PROCESSED_RECORDS_FILE_STATE_USERS =
      "preProcessedRecords_stateusers.txt";
  public static final String FAILED_SELF_DECLARED_MIGRATION_RECORDS =
      "selfdeclared_users_failed.txt";
  public static final String FAILED_STATE_MIGRATION_RECORDS = "state_users_failed.txt";
  public static final String INVALID_RECORDS = "invalid_user_record.txt";
  public static final String DELETE_FAILED_RECORDS = "delete_failed_user_record.txt";
}
