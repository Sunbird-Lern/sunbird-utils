/**
 * 
 */
package org.sunbird.common.request;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * @author Manzarul
 *
 */
public class UserRequestValidatorTest {
  
  @Test
  public void validateForgotPasswordSuccess() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "manzarul07");
    request.setRequest(requestObj);
    try {
    RequestValidator.validateForgotpassword(request);
    response = true;
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals(true, response);
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
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.NEW_PASSWORD, "password1");
    requestObj.put(JsonKey.PASSWORD, "password");
    request.setRequest(requestObj);
    try {
    RequestValidator.validateChangePassword(request);
    response = true;
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals(true, response);
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
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PASSWORD, "password");
    requestObj.put(JsonKey.USERNAME, "test123@test.com");
    requestObj.put(JsonKey.SOURCE, "web");
    request.setRequest(requestObj);
    try {
    RequestValidator.validateUserLogin(request);
    response = true;
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals(true, response);
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
  
  @Test
  public void validateCreateUserSuccessWithAllFields () {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>(); 
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.EMAIL, "test123@test.com");
    requestObj.put(JsonKey.USERNAME, "manzarul.haque");
    requestObj.put(JsonKey.FIRST_NAME, "manzarul");
    List<String> roles = new ArrayList<String>();
    roles.add("PUBLIC");
    roles.add("CONTENT-CREATOR");
    requestObj.put(JsonKey.ROLE, roles);
    List<String> language = new ArrayList<>();
    language.add("English");
    requestObj.put(JsonKey.LANGUAGE, language);
    List<Map<String,Object>> addressList = new ArrayList<>();
    Map<String,Object> map = new HashMap<>();
    map.put(JsonKey.ADDRESS_LINE1, "test");
    map.put(JsonKey.CITY, "Bangalore");
    map.put(JsonKey.COUNTRY, "India");
    map.put(JsonKey.ADD_TYPE, "current");
    addressList.add(map);
    requestObj.put(JsonKey.ADDRESS, addressList);
    
    List<Map<String,Object>> educationList = new ArrayList<>();
    Map<String,Object> map1 = new HashMap<>();
    map1.put(JsonKey.COURSE_NAME, "M.C.A");
    map1.put(JsonKey.DEGREE, "Master");
    map1.put(JsonKey.NAME, "CUSAT");
    educationList.add(map1);
    requestObj.put(JsonKey.EDUCATION, educationList);
    
    List<Map<String,Object>> jobProfileList = new ArrayList<>();
    map1 = new HashMap<>();
    map1.put(JsonKey.JOB_NAME, "SE");
    map1.put(JsonKey.ORGANISATION_NAME, "Tarento");
    jobProfileList.add(map1);
    requestObj.put(JsonKey.JOB_PROFILE, jobProfileList);
    request.setRequest(requestObj);
    try {
    RequestValidator.validateCreateUser(request);
    response = true;
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals(true, response);
  }
  
  @Test
  public void validatePhoneAndEmailUpdateSuccess () {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>(); 
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.EMAIL, "test123@test.com");
    request.put(JsonKey.PROVIDER, "sunbird");
    request.put(JsonKey.PHONE_VERIFIED, true);
    request.put(JsonKey.EMAIL_VERIFIED, true);
    request.setRequest(requestObj);
    try {
    RequestValidator.phoneAndEmailValidationForUpdateUser(request);
    response = true;
    } catch (ProjectCommonException e) {
    	 Assert.assertNull(e);
	}
    assertEquals(true, response);
  }
  
  @Test
  public void validateUpdateUserSuccess () {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>(); 
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.EMAIL, "test123@test.com");
    requestObj.put(JsonKey.USERNAME, "manzarul.haque");
    requestObj.put(JsonKey.FIRST_NAME, "manzarul");
    requestObj.put(JsonKey.ROOT_ORG_ID, "ORG123");
    List<String> roles = new ArrayList<String>();
    roles.add("PUBLIC");
    roles.add("CONTENT-CREATOR");
    requestObj.put(JsonKey.ROLE, roles);
    List<String> language = new ArrayList<>();
    language.add("English");
    requestObj.put(JsonKey.LANGUAGE, language);
    List<Map<String,Object>> addressList = new ArrayList<>();
    Map<String,Object> map = new HashMap<>();
    map.put(JsonKey.ADDRESS_LINE1, "test");
    map.put(JsonKey.CITY, "Bangalore");
    map.put(JsonKey.COUNTRY, "India");
    map.put(JsonKey.ADD_TYPE, "current");
    addressList.add(map);
    requestObj.put(JsonKey.ADDRESS, addressList);
    
    List<Map<String,Object>> educationList = new ArrayList<>();
    Map<String,Object> map1 = new HashMap<>();
    map1.put(JsonKey.COURSE_NAME, "M.C.A");
    map1.put(JsonKey.DEGREE, "Master");
    map1.put(JsonKey.NAME, "CUSAT");
    educationList.add(map1);
    requestObj.put(JsonKey.EDUCATION, educationList);
    
    List<Map<String,Object>> jobProfileList = new ArrayList<>();
    map1 = new HashMap<>(); 
    map1.put(JsonKey.JOB_NAME, "SE");
    map1.put(JsonKey.ORGANISATION_NAME, "Tarento");
    jobProfileList.add(map1);
    requestObj.put(JsonKey.JOB_PROFILE, jobProfileList);
    boolean response = false;
    request.setRequest(requestObj);
    try {
    RequestValidator.validateUpdateUser(request);
    response = true;
    } catch (ProjectCommonException e) {
    	 Assert.assertNull(e);
	}
    assertEquals(true, response);
  }
 
  @Test
  public void validateUploadUserWithOrgId () {
     Request request = new Request();
     boolean response = false;
     Map<String, Object> requestObj = new HashMap<>();
     requestObj.put(JsonKey.ORGANISATION_ID, "ORG-1233");
     request.setRequest(requestObj);
     try {
     RequestValidator.validateUploadUser(request);
     response = true;
     } catch (ProjectCommonException e) {
    	 Assert.assertNull(e); 	 
	}
     assertEquals(true, response);
  }
  
  @Test
  public void validateUploadUserWithProviderAndExternalId () {
     Request request = new Request();
     boolean response = false;
     Map<String, Object> requestObj = new HashMap<>();
     requestObj.put(JsonKey.PROVIDER, "ORG-provider");
     requestObj.put(JsonKey.EXTERNAL_ID, "ORG-1233");
     request.setRequest(requestObj);
     try {
     RequestValidator.validateUploadUser(request);
     response = true;
     } catch (ProjectCommonException e) {
    	 Assert.assertNull(e); 
	}
     assertEquals(true, response);
  }
 
  @Test
  public void validateAssignRoleWithUserId () {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USER_ID, "ORG-provider");
    List<String> roles =  new ArrayList<>();
    roles.add("PUBLIC");
    requestObj.put(JsonKey.ROLES, roles);
    request.setRequest(requestObj);
    try {
    RequestValidator.validateAssignRole(request);
    response = true;
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals(true, response); 
  }
  
  @Test
  public void validateAssignRoleWithProviderAndExternalId () {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PROVIDER, "ORG-provider");
    requestObj.put(JsonKey.EXTERNAL_ID, "ORG-1233");
    List<String> roles =  new ArrayList<>();
    roles.add("PUBLIC");
    requestObj.put(JsonKey.ROLES, roles);
    request.setRequest(requestObj);
    try {
    RequestValidator.validateAssignRole(request);
    response = true;
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals(true, response); 
  }
  
  
  @Test
  public void phoneAndEmailValidationForUpdateUser () {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9878888888");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.EMAIL, "test123@test.com");
    requestObj.put(JsonKey.PHONE_VERIFIED, true);
    request.setRequest(requestObj);
    try {
    RequestValidator.phoneAndEmailValidationForUpdateUser(request);
     response = true;
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals(true, response); 
  }
}
