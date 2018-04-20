package org.sunbird.common.request;

import java.util.Arrays;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * Base request validator class to house common validation methods.
 *
 * @author B Vinaya Kumar
 */
public class BaseRequestValidator {
  /**
   * Helper method which throws an exception if given parameter value is blank (null or empty).
   *
   * @param value Request parameter value.
   * @param error Error to be thrown in case of validation error.
   */
  public void validateParam(String value, ResponseCode error) {
    if (StringUtils.isBlank(value)) {
      throw new ProjectCommonException(
          error.getErrorCode(),
          error.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }

  /**
   * Method to check whether given mandatory fields is in given map or not .
   *
   * @param data Map contains the key value,
   * @param keys List of string reprents the mandatory fields .mvn
   */
  public void checkMandatoryFieldsPresent(Map<String, Object> data, String... keys) {
    if (MapUtils.isEmpty(data)) {
      throw new ProjectCommonException(
          ResponseCode.invalidRequestData.getErrorCode(),
          ResponseCode.invalidRequestData.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    Arrays.stream(keys)
        .forEach(
            key -> {
              if (StringUtils.isEmpty((String) data.get(key))) {
                throw new ProjectCommonException(
                    ResponseCode.mandatoryParamsMissing.getErrorCode(),
                    ResponseCode.mandatoryParamsMissing.getErrorMessage(),
                    ResponseCode.CLIENT_ERROR.getResponseCode(),
                    key);
              }
            });
  }
}
