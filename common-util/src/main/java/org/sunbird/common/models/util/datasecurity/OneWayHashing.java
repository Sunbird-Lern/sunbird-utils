package org.sunbird.common.models.util.datasecurity;

import java.security.MessageDigest;

import org.sunbird.common.models.util.LogHelper;


/**
 * This class will do one way data hashing.
 * @author Manzarul
 *
 */
public class OneWayHashing {
    private static final LogHelper logger = LogHelper.getInstance(OneWayHashing.class.getName());
    /**
     * This method will encrypt value using SHA-256 . it is one way encryption.
     * 
     * @param val
     *            String
     * @return String encrypted value or empty in case of exception
     */
	public static String encryptVal(String val) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(val.getBytes());
			byte byteData[] = md.digest();
			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			logger.info("encrypted value is==: " + sb.toString());
			return sb.toString();
		} catch (Exception e) {
			logger.error(e);
		}
		return "";
	}
	public static void main(String[] args) {
		System.out.println(encryptVal("test"));
	}
}
