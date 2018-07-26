package org.sunbird.validator.initialisation;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

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
          ResponseCode.channelRequiredForRootOrg.getErrorCode(),
          ResponseCode.channelRequiredForRootOrg.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }
}
