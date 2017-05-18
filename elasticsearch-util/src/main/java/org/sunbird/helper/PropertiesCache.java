package org.sunbird.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * @author Amit Kumar
 * this class is used for reading cassndra properties file
 */
public class PropertiesCache {
	
	private final String fileName = "elasticsearch.config.properties";
	private final Properties configProp = new Properties();

	private PropertiesCache()
	   {
	      InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
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
