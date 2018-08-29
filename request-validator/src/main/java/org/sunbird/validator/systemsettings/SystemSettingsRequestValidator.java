package org.sunbird.validator.systemsettings;

import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.BaseRequestValidator;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

public class SystemSettingsRequestValidator extends BaseRequestValidator {
  public void validateSetSystemSetting(Request request) {
    validateParam(
        (String) request.getRequest().get(JsonKey.ID), ResponseCode.mandatoryParamsMissing);
    validateParam(
        (String) request.getRequest().get(JsonKey.FIELD), ResponseCode.mandatoryParamsMissing);
    validateParam(
        (String) request.getRequest().get(JsonKey.VALUE), ResponseCode.mandatoryParamsMissing);
  }
}
