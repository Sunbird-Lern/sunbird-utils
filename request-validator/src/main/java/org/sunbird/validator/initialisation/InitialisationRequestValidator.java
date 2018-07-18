package org.sunbird.validator.initialisation;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * This class contains methods to validate the initialisation api request
 *
 * @author Loganathan
 */
public class InitialisationRequestValidator {

  /**
   * Validates the incoming request data This method verifies the required fields are passed or not
   *
   * @param request instance of Request class with org request data like orgName,channel
   */
  public void validateCreateFirstRootOrg(Request request) {
    if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.ORG_NAME))) {
      throw new ProjectCommonException(
          ResponseCode.organisationNameRequired.getErrorCode(),
          ResponseCode.organisationNameRequired.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    if (StringUtils.isBlank((String) request.getRequest().get(JsonKey.CHANNEL))) {
      throw new ProjectCommonException(
          ResponseCode.channelIdRequiredForRootOrg.getErrorCode(),
          ResponseCode.channelIdRequiredForRootOrg.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    if (!validateRemoteAddress(request.get(JsonKey.REMOTE_ADDRESS).toString())) {
      throw new ProjectCommonException(
          ResponseCode.restrictedRequest.getErrorCode(),
          ResponseCode.restrictedRequest.getErrorMessage(),
          ResponseCode.FORBIDDEN.getResponseCode());
    }
  }

  /**
   * This method will validates the client request host IP with existing initalisation hosts
   *
   * @param remoteAddress Ipaddress of requesting client
   */
  public Boolean validateRemoteAddress(String remoteAddress) {
    List<String> allowedInitialisationHosts =
        Arrays.asList(ProjectUtil.getConfigValue(JsonKey.INITIALISATION_HOSTS).split(","));
    Boolean validAddress = false;
    if (allowedInitialisationHosts.contains(remoteAddress)
        || remoteAddress == JsonKey.LOCAL_IPV4_ADDRESS) {
      validAddress = true;
    }
    return validAddress;
  }
}
