package org.sunbird.services.sso.impl;

import static java.util.Arrays.asList;
import static org.sunbird.common.models.util.ProjectUtil.isNotNull;
import static org.sunbird.common.models.util.ProjectUtil.isNull;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.keycloak.RSATokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.KeyCloakConnectionProvider;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.common.util.KeycloakRequiredActionLinkUtil;
import org.sunbird.services.sso.SSOManager;

/**
 * Single sign out service implementation with Key Cloak.
 *
 * @author Manzarul
 */
public class KeyCloakServiceImpl implements SSOManager {

  private Keycloak keycloak = KeyCloakConnectionProvider.getConnection();
  private static final String URL =
      KeyCloakConnectionProvider.SSO_URL
          + "realms/"
          + KeyCloakConnectionProvider.SSO_REALM
          + "/protocol/openid-connect/token";

  private static PublicKey SSO_PUBLIC_KEY = null;

  public PublicKey getPublicKey() {
    if (null == SSO_PUBLIC_KEY) {
      SSO_PUBLIC_KEY =
          new KeyCloakRsaKeyFetcher()
              .getPublicKeyFromKeyCloak(
                  KeyCloakConnectionProvider.SSO_URL, KeyCloakConnectionProvider.SSO_REALM);
    }
    return SSO_PUBLIC_KEY;
  }

  @Override
  public String verifyToken(String accessToken) {
    try {
      PublicKey publicKey = getPublicKey();
      if (publicKey == null) {
        ProjectLogger.log(
            "KeyCloakServiceImpl: SSO_PUBLIC_KEY is NULL. Keycloak server may need to be started. Read value from environment variable.",
            LoggerEnum.INFO);
        publicKey = toPublicKey(System.getenv(JsonKey.SSO_PUBLIC_KEY));
      }
      if (publicKey != null) {
        AccessToken token =
            RSATokenVerifier.verifyToken(
                accessToken,
                publicKey,
                KeyCloakConnectionProvider.SSO_URL
                    + "realms/"
                    + KeyCloakConnectionProvider.SSO_REALM,
                true,
                true);
        ProjectLogger.log(
            token.getId()
                + " "
                + token.issuedFor
                + " "
                + token.getProfile()
                + " "
                + token.getSubject()
                + " Active: "
                + token.isActive()
                + "  isExpired: "
                + token.isExpired()
                + " "
                + token.issuedNow().getExpiration(),
            LoggerEnum.INFO.name());
        return token.getSubject();
      } else {
        ProjectLogger.log(
            "KeyCloakServiceImpl:verifyToken: SSO_PUBLIC_KEY is NULL.", LoggerEnum.ERROR);
        throw new ProjectCommonException(
            ResponseCode.keyCloakDefaultError.getErrorCode(),
            ResponseCode.keyCloakDefaultError.getErrorMessage(),
            ResponseCode.keyCloakDefaultError.getResponseCode());
      }
    } catch (Exception e) {
      ProjectLogger.log(
          "KeyCloakServiceImpl:verifyToken: Exception occurred with message = " + e.getMessage(),
          LoggerEnum.ERROR);
      throw new ProjectCommonException(
          ResponseCode.unAuthorized.getErrorCode(),
          ResponseCode.unAuthorized.getErrorMessage(),
          ResponseCode.UNAUTHORIZED.getResponseCode());
    }
  }

