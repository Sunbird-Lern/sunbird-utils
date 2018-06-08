package org.sunbird.common.models.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class EmailValidator {

  private static Pattern pattern;
  private static final String EMAIL_PATTERN =
      "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
          + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

  private EmailValidator() {}

  static {
    pattern = Pattern.compile(EMAIL_PATTERN);
  }

  /**
   * Method to validate email.
   *
   * @param email Email value
   * @return boolean Is email valid or not.
   */
  public static boolean isEmailvalid(String email) {
    if (StringUtils.isBlank(email)) {
      return false;
    }
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }
}
