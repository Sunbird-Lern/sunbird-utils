package org.sunbird.migration;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import java.io.IOException;
import java.util.*;
import org.apache.log4j.Logger;
import org.sunbird.migration.connection.Connection;
import org.sunbird.migration.connection.factory.ConnectionFactory;
import org.sunbird.migration.tracker.StatusTracker;

public class RecordProcessor extends StatusTracker {

  private ConnectionFactory connectionFactory;
  public Connection connection;
  private RequestParams requestParams;
  private static Logger logger = LoggerFactory.getLoggerInstance(RecordProcessor.class.getName());

  /**
   * constructor for the class
   *
   * @param connectionFactory
   * @param requestParams
   */
  private RecordProcessor(ConnectionFactory connectionFactory, RequestParams requestParams) {
    this.connectionFactory = connectionFactory;
    this.requestParams = requestParams;
    this.connection =
        connectionFactory.getConnection(
            requestParams.getCassandraHost(),
            requestParams.getCassandraKeyspaceName(),
            requestParams.getCassandraPort());
  }

  /**
   * this method should be used to get the instance of the class..
   *
   * @param connectionFactory
   * @param requestParams
   * @return
   */
  public static RecordProcessor getInstance(
      ConnectionFactory connectionFactory, RequestParams requestParams) {
    return new RecordProcessor(connectionFactory, requestParams);
  }

  /**
   * this method is responsible to fetch the group data from cassandra and the table name is group
   *
   * @return List<User>
   */
  private List<Group> getGroupLists() {
    ResultSet resultSet = connection.getRecords("select * from sunbird_groups.group");
    List<Group> groupsList = CassandraHelper.getGroupFromResultSet(resultSet);
    logger.debug(groupsList.iterator().next());
    return groupsList;
  }

  /**
   * this method is responsible to fetch the group data from cassandra and the table name is group
   *
   * @return List<UserGroup>
   */
  private List<UserGroup> getUserGroupLists() {
    ResultSet resultSet = connection.getRecords("select * from sunbird_groups.user_group");
    List<UserGroup> groupsList = CassandraHelper.getUserGroupFromResultSet(resultSet);
    logger.debug(groupsList.iterator().next());
    return groupsList;
  }

  /**
   * this method is responsible to fetch the group data from cassandra and the table name is group
   *
   * @return List<GroupMember>
   */
  private List<GroupMember> getGroupMemberLists() {
    ResultSet resultSet = connection.getRecords("select * from sunbird_groups.group_member");
    List<GroupMember> groupsList = CassandraHelper.getGroupMemberFromResultSet(resultSet);
    logger.debug(groupsList.iterator().next());
    return groupsList;
  }

