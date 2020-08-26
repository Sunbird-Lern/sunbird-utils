package org.sunbird.migration;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import java.sql.Timestamp;
import java.util.*;
import org.sunbird.migration.constants.DbColumnConstants;

/**
 * this is a cassandra helper class used for various utility methods. provider, idtype, externalid,
 * createdby, createdon, lastupdatedby, lastupdatedon, originalexternalid, originalidtype,
 * originalprovider, userid
 *
 * @author anmolgupta
 * @count 11 keyspace sunbird table : usr_external_identity
 */
public class CassandraHelper {

  /** this variable is initialized so that list can easily handle the size of user.. */
  public static final int initialCapacity = 50000;
  /**
   * these methods will be used to convert resultSet into List of User entity Object.
   *
   * @param resultSet
   * @return
   */
  public static List<User> getUserListFromResultSet(ResultSet resultSet) {

    List<User> userList = new ArrayList<>(initialCapacity);
    Iterator<Row> iterator = resultSet.iterator();
    while (iterator.hasNext()) {
      Row row = iterator.next();
      User user =
          new User(
              row.getString(DbColumnConstants.provider),
              row.getString(DbColumnConstants.idType),
              row.getString(DbColumnConstants.externalId),
              row.getString(DbColumnConstants.createdBy),
              row.getTimestamp(DbColumnConstants.createdOn),
              row.getString(DbColumnConstants.lastUpdatedBy),
              row.getTimestamp(DbColumnConstants.lastUpdatedOn),
              row.getString(DbColumnConstants.originalExternalId),
              row.getString(DbColumnConstants.originalIdType),
              row.getString(DbColumnConstants.originalProvider),
              row.getString(DbColumnConstants.userId));
      userList.add(user);
    }
    return userList;
  }

  /**
   * these methods will be used to convert resultSet into List of User entity Object.
   *
   * @param resultSet
   * @return
   */
  public static Map<String, String> getOrgProviderMapFromResultSet(ResultSet resultSet) {
    Map<String, String> orgProviderMap = new HashMap<>();
    Iterator<Row> iterator = resultSet.iterator();
    while (iterator.hasNext()) {
      Row row = iterator.next();
      orgProviderMap.put(
          row.getString(DbColumnConstants.channel), row.getString(DbColumnConstants.id));
    }
    return orgProviderMap;
  }

  /**
   * this method is used to prepare insertQuery for cassandra db..
   *
   * @param userDeclareEntity
   * @return query(String)
   */
  public static String getInsertRecordQuery(UserDeclareEntity userDeclareEntity) {
    String query = getPreparedStatement();
    return query;
  }

  /**
   * this method is used to get the cql timestamp from date
   *
   * @param date
   * @return long
   */
  public static Timestamp getTimeStampFromDate(Date date) {
    return date != null
        ? new Timestamp(date.getTime())
        : new Timestamp(Calendar.getInstance().getTime().getTime());
  }

  public static String getPreparedStatement() {
    StringBuilder query = new StringBuilder();
    query.append(
        "INSERT INTO sunbird.user_declarations (userid, orgid, persona, status, errortype, userinfo, createdby, createdon, updatedby, updatedon) values(?,?,?,?,?,?,?,?,?,?)");
    return query.toString();
  }

  public static String getInsertRecordQueryForUser(User user, String provider) {
    return String.format(
        "INSERT INTO sunbird.usr_external_identity (provider, idtype, externalid, createdby, createdon, lastupdatedby, lastupdatedon, originalexternalid, originalidtype, originalprovider, userid) VALUES('%s','%s', '%s', '%s', %s,'%s',%s,'%s','%s','%s','%s');",
        provider,
        provider,
        user.getExternalId(),
        user.getCreatedBy(),
        getTimeStampFromDate(user.getCreatedOn()).getTime(),
        user.getLastUpdatedBy(),
        getLastUpdatedOn(),
        user.getOriginalExternalId(),
        provider,
        provider,
        user.getUserId());
  }

  /**
   * this method will give the today date timestamp and the field lastUpdatedOn will be updated by
   * today's date time...
   *
   * @return long
   */
  private static long getLastUpdatedOn() {
    return new Timestamp(Calendar.getInstance().getTime().getTime()).getTime();
  }
}
