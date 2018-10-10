package org.sunbird.common.request;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.responsecode.ResponseCode;

public class OrgMemberRequestValidator extends BaseRequestValidator {

  private static final int ERROR_CODE = ResponseCode.CLIENT_ERROR.getResponseCode();

  public void validateAddMember(Request request) {
    validateOrgMemberRequest(request);
    if (request.getRequest().containsKey(JsonKey.ROLES)
        && (!(request.getRequest().get(JsonKey.ROLES) instanceof List)
            || ((List) request.getRequest().get(JsonKey.ROLES)).isEmpty())) {
      throw new ProjectCommonException(
          ResponseCode.roleRequired.getErrorCode(),
          ResponseCode.roleRequired.getErrorMessage(),
          ERROR_CODE);
    }
  }

  public void validateOrgMemberRequest(Request request) {
    new OrgRequestValidator().validateOrgReqForIdOrExternalIdAndProvider(request);
    if ((StringUtils.isEmpty((String) request.getRequest().get(JsonKey.USERNAME))
            || StringUtils.isBlank((String) request.getRequest().get(JsonKey.PROVIDER)))
        && StringUtils.isBlank((String) request.getRequest().get(JsonKey.USER_ID))) {
      throw new ProjectCommonException(
          ResponseCode.usrValidationError.getErrorCode(),
          ResponseCode.usrValidationError.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }
}
