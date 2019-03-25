package org.sunbird.common.request;

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.responsecode.ResponseCode;

/** Created by rajatgupta on 20/03/19. */
public class BaseRequestValidatorTest {
  private static final BaseRequestValidator baseRequestValidator = new BaseRequestValidator();

  @Test
  public void testValidateSearchRequestFailureWithInvalidFieldType() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.FILTERS, new HashMap<>());
    requestObj.put(JsonKey.FIELDS, "invalid");
    request.setRequest(requestObj);
    try {
      baseRequestValidator.validateSearchRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
      assertEquals(
          MessageFormat.format(
              ResponseCode.dataTypeError.getErrorMessage(), JsonKey.FIELDS, "List"),
          e.getMessage());
    }
  }

  @Test
  public void testValidateSearchRequestFailureWithInvalidFieldsValueInList() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.FILTERS, new HashMap<>());
    requestObj.put(JsonKey.FIELDS, Arrays.asList(1));
    request.setRequest(requestObj);
    try {
      baseRequestValidator.validateSearchRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
      assertEquals(
          MessageFormat.format(
              ResponseCode.dataTypeError.getErrorMessage(), JsonKey.FIELDS, "List of String"),
          e.getMessage());
    }
  }
}
