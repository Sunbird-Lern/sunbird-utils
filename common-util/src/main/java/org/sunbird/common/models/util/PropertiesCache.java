package org.sunbird.common.models.util;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

/*
 * @author Amit Kumar
 * this class is used for reading properties file 
 */
public class PropertiesCache {
  
	private static final String[] fileName = ProjectUtil.FILE_NAME;
	private final Properties configProp = new Properties();

	/**
	 * private default constructor
	 */
	private PropertiesCache()
	   {
		 for (String file : fileName){
	      InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
	      try {
	          configProp.load(in);
	      } catch (IOException e) {
	        ProjectLogger.log("Error in properties cache",e);
	      }
		 }
	   }
	//Bill Pugh Solution for singleton pattern
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
}
