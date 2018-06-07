package org.sunbird.common.request;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
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
   * Helper method which throws an exception if given parameter value is blank (null or empty).
   *
   * @param value Request parameter value.
   * @param error Error to be thrown in case of validation error.
   * @param errorMsgArgument Argument for error message.
   */
  public void validateParam(String value, ResponseCode error, String errorMsgArgument) {
    if (StringUtils.isBlank(value)) {
      throw new ProjectCommonException(
          error.getErrorCode(),
          MessageFormat.format(error.getErrorMessage(), errorMsgArgument),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }

  /**
   * This method will create the ProjectCommonException by reading ResponseCode and errorCode.
   * incase ResponseCode is null then it will throw invalidData error.
   *
   * @param code Error response code
   * @param errorCode (Http error code)
   * @return custom project exception
   */
  public ProjectCommonException createExceptionByResponseCode(ResponseCode code, int errorCode) {
    if (code == null) {
      ProjectLogger.log("ResponseCode object is coming as null", LoggerEnum.INFO.name());
      return new ProjectCommonException(
          ResponseCode.invalidData.getErrorCode(),
          ResponseCode.invalidData.getErrorMessage(),
          errorCode);
    }
    return new ProjectCommonException(code.getErrorCode(), code.getErrorMessage(), errorCode);
  }

  /**
   * This method will create the ProjectCommonException by reading ResponseCode and errorCode.
   * incase ResponseCode is null then it will throw invalidData error.
   *
   * @param code Error response code
   * @param errorCode (Http error code)
   * @return custom project exception
   */
  public ProjectCommonException createExceptionByResponseCode(
      ResponseCode code, int errorCode, String errorMsgArgument) {
    if (code == null) {
      ProjectLogger.log("ResponseCode object is coming as null", LoggerEnum.INFO.name());
      return new ProjectCommonException(
          ResponseCode.invalidData.getErrorCode(),
          ResponseCode.invalidData.getErrorMessage(),
          errorCode);
    }
    return new ProjectCommonException(
        code.getErrorCode(),
        MessageFormat.format(code.getErrorMessage(), errorMsgArgument),
        errorCode);
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
   * @param data List of strings representing the header names in received request.
   * @param keys List of string reprents the headers fields.
   */
  public void checkMandatoryHeadersPresent(Map<String, String[]> data, String... keys) {
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
