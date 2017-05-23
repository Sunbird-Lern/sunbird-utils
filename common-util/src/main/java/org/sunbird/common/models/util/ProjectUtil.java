/**
 * 
 */
package org.sunbird.common.models.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
	
	private static Pattern pattern;
	private static final String EMAIL_PATTERN =
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	static {
		pattern = Pattern.compile(EMAIL_PATTERN);
	   }

	/**
	 * Validate email with regular expression
	 *
	 * @param email
	 * @return true valid email, false invalid email
	 */
	public static boolean isEmailvalid(final String email) {
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();

	}
   /**
    * This method will generate user auth token based on user name , source and timestamp	
    * @param userName String
    * @param source String 
    * @return String
    */
	public static String createUserAuthToken(String userName,String source) {
		String data = userName +source+System.currentTimeMillis();
		UUID authId = UUID.nameUUIDFromBytes(data.getBytes());
		return authId.toString();
	}
 
}
