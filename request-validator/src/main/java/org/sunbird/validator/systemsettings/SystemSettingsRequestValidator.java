package org.sunbird.validator.systemsettings;

import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * This class has the methods needed for validating System settings api request
 *
 * @author Loganathan
 */
public class SystemSettingsRequestValidator {

  /**
   * This methods validates the provided request for mandatory fields and throws exception if it
   * fails
   *
   * @param request instance of Request class has the values to be updated to System Settings
   *     (id,field,value)
   */
  public void validateUpdateSystemSetting(Request request) {
    if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.ID))) {
      throw new ProjectCommonException(
          ResponseCode.systemSettingIdRequiredError.getErrorCode(),
          ResponseCode.systemSettingIdRequiredError.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.FIELD))) {
      throw new ProjectCommonException(
          ResponseCode.systemSettingFieldRequiredError.getErrorCode(),
          ResponseCode.systemSettingFieldRequiredError.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.VALUE))) {
      throw new ProjectCommonException(
          ResponseCode.systemSettingValueRequiredError.getErrorCode(),
          ResponseCode.systemSettingValueRequiredError.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }
}
