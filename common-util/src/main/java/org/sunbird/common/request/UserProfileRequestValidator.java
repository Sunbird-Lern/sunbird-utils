package org.sunbird.common.request;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;

public class UserProfileRequestValidator extends BaseRequestValidator {

  @SuppressWarnings("unchecked")
  public void validateProfileVisibility(Request request) {
    if (request.getRequest().get(JsonKey.USER_ID) == null
        || StringUtils.isBlank(((String) request.getRequest().get(JsonKey.USER_ID)))) {
      throw new ProjectCommonException(
          ResponseCode.usernameOrUserIdError.getErrorCode(),
          ResponseCode.usernameOrUserIdError.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    validateUserId(request, JsonKey.USER_ID);
    checkExistenceAndDataTypeForPublicPrivate(request);
    checkKeyNotPresentInPublicPrivate(request);
  }

  private void checkExistenceAndDataTypeForPublicPrivate(Request request) {
    if (request.getRequest().get(JsonKey.PRIVATE) == null
        && request.getRequest().get(JsonKey.PUBLIC) == null) {
      throw new ProjectCommonException(
          ResponseCode.invalidData.getErrorCode(),
          ResponseCode.invalidData.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    checkNotInstanceOfList(request, JsonKey.PRIVATE);
    checkNotInstanceOfList(request, JsonKey.PUBLIC);
  }

  private void checkNotInstanceOfList(Request request, String forKey) {
    if (request.getRequest().containsKey(forKey)
        && !(request.getRequest().get(forKey) instanceof List)) {
      throw new ProjectCommonException(
          ResponseCode.dataTypeError.getErrorCode(),
          ProjectUtil.formatMessage(
              ResponseCode.dataTypeError.getErrorMessage(), forKey, JsonKey.LIST),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }

  private void checkKeyNotPresentInPublicPrivate(Request request) {
    if (null != request.getRequest().get(JsonKey.PRIVATE)
        && null != request.getRequest().get(JsonKey.PUBLIC)) {
      List<String> privateList = (List<String>) request.getRequest().get(JsonKey.PRIVATE);
      List<String> publicList = (List<String>) request.getRequest().get(JsonKey.PUBLIC);
      if (privateList.size() > publicList.size()) {
        checkNotExistsInBothList(publicList, privateList);
      } else {
        checkNotExistsInBothList(privateList, publicList);
      }
    }
  }

  private void checkNotExistsInBothList(List<String> checkInList, List<String> iterateList) {
    for (String field : iterateList) {
      if (checkInList.contains(field)) {
        throw new ProjectCommonException(
            ResponseCode.visibilityInvalid.getErrorCode(),
            ResponseCode.visibilityInvalid.getErrorMessage(),
            ResponseCode.CLIENT_ERROR.getResponseCode());
      }
    }
  }
}
