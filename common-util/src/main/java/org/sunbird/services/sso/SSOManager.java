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
	 * This method will take userId and authToken as
	 * a key and verify against SSO server , user is valid or not.
	 * @param request Map<String,String>
	 * @return String
	 */
	public String verifyToken(Map<String,String> request );
	
	/**
	 * This method will do user creation inside Single sign out server.
	 * @param request Map<String,Object>
	 * @return String
	 */
	public String createUser(Map<String,Object> request);
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public String updateUser (Map<String,Object> request);

}
