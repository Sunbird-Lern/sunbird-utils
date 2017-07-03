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

/**
 * Single sign out service implementation with Key Cloak.
 *
 * @author Manzarul
 */
public class KeyCloakServiceImpl implements SSOManager {

    private static final LogHelper logger = LogHelper.getInstance(KeyCloakServiceImpl.class.getName());
    private static PropertiesCache cache = PropertiesCache.getInstance();
    Keycloak keycloak = KeyCloakConnectionProvider.getConnection();

    @Override
    public String verifyToken(Map<String, String> request) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public String createUser(Map<String, Object> request) {
        String userId = null;

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        if (null != request.get(JsonKey.PASSWORD)) {
            credential.setValue((String) request.get(JsonKey.PASSWORD));
        }
        credential.setTemporary(true);

        UserRepresentation user = new UserRepresentation();
        user.setUsername((String) request.get(JsonKey.USERNAME));
        if (null != request.get(JsonKey.FIRST_NAME)) {
            user.setFirstName((String) request.get(JsonKey.FIRST_NAME));
        }
        if (null != request.get(JsonKey.LAST_NAME)) {
            user.setLastName((String) request.get(JsonKey.LAST_NAME));
        }
        if (null != request.get(JsonKey.EMAIL)) {
            user.setEmail((String) request.get(JsonKey.EMAIL));
        }
        user.setCredentials(asList(credential));
        user.setEnabled(true);

        Response result = keycloak.realm(cache.getProperty(JsonKey.SSO_REALM)).users().create(user);
        if (result.getStatus() != 201) {

            logger.error("Couldn't create user." + result.getStatus() + " " + result.toString(), new RuntimeException());
            if (result.getStatus() == 409) {
                ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.emailANDUserNameAlreadyExistError.getErrorCode(), ResponseCode.emailANDUserNameAlreadyExistError.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
                throw projectCommonException;
            } else {
                ProjectCommonException projectCommonException = new ProjectCommonException(ResponseCode.keyCloakDefaultError.getErrorCode(), ResponseCode.keyCloakDefaultError.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
                throw projectCommonException;
            }
        } else {
            userId = result.getHeaderString("Location").replaceAll(".*/(.*)$", "$1");
        }

        //reset the password with same password
        if (!(ProjectUtil.isStringNullOREmpty(userId))) {
            UserResource resource = keycloak.realm(cache.getProperty(JsonKey.SSO_REALM)).users().get(userId);

            CredentialRepresentation newCredential = new CredentialRepresentation();
            newCredential.setType(CredentialRepresentation.PASSWORD);
            if (null != request.get(JsonKey.PASSWORD)) {
                newCredential.setValue((String) request.get(JsonKey.PASSWORD));
            }
            newCredential.setTemporary(true);
            resource.resetPassword(newCredential);
            UserRepresentation ur = resource.toRepresentation();
            try {
                resource.update(ur);
            } catch (Exception ex) {
                ProjectCommonException projectCommonException = new ProjectCommonException(ex.getMessage() + result.getStatus(), ex.getMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
                throw projectCommonException;
            }
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
        if (null != request.get(JsonKey.FIRST_NAME)) {
            ur.setFirstName((String) request.get(JsonKey.FIRST_NAME));
        }
        if (null != request.get(JsonKey.LAST_NAME)) {
            ur.setLastName((String) request.get(JsonKey.LAST_NAME));
        }
        if (null != request.get(JsonKey.EMAIL)) {
            ur.setEmail((String) request.get(JsonKey.EMAIL));
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

        if (resource != null) {
            try {
                resource.remove();
            } catch (Exception ex) {
                ProjectCommonException projectCommonException = new ProjectCommonException(ex.getMessage(), ex.getMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
                throw projectCommonException;
            }

        }
        return (String) JsonKey.SUCCESS;
    }

}
