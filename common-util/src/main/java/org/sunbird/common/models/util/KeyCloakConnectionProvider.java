/**
 * 
 */
package org.sunbird.common.models.util;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

/**
 * @author Manzarul
 * This class will connect to key cloak server 
 * and provide the connection to do other operations.
 */
public class KeyCloakConnectionProvider {
   private static final LogHelper LOGGER = LogHelper.getInstance(KeyCloakConnectionProvider.class.getName()); 
   private static Keycloak keycloak; 
   private static PropertiesCache cache = PropertiesCache.getInstance();
   static {
		initialiseConnection();
		registerShutDownHook();
	}

	/**
	 * Method to initializate the Keycloak connection
	 * @return Keycloak connection
	 */
   public static Keycloak initialiseConnection() {
	   LOGGER.info("key cloak instance is creation started.");
	    keycloak = KeycloakBuilder.builder() 
		.serverUrl(cache.getProperty(JsonKey.SSO_URL))
		.realm(cache.getProperty(JsonKey.SSO_REALM))
		.username(cache.getProperty(JsonKey.SSO_USERNAME))
		.password(cache.getProperty(JsonKey.SSO_PASSWORD))
		.clientId(cache.getProperty(JsonKey.SSO_CLIENT_ID)).clientSecret(JsonKey.SSO_CLIENT_SECRET)
		.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(Integer.parseInt(cache.getProperty(JsonKey.SSO_POOL_SIZE))).build())
		.build();
	    LOGGER.info("key cloak instance is created successfully.");
	   System.out.println("SUCCESS");
	   return keycloak;
   }
   
   /**
	 * This method will provide key cloak
	 * connection instance. 
	 * @return Keycloak
	 */
	public static Keycloak getConnection(){
		if(keycloak !=null) {
		   return keycloak;
		}
		else {
			return initialiseConnection();
		}
	}
	
	/**
	 * This class will be called by registerShutDownHook to 
	 * register the call inside jvm , when jvm terminate it will call
	 * the run method to clean up the resource.
	 * @author Manzarul
	 *
	 */
	static class ResourceCleanUp extends Thread {
		  public void run() {
			  LOGGER.info("started resource cleanup.");
			  keycloak.close(); 
			  LOGGER.info("completed resource cleanup.");
		  }
	}
	
	/**
	 * Register the hook for resource clean up.
	 * this will be called when jvm shut down.
	 */
	public static void registerShutDownHook() {
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new ResourceCleanUp());
		LOGGER.info("ShutDownHook registered.");
	}
	
}
