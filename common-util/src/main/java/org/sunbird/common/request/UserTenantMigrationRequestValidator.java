package org.sunbird.common.request;

import java.util.Map;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.responsecode.ResponseCode;

public class UserTenantMigrationRequestValidator extends UserRequestValidator {

  public void validateUserTenantMigrateRequest(Request request) {
    Map<String, Object> req = request.getRequest();
    validateParam(
        (String) req.get(JsonKey.CHANNEL), ResponseCode.mandatoryParamsMissing, JsonKey.CHANNEL);
    validateParam(
        (String) req.get(JsonKey.USER_ID), ResponseCode.mandatoryParamsMissing, JsonKey.USER_ID);
    externalIdsValidation(request, JsonKey.CREATE);
  }
}
