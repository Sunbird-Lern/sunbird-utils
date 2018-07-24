/** */
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

/** @author Manzarul */
public class UserRequestValidatorTest {

  @Test
  public void validateForgotPasswordSuccess() {
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
  public void validateForgotPasswordUserNameEmpty() {
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
  public void validateForgotPasswordUserNameKeyMissing() {
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
  public void validateChangePasswordSuccess() {
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
  public void validateChangePasswordWithEmptyNewPass() {
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
  public void validateChangePasswordWithMissingNewPass() {
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
  public void validateChangePasswordWithSameOldAndNewPass() {
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
  public void validateChangePasswordWithPassWordMissing() {
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
  public void validateCreateUserSuccessWithAllFields() {
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
      UserRequestValidator.validateCreateUser(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void validateCreateUser1() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.addressTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void validateCreateUser2() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void validatePhoneAndEmailUpdateSuccess() {
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
  public void validatePhone() {
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
  public void validatePhoneAndEmailUpdate1() {
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
  public void validatePhoneAndEmailUpdate2() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
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
  public void validatePhoneAndEmailUpdate3() {
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
  public void validatePhoneAndEmailUpdate4() {
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
  public void validatePhoneAndEmailUpdate5() {
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
  public void validateUpdateUserSuccess() {
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
      UserRequestValidator.validateUpdateUser(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test
  public void validateUploadUserWithOrgId() {
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
  public void validateUploadUserWithProviderAndExternalId() {
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
  public void validateAssignRoleWithUserId() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USER_ID, "ORG-provider");
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
  public void validateAssignRoleWithProviderAndExternalId() {
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
  public void profileVisibilityValidatorTest() {
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
  public void profileVisibilityInvalidUserTest() {
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
  public void profileVisibilityInvalidPrivateFieldsTest() {
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
  public void profileVisibilityInvalidPublicFieldsTest() {
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
  public void validateWebPagesTest() {
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
  public void validateWebPagesTestWhenNull() {
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
  public void doUserBasicValidationUserNameTest() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "");
    request.setRequest(requestObj);
    try {
      UserRequestValidator.createUserBasicValidation(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.userNameRequired.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void doUserBasicValidationTest() {
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
  public void doUserBasicValidationTest1() {
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
  public void doUserBasicValidationTest2() {
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
  public void doUserBasicValidationFirstNameTest() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.USERNAME, "test123");
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
  public void doUserBasicValidationRolesTest() {
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
  public void doUserBasicValidationLanguageTest() {
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
  public void createUserAddressTest() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
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
  public void createUserEducationTest() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PHONE, "9321234123");
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
  public void createUserAddressTypeTest() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressTypeError.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void phoneAndEmailValidationForCreateUserTest() {
    Request request = new Request();
    request.getRequest().put(JsonKey.COUNTRY_CODE, "+as");
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.invalidCountryCode.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void phoneAndEmailValidationForCreateUserTest2() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "");
    request.getRequest().put(JsonKey.PHONE, "");
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.emailorPhoneRequired.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void phoneAndEmailValidationForCreateUserTest3() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "am@ds@cmo");

    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.emailFormatError.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void phoneAndEmailValidationForCreateUserTest4() {
    Request request = new Request();
    request.getRequest().put(JsonKey.PROVIDER, "BLR");
    request.getRequest().put(JsonKey.PHONE, "7894561230");
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void phoneAndEmailValidationForCreateUserTest5() {
    Request request = new Request();
    request.getRequest().put(JsonKey.PROVIDER, "BLR");
    request.getRequest().put(JsonKey.PHONE, "7894561230");
    request.getRequest().put(JsonKey.PHONE_VERIFIED, "true");
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.phoneVerifiedError.getErrorCode(), e.getCode());
    }
  }

  // @Test
  public void phoneAndEmailValidationForCreateUserTest6() {
    Request request = new Request();
    request.getRequest().put(JsonKey.PROVIDER, "BLR");
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
  public void educationValidationTest1() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.educationNameError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void educationValidationTest2() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.educationDegreeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void educationValidationAddressTest3() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void educationValidationAddressTest4() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void jobProfileValidationTest() {
    Request request = new Request();
    request.getRequest().put(JsonKey.EMAIL, "asd@asd.com");
    request.getRequest().put(JsonKey.PHONE, "9874561230");
    request.getRequest().put(JsonKey.USERNAME, "98745");
    request.getRequest().put(JsonKey.FIRST_NAME, "98745");
    request.getRequest().put(JsonKey.JOB_PROFILE, "");
    try {
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void jobProfileValidationTest1() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.jobNameError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void jobProfileValidationTest3() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dateFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void jobProfileValidationTest4() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.dateFormatError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void jobProfileValidationTest5() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.organisationNameError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void jobProfileValidationTest2() {
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
      UserRequestValidator.validateCreateUser(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.addressError.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void validatePhoneNoTest() {
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
  public void validateGetUserCountTest() {
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
  public void validateGetUserCountTest1() {
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
  public void validateGetUserCountTest2() {
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
  public void validateGetUserCountTest3() {
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
  public void validateGetUserCountTest4() {
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
  public void validateGetUserCountTest5() {
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
}
