package org.sunbird.validator.user;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.models.user.UserType;

public class UserBulkUploadRequestValidator {

  private UserBulkUploadRequestValidator() {}

  public static void validateUserBulkUploadRequest(Map<String, Object> userMap) {
    validateUserType(userMap);
    validateOrganisationId(userMap);
  }

  public static void validateUserType(Map<String, Object> userMap) {
    List<String> userTypes =
        Stream.of(UserType.values()).map(UserType::name).collect(Collectors.toList());
    userTypes.remove(JsonKey.SELF_SIGN_UP);
    String userType = (String) userMap.get(JsonKey.USER_TYPE);
    if (userTypes.contains(userType.trim().toUpperCase())) {
      userMap.put(JsonKey.USER_TYPE, userType.trim().toUpperCase());
    } else {
      throw new ProjectCommonException(
          ResponseCode.invalidValue.getErrorCode(),
          ProjectUtil.formatMessage(
              ResponseCode.invalidValue.getErrorMessage(), JsonKey.USER_TYPE, userType, userTypes),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }

  public static void validateOrganisationId(Map<String, Object> userMap) {
    String userType = (String) userMap.get("userType");
    if (UserType.TEACHER.name().equalsIgnoreCase(userType.trim().toUpperCase())
        && StringUtils.isBlank((String) userMap.get(JsonKey.ORG_ID))) {
      throw new ProjectCommonException(
          ResponseCode.mandatoryParamsMissing.getErrorCode(),
          ProjectUtil.formatMessage(
              ResponseCode.mandatoryParamsMissing.getErrorMessage(), JsonKey.ORGANISATION_ID),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }
}
