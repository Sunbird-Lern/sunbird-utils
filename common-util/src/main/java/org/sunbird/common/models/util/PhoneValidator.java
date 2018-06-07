package org.sunbird.common.models.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * This class will provide helper method to validate phone number and its country code.
 *
 * @author Amit Kumar
 */
public class PhoneValidator {

  private static final int ERROR_CODE = ResponseCode.CLIENT_ERROR.getResponseCode();

  private PhoneValidator() {}

  public static boolean validatePhoneNo(String phone, String countryCode) {
    if (phone.contains("+")) {
      throw new ProjectCommonException(
          ResponseCode.invalidPhoneNumber.getErrorCode(),
          ResponseCode.invalidPhoneNumber.getErrorMessage(),
          ERROR_CODE);
    }
    if (StringUtils.isNotBlank(countryCode)) {
      boolean bool = validateCountryCode(countryCode);
      if (!bool) {
        throw new ProjectCommonException(
            ResponseCode.invalidCountryCode.getErrorCode(),
            ResponseCode.invalidCountryCode.getErrorMessage(),
            ERROR_CODE);
      }
    }
    if (ProjectUtil.validatePhone(phone, countryCode)) {
      return true;
    } else {
      throw new ProjectCommonException(
          ResponseCode.phoneNoFormatError.getErrorCode(),
          ResponseCode.phoneNoFormatError.getErrorMessage(),
          ERROR_CODE);
    }
  }

  public static boolean validateCountryCode(String countryCode) {
    String pattern = "^(?:[+] ?){0,1}(?:[0-9] ?){1,3}";
    try {
      Pattern patt = Pattern.compile(pattern);
      Matcher matcher = patt.matcher(countryCode);
      return matcher.matches();
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean validatePhone(String phNumber, String countryCode) {
    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    String contryCode = countryCode;
    if (!StringUtils.isBlank(countryCode) && (countryCode.charAt(0) != '+')) {
      contryCode = "+" + countryCode;
    }
    Phonenumber.PhoneNumber phoneNumber = null;
    try {
      if (StringUtils.isBlank(countryCode)) {
        contryCode = PropertiesCache.getInstance().getProperty("sunbird_default_country_code");
      }
      String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(contryCode));
      phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
      return phoneNumberUtil.isValidNumber(phoneNumber);
    } catch (NumberParseException e) {
      ProjectLogger.log(
          "PhoneValidator: validatePhone: Exception occurred while validating phone number = ", e);
    }
    return false;
  }
}
