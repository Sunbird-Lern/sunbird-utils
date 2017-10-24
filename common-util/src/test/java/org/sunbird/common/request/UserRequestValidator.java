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
  
  @Test
  public void validateChangePasswordSuccess() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.NEW_PASSWORD, "password1");
    requestObj.put(JsonKey.PASSWORD, "password");
    request.setRequest(requestObj);
    RequestValidator.validateChangePassword(request);
    assertEquals(true, true);
  }
  
  @Test
  public void validateChangePasswordWithEmptyNewPass() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.NEW_PASSWORD, "");
    requestObj.put(JsonKey.PASSWORD, "password");
    request.setRequest(requestObj);
    try {
    RequestValidator.validateChangePassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.newPasswordEmpty.getErrorCode(), e.getCode());
    }
  }
 
  @Test
  public void validateChangePasswordWithMissingNewPass() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PASSWORD, "password");
    request.setRequest(requestObj);
    try {
    RequestValidator.validateChangePassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.newPasswordRequired.getErrorCode(), e.getCode());
    }
  }
  
  @Test
  public void validateChangePasswordWithSameOldAndNewPass() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.NEW_PASSWORD, "password");
    requestObj.put(JsonKey.PASSWORD, "password");
    request.setRequest(requestObj);
    try {
    RequestValidator.validateChangePassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.samePasswordError.getErrorCode(), e.getCode());
    }
  }
 
  @Test
  public void validateChangePasswordWithPassWordMissing() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.NEW_PASSWORD, "password");
    request.setRequest(requestObj);
    try {
    RequestValidator.validateChangePassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.passwordRequired.getErrorCode(), e.getCode());
    }
  }
  
  @Test
  public void validateUserSuccessLogin() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PASSWORD, "password");
    requestObj.put(JsonKey.USERNAME, "test123@test.com");
    requestObj.put(JsonKey.SOURCE, "web");
    request.setRequest(requestObj);
    RequestValidator.validateUserLogin(request);
    assertEquals(true, true);
  }
  
  @Test
  public void validateUserLoginWithOutSource() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PASSWORD, "password");
    requestObj.put(JsonKey.USERNAME, "test123@test.com");
    request.setRequest(requestObj);
    try { 
    RequestValidator.validateUserLogin(request);
     } catch (ProjectCommonException e) {
       assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
       assertEquals(ResponseCode.sourceRequired.getErrorCode(), e.getCode()); 
     }    
  }
  
  @Test
  public void validateUserLoginWithOutUserName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PASSWORD, "password");
    requestObj.put(JsonKey.SOURCE, "web");
    request.setRequest(requestObj);
    try { 
    RequestValidator.validateUserLogin(request);
     } catch (ProjectCommonException e) {
       assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
       assertEquals(ResponseCode.userNameRequired.getErrorCode(), e.getCode()); 
     }    
  }
  
  @Test
  public void validateUserLoginWithEmptyUserName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PASSWORD, "password");
    requestObj.put(JsonKey.SOURCE, "web");
    requestObj.put(JsonKey.USERNAME, "test123@test.com");
    request.setRequest(requestObj);
    try { 
    RequestValidator.validateUserLogin(request);
     } catch (ProjectCommonException e) {
       assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
       assertEquals(ResponseCode.userNameRequired.getErrorCode(), e.getCode()); 
     }    
  }
  
  @Test
  public void validateUserLoginWithEmptyPassword() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PASSWORD, "");
    requestObj.put(JsonKey.SOURCE, "web");
    requestObj.put(JsonKey.USERNAME, "test123@test.com");
    request.setRequest(requestObj);
    try { 
    RequestValidator.validateUserLogin(request);
     } catch (ProjectCommonException e) {
       assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
       assertEquals(ResponseCode.passwordRequired.getErrorCode(), e.getCode()); 
     }    
  }
 
  @Test
  public void validateUserLoginWithOutPassword() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SOURCE, "web");
    requestObj.put(JsonKey.USERNAME, "test123@test.com");
    request.setRequest(requestObj);
    try { 
    RequestValidator.validateUserLogin(request);
     } catch (ProjectCommonException e) {
       assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
       assertEquals(ResponseCode.passwordRequired.getErrorCode(), e.getCode()); 
     }    
  }
  
}
