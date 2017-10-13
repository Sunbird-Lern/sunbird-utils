/**
 * 
 */
package org.sunbird.common.request;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * @author Manzarul
 *
 */
public class UserRequestValidator {
  
  @Test
  public void validateForgotPasswordSuccess() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "manzarul07");
    request.setRequest(requestObj);
    RequestValidator.validateForgotpassword(request);
    assertEquals(true, true);
  }

  @Test
  public void validateForgotPasswordUserNameEmpty() {
    try {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "");
    request.setRequest(requestObj);
    RequestValidator.validateForgotpassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.userNameRequired.getErrorCode(), e.getCode());
    }
  }
 
  
  @Test
  public void validateForgotPasswordUserNameKeyMissing() {
    try {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    request.setRequest(requestObj);
    RequestValidator.validateForgotpassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.userNameRequired.getErrorCode(), e.getCode());
    }
  }
  
}
