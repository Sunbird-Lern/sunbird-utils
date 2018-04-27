package org.sunbird.common.request;

import java.util.Arrays;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
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

  /**
   * Method to check whether given fields is in given map or not .If it is there throw exception.
   * because in some update request cases we don't want to update some props to , if it is there in
   * request , throw exception.
   *
   * @param data Map contains the key value
   * @param keys List of string represents the must not present fields.
   */
  public void checkReadOnlyAttributesAbsent(Map<String, Object> data, String... keys) {

    if (MapUtils.isEmpty(data)) {
      throw new ProjectCommonException(
          ResponseCode.invalidRequestData.getErrorCode(),
          ResponseCode.invalidRequestData.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    Arrays.stream(keys)
        .forEach(
            key -> {
              if (data.containsKey(key)) {
                throw new ProjectCommonException(
                    ResponseCode.unupdatableField.getErrorCode(),
                    ResponseCode.unupdatableField.getErrorMessage(),
                    ResponseCode.CLIENT_ERROR.getResponseCode(),
                    key);
              }
            });
  }

  /**
   * Method to check whether given header fields present or not.
   *
   * @param data Map contains the header as key value,
   * @param keys List of string reprents the headers fields.
   */
  public void checkMandatoryHeaderssPresent(Map<String, String[]> data, String... keys) {
    if (MapUtils.isEmpty(data)) {
      throw new ProjectCommonException(
          ResponseCode.invalidRequestData.getErrorCode(),
          ResponseCode.invalidRequestData.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    Arrays.stream(keys)
        .forEach(
            key -> {
              if (ArrayUtils.isEmpty(data.get(key))) {
                throw new ProjectCommonException(
                    ResponseCode.mandatoryHeadersMissing.getErrorCode(),
                    ResponseCode.mandatoryHeadersMissing.getErrorMessage(),
                    ResponseCode.CLIENT_ERROR.getResponseCode(),
                    key);
              }
            });
  }
}
