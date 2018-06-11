package org.sunbird.extension.user;

import java.util.Map;

/**
 * UserExtension is the interface for all extensions of Sunbird user profile. It defines methods for performing CRUD operations on user profile.
 * @author Jaikumar Soundara Rajan
 *
 */
public interface UserExtension {
	
	/**
	 * Creates user profile information.
	 * @param userProfileMap User profile information
	 */
	public void create(Map<String,Object> userProfileMap);
	
}