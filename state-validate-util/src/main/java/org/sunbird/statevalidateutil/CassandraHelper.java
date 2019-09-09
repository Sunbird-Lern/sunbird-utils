package org.sunbird.statevalidateutil;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import java.sql.Timestamp;
import java.util.*;


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
      User user = (User) row;
      userList.add(user);
    }
    return userList;
  }

  /**
   * this method is used to prepare insertQuery for cassandra db..
   *
   * @param user
   * @return query(String)
   */
  public static String getInsertRecordQuery(User user) {
    return String.format(
        "UPDATE sunbird.user set flagsValue='%s' where userid='%s'",
        user.getFlagsValue(),
        user.getUserId());
  }

  /**
   * this method is used to get the cql timestamp from date
   *
   * @param date
   * @return long
   */
  private static long getTimeStampFromDate(Date date) {
    return date != null
        ? new Timestamp(date.getTime()).getTime()
        : new Timestamp(Calendar.getInstance().getTime().getTime()).getTime();
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
