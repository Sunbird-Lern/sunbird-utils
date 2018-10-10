package org.sunbird.common.request;

import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.responsecode.ResponseCode;

public class OrgTypeRequestValidator extends BaseRequestValidator {

  public void validateUpdateOrgType(Request request) {
    validateParam(
        (String) request.getRequest().get(JsonKey.NAME),
        ResponseCode.mandatoryParamsMissing,
        JsonKey.NAME);
    validateParam(
        (String) request.getRequest().get(JsonKey.ID),
        ResponseCode.mandatoryParamsMissing,
        JsonKey.ID);
  }

  public void validateCreateOrgType(Request request) {
    validateParam(
        (String) request.getRequest().get(JsonKey.NAME),
        ResponseCode.mandatoryParamsMissing,
        JsonKey.NAME);
  }
}
