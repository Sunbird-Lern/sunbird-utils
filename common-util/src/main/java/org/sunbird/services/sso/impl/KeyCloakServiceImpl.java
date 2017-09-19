/**
 *
 */
package org.sunbird.services.sso.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.keycloak.RSATokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.*;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.services.sso.SSOManager;

import javax.ws.rs.core.Response;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.sunbird.common.models.util.ProjectUtil.isNull;
import static org.sunbird.common.models.util.ProjectUtil.isNotNull;

/**
 * Single sign out service implementation with Key Cloak.
 *
 * @author Manzarul
 */
public class KeyCloakServiceImpl implements SSOManager {

    Keycloak keycloak = KeyCloakConnectionProvider.getConnection();
    private static final boolean IS_EMAIL_SETUP_COMPLETE = false;
   private static final String URL = KeyCloakConnectionProvider.SSO_URL+ "realms/" + KeyCloakConnectionProvider.SSO_REALM+"/protocol/openid-connect/token"; 
  @Override
  public String verifyToken(String accessToken) {
    String userId = "";
    try {
      PublicKey publicKey =
          toPublicKey(keycloak.realm(KeyCloakConnectionProvider.SSO_REALM)
              .keys().getKeyMetadata().getKeys().get(0).getPublicKey());
      AccessToken token =
          RSATokenVerifier.verifyToken(
              accessToken, publicKey, KeyCloakConnectionProvider.SSO_URL
                  + "realms/" + KeyCloakConnectionProvider.SSO_REALM,
              true, true);
      userId = token.getSubject();
      ProjectLogger.log(token.getId() + " " + token.issuedFor + " "
          + token.getProfile() + " " + token.getSubject() + " Active== "
          + token.isActive() + "  isExpired==" + token.isExpired() + " "
          + token.issuedNow().getExpiration());
    } catch (VerificationException e) {
      ProjectLogger.log("User token is not authorized==" + e);
      throw new ProjectCommonException(ResponseCode.unAuthorised.getErrorCode(),
          ResponseCode.unAuthorised.getErrorMessage(),
          ResponseCode.UNAUTHORIZED.getResponseCode());
    }
    return userId;
  }

