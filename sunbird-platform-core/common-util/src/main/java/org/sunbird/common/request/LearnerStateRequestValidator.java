package org.sunbird.common.request;

import org.sunbird.common.models.util.JsonKey;

/** @author arvind */
public class LearnerStateRequestValidator extends BaseRequestValidator {

  /**
   * Method to validate the get content state request.
   *
   * @param request Representing the request object.
   */
  public void validateGetContentState(Request request) {
    checkMandatoryFieldsPresent(request.getRequest(), JsonKey.USER_ID);
    validateListParam(request.getRequest(), JsonKey.COURSE_IDS, JsonKey.CONTENT_IDS);
  }
}