  /**
   * This method will generate Public key form keycloak realm publickey String
   *
   * @param publicKeyString String
   * @return PublicKey
   */
  private PublicKey toPublicKey(String publicKeyString) {
    try {
      byte[] publicBytes = Base64.getDecoder().decode(publicKeyString);
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePublic(keySpec);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public Map<String, String> createUser(Map<String, Object> request) {
    String userId = null;
    String accessToken = null;
    UserRepresentation user = createUserReqObj(request);
    Response result = null;
    try {
      result = keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().create(user);
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
      ProjectUtil.createAndThrowServerError();
    }
    if (request != null) {
      if (result.getStatus() != 201) {
        ProjectLogger.log(
            "Couldn't create user." + result.getStatus() + " " + result.toString(),
            new RuntimeException());
        if (result.getStatus() == 409) {
          throw new ProjectCommonException(
              ResponseCode.emailANDUserNameAlreadyExistError.getErrorCode(),
              ResponseCode.emailANDUserNameAlreadyExistError.getErrorMessage(),
              ResponseCode.CLIENT_ERROR.getResponseCode());
        } else {
          ProjectUtil.createAndThrowServerError();
        }
      } else {
        userId = result.getHeaderString("Location").replaceAll(".*/(.*)$", "$1");
      }
    } else {
      ProjectUtil.createAndThrowServerError();
    }
    Map<String, String> map = new HashMap<>();
    map.put(JsonKey.USER_ID, userId);
    map.put(JsonKey.ACCESSTOKEN, accessToken);
    return map;
  }

  @Override
  public String updateUser(Map<String, Object> request) {
    String userId = (String) request.get(JsonKey.USER_ID);
    UserRepresentation ur = null;
    UserResource resource = null;
    boolean needTobeUpdate = false;
    try {
      resource = keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
      ur = resource.toRepresentation();
    } catch (Exception e) {
      ProjectUtil.createAndThrowInvalidUserDataException();
    }

    // set the UserRepresantation with the map value...
    if (isNotNull(request.get(JsonKey.FIRST_NAME))) {
      needTobeUpdate = true;
      ur.setFirstName((String) request.get(JsonKey.FIRST_NAME));
    }
    if (isNotNull(request.get(JsonKey.LAST_NAME))) {
      needTobeUpdate = true;
      ur.setLastName((String) request.get(JsonKey.LAST_NAME));
    }
    if (isNotNull(request.get(JsonKey.EMAIL))) {
      needTobeUpdate = true;
      ur.setEmail((String) request.get(JsonKey.EMAIL));
      ur.setEmailVerified(false);

      Map<String, List<String>> map = ur.getAttributes();
      List<String> list = new ArrayList<>();
      list.add("false");
      if (map == null) {
        map = new HashMap<>();
      }
      map.put(JsonKey.EMAIL_VERIFIED_UPDATED, list);
      ur.setAttributes(map);
    }
    if (!StringUtils.isBlank((String) request.get(JsonKey.PHONE))) {
      needTobeUpdate = true;
      Map<String, List<String>> map = ur.getAttributes();
      List<String> list = new ArrayList<>();
      list.add((String) request.get(JsonKey.PHONE));
      if (map == null) {
        map = new HashMap<>();
      }
      map.put(JsonKey.PHONE, list);
      ur.setAttributes(map);
    }

    if (!StringUtils.isBlank((String) request.get(JsonKey.COUNTRY_CODE))) {
      needTobeUpdate = true;
      Map<String, List<String>> map = ur.getAttributes();
      if (map == null) {
        map = new HashMap<>();
      }
      List<String> list = new ArrayList<>();
      list.add(PropertiesCache.getInstance().getProperty("sunbird_default_country_code"));
      if (!StringUtils.isBlank((String) request.get(JsonKey.COUNTRY_CODE))) {
        list.add(0, (String) request.get(JsonKey.COUNTRY_CODE));
      }
      map.put(JsonKey.COUNTRY_CODE, list);
      ur.setAttributes(map);
    }

    try {
      // if user sending any basic profile data
      // then no need to make api call to keycloak to update profile.
      if (needTobeUpdate) {
        resource.update(ur);
      }
    } catch (Exception ex) {
      ProjectUtil.createAndThrowInvalidUserDataException();
    }
    return JsonKey.SUCCESS;
  }

  @Override
  public String syncUserData(Map<String, Object> request) {
    String userId = (String) request.get(JsonKey.USER_ID);
    UserRepresentation ur = null;
    UserResource resource = null;
    boolean needTobeUpdate = false;
    try {
      resource = keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
      ur = resource.toRepresentation();
    } catch (Exception e) {
      ProjectUtil.createAndThrowInvalidUserDataException();
    }

    // set the UserRepresantation with the map value...
    if (isNotNull(request.get(JsonKey.FIRST_NAME))) {
      needTobeUpdate = true;
      ur.setFirstName((String) request.get(JsonKey.FIRST_NAME));
    }
    if (isNotNull(request.get(JsonKey.LAST_NAME))) {
      needTobeUpdate = true;
      ur.setLastName((String) request.get(JsonKey.LAST_NAME));
    }

    if (isNotNull(request.get(JsonKey.EMAIL))) {
      needTobeUpdate = true;
      ur.setEmail((String) request.get(JsonKey.EMAIL));
    }
    ProjectLogger.log(
        "check user email is verified or not ,resource.toRepresentation().isEmailVerified() :"
            + resource.toRepresentation().isEmailVerified()
            + " for userId :"
            + userId);
    if (!resource.toRepresentation().isEmailVerified()) {
      needTobeUpdate = true;
      Map<String, List<String>> map = ur.getAttributes();
      List<String> list = new ArrayList<>();
      list.add("false");
      if (map == null) {
        map = new HashMap<>();
      }
      map.put(JsonKey.EMAIL_VERIFIED_UPDATED, list);
      ur.setAttributes(map);
    } else {
      needTobeUpdate = true;
      Map<String, List<String>> map = ur.getAttributes();
      List<String> list = new ArrayList<>();
      list.add("true");
      if (map == null) {
        map = new HashMap<>();
      }
      map.put(JsonKey.EMAIL_VERIFIED_UPDATED, list);
      ur.setAttributes(map);
    }

    if (isNotNull(request.get(JsonKey.LOGIN_ID))) {
      needTobeUpdate = true;
      ur.setUsername((String) request.get(JsonKey.LOGIN_ID));
    }
    if (!StringUtils.isBlank((String) request.get(JsonKey.PHONE))) {
      needTobeUpdate = true;
      Map<String, List<String>> map = ur.getAttributes();
      List<String> list = new ArrayList<>();
      list.add((String) request.get(JsonKey.PHONE));
      if (map == null) {
        map = new HashMap<>();
      }
      map.put(JsonKey.PHONE, list);
      ur.setAttributes(map);
    }
    Map<String, List<String>> map = ur.getAttributes();
    if (map == null) {
      map = new HashMap<>();
    }
    List<String> list = new ArrayList<>();
    list.add(PropertiesCache.getInstance().getProperty("sunbird_default_country_code"));
    map.put(JsonKey.COUNTRY_CODE, list);
    if (!StringUtils.isBlank((String) request.get(JsonKey.COUNTRY_CODE))) {
      needTobeUpdate = true;
      list.add(0, (String) request.get(JsonKey.COUNTRY_CODE));
      map.put(JsonKey.COUNTRY_CODE, list);
    }
    ur.setAttributes(map);
    try {
      // if user sending any basic profile data
      // then no need to make api call to keycloak to update profile.
      if (needTobeUpdate) {
        resource.update(ur);
      }
    } catch (Exception ex) {
      ProjectUtil.createAndThrowInvalidUserDataException();
    }
    return JsonKey.SUCCESS;
  }

  /**
   * Method to remove the user on basis of user id.
   *
   * @param request Map
   * @return boolean true if success otherwise false .
   */
  @Override
  public String removeUser(Map<String, Object> request) {
    Keycloak keycloak = KeyCloakConnectionProvider.getConnection();
    String userId = (String) request.get(JsonKey.USER_ID);
    try {
      UserResource resource =
          keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
      if (isNotNull(resource)) {
        resource.remove();
      }
    } catch (Exception ex) {
      ProjectUtil.createAndThrowInvalidUserDataException();
    }
    return JsonKey.SUCCESS;
  }

  /**
   * Method to deactivate the user on basis of user id.
   *
   * @param request Map
   * @return boolean true if success otherwise false .
   */
  @Override
  public String deactivateUser(Map<String, Object> request) {
    String userId = (String) request.get(JsonKey.USER_ID);
    makeUserActiveOrInactive(userId, false);
    return JsonKey.SUCCESS;
  }

  /**
   * Method to activate the user on basis of user id.
   *
   * @param request Map
   * @return boolean true if success otherwise false .
   */
  @Override
  public String activateUser(Map<String, Object> request) {
    String userId = (String) request.get(JsonKey.USER_ID);
    makeUserActiveOrInactive(userId, true);
    return JsonKey.SUCCESS;
  }

  /**
   * This method will take userid and boolean status to update user status
   *
   * @param userId String
   * @param status boolean
   * @throws ProjectCommonException
   */
  private void makeUserActiveOrInactive(String userId, boolean status) {
    try {
      validateUserId(userId);
      Keycloak keycloak = KeyCloakConnectionProvider.getConnection();
      UserResource resource =
          keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
      UserRepresentation ur = resource.toRepresentation();
      ur.setEnabled(status);
      if (isNotNull(resource)) {
        resource.update(ur);
      }
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
      ProjectUtil.createAndThrowInvalidUserDataException();
    }
  }

  /**
   * This method will check userId value, if value is null or empty then it will throw
   * ProjectCommonException
   *
   * @param userId String
   * @throws ProjectCommonException
   */
  private void validateUserId(String userId) {
    if (StringUtils.isBlank(userId)) {
      ProjectUtil.createAndThrowInvalidUserDataException();
    }
  }

  /**
   * This method will send email verification link to registered user email
   *
   * @param userId keycloak id.
   */
  private void verifyEmail(String userId) {
    try {
      ProjectLogger.log("Sending mail for Email verification start.");
      keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId).sendVerifyEmail();
    } catch (Exception ex) {
      ProjectLogger.log(
          "Exception occurred while calling sendVerifyEmail method of keycloak :: ", ex);
    }
    ProjectLogger.log("Sending mail for Email verification end.");
  }

  @Override
  public boolean isEmailVerified(String userId) {
    UserResource resource =
        keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
    if (isNull(resource)) {
      return false;
    }
    return resource.toRepresentation().isEmailVerified();
  }

  @Override
  public void setEmailVerifiedUpdatedFlag(String userId, String flag) {
    UserResource resource =
        keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
    UserRepresentation user = resource.toRepresentation();
    Map<String, List<String>> map = user.getAttributes();
    List<String> list = new ArrayList<>();
    list.add(flag);
    if (map == null) {
      map = new HashMap<>();
    }
    map.put(JsonKey.EMAIL_VERIFIED_UPDATED, list);
    user.setAttributes(map);
    resource.update(user);
  }

  @Override
  public String getEmailVerifiedUpdatedFlag(String userId) {
    UserResource resource =
        keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
    UserRepresentation user = resource.toRepresentation();
    Map<String, List<String>> map = user.getAttributes();
    List<String> list = null;
    if (MapUtils.isNotEmpty(map)) {
      list = map.get(JsonKey.EMAIL_VERIFIED_UPDATED);
    }
    if (CollectionUtils.isNotEmpty(list)) {
      return list.get(0);
    } else {
      return "";
    }
  }

  /**
   * This method will create user object from in coming map data. it will read only some predefine
   * key from the map.
   *
   * @param request Map<String, Object>
   * @return UserRepresentation
   */
  private UserRepresentation createUserReqObj(Map<String, Object> request) {
    CredentialRepresentation credential = new CredentialRepresentation();
    UserRepresentation user = new UserRepresentation();
    user.setUsername((String) request.get(JsonKey.USERNAME));
    if (isNotNull(request.get(JsonKey.FIRST_NAME))) {
      user.setFirstName((String) request.get(JsonKey.FIRST_NAME));
    }
    if (isNotNull(request.get(JsonKey.LAST_NAME))) {
      user.setLastName((String) request.get(JsonKey.LAST_NAME));
    }
    if (isNotNull(request.get(JsonKey.EMAIL))) {
      user.setEmail((String) request.get(JsonKey.EMAIL));
    }
    if (isNotNull(request.get(JsonKey.PASSWORD))) {
      credential.setValue((String) request.get(JsonKey.PASSWORD));
      credential.setType(CredentialRepresentation.PASSWORD);
      credential.setTemporary(true);
      user.setCredentials(asList(credential));
    }
    user.setEmailVerified(false);
    if (isNotNull(request.get(JsonKey.PHONE))) {
      Map<String, List<String>> map = user.getAttributes();
      if (map == null) {
        map = new HashMap<>();
      }
      List<String> list = new ArrayList<>();
      list.add((String) request.get(JsonKey.PHONE));

      map.put(JsonKey.PHONE, list);
      List<String> list2 = new ArrayList<>();
      if (!StringUtils.isBlank((String) request.get(JsonKey.COUNTRY_CODE))) {
        list2.add((String) request.get(JsonKey.COUNTRY_CODE));
      } else {
        list2.add(PropertiesCache.getInstance().getProperty("sunbird_default_country_code"));
      }
      map.put(JsonKey.COUNTRY_CODE, list2);

      user.setAttributes(map);
    }

    Map<String, List<String>> map = user.getAttributes();
    List<String> list = new ArrayList<>();
    list.add("false");
    if (map == null) {
      map = new HashMap<>();
    }
    map.put(JsonKey.EMAIL_VERIFIED_UPDATED, list);
    user.setAttributes(map);

    user.setEnabled(true);
    return user;
  }

  /**
   * This method will do the user password update.
   *
   * @param userId String
   * @param password String
   * @return boolean true/false
   */
  @Override
  public boolean doPasswordUpdate(String userId, String password) {
    boolean response = false;
    try {
      UserResource resource =
          keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
      CredentialRepresentation newCredential = new CredentialRepresentation();
      newCredential.setValue(password);
      newCredential.setType(CredentialRepresentation.PASSWORD);
      newCredential.setTemporary(true);
      resource.resetPassword(newCredential);
      response = true;
    } catch (Exception ex) {
      ProjectLogger.log(ex.getMessage(), ex);
    }
    return response;
  }

  /**
   * This method will call keycloak service to user login. after successfull login it will provide
   * access token.
   *
   * @param userName String
   * @param password String
   * @return String access token
   */
  @Override
  public String login(String userName, String password) {
    String accessTokenId = "";
    StringBuilder builder = new StringBuilder();
    builder.append(
        "client_id="
            + KeyCloakConnectionProvider.CLIENT_ID
            + "&username="
            + userName
            + "&password="
            + password
            + "&grant_type=password");
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("Content-Type", "application/x-www-form-urlencoded");
    try {
      String response = HttpUtil.sendPostRequest(URL, builder.toString(), headerMap);
      if (!StringUtils.isBlank(response)) {
        JSONObject object = new JSONObject(response);
        accessTokenId = object.getString(JsonKey.ACCESS_TOKEN);
      }
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    return accessTokenId;
  }

  @Override
  public String getLastLoginTime(String userId) {
    String lastLoginTime = null;
    try {
      UserResource resource =
          keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
      UserRepresentation ur = resource.toRepresentation();
      Map<String, List<String>> map = ur.getAttributes();
      if (map == null) {
        map = new HashMap<>();
      }
      List<String> list = map.get(JsonKey.LAST_LOGIN_TIME);
      if (list != null && !list.isEmpty()) {
        lastLoginTime = list.get(0);
      }
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    return lastLoginTime;
  }

  @Override
  public boolean addUserLoginTime(String userId) {
    boolean response = true;
    try {
      UserResource resource =
          keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
      UserRepresentation ur = resource.toRepresentation();
      Map<String, List<String>> map = ur.getAttributes();
      List<String> list = new ArrayList<>();
      if (map == null) {
        map = new HashMap<>();
      }
      List<String> currentLogTime = map.get(JsonKey.CURRENT_LOGIN_TIME);
      if (currentLogTime == null || currentLogTime.isEmpty()) {
        currentLogTime = new ArrayList<>();
        currentLogTime.add(Long.toString(System.currentTimeMillis()));
      } else {
        list.add(currentLogTime.get(0));
        currentLogTime.clear();
        currentLogTime.add(0, Long.toString(System.currentTimeMillis()));
      }
      map.put(JsonKey.CURRENT_LOGIN_TIME, currentLogTime);
      map.put(JsonKey.LAST_LOGIN_TIME, list);
      ur.setAttributes(map);
      resource.update(ur);
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
      response = false;
    }
    return response;
  }

  @Override
  public String setEmailVerifiedTrue(String userId) {
    updateEmailVerifyStatus(userId, true);
    return JsonKey.SUCCESS;
  }

  @Override
  public String setEmailVerifiedAsFalse(String userId) {
    updateEmailVerifyStatus(userId, false);
    return JsonKey.SUCCESS;
  }

  /**
   * This method will update user email verified status
   *
   * @param userId String
   * @param status boolean
   * @throws ProjectCommonException
   */
  private void updateEmailVerifyStatus(String userId, boolean status) {
    try {
      UserResource resource =
          keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
      UserRepresentation ur = resource.toRepresentation();
      ur.setEmailVerified(status);
      if (isNotNull(resource)) {
        resource.update(ur);
      }
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
      ProjectUtil.createAndThrowInvalidUserDataException();
    }
  }

  @Override
  public void setRequiredAction(String userId, String requiredAction) {
    UserResource resource =
        keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);

    UserRepresentation userRepresentation = resource.toRepresentation();
    userRepresentation.setRequiredActions(asList(requiredAction));
    if (KeycloakRequiredActionLinkUtil.VERIFY_EMAIL.equalsIgnoreCase(requiredAction)) {
      userRepresentation.setEmailVerified(false);
    }
    resource.update(userRepresentation);
  }

  @Override
  public String getUsernameById(String userId) {
 try {
    UserResource resource =
        keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
    UserRepresentation ur = resource.toRepresentation();
    return ur.getUsername();
     } catch (Exception e) {
      ProjectLogger.log("KeyCloakServiceImpl getUsernameById user not found for userId "+ userId +" " +e.getMessage(), e);
    }
     ProjectLogger.log("KeyCloakServiceImpl getUsernameById user not found for userId "+ userId, LoggerEnum.INFO.name());
    return ""; 
  }
}
