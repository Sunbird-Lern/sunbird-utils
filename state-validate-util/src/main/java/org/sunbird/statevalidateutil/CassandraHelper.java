package org.sunbird.statevalidateutil;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import org.sunbird.statevalidateutil.constants.DbColumnConstants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
      User user = new User(row.getBool(DbColumnConstants.emailVerified),
              row.getBool(DbColumnConstants.phoneVerified),
              row.getString(DbColumnConstants.userId));
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
  public static String getUpdateRecordQuery(User user) {
    return String.format(
        "UPDATE sunbird.user set flagsValue=%d, updatedDate='%s' where id='%s'",
        user.getFlagsValue(), getFormattedDate(), user.getUserId());
  }


  /** This method returns date of format "yyyy-MM-dd HH:mm:ss:SSSZ"
   * @return
   */
  public static String getFormattedDate() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSZ");
    simpleDateFormat.setLenient(false);
    return simpleDateFormat.format(new Date());
  }
}
