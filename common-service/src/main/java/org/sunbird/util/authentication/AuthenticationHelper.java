package org.sunbird.util.authentication;

import java.util.List;
import java.util.Map;
import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
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

  private static CassandraOperation cassandraOperation = ServiceFactory.getInstance();
  private static final String MASTER_KEY_TABLE_NAME = "client_info";
  private static final String KEYSPACE_NAME = "sunbird";

  /**
   * Authenticates given user JWT token
   *
   * @param token User JWT token
   * @return User for successfully authenticated request. For unauthenticated requests, UNAUTHORIZED
   *     is returned
   */
  @SuppressWarnings("unchecked")
  public static String verifyUserToken(String token) {
    SSOManager ssoManager = SSOServiceFactory.getInstance();
    String userId = JsonKey.UNAUTHORIZED;
    try {
      userId = ssoManager.verifyToken(token);
    } catch (Exception e) {
      ProjectLogger.log("AuthenticationHelper:verifyUserToken: invalid auth token = " + token, e);
    }
    return userId;
  }

  /**
   * Authenticates given master client id and client token
   *
   * @param clientId master key client id
   * @param clientToken master key client token
   * @return Master client id If combination of client id and token are valid . For unauthenticated
   *     requests, UNAUTHORIZED is returned
   */
  public static String verifyClientAccessToken(String clientId, String clientToken) {

    String validClientId = JsonKey.UNAUTHORIZED;
    try {
      Response clientResponse =
          cassandraOperation.getRecordById(KEYSPACE_NAME, MASTER_KEY_TABLE_NAME, clientId);
      if (null != clientResponse && !clientResponse.getResult().isEmpty()) {
        List<Map<String, Object>> dataList =
            (List<Map<String, Object>>) clientResponse.getResult().get(JsonKey.RESPONSE);
        String clientTokenFromDb = (String) dataList.get(0).get(JsonKey.MASTER_KEY);
        if (clientToken.equals(clientTokenFromDb)) {
          validClientId = clientId;
        }
      }
    } catch (Exception e) {
      ProjectLogger.log(
          "AuthenticationHelper:verifyClientAccessToken: Validating client token failed due to : "
              + e.getMessage(),
          e);
    }
    return validClientId;
  }
}
