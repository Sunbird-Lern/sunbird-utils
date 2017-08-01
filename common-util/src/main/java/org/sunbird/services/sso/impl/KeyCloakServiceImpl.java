/**
 *
 */
package org.sunbird.services.sso.impl;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.*;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.services.sso.SSOManager;

import javax.ws.rs.core.Response;

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

    private static PropertiesCache cache = PropertiesCache.getInstance();
    Keycloak keycloak = KeyCloakConnectionProvider.getConnection();
    private static final boolean IS_EMAIL_SETUP_COMPLETE = false;
    
    @Override
    public String verifyToken(Map<String, String> request) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public String createUser(Map<String, Object> request) {
        String userId = null;
        UserRepresentation user = createUserReqObj(request);
        Response result = null;
        try {
          result = keycloak.realm(cache.getProperty(JsonKey.SSO_REALM)).users().create(user);
        } catch (Exception e) {
          ProjectLogger.log(e.getMessage(), e);
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
    }

        return userId;
    }


    @Override
    public String updateUser(Map<String, Object> request) {
        // TODO Auto-generated method stub
        String userId = (String) request.get(JsonKey.USER_ID);
        UserResource resource = keycloak.realm(cache.getProperty(JsonKey.SSO_REALM)).users().get(userId);
        UserRepresentation ur = resource.toRepresentation();
        // set the UserRepresantation with the map value...
        if (isNotNull(request.get(JsonKey.FIRST_NAME))) {
            ur.setFirstName((String) request.get(JsonKey.FIRST_NAME));
        }
        if (isNotNull(request.get(JsonKey.LAST_NAME))) {
            ur.setLastName((String) request.get(JsonKey.LAST_NAME));
        }
        if (isNotNull(request.get(JsonKey.EMAIL))) {
            ur.setEmail((String) request.get(JsonKey.EMAIL));
        }
        if(isNotNull(request.get(JsonKey.USERNAME))) {
      if (isNotNull(request.get(JsonKey.PROVIDER))) {
        ur.setUsername((String) request.get(JsonKey.USERNAME) + JsonKey.LOGIN_ID_DELIMETER
            + (String) request.get(JsonKey.PROVIDER));
      }else{
          ur.setUsername((String) request.get(JsonKey.USERNAME));
        }
        }
        try {
            resource.update(ur);
        } catch (Exception ex) {
            ProjectCommonException projectCommonException = new ProjectCommonException(ex.getMessage(), ex.getMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
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
        UserResource resource = keycloak.realm(cache.getProperty(JsonKey.SSO_REALM)).users().get(userId);

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
    Keycloak keycloak = KeyCloakConnectionProvider.getConnection();
    String userId = (String) request.get(JsonKey.USER_ID);
    UserResource resource = keycloak.realm(cache.getProperty(JsonKey.SSO_REALM)).users().get(userId);
    UserRepresentation ur = resource.toRepresentation();
    ur.setEnabled(false);
    if (isNotNull(resource)) {
      try {
        resource.update(ur);
      } catch (Exception ex) {
        ProjectCommonException projectCommonException = new ProjectCommonException(ex.getMessage(), ex.getMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
        throw projectCommonException;
      }

    }
    return JsonKey.SUCCESS;
  }
    
    /**
     * This method will send email verification link to registered user email
     * @param userId key claok id.
     */
    private void verifyEmail(String userId) {
      keycloak.realm(cache.getProperty(JsonKey.SSO_REALM)).users().get(userId).sendVerifyEmail();
    }


    @Override
  public boolean isEmailVerified(String userId) {
    UserResource resource =
        keycloak.realm(cache.getProperty(JsonKey.SSO_REALM)).users().get(userId);
    if (isNull(resource)) {
      return false;
    }
    return resource.toRepresentation().isEmailVerified();

  }
 
    /**
     * 
     * @param request
     * @return
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
  
  private void doPasswordUpdate(String userId, String password) {
    UserResource resource =
        keycloak.realm(cache.getProperty(JsonKey.SSO_REALM)).users().get(userId);
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
  
  
 }
