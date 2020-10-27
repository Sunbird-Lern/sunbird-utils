package org.sunbird.migration;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import java.util.*;
import org.sunbird.migration.constants.DbColumnConstants;

public class CassandraHelper {

  /** this variable is initialized so that list can easily handle the size of user.. */
  public static final int initialCapacity = 50000;

  /**
   * these methods will be used to convert resultSet into List of Group entity Object.
   *
   * @param resultSet
   * @return
   */
  public static List<Group> getGroupFromResultSet(ResultSet resultSet) {

    List<Group> groupList = new ArrayList<>(initialCapacity);
    Iterator<Row> iterator = resultSet.iterator();
    while (iterator.hasNext()) {
      Row row = iterator.next();
      Group group =
          new Group(
              row.getString(DbColumnConstants.id),
              row.getString(DbColumnConstants.name),
              row.getString(DbColumnConstants.status));
      groupList.add(group);
    }
    return groupList;
  }

  public static List<UserGroup> getUserGroupFromResultSet(ResultSet resultSet) {

    List<UserGroup> groupList = new ArrayList<>(initialCapacity);
    Iterator<Row> iterator = resultSet.iterator();
    while (iterator.hasNext()) {
      Row row = iterator.next();
      UserGroup userGroup =
          new UserGroup(
              row.getString(DbColumnConstants.userId),
              row.getSet(DbColumnConstants.groupId, String.class));
      groupList.add(userGroup);
    }
    return groupList;
  }

  public static List<GroupMember> getGroupMemberFromResultSet(ResultSet resultSet) {

    List<GroupMember> groupList = new ArrayList<>(initialCapacity);
    Iterator<Row> iterator = resultSet.iterator();
    while (iterator.hasNext()) {
      Row row = iterator.next();
      GroupMember groupMember =
          new GroupMember(
              row.getString(DbColumnConstants.userId),
              row.getString(DbColumnConstants.groupId),
              row.getString(DbColumnConstants.status));
      groupList.add(groupMember);
    }
    return groupList;
  }

  /**
   * this method is used to prepare delete query for cassandra db..
   *
   * @return query(String)
   */
  public static String getDeleteQuery() {
    String query = getPreparedStatement();
    return query;
  }

  public static String getPreparedStatement() {
    StringBuilder query = new StringBuilder();
    query.append("DELETE  from sunbird_groups.group where id= ?");
    return query.toString();
  }

  /**
   * this method is used to prepare delete query for cassandra db..
   *
   * @return query(String)
   */
  public static String getUpdateQuery() {
    String query = getUpdatePreparedStatement();
    return query;
  }

  public static String getUpdatePreparedStatement() {
    StringBuilder query = new StringBuilder();
    query.append("UPDATE  sunbird_groups.user_group SET groupid = ? where userid= ?");
    return query.toString();
  }

  public static String getDeleteGroupQuery() {
    String query = getDeleteGroupPreparedStatement();
    return query;
  }

  public static String getDeleteGroupPreparedStatement() {
    StringBuilder query = new StringBuilder();
    query.append("DELETE  FROM sunbird_groups.group_member WHERE groupid = ? and userid= ?");
    return query.toString();
  }

  public static String getDeleteUserGroupQuery() {
    StringBuilder query = new StringBuilder();
    query.append("DELETE FROM sunbird_groups.user_group WHERE userid= ?");
    return query.toString();
  }
}
