/**
 * 
 */
package org.sunbird.common.models.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * This class will contains all the common utility 
 * methods.
 * @author Manzarul
 *
 */
public class ProjectUtil {
	
	/**
	 * format the date in YYYY-MM-DD hh:mm:ss:SSZ
	 */
	 public static final SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss:SSSZ");
	 public static final SimpleDateFormat cassandraFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
	
	/**
	 * This method will check incoming value is null or empty
	 * it will do empty check by doing trim method. in case of 
	 * null or empty it will return true else false.
	 * @param value
	 * @return
	 */
	public static boolean isStringNullOREmpty(String value) {
		if (value == null || "".equals(value.trim())) {
			return true;
		}
		return false;
	}
	
	/**
	 * This method will provide formatted date
	 * @return
	 */
	public static String getFormattedDate() {
		return format.format(new Date());
	}

}