  /**
   * this method will be used to process externalId this method will get the count of total number
   * of records that need to be processed
   *
   * @return
   */
  public void startProcessing() throws IOException {

    // List of Groups
    List<Group> groups = getRecordsToBeProcessed();
    int size = groups.size();
    int count1[] = new int[1];

    /** List of user groups */
    logger.info("Total Group Records: " + groups.size());

    List<UserGroup> userGroups = getUserGroupLists();
    logger.info("Total User Group Records: " + userGroups.size());

    List<GroupMember> groupMembers = getGroupMemberLists();
    logger.info("Total  Group Members Records: " + groupMembers.size());

    // Get Status Map of each Group
    Map<String, String> groupsMap = new HashMap<>();
    for (Group group : groups) {
      groupsMap.put(group.getId(), group.getStatus());
    }

    Iterator itr = userGroups.iterator();

    while (itr.hasNext()) {
      boolean remove = true;
      UserGroup userGroup = (UserGroup) itr.next();
      if (userGroup.getGroupIds() != null) {
        for (String groupId : userGroup.getGroupIds()) {
          if ("inactive".equals(groupsMap.get(groupId)) || groupsMap.get(groupId) == null) {
            remove = false;
          }
        }
      }
      if (remove) {
        itr.remove();
      }
    }

    logger.info(
        "==================Starting Processing User Group=======================================");
    userGroups
        .stream()
        .forEach(
            userGroup -> {
              try {
                startTracingUserRecord(userGroup.getUserId());
                boolean isSuccess =
                    performSequentialOperationOnUserGroupRecord(userGroup, groupsMap);
                if (isSuccess) {
                  count1[0] += 1;
                }
              } catch (Exception ex) {
                logUpdateFailedRecord(userGroup.getUserId());
              } finally {
                endTracingUserRecord(userGroup.getUserId());
              }
            });
    logger.info(
        "==================Ending Processing User Group=======================================");

    int count2[] = new int[1];

    itr = groupMembers.iterator();

    while (itr.hasNext()) {
      boolean remove = true;
      GroupMember groupMember = (GroupMember) itr.next();
      if (groupMember != null) {
        if ("inactive".equals(groupsMap.get(groupMember.getGroupId()))
            || groupsMap.get(groupMember.getGroupId()) == null) {
          remove = false;
        }
      }
      if (remove) {
        itr.remove();
      }
    }

    logger.info(
        "==================Starting Processing Group Member=======================================");
    groupMembers
        .stream()
        .forEach(
            groupMember -> {
              try {
                startTracingRecord(groupMember.getUserId());
                boolean isSuccess = performSequentialOperationOnGroupMemberRecord(groupMember);
                if (isSuccess) {
                  count2[0] += 1;
                }
              } catch (Exception ex) {
                logUpdateFailedRecord(groupMember.getUserId());
              } finally {
                endTracingRecord(groupMember.getUserId());
              }
            });
    logger.info(
        "==================Ending Processing Group Delete=======================================");

    logger.info("Purge Soft delete Groups");
    List<Group> groupsToBeDeleted = getDeletedGroups(groups);
    int count3[] = new int[1];
    logger.info(
        "==================Starting Processing Delete Groups=======================================");
    groupsToBeDeleted
        .stream()
        .forEach(
            group -> {
              try {
                startTracingRecord(group.getId());
                boolean isSuccess = performSequentialOperationOnRecord(group);
                if (isSuccess) {
                  count3[0] += 1;
                }
              } catch (Exception ex) {
                logDeleteFailedRecord(group.getId());
              } finally {
                endTracingRecord(group.getId());
              }
            });
    logger.info(
        "==================Ending Processing Group Delete=======================================");

    // Close self declare file writer
    closeFwSelfDeclaredFailureWriterConnection();
    closeFwSelfDeclaredSuccessWriterConnection();
    // Close self declare file writer
    closeUserGroupSuccessWriterConnection();
    closeUserGroupFailureWriterConnection();
    closeGroupMemberSuccessWriterConnection();
    closeGroupMemberFailureWriterConnection();
    connection.closeConnection();

    logger.info(
        " Total User group Records to be corrected: "
            + userGroups.size()
            + " Successfully User Group Records Corrected: "
            + count1[0]
            + " Failed User Group Records:"
            + (userGroups.size() - count1[0]));

    logger.info(
        " Total Group Member Records to be corrected: "
            + groupMembers.size()
            + " Successfully Group Member Records Corrected: "
            + count2[0]
            + " Failed Group Member Records:"
            + (groupMembers.size() - count2[0]));
    logger.info(
        " Total Group Records to be corrected: "
            + groupsToBeDeleted.size()
            + " Successfully  Group Records Corrected: "
            + count3[0]
            + " Failed  Group Records:"
            + (groupsToBeDeleted.size() - count3[0]));
  }

  private List<Group> getDeletedGroups(List<Group> groups) {

    List<Group> deletedGroups = new ArrayList<>();
    for (Group group : groups) {
      if ("inactive".equals(group.getStatus())) {
        deletedGroups.add(group);
      }
    }
    return deletedGroups;
  }

