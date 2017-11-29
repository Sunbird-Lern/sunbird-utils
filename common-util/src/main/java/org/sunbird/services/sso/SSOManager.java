/**
 * 
 */
package org.sunbird.services.sso;

import java.util.Map;

/**
 * @author Manzarul
 * This interface will handle all call related to 
 * single sign out.
 */
public interface SSOManager {
	
	/**
	 * This method will verify user access token and provide
	 * userId if token is valid. in case of invalid access token it
	 * will throw ProjectCommon exception with 401.
	 * @param token String JWT access token
	 * @return String
	 */
	String verifyToken(String token );
	
	/**
	 * This method will do user creation inside Single sign on server.
	 * @param request Map<String,Object>
	 * @return Map<String,String>
	 */
	Map<String,String> createUser(Map<String,Object> request);
	
	/**
	 * Method to update user account in keycloak on basis of userId.
	 * @param request
	 * @return
	 */
	String updateUser (Map<String,Object> request);

	/**
	 *Method to remove user from keycloak account on basis of userId .
	 * @param request
	 * @return
	 */
	String removeUser (Map<String,Object> request);
	
	/**
	 * This method will check email is verified by user or not.
	 * @param userId String
	 * @return boolean
	 */
	boolean isEmailVerified(String userId);

	/**
	 * Method to deactivate user from keycloak , it is like soft delete .
	 * @param request
	 * @return
	 */
	String deactivateUser(Map<String, Object> request);

	/**
	 * Method to activate user from keycloak , it is like soft delete .
	 * @param request
	 * @return
	 */
	String activateUser(Map<String, Object> request);
	
	/**
	 * This method will read user last login time from key claok.
	 * @param userId String
	 * @return String (as epoch value or null)
	 */
	String getLastLoginTime (String userId);
	
	/**
	 * This method will add user current login time to keycloak.
	 * @param userId String
	 * @return boolean
	 */
	boolean addUserLoginTime (String userId);
	
	/**
	 * this method will do the user login with key cloak.
	 * after login it will provide access token. 
	 * @param userName String
	 * @param password String
	 * @return String
	 */
	String login(String userName, String password);

	/**
	 * this method will set emailVerified flag of keycloak as false.
	 * @param userId
	 */
  void setEmailVerifiedAsFalse(String userId);

  void setEmailVerifiedUpdatedFlag(String userId, String flag);

  String getEmailVerifiedUpdatedFlag(String userId);

  String syncUserData(Map<String, Object> request);
}
