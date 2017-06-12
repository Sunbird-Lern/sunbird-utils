package org.sunbird.common.models.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * @author Amit Kumar
 * this class is used for reading cassndra table column name and will map
 * it with java standard name.
 */
public class PropertiesCache {
	private final String[] fileName = ProjectUtil.FILE_NAME;
	private final Properties configProp = new Properties();

	private PropertiesCache()
	   {
		 for (String file : fileName){
	      InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
	      try {
	          configProp.load(in);
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
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
	      return configProp.getProperty(key)!=null?configProp.getProperty(key):key;
	   }
	    
	   public static void main(String[] args) {
		   PropertiesCache.getInstance().getProperty("es.host.name");
	}
}
