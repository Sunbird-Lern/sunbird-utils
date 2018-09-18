package org.sunbird.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.body.RequestBodyEntity;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.RestUtil;

/**
 * Contains utility methods of keycloak.
 *
 * @author Amit Kumar
 */
public class KeyCloakUtil {

  public static final String UPDATE_PASSWORD_LINK = "updatePasswordLink";
  public static final String VERIFY_EMAIL_LINK = "verifyEmailLink";
  private static final String VERIFY_EMAIL = "VERIFY_EMAIL";
  private static final String UPDATE_PASSWORD = "UPDATE_PASSWORD";
  private static final String CLIENT_ID = "clientId";
  private static final String ACTION = "action";
  private static final String USERNAME = "userName";
  private static final String LIFE_SPAN = "lifespan";
  private static final String RE_DIRECT_URI = "redirectUri";
  private static final String SUNBIRD_KEYCLOAK_CLIENT_ID = "sunbird_keycloak_client_id";
  private static final String SUNBIRD_KEYCLOAK_LINK_EXPIRATION_TIME =
      "sunbird_keycloak_link_expiration_time";
  private static final String SUNBIRD_KEYCLOAK_ADMIN_USERNAME = "sunbird_keycloak_admin_username";
  private static final String SUNBIRD_KEYCLOAK_ADMIN_PASSWORD = "sunbird_keycloak_admin_password";
  private static final String SUNBIRD_KEYCLOAK_BASE_URL = "sunbird_keycloak_base_url";
  private static final String SUNBIRD_KEYCLOAK_REALM_NAME = "sunbird_keycloak_realm_name";

  private static ObjectMapper mapper = new ObjectMapper();

  /**
   * This method will generate the link for update password or verify email for given userName and
   * link type(updatePasswordLink or verifyEmailLink)
   *
   * @param userName user name.
   * @param linkType link type.
   * @return String link.
   */
  public static String getUpdatePasswordOrVerifyEmailLink(
      String userName, String redirectUri, String linkType) {
    Map<String, String> request = new HashMap<>();
    request.put(CLIENT_ID, ProjectUtil.getConfigValue(SUNBIRD_KEYCLOAK_CLIENT_ID));
    request.put(USERNAME, userName);
    if (linkType.equalsIgnoreCase(UPDATE_PASSWORD_LINK)) {
      request.put(ACTION, UPDATE_PASSWORD);
    } else {
      request.put(ACTION, VERIFY_EMAIL);
    }
    String lifespan = ProjectUtil.getConfigValue(SUNBIRD_KEYCLOAK_LINK_EXPIRATION_TIME);
    if (StringUtils.isNotBlank(lifespan)) {
      request.put(LIFE_SPAN, lifespan);
    } else {
      request.put(LIFE_SPAN, "3600");
    }
    request.put(RE_DIRECT_URI, redirectUri);
    request.put("isAuthRequired", "false");
    try {
      return generateLink(request);
    } catch (Exception ex) {
      ProjectLogger.log(
          "KeyCloakUtil:getUpdatePasswordOrVerifyEmailLink: Exception occurred while generating link.",
          ex);
      return "";
    }
  }

  private static String generateLink(Map<String, String> request) throws Exception {
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    if (request.get("isAuthRequired").equalsIgnoreCase("true")) {
      headers.put(JsonKey.AUTHORIZATION, JsonKey.BEARER + getAdminAccessToken());
    }
    RequestBodyEntity baseRequest =
        Unirest.post(
                ProjectUtil.getConfigValue(SUNBIRD_KEYCLOAK_BASE_URL)
                    + "/realms/"
                    + ProjectUtil.getConfigValue(SUNBIRD_KEYCLOAK_REALM_NAME)
                    + "/getlink")
            .headers(headers)
            .body(mapper.writeValueAsString(request));
    HttpResponse<JsonNode> response = baseRequest.asJson();

    return response.getBody().getObject().getString("link");
  }

  private static String getAdminAccessToken() throws Exception {
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
    BaseRequest request =
        Unirest.post(
                ProjectUtil.getConfigValue(SUNBIRD_KEYCLOAK_BASE_URL)
                    + "/realms/"
                    + ProjectUtil.getConfigValue(SUNBIRD_KEYCLOAK_REALM_NAME)
                    + "/protocol/openid-connect/token")
            .headers(headers)
            .field("client_id", ProjectUtil.getConfigValue(SUNBIRD_KEYCLOAK_CLIENT_ID))
            .field("username", ProjectUtil.getConfigValue(SUNBIRD_KEYCLOAK_ADMIN_USERNAME))
            .field("password", ProjectUtil.getConfigValue(SUNBIRD_KEYCLOAK_ADMIN_PASSWORD))
            .field("grant_type", "password");

    HttpResponse<JsonNode> response = RestUtil.execute(request);
    return response.getBody().getObject().getString("access_token");
  }
}
