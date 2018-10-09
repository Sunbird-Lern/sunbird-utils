package org.sunbird.common.request;

import java.math.BigInteger;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.ProjectUtil.AddressType;
import org.sunbird.common.responsecode.ResponseCode;

public class OrgRequestValidator extends BaseRequestValidator {

  private static final int ERROR_CODE = ResponseCode.CLIENT_ERROR.getResponseCode();

  public void validateCreateOrgRequest(Request orgRequest) {
    validateParam(
        (String) orgRequest.getRequest().get(JsonKey.ORG_NAME),
        ResponseCode.mandatoryParamsMissing,
        JsonKey.ORG_NAME);

    if ((null != orgRequest.getRequest().get(JsonKey.IS_ROOT_ORG)
            && (Boolean) orgRequest.getRequest().get(JsonKey.IS_ROOT_ORG))
        && StringUtils.isEmpty((String) orgRequest.getRequest().get(JsonKey.CHANNEL))) {
      throw new ProjectCommonException(
          ResponseCode.channelIdRequiredForRootOrg.getErrorCode(),
          ResponseCode.channelIdRequiredForRootOrg.getErrorMessage(),
          ERROR_CODE);
    }
    if (MapUtils.isNotEmpty((Map<String, Object>) orgRequest.getRequest().get(JsonKey.ADDRESS))) {
      validateAddress(
          (Map<String, Object>) orgRequest.getRequest().get(JsonKey.ADDRESS), JsonKey.ORGANISATION);
    }
  }

  public void validateUpdateOrgRequest(Request request) {
    validateOrgReqForIdOrExternalIdAndProvider(request);
    if (request.getRequest().containsKey(JsonKey.ROOT_ORG_ID)
        && StringUtils.isEmpty((String) request.getRequest().get(JsonKey.ROOT_ORG_ID))) {
      throw new ProjectCommonException(
          ResponseCode.invalidRootOrganisationId.getErrorCode(),
          ResponseCode.invalidRootOrganisationId.getErrorMessage(),
          ERROR_CODE);
    }
    if (request.getRequest().get(JsonKey.STATUS) != null) {
      throw new ProjectCommonException(
          ResponseCode.invalidRequestParameter.getErrorCode(),
          ProjectUtil.formatMessage(
              ResponseCode.invalidRequestParameter.getErrorMessage(), JsonKey.STATUS),
          ERROR_CODE);
    }
    if ((null != request.getRequest().get(JsonKey.IS_ROOT_ORG)
            && (Boolean) request.getRequest().get(JsonKey.IS_ROOT_ORG))
        && StringUtils.isEmpty((String) request.getRequest().get(JsonKey.CHANNEL))) {
      throw new ProjectCommonException(
          ResponseCode.channelIdRequiredForRootOrg.getErrorCode(),
          ResponseCode.channelIdRequiredForRootOrg.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    if (MapUtils.isNotEmpty((Map<String, Object>) request.getRequest().get(JsonKey.ADDRESS))) {
      validateAddress(
          (Map<String, Object>) request.getRequest().get(JsonKey.ADDRESS), JsonKey.ORGANISATION);
    }
  }

  public void validateUpdateOrgStatusRequest(Request request) {
    validateOrgReqForIdOrExternalIdAndProvider(request);
    if (!request.getRequest().containsKey(JsonKey.STATUS)) {
      throw new ProjectCommonException(
          ResponseCode.invalidRequestData.getErrorCode(),
          ResponseCode.invalidRequestData.getErrorMessage(),
          ERROR_CODE);
    }
    if (!(request.getRequest().get(JsonKey.STATUS) instanceof BigInteger)) {
      throw new ProjectCommonException(
          ResponseCode.invalidRequestData.getErrorCode(),
          ResponseCode.invalidRequestData.getErrorMessage(),
          ERROR_CODE);
    }
  }

  public void validateOrgReqForIdOrExternalIdAndProvider(Request request) {
    if (StringUtils.isEmpty((String) request.getRequest().get(JsonKey.ORGANISATION_ID))
        && ((StringUtils.isEmpty((String) request.getRequest().get(JsonKey.PROVIDER)))
            || (StringUtils.isBlank((String) request.getRequest().get(JsonKey.EXTERNAL_ID))))) {
      throw new ProjectCommonException(
          ResponseCode.sourceAndExternalIdValidationError.getErrorCode(),
          ResponseCode.sourceAndExternalIdValidationError.getErrorMessage(),
          ERROR_CODE);
    }
  }

  private static void validateAddress(Map<String, Object> address, String type) {
    if (StringUtils.isBlank((String) address.get(JsonKey.ADDRESS_LINE1))) {
      throw new ProjectCommonException(
          ResponseCode.addressError.getErrorCode(),
          ProjectUtil.formatMessage(
              ResponseCode.addressError.getErrorMessage(), type, JsonKey.ADDRESS_LINE1),
          ERROR_CODE);
    }
    if (StringUtils.isBlank((String) address.get(JsonKey.CITY))) {
      throw new ProjectCommonException(
          ResponseCode.addressError.getErrorCode(),
          ProjectUtil.formatMessage(
              ResponseCode.addressError.getErrorMessage(), type, JsonKey.CITY),
          ERROR_CODE);
    }
    if (address.containsKey(JsonKey.ADD_TYPE)) {

      if (StringUtils.isBlank((String) address.get(JsonKey.ADD_TYPE))) {
        throw new ProjectCommonException(
            ResponseCode.addressError.getErrorCode(),
            ProjectUtil.formatMessage(
                ResponseCode.addressError.getErrorMessage(), JsonKey.ADDRESS, JsonKey.TYPE),
            ERROR_CODE);
      }

      if (!StringUtils.isBlank((String) address.get(JsonKey.ADD_TYPE))
          && !checkAddressType((String) address.get(JsonKey.ADD_TYPE))) {
        throw new ProjectCommonException(
            ResponseCode.addressTypeError.getErrorCode(),
            ResponseCode.addressTypeError.getErrorMessage(),
            ERROR_CODE);
      }
    }
  }

  private static boolean checkAddressType(String addrType) {
    for (AddressType type : AddressType.values()) {
      if (type.getTypeName().equals(addrType)) {
        return true;
      }
    }
    return false;
  }
}
