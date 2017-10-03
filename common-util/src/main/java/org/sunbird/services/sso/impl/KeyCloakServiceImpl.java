/**
 *
 */
package org.sunbird.services.sso.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.keycloak.RSATokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.*;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.services.sso.SSOManager;
import org.sunbird.services.sso.SSOServiceFactory;

import javax.ws.rs.core.Response;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
   private static final String SSO_PUBLIC_KEY = System.getenv(JsonKey.SSO_PUBLIC_KEY);
   
   @Override
  public String verifyToken(String accessToken) {
    String userId = "";
    try {
      PublicKey publicKey = toPublicKey(SSO_PUBLIC_KEY);
      AccessToken token =
          RSATokenVerifier.verifyToken(
              accessToken, publicKey, KeyCloakConnectionProvider.SSO_URL
                  + "realms/" + KeyCloakConnectionProvider.SSO_REALM,
              true, true);
      userId = token.getSubject();
      ProjectLogger.log(token.getId() + " " + token.issuedFor + " "
          + token.getProfile() + " " + token.getSubject() + " Active== "
          + token.isActive() + "  isExpired==" + token.isExpired() + " "
          + token.issuedNow().getExpiration(),LoggerEnum.INFO.name());
    } catch (Exception e) {
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
  
  public static void main(String[] args) {
    SSOManager sso = SSOServiceFactory.getInstance();
    /*Map<String,Object> map = new HashMap<>();
    map.put(JsonKey.USER_ID, "607476a7-a072-4e18-8d0f-5e8279642cb3");
    map.put(JsonKey.FIRST_NAME, "Test");
    sso.updateUser(map);*/
    sso.verifyToken("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ1WXhXdE4tZzRfMld5MG5PS1ZoaE5hU0gtM2lSSjdXU25ibFlwVVU0TFRrIn0.eyJqdGkiOiIzMTc1ODAxNi04ZDczLTQ2ZmEtOGQyYS1jMmNhNzFmZjk1MmEiLCJleHAiOjE1MDcwMjU2MzAsIm5iZiI6MCwiaWF0IjoxNTA3MDIyMDMwLCJpc3MiOiJodHRwczovL2Rldi5vcGVuLXN1bmJpcmQub3JnL2F1dGgvcmVhbG1zL3N1bmJpcmQiLCJhdWQiOiJwb3J0YWwiLCJzdWIiOiJhM2Q0MTUxYi00ZDNlLTQwNjgtODk1MC1kNWIyN2IxMDQ4N2UiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJwb3J0YWwiLCJhdXRoX3RpbWUiOjE1MDcwMjIwMzAsInNlc3Npb25fc3RhdGUiOiJjZDRiM2NhYy1hYjdjLTQ3MDQtOTlmYS1jZGVjOTRkZDllYzciLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IjI2c2VwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiMjZzZXAiLCJnaXZlbl9uYW1lIjoiMjZzZXAiLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoiMjZzZXBAZ21haWwuY29tIn0.BvUg3axfgQAGnri-94BywoZTbJHvw4CtJE93L4cg7ZHywPcSdkPJsKoFlPBrFiFrPVzbVzFQ2UqfVjMlpgjniznRj90NQXXxo8Sj007DlIYV1z-ZbQ1HpW4b8NWB5WxGIMnlXX_Eo2lbuRZ7OwMDV5TXw-GpZt9JIXYv3LHTWQoW2-cSBTt_ac_-FmSeyqPrvVYwZAWsC9yyIaM6YPB3MuPM05RU7pIJPTjO1zHV-cFR5tl8HZO1JNbznbVXyZwaGRQqvmewjOmYRAWuPEeDJztm8xkaU3X6a9ftcEVLovbGci2ygoiYg6Ahd56M-uFb8WIVjC9Bc5wKqCjLyh7LRQ");
  }

  @Override
  public String getLastLoginTime(String userId) {
    String lastLoginTime = null;
    try {
      UserResource resource = keycloak
          .realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
      UserRepresentation ur = resource.toRepresentation();
      Map<String, List<String>> map = ur.getAttributes();
      if(map == null) {
        map = new HashMap<>();
      }
      List<String> list = map.get(JsonKey.LAST_LOGIN_TIME);
      if (list != null && list.size() > 0) {
        lastLoginTime = list.get(0);
      }
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(),e);
    }
    return lastLoginTime;
  }

  @Override
  public boolean addUserLoginTime(String userId) {
    boolean response = true;
    try {
      UserResource resource = keycloak
          .realm(KeyCloakConnectionProvider.SSO_REALM).users().get(userId);
      UserRepresentation ur = resource.toRepresentation();
      Map<String, List<String>> map = ur.getAttributes();
      List<String> list = new ArrayList<>();
      if(map == null) {
        map = new HashMap<>();
      }
      List<String> currentLogTime = map.get(JsonKey.CURRENT_LOGIN_TIME);
      if (currentLogTime == null || currentLogTime.size() == 0) {
        currentLogTime = new ArrayList<String>();
        currentLogTime.add(System.currentTimeMillis() + "");
      } else {
        list.add(currentLogTime.get(0));
        currentLogTime.clear();
        currentLogTime.add(0, System.currentTimeMillis() + "");
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
  
 }
