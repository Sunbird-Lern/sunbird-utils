package org.sunbird.extension.user;

import java.util.Map;

/**
 * This interface will provide utility methods to extend user management flows
 * @author Jaikumar Soundara Rajan
 *
 */
public interface UserExtension {
	
	/**
	 * @param extensionMap
	 */
	public void create(Map<String,Object> extensionMap);
	
}