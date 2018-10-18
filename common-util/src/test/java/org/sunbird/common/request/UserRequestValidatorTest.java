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

public class UserRequestValidatorTest {

  private static final UserRequestValidator userRequestValidator = new UserRequestValidator();
  
  @Test
  public void testValidateForgotPasswordSuccess() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "manzarul07");
    request.setRequest(requestObj);
    try {
      userRequestValidator.validateForgotPassword(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void testValidateForgotPasswordFailureWithEmptyUserName() {
    try {
      Request request = new Request();
      Map<String, Object> requestObj = new HashMap<>();
      requestObj.put(JsonKey.USERNAME, "");
      request.setRequest(requestObj);
      userRequestValidator.validateForgotPassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.userNameRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateForgotPasswordFailureWithoutUserName() {
    try {
      Request request = new Request();
      Map<String, Object> requestObj = new HashMap<>();
      request.setRequest(requestObj);
      userRequestValidator.validateForgotPassword(request);
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
      userRequestValidator.validateChangePassword(request);
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
      userRequestValidator.validateChangePassword(request);
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
      userRequestValidator.validateChangePassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.newPasswordRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateChangePasswordFailureWithSameOldNewPassword() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.NEW_PASSWORD, "password");
    requestObj.put(JsonKey.PASSWORD, "password");
    request.setRequest(requestObj);
    try {
      userRequestValidator.validateChangePassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.samePasswordError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateChangePasswordFailureWithoutOldPassword() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.NEW_PASSWORD, "password");
    request.setRequest(requestObj);
    try {
      userRequestValidator.validateChangePassword(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.passwordRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserSuccessWithAllFields() {
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
      userRequestValidator.validateCreateUserRequest(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  @Ignore
  public void testValidateCreateUserFailureWithoutAddressType() {
    Request request = new Request();
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
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.addressTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserFailureWithEmptyAddressType() {
    Request request = new Request();
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
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidatePhoneAndEmailSuccess() {
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
      userRequestValidator.phoneValidation(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void testValidatePhoneFailureWithInvalidPhone() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "+9321234123");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.PHONE_VERIFIED, true);
    requestObj.put(JsonKey.EMAIL_VERIFIED, true);
    request.setRequest(requestObj);
    try {
      userRequestValidator.phoneValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.invalidPhoneNumber.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidatePhoneFailureWithInvalidCountryCode() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "+9321234123");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91968");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.PHONE_VERIFIED, true);
    requestObj.put(JsonKey.EMAIL_VERIFIED, true);
    request.setRequest(requestObj);
    try {
      userRequestValidator.phoneValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.invalidCountryCode.getErrorCode(), e.getCode());
    }
  }

  

  @Test
  public void testValidatePhoneFailureWithEmptyPhoneVerified() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.PHONE_VERIFIED, "");
    request.setRequest(requestObj);
    try {
      userRequestValidator.phoneValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidatePhoneFailureWithPhoneVerifiedFalse() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.PHONE_VERIFIED, false);
    request.setRequest(requestObj);
    try {
      userRequestValidator.phoneValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidatePhoneFailureWithPhoneVerifiedNull() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.COUNTRY_CODE, "+91");
    requestObj.put(JsonKey.PROVIDER, "sunbird");
    requestObj.put(JsonKey.PHONE_VERIFIED, null);
    request.setRequest(requestObj);
    try {
      userRequestValidator.phoneValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateUpdateUserSuccess() {
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
      userRequestValidator.validateUpdateUserRequest(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  @Ignore
  public void testValidateUpdateUserSuccessWithOrgId() {
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORGANISATION_ID, "ORG-1233");
    try {
      RequestValidator.validateUploadUser(requestObj);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  @Ignore
  public void testValidateUpdateUserSuccessWithProviderAndExternalId() {
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PROVIDER, "ORG-provider");
    requestObj.put(JsonKey.EXTERNAL_ID, "ORG-1233");
    try {
      RequestValidator.validateUploadUser(requestObj);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  @Ignore
  public void testValidateUpdateUserSuccessWithUserId() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USER_ID, "ORG-provider");
    List<String> roles = new ArrayList<>();
    roles.add("PUBLIC");
    requestObj.put(JsonKey.ROLES, roles);
    request.setRequest(requestObj);
    try {
      userRequestValidator.validateAssignRole(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  @Ignore
  public void testValidateUpdateUserSuccessWithExternalIdAndProvider() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PROVIDER, "ORG-provider");
    requestObj.put(JsonKey.EXTERNAL_ID, "ORG-1233");
    List<String> roles = new ArrayList<>();
    roles.add("PUBLIC");
    requestObj.put(JsonKey.ROLES, roles);
    request.setRequest(requestObj);
    try {
      userRequestValidator.validateAssignRole(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void testValidateWebPagesFailureWithEmptyList() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.WEB_PAGES, new ArrayList<>());
    request.setRequest(requestObj);
    try {
      userRequestValidator.validateWebPages(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.invalidWebPageData.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateWebPagesFailureWithNull() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.WEB_PAGES, null);
    request.setRequest(requestObj);
    try {
      userRequestValidator.validateWebPages(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.invalidWebPageData.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testCreateUserBasicValidationFailureWithEmptyUserName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "");
    request.setRequest(requestObj);
    try {
      userRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.userNameRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserBasicValidationFailureWithInvalidDobFormat() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.DOB, "20-10-15");
    request.setRequest(requestObj);
    try {
      userRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dateFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserBasicValidationFailureWithoutEmailAndPhone() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.DOB, "2018-10-15");
    request.setRequest(requestObj);
    try {
      userRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.emailorPhoneRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserBasicValidationFailureWithInvalidEmailFormat() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.DOB, "2018-10-15");
    requestObj.put(JsonKey.EMAIL, "asd@as");
    request.setRequest(requestObj);
    try {
      userRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.emailFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserBasicValidationFailureWithEmptyFirstName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "");
    request.setRequest(requestObj);
    try {
      userRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.firstNameRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testCreateUserBasicValidationFailureWithInvalidRoleDataType() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.ROLES, "");
    request.setRequest(requestObj);
    try {
      userRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserRequestnFailureWithInvalidLangaugeDataType() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.EMAIL, "test123@test.com");
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.LANGUAGE, "");
    request.setRequest(requestObj);
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestnFailureWithInvalidAddressDataType() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.EMAIL, "test123@test.com");
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.ADDRESS, "");
    request.setRequest(requestObj);
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestnFailureWithInvalidEducationDataType() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
    requestObj.put(JsonKey.EMAIL, "test123@test.com");
    requestObj.put(JsonKey.USERNAME, "test123");
    requestObj.put(JsonKey.FIRST_NAME, "test123");
    requestObj.put(JsonKey.EDUCATION, "");
    request.setRequest(requestObj);
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestnFailureWithInvalidAddressType() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
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
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressTypeError.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void testValidateCreateUserRequestFailureWithInvalidCountryCode() {
    Request request = new Request();
    request.getRequest().put(JsonKey.COUNTRY_CODE, "+as");
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.invalidCountryCode.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void testValidateCreateUserRequestFailureWithoutEmailAndPhone() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "");
    request.getRequest().put(JsonKey.PHONE, "");
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.emailorPhoneRequired.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void testValidateCreateUserRequestFailureWithInvalidEmailFormat() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "am@ds@cmo");

    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.emailFormatError.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void testValidateCreateUserRequestFailureWithoutPhoneVerified() {
    Request request = new Request();
    request.getRequest().put(JsonKey.PROVIDER, "BLR");
    request.getRequest().put(JsonKey.PHONE, "7894561230");
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void testValidateCreateUserRequestFailureWithPhoneVerifiedEmpty() {
    Request request = new Request();
    request.getRequest().put(JsonKey.PROVIDER, "BLR");
    request.getRequest().put(JsonKey.PHONE, "7894561230");
    request.getRequest().put(JsonKey.PHONE_VERIFIED, "");
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void testValidateCreateUserRequestFailureWithPhoneVerifiedFalse() {
    Request request = new Request();
    request.getRequest().put(JsonKey.PROVIDER, "BLR");
    request.getRequest().put(JsonKey.PHONE, "7894561230");
    request.getRequest().put(JsonKey.PHONE_VERIFIED, false);
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestFailureWithoutEducationName() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.NAME, "");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);

    request.getRequest().put(JsonKey.EDUCATION, list);
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.educationNameError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestFailureWithoutEducationDegree() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.NAME, "name");
    map.put(JsonKey.DEGREE, "");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);

    request.getRequest().put(JsonKey.EDUCATION, list);
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.educationDegreeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestFailureWithoutAddressLine1() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
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
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestFailureWithoutCity() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
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
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestFailureWithInvalidJobProfileType() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
    request.getRequest().put(JsonKey.JOB_PROFILE, "");
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestFailureWithoutJobName() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.JOB_NAME, "");
    map.put(JsonKey.ORG_NAME, "degree");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);
    request.getRequest().put(JsonKey.JOB_PROFILE, list);
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.jobNameError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestFailureWithInvalidJoinDate() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.JOB_NAME, "kijklo");
    map.put(JsonKey.ORG_NAME, "degree");
    map.put(JsonKey.JOINING_DATE, "20-15-18");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);
    request.getRequest().put(JsonKey.JOB_PROFILE, list);
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dateFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestFailureWithInvalidEndDate() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.JOB_NAME, "kijklo");
    map.put(JsonKey.ORG_NAME, "degree");
    map.put(JsonKey.END_DATE, "20-15-18");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);
    request.getRequest().put(JsonKey.JOB_PROFILE, list);
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dateFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestFailureWithEmptyOrgName() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.JOB_NAME, "kijklo");
    map.put(JsonKey.ORG_NAME, "");
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(map);
    request.getRequest().put(JsonKey.JOB_PROFILE, list);
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.organisationNameError.getErrorCode(), e.getCode());
    }
  }

  @Test
  @Ignore
  public void testValidateCreateUserRequestFailureWithEmptyCity() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
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
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidateCreateUserRequestFailureWithWrongPhoneFormat() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.COUNTRY_CODE, "+001");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
    try {
      userRequestValidator.validateCreateUserRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.phoneNoFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValdateGetUserCountFailureWithInvalidLocationIdsType() {
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
  public void testValdateGetUserCountFailureWithEmptyLocationIdList() {
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
  public void testValdateGetUserCountFailureWithNullLocationIdList() {
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
  public void testValdateGetUserCountFailureWithEmptyFunctionalityMissing() {
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
  public void testValdateGetUserCountFailureWithInvalidEstCntReqType() {
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
  public void testValidateVerifyUserSuccess() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.LOGIN_ID, "username@provider");
    request.setRequest(requestObj);
    boolean response = false;
    try {
      new UserRequestValidator().validateVerifyUser(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    Assert.assertTrue(response);
  }

  @Test
  public void testValidateVerifyUserFailureWithEmptyLogin() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.LOGIN_ID, "");
    request.setRequest(requestObj);
    boolean response = false;
    try {
      new UserRequestValidator().validateVerifyUser(request);
      response = true;
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.loginIdRequired.getErrorCode(), e.getCode());
    }
    Assert.assertFalse(response);
  }
}