  /**
   * This method will delete inactive groups from the table
   *
   * @param userGroup
   */
  private boolean performSequentialOperationOnUserGroupRecord(
      UserGroup userGroup, Map<String, String> groupMap) {
    try {

      Iterator itr = userGroup.getGroupIds().iterator();
      while (itr.hasNext()) {
        String groupId = (String) itr.next();
        if ("inactive".equals(groupMap.get(groupId)) || groupMap.get(groupId) == null) {
          itr.remove();
        }
      }
      String query = "";
      if (userGroup.getGroupIds().size() < 1) {
        query = CassandraHelper.getDeleteUserGroupQuery();
      } else {
        query = CassandraHelper.getUpdateQuery();
      }
      logUserGroupQuery(query);
      PreparedStatement preparedStatement = connection.getSession().prepare(query);
      BoundStatement bs = null;
      if (userGroup.getGroupIds().size() < 1) {
        bs = preparedStatement.bind(userGroup.getUserId());
      } else {
        bs = preparedStatement.bind(userGroup.getGroupIds(), userGroup.getUserId());
      }

      connection.getSession().execute(bs);
      logUserGroupUpdateSuccessQuery(userGroup.getUserId());

    } catch (Exception ex) {
      logger.error(ex.getMessage());
      logDeleteFailedRecord(userGroup.getUserId());
      return false;
    }
    return true;
  }

  /**
   * This method will delete inactive groups from the table
   *
   * @param groupMember
   */
  private boolean performSequentialOperationOnGroupMemberRecord(GroupMember groupMember) {
    try {

      String query = CassandraHelper.getDeleteGroupQuery();
      logUserGroupQuery(query);
      PreparedStatement preparedStatement = connection.getSession().prepare(query);

      BoundStatement bs = preparedStatement.bind(groupMember.getGroupId(), groupMember.getUserId());

      connection.getSession().execute(bs);
      logUserGroupUpdateSuccessQuery(groupMember.getUserId());

    } catch (Exception ex) {
      logger.error(ex.getMessage());
      logDeleteFailedRecord(groupMember.getUserId());
      return false;
    }
    return true;
  }

  /**
   * This method will delete inactive groups from the table
   *
   * @param group
   */
  private boolean performSequentialOperationOnRecord(Group group) {

    try {

      String query = CassandraHelper.getDeleteQuery();
      logDeletedGroupQuery(query);
      PreparedStatement preparedStatement = connection.getSession().prepare(query);
      BoundStatement bs = preparedStatement.bind(group.getId());
      connection.getSession().execute(bs);
      logSuccessfulDeleteRecord(group.getId());

    } catch (Exception ex) {
      logger.error(ex.getMessage());
      logDeleteFailedRecord(group.getId());
      return false;
    }
    return true;
  }

  /**
   * this methods
   *
   * @param preProcessedRecords
   * @param totalRecords
   * @return
   */
  private List<Group> removePreProcessedRecordFromList(
      List<String> preProcessedRecords, List<Group> totalRecords) {
    totalRecords.removeIf(
        group -> (preProcessedRecords.contains(String.format("%s", group.getId()))));
    logTotalRecords(totalRecords.size());
    return totalRecords;
  }

  /**
   * this methods get the count of records from cassandra as totalUserList then this method will
   * remove the preprocessed records from totalUserList.
   *
   * @return List<String>
   * @throws IOException
   */
  private List<Group> getRecordsToBeProcessed() throws IOException {
    List<Group> groupsList = getGroupLists();
    /*  logger.info("total records in db is " + groupsList.size());
    List<String> preProcessedRecords = RecordTracker.getGroupPreProcessedRecordsAsList();
    logger.info("total records found preprocessed is " + preProcessedRecords.size());
    return removePreProcessedRecordFromList(preProcessedRecords, groupsList);*/
    return groupsList;
  }

  private List<UserGroup> getUserGroupRecords() throws IOException {
    List<UserGroup> userGroup = getUserGroupLists();
    return userGroup;
  }
}
