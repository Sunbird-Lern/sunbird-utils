package org.sunbird.common.request.orgvalidator;

import java.text.MessageFormat;
import java.util.List;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

public class OrgMemberRequestValidator extends BaseOrgRequestValidator {

  public void validateAddMemberRequest(Request request) {
    validateCommon(request);
    if (request.getRequest().containsKey(JsonKey.ROLES)
        && (!(request.getRequest().get(JsonKey.ROLES) instanceof List))) {
      throw new ProjectCommonException(
          ResponseCode.dataTypeError.getErrorCode(),
          MessageFormat.format(
              ResponseCode.dataTypeError.getErrorMessage(), JsonKey.ROLES, JsonKey.LIST),
          ERROR_CODE);
    }
  }

  public void validateCommon(Request request) {
    validateOrgReference(request);
    validateParam(
        (String) request.getRequest().get(JsonKey.USER_ID),
        ResponseCode.mandatoryParamsMissing,
        JsonKey.USER_ID);
  }
}
