package org.sunbird.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * @author Amit Kumar
 * this class is used for reading cassndra properties file
 */
public class PropertiesCache {
	
	
	private final Properties configProp = new Properties();
    
	   private PropertiesCache()
	   {
	      InputStream in = this.getClass().getClassLoader().getResourceAsStream(Constants.CASSANDRA_PROPERTIES_FILE);
	      try {
	          configProp.load(in);
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	   }
	 
	   private static class LazyHolder
	   {
	      private static final PropertiesCache INSTANCE = new PropertiesCache();
	   }
	 
	   public static PropertiesCache getInstance()
	   {
	      return LazyHolder.INSTANCE;
	   }
	    
	   public String getProperty(String key){
	      return configProp.getProperty(key);
	   }
	    
}
