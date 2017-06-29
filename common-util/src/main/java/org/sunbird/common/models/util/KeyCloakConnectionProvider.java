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
    * TODO add the comment
    */
   public static Keycloak initialiseConnection() {
	   LOGGER.info("key cloak instance is creation started.");
	    keycloak = KeycloakBuilder.builder() 
		.serverUrl("http://localhost:8081/auth") 
		.realm("rest-example")
		.username("rest-user-admin") 
		.password("password")
		.clientId("admin-cli").clientSecret("")
		.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
		.build();
	    LOGGER.info("key cloak instance is created successfully.");
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