    /**
     * This method will generate Public key form keycloak realm publickey String
     * @param publicKeyString String
     * @return  PublicKey
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
    public Map<String,String> createUser(Map<String, Object> request) {
        String userId = null;
        String accessToken = null;
        UserRepresentation user = createUserReqObj(request);
        Response result = null;
        try {
          result = keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().create(user);
        } catch (Exception e) {
          ProjectLogger.log(e.getMessage(), e);
          ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(), ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
          throw projectCommonException;
        }
         if(request != null) {
        if (result.getStatus() != 201) {
            ProjectLogger.log("Couldn't create user." + result.getStatus() + " " + result.toString(), new RuntimeException());
            if (result.getStatus() == 409) {
                ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.emailANDUserNameAlreadyExistError.getErrorCode(), ResponseCode.emailANDUserNameAlreadyExistError.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
                throw projectCommonException;
            } else {
                ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(), ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
                throw projectCommonException;
            }
        } else {
            userId = result.getHeaderString("Location").replaceAll(".*/(.*)$", "$1");
        }
         }else {
           ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(), ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
           throw projectCommonException;
         }

        //reset the password with same password
    if (!(ProjectUtil.isStringNullOREmpty(userId))
        && ((request.get(JsonKey.PASSWORD) != null) && !ProjectUtil
            .isStringNullOREmpty((String) request.get(JsonKey.PASSWORD)))) {
      doPasswordUpdate(userId, (String) request.get(JsonKey.PASSWORD));
       if(request.get(JsonKey.BULK_USER_UPLOAD) == null) {
       accessToken = login(user.getUsername(), (String) request.get(JsonKey.PASSWORD));
       }
    }
      Map<String,String> map = new HashMap<>();
      map.put(JsonKey.USER_ID, userId);
      map.put(JsonKey.ACCESSTOKEN, accessToken);
        return map;
    }


    @Override
    public String updateUser(Map<String, Object> request) {
        String userId = (String) request.get(JsonKey.USER_ID);
        UserRepresentation  ur= null;
        UserResource resource = null;
        boolean needTobeUpdate = false;
        try {
          resource = keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
          ur = resource.toRepresentation();
        } catch (Exception e) {
          ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.invalidUsrData.getErrorCode(), ResponseCode.invalidUsrData.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
          throw projectCommonException;
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
        if(isNotNull(request.get(JsonKey.USERNAME))) {
      if (isNotNull(request.get(JsonKey.PROVIDER))) {
        needTobeUpdate = true;
        ur.setUsername((String) request.get(JsonKey.USERNAME) + JsonKey.LOGIN_ID_DELIMETER
            + (String) request.get(JsonKey.PROVIDER));
      }else{
          needTobeUpdate = true;
          ur.setUsername((String) request.get(JsonKey.USERNAME));
        }
        }
        try {
          // if user sending any basic profile data
          //then no need to make api call to keycloak to update profile.
            if(needTobeUpdate){
            resource.update(ur);
            }
        } catch (Exception ex) {
            ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.invalidUsrData.getErrorCode(), ResponseCode.invalidUsrData.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
            throw projectCommonException;
        }
        return (String) JsonKey.SUCCESS;
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
        UserResource resource = keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);

        if (isNotNull(resource)) {
            try {
                resource.remove();
            } catch (Exception ex) {
                ProjectCommonException projectCommonException = new ProjectCommonException(ex.getMessage(), ex.getMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
                throw projectCommonException;
            }

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
    try {
    Keycloak keycloak = KeyCloakConnectionProvider.getConnection();
    String userId = (String) request.get(JsonKey.USER_ID);
    UserResource resource = keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
    UserRepresentation ur = resource.toRepresentation();
    ur.setEnabled(false);
    if (isNotNull(resource)) {
      try {
        resource.update(ur);
      } catch (Exception ex) {
        ProjectLogger.log(ex.getMessage(), ex);
        ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.invalidUsrData.getErrorCode(), ResponseCode.invalidUsrData.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
        throw projectCommonException;
      }

    }
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
      ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.invalidUsrData.getErrorCode(), ResponseCode.invalidUsrData.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
      throw projectCommonException; 
    }
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
    try {
    Keycloak keycloak = KeyCloakConnectionProvider.getConnection();
    String userId = (String) request.get(JsonKey.USER_ID);
    UserResource resource = keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
    UserRepresentation ur = resource.toRepresentation();
    ur.setEnabled(true);
    if (isNotNull(resource)) {
      try {
        resource.update(ur);
      } catch (Exception ex) {
        ProjectLogger.log(ex.getMessage(), ex);
        ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.invalidUsrData.getErrorCode(), ResponseCode.invalidUsrData.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
        throw projectCommonException;
      }

    }
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
      ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.invalidUsrData.getErrorCode(), ResponseCode.invalidUsrData.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
      throw projectCommonException; 
    }
    return JsonKey.SUCCESS;
  }
    
    /**
     * This method will send email verification link to registered user email
     * @param userId key claok id.
     */
    private void verifyEmail(String userId) {
      keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId).sendVerifyEmail();
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
 
    /**
     * This method will create user object from in coming map data.
     * it will read only some predefine key from the map.
     * @param request Map<String, Object>
     * @return UserRepresentation
     */
  private UserRepresentation createUserReqObj(Map<String, Object> request) {
    CredentialRepresentation credential = new CredentialRepresentation();
    UserRepresentation user = new UserRepresentation();
    if (isNotNull(request.get(JsonKey.PROVIDER))) {
      user.setUsername((String) request.get(JsonKey.USERNAME) + JsonKey.LOGIN_ID_DELIMETER
          + (String) request.get(JsonKey.PROVIDER));
    } else {
      user.setUsername((String) request.get(JsonKey.USERNAME));
    }
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
    if (isNotNull(request.get(JsonKey.EMAIL_VERIFIED))) {
      user.setEmailVerified((Boolean) request.get(JsonKey.EMAIL_VERIFIED));
    }

    user.setEnabled(true);
    return user;
  }
  
  /**
   * This method will do the user password update. it will send verify email
   * link to user if IS_EMAIL_SETUP_COMPLETE value is true , by default it's false.
   * @param userId String 
   * @param password String
   */
  private void doPasswordUpdate(String userId, String password) {
    UserResource resource =
        keycloak.realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
    CredentialRepresentation newCredential = new CredentialRepresentation();
    UserRepresentation ur = resource.toRepresentation();
    newCredential.setValue(password);
    newCredential.setType(CredentialRepresentation.PASSWORD);
    newCredential.setTemporary(true);
    resource.resetPassword(newCredential);
    try {
      resource.update(ur);
      if (IS_EMAIL_SETUP_COMPLETE) {
        verifyEmail(userId);
      }
    } catch (Exception ex) {
      ProjectCommonException projectCommonException = new ProjectCommonException(ex.getMessage(),
          ex.getMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
      throw projectCommonException;
    }
  }
  
  
  /**
   * This method will call keycloak service to user login. after 
   * successfull login it will provide access token.
   * @param userName String
   * @param password String
   * @return String access token
   */
  public String login(String userName, String password) {
    String accessTokenId = "";
    StringBuilder builder = new StringBuilder();
    builder.append(
        "client_id=" + KeyCloakConnectionProvider.CLIENT_ID + "&username="
            + userName + "&password=" + password + "&grant_type=password");
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("Content-Type", "application/x-www-form-urlencoded");
    try {
      String response =
          HttpUtil.sendPostRequest(URL, builder.toString(), headerMap);
      if (!ProjectUtil.isStringNullOREmpty(response)) {
        try {
          JSONObject object = new JSONObject(response);
          accessTokenId = object.getString(JsonKey.ACCESS_TOKEN);
        } catch (JSONException e) {
          ProjectLogger.log(e.getMessage(), e);
        }

      }
    } catch (IOException e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    return accessTokenId;
  }
  
 }
