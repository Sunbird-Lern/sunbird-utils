/** */
package org.sunbird.util.authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.helper.ServiceFactory;
import org.sunbird.services.sso.SSOManager;
import org.sunbird.services.sso.SSOServiceFactory;

/**
 * This class will handle all the method related to authentication. For example verifying user
 * access token, creating access token after success login.
 *
 * @author Manzarul
 */
public class AuthenticationHelper {
  static {
    // Application.checkCassandraConnection();
  }

  private static boolean ssoEnabled =
      (StringUtils.isNotBlank(System.getenv(JsonKey.SSO_PUBLIC_KEY))
          && Boolean.parseBoolean(
              PropertiesCache.getInstance().getProperty(JsonKey.IS_SSO_ENABLED)));
  private static CassandraOperation cassandraOperation = ServiceFactory.getInstance();
  private static final String USER_AUTH_TABLE_NAME = "user_auth";
  private static final String MASTER_KEY_TABLE_NAME = "client_info";
  private static final String USER_TABLE_NAME = "user";
  private static final String KEYSPACE = "sunbird";

  /**
   * This method will verify the incoming user access token against store data base /cache. If token
   * is valid then it would be associated with some user id. In case of token matched it will
   * provide user id. else will provide empty string.
   *
   * @param token String
   * @return String
   */
  @SuppressWarnings("unchecked")
  public static String verifyUserAccesToken(String token) {
    SSOManager ssoManager = SSOServiceFactory.getInstance();
    String userId = JsonKey.UNAUTHORIZED;
    try {
      if (ssoEnabled) {
        userId = ssoManager.verifyToken(token);
      } else {
        Response authResponse =
            cassandraOperation.getRecordById(KEYSPACE, USER_AUTH_TABLE_NAME, token);
        if (authResponse != null && authResponse.get(JsonKey.RESPONSE) != null) {
          List<Map<String, Object>> authList =
              (List<Map<String, Object>>) authResponse.get(JsonKey.RESPONSE);
          if (authList != null && !authList.isEmpty()) {
            Map<String, Object> authMap = authList.get(0);
            userId = (String) authMap.get(JsonKey.USER_ID);
          }
        }
      }
    } catch (Exception e) {
      ProjectLogger.log("invalid auth token =" + token, e);
    }
    return userId;
  }

  @SuppressWarnings("unchecked")
  public static String verifyClientAccessToken(String clientId, String clientToken) {
    Map<String, Object> propertyMap = new HashMap<>();
    propertyMap.put(JsonKey.ID, clientId);
    propertyMap.put(JsonKey.MASTER_KEY, clientToken);
    String validClientId = JsonKey.UNAUTHORIZED;
    try {
      Response clientResponse =
          cassandraOperation.getRecordsByProperties(KEYSPACE, MASTER_KEY_TABLE_NAME, propertyMap);
      if (null != clientResponse && !clientResponse.getResult().isEmpty()) {
        List<Map<String, Object>> dataList =
            (List<Map<String, Object>>) clientResponse.getResult().get(JsonKey.RESPONSE);
        validClientId = (String) dataList.get(0).get(JsonKey.ID);
      }
    } catch (Exception e) {
      ProjectLogger.log("Validating client token failed due to : ", e);
    }
    return validClientId;
  }

  public static Map<String, Object> getClientAccessTokenDetail(String clientId) {

    Map<String, Object> response = null;
    Map<String, Object> propertyMap = new HashMap<>();
    propertyMap.put(JsonKey.ID, clientId);
    try {
      Response clientResponse =
          cassandraOperation.getRecordById(KEYSPACE, MASTER_KEY_TABLE_NAME, clientId);
      if (null != clientResponse && !clientResponse.getResult().isEmpty()) {
        List<Map<String, Object>> dataList =
            (List<Map<String, Object>>) clientResponse.getResult().get(JsonKey.RESPONSE);
        response = dataList.get(0);
      }
    } catch (Exception e) {
      ProjectLogger.log("Validating client token failed due to : ", e);
    }
    return response;
  }

  public static Map<String, Object> getUserDetail(String userId) {

    Map<String, Object> response = null;
    try {
      Response userResponse = cassandraOperation.getRecordById(KEYSPACE, USER_TABLE_NAME, userId);
      if (null != userResponse && !userResponse.getResult().isEmpty()) {
        List<Map<String, Object>> dataList =
            (List<Map<String, Object>>) userResponse.getResult().get(JsonKey.RESPONSE);
        response = dataList.get(0);
      }
    } catch (Exception e) {
      ProjectLogger.log("fetching user for id " + userId + " failed due to : ", e);
    }
    return response;
  }

  public static Map<String, Object> getOrgDetail(String orgId) {

    Map<String, Object> response = null;
    try {
      Response userResponse = cassandraOperation.getRecordById(KEYSPACE, USER_TABLE_NAME, orgId);
      if (null != userResponse && !userResponse.getResult().isEmpty()) {
        List<Map<String, Object>> dataList =
            (List<Map<String, Object>>) userResponse.getResult().get(JsonKey.RESPONSE);
        response = dataList.get(0);
      }
    } catch (Exception e) {
      ProjectLogger.log("fetching user for id " + orgId + " failed due to : ", e);
    }
    return response;
  }

  /**
   * This method will save the user access token in side data base.
   *
   * @param token String
   * @param userId String
   * @return boolean
   */
  public static boolean saveUserAccessToken(String token, String userId) {

    return false;
  }

  /**
   * This method will invalidate the user access token.
   *
   * @param token String
   * @return boolean
   */
  public static boolean invalidateToken(String token) {

    return false;
  }

  public static Map<String, Object> getUserFromExternalIdAndProvider(
      String externalId, String provider) {
    String keyspace = "sunbird";
    String userExtTable = "user_external_identity";

    Map<String, Object> user = null;
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.PROVIDER, (provider).toLowerCase());
    map.put(JsonKey.EXTERNAL_ID, (externalId).toLowerCase());
    Response response = cassandraOperation.getRecordsByProperties(keyspace, userExtTable, map);
    List<Map<String, Object>> userRecordList =
        (List<Map<String, Object>>) response.get(JsonKey.RESPONSE);
    if (CollectionUtils.isNotEmpty(userRecordList)) {
      Map<String, Object> userExtIdRecord = userRecordList.get(0);
      Response res =
          cassandraOperation.getRecordById(
              KEYSPACE, USER_TABLE_NAME, (String) userExtIdRecord.get(JsonKey.USER_ID));
      if (CollectionUtils.isNotEmpty((List<Map<String, Object>>) res.get(JsonKey.RESPONSE))) {
        // user exist
        user = ((List<Map<String, Object>>) res.get(JsonKey.RESPONSE)).get(0);
      }
    }
    return user;
  }
}
