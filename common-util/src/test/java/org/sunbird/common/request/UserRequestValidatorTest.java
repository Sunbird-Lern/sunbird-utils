/** */
package org.sunbird.common.request;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.responsecode.ResponseCode;

/** @author Manzarul */
public class UserRequestValidatorTest {

  @Test
  public void testValidateForgotPasswordSuccess() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "manzarul07");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateForgotpassword(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void testValidateForgotPasswordFailureWithEmptyName() {
    try {
      Request request = new Request();
      Map<String, Object> requestObj = new HashMap<>();
      requestObj.put(JsonKey.USERNAME, "");
      request.setRequest(requestObj);
      UserRequestValidator.validateForgotpassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.userNameRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateForgotPasswordFailureWithoutName() {
    try {
      Request request = new Request();
      Map<String, Object> requestObj = new HashMap<>();
      request.setRequest(requestObj);
      UserRequestValidator.validateForgotpassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.userNameRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateChangePasswordSuccess() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.NEW_PASSWORD, "password1");
    requestObj.put(JsonKey.PASSWORD, "password");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateChangePassword(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void testValidateChangePasswordFailureWithEmptyNewPassword() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.NEW_PASSWORD, "");
    requestObj.put(JsonKey.PASSWORD, "password");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateChangePassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.newPasswordEmpty.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateChangePasswordFailureWithoutNewPassword() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PASSWORD, "password");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateChangePassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.newPasswordRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateChangePasswordFailureWithOldNewSame() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.NEW_PASSWORD, "password");
    requestObj.put(JsonKey.PASSWORD, "password");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateChangePassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.samePasswordError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateChangePasswordFailureWithPasswordMissing() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.NEW_PASSWORD, "password");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateChangePassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.passwordRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserSuccess() {
	boolean response = false;
    Request request = initailizeRequest();
    Map<String,Object> requestObj = request.getRequest();
    List<String> roles = new ArrayList<String>();
    roles.add("PUBLIC");
    roles.add("CONTENT-CREATOR");
    requestObj.put(JsonKey.ROLE, roles);
    List<String> language = new ArrayList<>();
    language.add("English");
    requestObj.put(JsonKey.LANGUAGE, language);
    List<Map<String, Object>> addressList = new ArrayList<>();
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.ADDRESS_LINE1, "test");
    map.put(JsonKey.CITY, "Bangalore");
    map.put(JsonKey.COUNTRY, "India");
    map.put(JsonKey.ADD_TYPE, "current");
    addressList.add(map);
    requestObj.put(JsonKey.ADDRESS, addressList);

    List<Map<String, Object>> educationList = new ArrayList<>();
    Map<String, Object> map1 = new HashMap<>();
    map1.put(JsonKey.COURSE_NAME, "M.C.A");
    map1.put(JsonKey.DEGREE, "Master");
    map1.put(JsonKey.NAME, "CUSAT");
    educationList.add(map1);
    requestObj.put(JsonKey.EDUCATION, educationList);

    List<Map<String, Object>> jobProfileList = new ArrayList<>();
    map1 = new HashMap<>();
    map1.put(JsonKey.JOB_NAME, "SE");
    map1.put(JsonKey.ORGANISATION_NAME, "Tarento");
    jobProfileList.add(map1);
    requestObj.put(JsonKey.JOB_PROFILE, jobProfileList);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateCreateUser(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void testValidateCreateUserFailureWithWrongAddType() {
	Request request = initailizeRequest();
	Map<String, Object> requestObj = request.getRequest();
    List<String> roles = new ArrayList<String>();
    roles.add("PUBLIC");
    roles.add("CONTENT-CREATOR");
    requestObj.put(JsonKey.ROLE, roles);
    List<String> language = new ArrayList<>();
    language.add("English");
    requestObj.put(JsonKey.LANGUAGE, language);
    List<Map<String, Object>> addressList = new ArrayList<>();
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.ADDRESS_LINE1, "test");
    map.put(JsonKey.CITY, "Bangalore");
    map.put(JsonKey.COUNTRY, "India");
    map.put(JsonKey.ADD_TYPE, "lmlkmkl");
    addressList.add(map);
    requestObj.put(JsonKey.ADDRESS, addressList);

    List<Map<String, Object>> educationList = new ArrayList<>();
    Map<String, Object> map1 = new HashMap<>();
    map1.put(JsonKey.COURSE_NAME, "M.C.A");
    map1.put(JsonKey.DEGREE, "Master");
    map1.put(JsonKey.NAME, "CUSAT");
    educationList.add(map1);
    requestObj.put(JsonKey.EDUCATION, educationList);

    List<Map<String, Object>> jobProfileList = new ArrayList<>();
    map1 = new HashMap<>();
    map1.put(JsonKey.JOB_NAME, "SE");
    map1.put(JsonKey.ORGANISATION_NAME, "Tarento");
    jobProfileList.add(map1);
    requestObj.put(JsonKey.JOB_PROFILE, jobProfileList);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.addressTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithEmptyAddType() {
    Request request = initailizeRequest();
    Map<String, Object> requestObj = request.getRequest();
    List<String> roles = new ArrayList<String>();
    roles.add("PUBLIC");
    roles.add("CONTENT-CREATOR");
    requestObj.put(JsonKey.ROLE, roles);
    List<String> language = new ArrayList<>();
    language.add("English");
    requestObj.put(JsonKey.LANGUAGE, language);
    List<Map<String, Object>> addressList = new ArrayList<>();
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.ADDRESS_LINE1, "test");
    map.put(JsonKey.CITY, "Bangalore");
    map.put(JsonKey.COUNTRY, "India");
    map.put(JsonKey.ADD_TYPE, "");
    addressList.add(map);
    requestObj.put(JsonKey.ADDRESS, addressList);

    List<Map<String, Object>> educationList = new ArrayList<>();
    Map<String, Object> map1 = new HashMap<>();
    map1.put(JsonKey.COURSE_NAME, "M.C.A");
    map1.put(JsonKey.DEGREE, "Master");
    map1.put(JsonKey.NAME, "CUSAT");
    educationList.add(map1);
    requestObj.put(JsonKey.EDUCATION, educationList);

    List<Map<String, Object>> jobProfileList = new ArrayList<>();
    map1 = new HashMap<>();
    map1.put(JsonKey.JOB_NAME, "SE");
    map1.put(JsonKey.ORGANISATION_NAME, "Tarento");
    jobProfileList.add(map1);
    requestObj.put(JsonKey.JOB_PROFILE, jobProfileList);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testPhoneValidationSuccess() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.PHONE_VERIFIED, true);
    requestObj.put(JsonKey.EMAIL_VERIFIED, true);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.phoneValidation(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void testPhoneValidationFailureWithInvalidPhone() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "+9321234123");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.PHONE_VERIFIED, true);
    requestObj.put(JsonKey.EMAIL_VERIFIED, true);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.phoneValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.invalidPhoneNumber.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testPhoneValidationFailureWithInvalidCountryCode() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "+9321234123");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91968");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.PHONE_VERIFIED, true);
    requestObj.put(JsonKey.EMAIL_VERIFIED, true);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.phoneValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.invalidCountryCode.getErrorCode(), e.getCode());
    }
  }

  
  @Test
  public void testPhoneValidationFailureWithEmptyPhoneVerified() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.PHONE_VERIFIED, "");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.phoneValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testPhoneValidationFailureWithPhoneVerifiedFalse() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.PHONE_VERIFIED, false);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.phoneValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testPhoneValidationFailureWithPhoneVerifiedNull() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.PHONE_VERIFIED, null);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.phoneValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testUpdateUserSuccess() {
    Request request = initailizeRequest();
    Map<String, Object> requestObj = request.getRequest();
    requestObj.remove(JsonKey.USERNAME);
    requestObj.put(JsonKey.USER_ID, "userId");
    
    List<String> roles = new ArrayList<String>();
    roles.add("PUBLIC");
    roles.add("CONTENT-CREATOR");
    requestObj.put(JsonKey.ROLE, roles);
    List<String> language = new ArrayList<>();
    language.add("English");
    requestObj.put(JsonKey.LANGUAGE, language);
    List<Map<String, Object>> addressList = new ArrayList<>();
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.ADDRESS_LINE1, "test");
    map.put(JsonKey.CITY, "Bangalore");
    map.put(JsonKey.COUNTRY, "India");
    map.put(JsonKey.ADD_TYPE, "current");
    addressList.add(map);
    requestObj.put(JsonKey.ADDRESS, addressList);

    List<Map<String, Object>> educationList = new ArrayList<>();
    Map<String, Object> map1 = new HashMap<>();
    map1.put(JsonKey.COURSE_NAME, "M.C.A");
    map1.put(JsonKey.DEGREE, "Master");
    map1.put(JsonKey.NAME, "CUSAT");
    educationList.add(map1);
    requestObj.put(JsonKey.EDUCATION, educationList);

    List<Map<String, Object>> jobProfileList = new ArrayList<>();
    map1 = new HashMap<>();
    map1.put(JsonKey.JOB_NAME, "SE");
    map1.put(JsonKey.ORGANISATION_NAME, "Tarento");
    jobProfileList.add(map1);
    requestObj.put(JsonKey.JOB_PROFILE, jobProfileList);
    boolean response = false;
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateUpdateUser(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void testValidateUploadUserSuccessWithOrgId() {
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORGANISATION_ID, "ORG-1233");
    requestObj.put(JsonKey.EXTERNAL_ID_PROVIDER, "EXT_ID_PROVIDER");
    requestObj.put(JsonKey.FILE, "EXT_ID_PROVIDER");
    
    try {
      RequestValidator.validateUploadUser(requestObj);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void testValidateUploadUserSuccessWithExternalId() {
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PROVIDER, "ORG-provider");
    requestObj.put(JsonKey.EXTERNAL_ID, "ORG-1233");
    requestObj.put(JsonKey.ORGANISATION_ID, "ORG-1233");
    requestObj.put(JsonKey.ORG_PROVIDER, "ORG-Provider");
    requestObj.put(JsonKey.FILE, "ORG-Provider");
    try {
      RequestValidator.validateUploadUser(requestObj);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void testValidateAssignRoleSuccess() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USER_ID, "ORG-provider");
    requestObj.put(JsonKey.EXTERNAL_ID, "EXT_ID");
    requestObj.put(JsonKey.ORGANISATION_ID, "ORG_ID");
    requestObj.put(JsonKey.ORG_PROVIDER, "ORG_PROVIDER");
    List<String> roles = new ArrayList<>();
    roles.add("PUBLIC");
    requestObj.put(JsonKey.ROLES, roles);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateAssignRole(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void testValidateUploadUserSuccessWithProviderAndExternalId() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PROVIDER, "ORG-provider");
    requestObj.put(JsonKey.EXTERNAL_ID, "ORG-1233");
    requestObj.put(JsonKey.USER_ID, "User1");
    List<String> roles = new ArrayList<>();
    roles.add("PUBLIC");
    requestObj.put(JsonKey.ROLES, roles);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateAssignRole(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  /*
   * @Test public void phoneAndEmailValidationForUpdateUser () { Request request =
   * new Request(); boolean response = false; Map<String, Object> requestObj = new
   * HashMap<>(); requestObj.put(JsonKey.PHONE, "9878888888");
   * requestObj.put(JsonKey.COUNTRY_CODE, "+91"); requestObj.put(JsonKey.PROVIDER,
   * "sunbird"); requestObj.put(JsonKey.EMAIL, "test123@test.com");
   * requestObj.put(JsonKey.PHONE_VERIFIED, true); request.setRequest(requestObj);
   * try { UserRequestValidator.phoneAndEmailValidationForUpdateUser(request);
   * response = true; } catch (ProjectCommonException e) { Assert.assertNull(e); }
   * assertEquals(true, response); }
   */

  @Test
  public void testValidateProfileVisibilitySuccess() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USER_ID, "9878888888");
    List<String> publicList = new ArrayList<>();
    publicList.add("Education");
    requestObj.put(JsonKey.PUBLIC, publicList);
    List<String> privateList = new ArrayList<>();
    privateList.add("Education");
    requestObj.put(JsonKey.PRIVATE, privateList);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateProfileVisibility(request);
    } catch (ProjectCommonException e) {
      Assert.assertNotNull(e);
    }
  }

  @Test
  public void testValidateProfileVisibilityFailureWithEmptyUserId() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USER_ID, "");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateProfileVisibility(request);
    } catch (ProjectCommonException e) {
      Assert.assertNotNull(e);
    }
  }

  @Test
  public void testValidateProfileVisibilityFailureWithInvalidPrivateField() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USER_ID, "123");
    requestObj.put(JsonKey.PRIVATE, "");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateProfileVisibility(request);
    } catch (ProjectCommonException e) {
      Assert.assertNotNull(e);
    }
  }

  @Test
  public void testValidateProfileVisibilityFailureWithInvalidPublicField() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USER_ID, "123");
    requestObj.put(JsonKey.PUBLIC, "");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateProfileVisibility(request);
    } catch (ProjectCommonException e) {
      Assert.assertNotNull(e);
    }
  }

  @Test
  public void testValidateWebPagesFailureWithEmptyWebPages() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.WEB_PAGES, new ArrayList<>());
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateWebPages(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.invalidWebPageData.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateWebPagesFailureWithNullWebPages() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.WEB_PAGES, null);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateWebPages(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.invalidWebPageData.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserValidationFailureWithEmptyFirstName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.FIRST_NAME, "");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.firstNameRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserValidationFailureWithInvalidDOB() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.DOB, "20-10-15");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dateFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserValidationFailureWithoutEmailAndPhone() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.DOB, "2018-10-15");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.emailorPhoneRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserValidationFailureWithInvalidEmail() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.DOB, "2018-10-15");
    requestObj.put(JsonKey.EMAIL, "asd@as");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.emailFormatError.getErrorCode(), e.getCode());
    }
  }

 

  @Test
  public void testCreateUserValidationFailureWithInvalidRoles() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.ROLES, "");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserValidationFailureWithInvalidLanguage() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.EMAIL, "test123@test.com");
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.LANGUAGE, "");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserValidationFailureWithInvalidAddress() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.PHONE_VERIFIED, true);
    requestObj.put(JsonKey.EMAIL, "test123@test.com");
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.ADDRESS, "");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserValidationFailureWithInvalidEducation() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.PHONE_VERIFIED, true);
    requestObj.put(JsonKey.EMAIL, "test123@test.com");
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.EDUCATION, "");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserValidationFailureWithInvalidAddressType() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.PHONE_VERIFIED, true);
    requestObj.put(JsonKey.EMAIL, "test123@test.com");
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    List<Map<String, Object>> addressList = new ArrayList<>();
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.ADDRESS_LINE1, "test");
    map.put(JsonKey.CITY, "Bangalore");
    map.put(JsonKey.COUNTRY, "India");
    map.put(JsonKey.ADD_TYPE, "localr");
    addressList.add(map);
    requestObj.put(JsonKey.ADDRESS, addressList);
    request.setRequest(requestObj);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserValidationFailureWithInvalidCountryCode() {
	  Request request = new Request();
	    Map<String, Object> requestObj = new HashMap<>();
	    requestObj.put(JsonKey.PHONE, "9321234123");
	    requestObj.put(JsonKey.PHONE_VERIFIED, true);
	    requestObj.put(JsonKey.EMAIL, "test123@test.com");
	    requestObj.put(JsonKey.USERNAME, "test123");
	    requestObj.put(JsonKey.FIRST_NAME, "test123");
	    request.setRequest(requestObj);
    request.getRequest().put(JsonKey.COUNTRY_CODE, "+as");
    
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.invalidCountryCode.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserValidationFailureWithEmptyEmailAndPhone() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.PHONE_VERIFIED, true);
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.EMAIL, "");
    requestObj.put(JsonKey.PHONE, "");
    request.setRequest(requestObj);
    
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.emailorPhoneRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithInvalidEmail() {
    Request request = initailizeRequest();
    request.getRequest().put(JsonKey.EMAIL, "am@ds@cmo");

    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.emailFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithoutPhoneVerified() {
    Request request = initailizeRequest();
    request.getRequest().put(JsonKey.PHONE, "7894561230");
    
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserSuccess() {
    Request request = initailizeRequest();
    request.getRequest().put(JsonKey.PHONE, "7894561230");
    request.getRequest().put(JsonKey.PHONE_VERIFIED, "true");
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithPhoneVerifiedFalse() {
    Request request = initailizeRequest();
    request.getRequest().put(JsonKey.PHONE, "7894561230");
    request.getRequest().put(JsonKey.PHONE_VERIFIED, false);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithEmptyEducationName() {
    Request request = initailizeRequest();
    request.getRequest().put(JsonKey.PHONE_VERIFIED, true);
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.NAME, "");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);

    request.getRequest().put(JsonKey.EDUCATION, list);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.educationNameError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithEmptyEducationDegree() {
	Request request = initailizeRequest();
	request.getRequest().put(JsonKey.PHONE_VERIFIED, true);    
	Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.NAME, "name");
    map.put(JsonKey.DEGREE, "");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);

    request.getRequest().put(JsonKey.EDUCATION, list);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.educationDegreeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithEmptyEducationAddress() {
	Request request = initailizeRequest();
	request.getRequest().put(JsonKey.PHONE_VERIFIED, true);
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.NAME, "name");
    map.put(JsonKey.DEGREE, "degree");
    Map<String, Object> address = new HashMap<>();
    address.put(JsonKey.ADDRESS_LINE1, "");
    map.put(JsonKey.ADDRESS, address);
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);
    request.getRequest().put(JsonKey.EDUCATION, list);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithEmptyEducationCity() {
	Request request = initailizeRequest();
	request.getRequest().put(JsonKey.PHONE_VERIFIED, true);
	
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.NAME, "name");
    map.put(JsonKey.DEGREE, "degree");
    Map<String, Object> address = new HashMap<>();
    address.put(JsonKey.ADDRESS_LINE1, "line1");
    address.put(JsonKey.CITY, "");
    map.put(JsonKey.ADDRESS, address);
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);
    request.getRequest().put(JsonKey.EDUCATION, list);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithEmptyJobProfile() {
	Request request = initailizeRequest();
	request.getRequest().put(JsonKey.PHONE_VERIFIED, true);
    request.getRequest().put(JsonKey.JOB_PROFILE, "");
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithEmptyJobName() {
	Request request = initailizeRequest();
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.JOB_NAME, "");
    map.put(JsonKey.ORG_NAME, "degree");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);
    request.getRequest().put(JsonKey.JOB_PROFILE, list);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.jobNameError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithInvalidJobProfileJoiningDate() {
	Request request = initailizeRequest();
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.JOB_NAME, "kijklo");
    map.put(JsonKey.ORG_NAME, "degree");
    map.put(JsonKey.JOINING_DATE, "20-15-18");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);
    request.getRequest().put(JsonKey.JOB_PROFILE, list);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dateFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithInvalidJobProfileEndDate() {
	Request request = initailizeRequest();
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.JOB_NAME, "kijklo");
    map.put(JsonKey.ORG_NAME, "degree");
    map.put(JsonKey.END_DATE, "20-15-18");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);
    request.getRequest().put(JsonKey.JOB_PROFILE, list);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dateFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithEmptyJobProfileOrgName() {
	Request request = initailizeRequest();
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.JOB_NAME, "kijklo");
    map.put(JsonKey.ORG_NAME, "");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);
    request.getRequest().put(JsonKey.JOB_PROFILE, list);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.organisationNameError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithEmptyJobProfileCity() {
    Request request = initailizeRequest();
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.JOB_NAME, "jabName");
    map.put(JsonKey.ORG_NAME, "orgName");
    Map<String, Object> address = new HashMap<>();
    address.put(JsonKey.ADDRESS_LINE1, "line1");
    address.put(JsonKey.CITY, "");
    map.put(JsonKey.ADDRESS, address);
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);
    request.getRequest().put(JsonKey.JOB_PROFILE, list);
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserFailureWithInvalidPhoneFormat() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.COUNTRY_CODE, "+001");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.phoneNoFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateGerUserCountFailureWithInvalidLocationIds() {
    Request request = new Request();
    request.getRequest().put(JsonKey.LOCATION_IDS, "");

    try {
      RequestValidator.validateGetUserCount(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateGerUserCountFailureWithEmptyLocationIds() {
    Request request = new Request();
    List<String> list = new ArrayList<>();
    list.add("");
    request.getRequest().put(JsonKey.LOCATION_IDS, list);

    try {
      RequestValidator.validateGetUserCount(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.locationIdRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateGerUserCountFailureWithInvalidUserLstReq() {
    Request request = new Request();
    List<String> list = new ArrayList<>();
    list.add("4645");
    request.getRequest().put(JsonKey.LOCATION_IDS, list);
    request.getRequest().put(JsonKey.USER_LIST_REQ, null);

    try {
      RequestValidator.validateGetUserCount(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateGerUserCountFailureWithUserLstReqTrue() {
    Request request = new Request();
    List<String> list = new ArrayList<>();
    list.add("4645");
    request.getRequest().put(JsonKey.LOCATION_IDS, list);
    request.getRequest().put(JsonKey.USER_LIST_REQ, true);

    try {
      RequestValidator.validateGetUserCount(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.functionalityMissing.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateGerUserCountFailureWithEmptyEstCntReq() {
    Request request = new Request();
    List<String> list = new ArrayList<>();
    list.add("4645");
    request.getRequest().put(JsonKey.LOCATION_IDS, list);
    request.getRequest().put(JsonKey.ESTIMATED_COUNT_REQ, "");

    try {
      RequestValidator.validateGetUserCount(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateGerUserCountFailureWithEstCntReqTrue() {
    Request request = new Request();
    List<String> list = new ArrayList<>();
    list.add("4645");
    request.getRequest().put(JsonKey.LOCATION_IDS, list);
    request.getRequest().put(JsonKey.ESTIMATED_COUNT_REQ, true);

    try {
      RequestValidator.validateGetUserCount(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.functionalityMissing.getErrorCode(), e.getCode());
    }
  }
  
  
  private Request initailizeRequest() {
	  Request request = new Request();
	    Map<String, Object> requestObj = new HashMap<>();
	    requestObj.put(JsonKey.USERNAME, "test123");
	    requestObj.put(JsonKey.PHONE, "9321234123");
	    requestObj.put(JsonKey.PHONE_VERIFIED, true);
	    requestObj.put(JsonKey.FIRST_NAME, "test123");
	    request.setRequest(requestObj);
	    return request;
  }
}
