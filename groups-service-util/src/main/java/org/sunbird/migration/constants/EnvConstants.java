package org.sunbird.migration.constants;

/**
 * this class has constants which is required to set as env.
 *
 * @author anmolgupta
 */
public class EnvConstants {
  public static final String SUNBIRD_CASSANDRA_HOST = "sunbird_cassandra_host";
  public static final String SUNBIRD_CASSANDRA_GROUPS_KEYSPACENAME =
      "sunbird_cassandra_groups_keyspace";
  public static final String SUNBIRD_CASSANDRA_PORT = "sunbird_cassandra_port";
  public static final String PRE_PROCESSED_RECORDS_FILE_DELETED_GROUPS =
      "preProcessedRecords_groups_deleted.txt";

  public static final String FAILED_DELETE_GROUP_OPERATION = "groups_delete_failed.txt";

  public static final String FAILED_DELETE_GROUP_MEMBER_OPERATION =
      "groups_member_delete_failed.txt";

  public static final String PRE_PROCESSED_UPDATED_USER_GROUPS =
      "preProcessedRecords_user_group.txt";

  public static final String FAILED_UPDATED_USER_GROUP = "user_group_failed.txt";

  public static final String PRE_PROCESSED_RECORDS_FILE_DELETED_GROUPS_MEMBER =
      "preProcessedRecords_groups_member_deleted.txt";
}
