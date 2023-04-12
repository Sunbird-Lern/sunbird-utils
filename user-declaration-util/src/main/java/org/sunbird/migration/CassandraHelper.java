package org.sunbird.migration;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
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
  public static List<UserDeclareEntity> getUserListFromResultSet(ResultSet resultSet) {

    List<UserDeclareEntity> userList = new ArrayList<>(initialCapacity);
    Iterator<Row> iterator = resultSet.iterator();
    while (iterator.hasNext()) {
      Row row = iterator.next();
      UserDeclareEntity user =
          new UserDeclareEntity(
              row.getString(DbColumnConstants.userId),
              row.getString(DbColumnConstants.orgId),
              row.getString(DbColumnConstants.persona),
              (Map<String, Object>) row.getObject(DbColumnConstants.userinfo),
              row.getString(DbColumnConstants.status));
      userList.add(user);
    }
    return userList;
  }

  /**
   * this method is used to prepare updateQuery for cassandra db..
   *
   * @return query(String)
   */
  public static String getUpdateRecordQuery() {
    String query = getPreparedStatement();
    return query;
  }

  public static String getPreparedStatement() {
    StringBuilder query = new StringBuilder();
    query.append(
            "INSERT INTO sunbird.user_feed (id, category,createdby, createdon, data, expireon, priority, priority,status,updatedby, updatedon, userid) values(?,?,?,?,?,?,?,?,?,?,?)");
    return query.toString();
  }
}
