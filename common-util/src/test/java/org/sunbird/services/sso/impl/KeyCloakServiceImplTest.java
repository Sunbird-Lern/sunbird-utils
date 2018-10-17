package org.sunbird.services.sso.impl;

import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.BaseForHttpTest;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.KeyCloakConnectionProvider;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.services.sso.SSOManager;
import org.sunbird.services.sso.SSOServiceFactory;

/** @author arvind */
public class KeyCloakServiceImplTest extends BaseForHttpTest {

  private SSOManager keyCloakService = SSOServiceFactory.getInstance();

  private static Map<String, String> userId = new HashMap<>();
  private static final String userName = UUID.randomUUID().toString().replaceAll("-", "");
  private static Class t = null;

  private static final Map<String, Object> USER_SUCCESS = new HashMap<>();

  static {
    USER_SUCCESS.put(JsonKey.USERNAME, userName);
    USER_SUCCESS.put(JsonKey.PASSWORD, "password");
    USER_SUCCESS.put(JsonKey.FIRST_NAME, "A");
    USER_SUCCESS.put(JsonKey.LAST_NAME, "B");
    USER_SUCCESS.put(JsonKey.PHONE, "9870060000");
    USER_SUCCESS.put(JsonKey.EMAIL, userName.substring(0, 10));
  }

  private static final Map<String, Object> USER_SAME_EMAIL = new HashMap<>();

  static {
    USER_SAME_EMAIL.put(JsonKey.USERNAME, userName);
    USER_SAME_EMAIL.put(JsonKey.PASSWORD, "password");
    USER_SAME_EMAIL.put(JsonKey.FIRST_NAME, "A");
    USER_SAME_EMAIL.put(JsonKey.LAST_NAME, "B");
    USER_SAME_EMAIL.put(JsonKey.PHONE, "9870060000");
    USER_SAME_EMAIL.put(JsonKey.EMAIL, userName.substring(0, 10));
  }

  private static UsersResource usersRes = mock(UsersResource.class);

  @BeforeClass
  public static void init() {
    try {
      t = Class.forName("org.sunbird.services.sso.SSOServiceFactory");
    } catch (ClassNotFoundException e) {
    }
    Keycloak kcp = mock(Keycloak.class);
    RealmResource realmRes = mock(RealmResource.class);
    UserResource userRes = mock(UserResource.class);
    UserRepresentation userRep = mock(UserRepresentation.class);
    Response response = mock(Response.class);
    PowerMockito.mockStatic(KeyCloakConnectionProvider.class);
    try {

      doReturn(kcp).when(KeyCloakConnectionProvider.class, "getConnection");
      doReturn(realmRes).when(kcp).realm(Mockito.anyString());
      doReturn(usersRes).when(realmRes).users();
      doReturn(response)
          .doThrow(
              new ProjectCommonException(
                  ResponseCode.emailANDUserNameAlreadyExistError.getErrorCode(),
                  ResponseCode.emailANDUserNameAlreadyExistError.getErrorMessage(),
                  ResponseCode.CLIENT_ERROR.getResponseCode()))
          .doReturn(response)
          .when(usersRes)
          .create(Mockito.any(UserRepresentation.class));
      doReturn(201).when(response).getStatus();
      doReturn("userdata").when(response).getHeaderString(Mockito.eq("Location"));

      doReturn(userRes).when(usersRes).get(Mockito.anyString());
      doReturn(userRep).when(userRes).toRepresentation();
      doNothing().when(userRes).update(Mockito.any(UserRepresentation.class));

      doNothing().when(userRes).remove();

      Map<String, Object> map = new HashMap<>();
      map.put(JsonKey.LAST_LOGIN_TIME, Arrays.asList(String.valueOf(System.currentTimeMillis())));
      doReturn(map).when(userRep).getAttributes();
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(
          "Failed in initialization of mock rules, underlying error: " + e.getLocalizedMessage());
    }
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testInstanceCreationSucccess() {
    Exception exp = null;
    try {
      Constructor<SSOServiceFactory> constructor = t.getDeclaredConstructor();
      constructor.setAccessible(true);
      SSOServiceFactory application = constructor.newInstance();
      Assert.assertNotNull(application);
    } catch (Exception e) {
      exp = e;
    }
    Assert.assertNull(exp);
  }

  @Test
  public void testUserCreateSuccess() {

    Map<String, Object> request = USER_SUCCESS;
    userId = keyCloakService.createUser(request);
    Assert.assertNotNull(userId);
  }

  @Test(expected = ProjectCommonException.class)
  public void testUserCreateFailureWithSameEmail() {
    Map<String, Object> request = USER_SAME_EMAIL;
    userId = keyCloakService.createUser(request);
    Assert.assertNotNull(userId);
  }

  @Test
  public void testUserUpdateTestSuccesWithAllData() {
    Map<String, Object> request = new HashMap<String, Object>();
    request.put(JsonKey.USER_ID, userId.get(JsonKey.USER_ID));
    request.put(JsonKey.FIRST_NAME, userName);
    request.put(JsonKey.PHONE, "9870060000");
    request.put(JsonKey.EMAIL, userName.substring(0, 10));
    request.put(JsonKey.USERNAME, userName);
    request.put(JsonKey.PROVIDER, "ntp");
    String result = keyCloakService.updateUser(request);
    Assert.assertNotNull(result);
  }

  @Test
  public void testUpdateUserSuccessWithoutProvider() {
    Map<String, Object> request = new HashMap<String, Object>();
    request.put(JsonKey.USER_ID, userId.get(JsonKey.USER_ID));
    request.put(JsonKey.FIRST_NAME, userName);
    request.put(JsonKey.PHONE, "9870060000");
    request.put(JsonKey.COUNTRY_CODE, "+91");
    request.put(JsonKey.EMAIL, userName.substring(0, 10));
    request.put(JsonKey.USERNAME, userName);
    String result = keyCloakService.updateUser(request);
    Assert.assertNotNull(result);
  }

  @Test
  public void testUpdateUserSuccessWithoutProviderAndCountryCode() {
    Map<String, Object> request = new HashMap<String, Object>();
    request.put(JsonKey.USER_ID, userId.get(JsonKey.USER_ID));
    request.put(JsonKey.FIRST_NAME, userName);
    request.put(JsonKey.PHONE, "9870060000");
    request.put(JsonKey.EMAIL, userName.substring(0, 10));
    request.put(JsonKey.USERNAME, userName);
    String result = keyCloakService.updateUser(request);
    Assert.assertNotNull(result);
  }

  @Test
  public void testUpdateUserSuccessWithoutAnyField() {

    Map<String, Object> request = new HashMap<String, Object>();
    request.put(JsonKey.USER_ID, userId.get(JsonKey.USER_ID));
    String result = keyCloakService.updateUser(request);
    Assert.assertNotNull(result);
  }

  @Test
  public void testDeactivateUserSuccess() {

    Map<String, Object> request = new HashMap<String, Object>();
    request.put(JsonKey.USER_ID, "123");
    request.put(JsonKey.FIRST_NAME, userName);
    String result = keyCloakService.deactivateUser(request);
    Assert.assertNotNull(result);
  }

  @Test
  public void testRemoveUserSuccess() {

    Map<String, Object> request = new HashMap<String, Object>();
    request.put(JsonKey.USER_ID, "123");
    String result = keyCloakService.removeUser(request);
    Assert.assertNotNull(result);
  }

  @Test(expected = ProjectCommonException.class)
  public void testVerifTokenSuccess() {
    keyCloakService.verifyToken(
        "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI5emhhVnZDbl81OEtheHpldHBzYXNZQ2lEallkemJIX3U2LV93SDk4SEc0In0.eyJqdGkiOiI5ZmQzNzgzYy01YjZmLTQ3OWQtYmMzYy0yZWEzOGUzZmRmYzgiLCJleHAiOjE1MDUxMTQyNDYsIm5iZiI6MCwiaWF0IjoxNTA1MTEzNjQ2LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoic2VjdXJpdHktYWRtaW4tY29uc29sZSIsInN1YiI6ImIzYTZkMTY4LWJjZmQtNDE2MS1hYzVmLTljZjYyODIyNzlmMyIsInR5cCI6IkJlYXJlciIsImF6cCI6InNlY3VyaXR5LWFkbWluLWNvbnNvbGUiLCJub25jZSI6ImMxOGVlMDM2LTAyMWItNGVlZC04NWVhLTc0MjMyYzg2ZmI4ZSIsImF1dGhfdGltZSI6MTUwNTExMzY0Niwic2Vzc2lvbl9zdGF0ZSI6ImRiZTU2NDlmLTY4MDktNDA3NS05Njk5LTVhYjIyNWMwZTkyMiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOltdLCJyZXNvdXJjZV9hY2Nlc3MiOnt9LCJuYW1lIjoiTWFuemFydWwgaGFxdWUiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0MTIzNDU2NyIsImdpdmVuX25hbWUiOiJNYW56YXJ1bCBoYXF1ZSIsImVtYWlsIjoidGVzdDEyM0B0LmNvbSJ9.Xdjqe16MSkiR94g-Uj_pVZ2L3gnIdKpkJ6aB82W_w_c3yEmx1mXYBdkxe4zMz3ks4OX_PWwSFEbJECHcnujUwF6Ula0xtXTfuESB9hFyiWHtVAhuh5UlCCwPnsihv5EqK6u-Qzo0aa6qZOiQK3Zo7FLpnPUDxn4yHyo3mRZUiWf76KTl8PhSMoXoWxcR2vGW0b-cPixILTZPV0xXUZoozCui70QnvTgOJDWqr7y80EWDkS4Ptn-QM3q2nJlw63mZreOG3XTdraOlcKIP5vFK992dyyHlYGqWVzigortS9Ah4cprFVuLlX8mu1cQvqHBtW-0Dq_JlcTMaztEnqvJ6XA");
  }

  @Test
  public void testAddLoginTimeSuccess() {
    boolean response = keyCloakService.addUserLoginTime(userId.get(JsonKey.USER_ID));
    Assert.assertEquals(true, response);
  }

  @Test
  public void testVerifyLastLoginSuccess() {
    String lastLoginTime = keyCloakService.getLastLoginTime(userId.get(JsonKey.USER_ID));
    Assert.assertNull(lastLoginTime);
  }

  @Test
  public void testActiveUserSuccess() {
    Map<String, Object> reqMap = new HashMap<>();
    reqMap.put(JsonKey.USER_ID, userId.get(JsonKey.USER_ID));
    String response = keyCloakService.activateUser(reqMap);
    Assert.assertEquals(JsonKey.SUCCESS, response);
  }

  @Test
  public void testActivateUserFailureWithEmptyUserId() {
    Map<String, Object> reqMap = new HashMap<>();
    reqMap.put(JsonKey.USER_ID, "");
    try {
      keyCloakService.activateUser(reqMap);
    } catch (ProjectCommonException e) {
      Assert.assertEquals(ResponseCode.invalidUsrData.getErrorCode(), e.getCode());
      Assert.assertEquals(ResponseCode.invalidUsrData.getErrorMessage(), e.getMessage());
      Assert.assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
    }
  }

  @Test
  public void testLoginSuccess() {
    httpRules("realms/sunbird/protocol/openid-connect/token", "{\"access_token\":\"_your_token\"}");
    String authKey = keyCloakService.login(userName, "password");
    Assert.assertNotEquals("", authKey);
  }

  @Test
  public void testLoginFailureWithInvalidPassword() {
    httpRules("realms/sunbird/protocol/openid-connect/token", null, true, "&password=password123&");
    String authKey = keyCloakService.login(userName, "password123");
    Assert.assertEquals("", authKey);
  }

  @Test
  public void testIsEmailVerifiedSuccess() {
    boolean response = keyCloakService.isEmailVerified(userId.get(JsonKey.USER_ID));
    Assert.assertEquals(false, response);
  }

  @Test
  public void testEmailVerifiedSuccessWithVerifiedFalse() {
    keyCloakService.setEmailVerifiedAsFalse(userId.get(JsonKey.USER_ID));
    boolean response = keyCloakService.isEmailVerified(userId.get(JsonKey.USER_ID));
    Assert.assertNotEquals(true, response);
  }

  @Test
  public void testEmailVerifiedSuccessWithVerifiedUpdateFalse() {
    keyCloakService.setEmailVerifiedUpdatedFlag(userId.get(JsonKey.USER_ID), "false");
    String response = keyCloakService.getEmailVerifiedUpdatedFlag(userId.get(JsonKey.USER_ID));
    Assert.assertEquals(false + "", response);
  }

  @Test
  public void testEmailVerifiedSuccessWithVerifiedUpdateTrue() {
    keyCloakService.setEmailVerifiedUpdatedFlag(userId.get(JsonKey.USER_ID), "true");
    String response = keyCloakService.getEmailVerifiedUpdatedFlag(userId.get(JsonKey.USER_ID));
    Assert.assertEquals(true + "", response);
  }

  @Test
  public void testEmailVerifiedSuccessWithVerifiedTrue() {
    String response = keyCloakService.setEmailVerifiedTrue(userId.get(JsonKey.USER_ID));
    Assert.assertEquals(JsonKey.SUCCESS, response);
  }

  @Test
  public void testSyncUserDataSuccess() {
    Map<String, Object> request = new HashMap<String, Object>();
    request.put(JsonKey.USERNAME, userName);
    request.put(JsonKey.PROVIDER, "ntp");
    request.put(JsonKey.PASSWORD, "password");
    request.put(JsonKey.FIRST_NAME, "A");
    request.put(JsonKey.LAST_NAME, "B");
    request.put(JsonKey.PHONE, "9870060000");
    request.put(JsonKey.COUNTRY_CODE, "+91");
    request.put(JsonKey.EMAIL, userName.substring(0, 10));
    request.put(JsonKey.USER_ID, userId.get(JsonKey.USER_ID));
    String response = keyCloakService.syncUserData(request);
    Assert.assertEquals(JsonKey.SUCCESS, response);
  }

  @Test
  public void testSyncUserDataSuccessWithoutConuntryCode() {
    Map<String, Object> request = new HashMap<String, Object>();
    request.put(JsonKey.USERNAME, userName);
    request.put(JsonKey.PROVIDER, "ntp");
    request.put(JsonKey.PASSWORD, "password");
    request.put(JsonKey.FIRST_NAME, "A");
    request.put(JsonKey.LAST_NAME, "B");
    request.put(JsonKey.PHONE, "9870060000");
    request.put(JsonKey.EMAIL, userName.substring(0, 10));
    request.put(JsonKey.USER_ID, userId.get(JsonKey.USER_ID));
    String response = keyCloakService.syncUserData(request);
    Assert.assertEquals(JsonKey.SUCCESS, response);
  }

  @Test
  public void testSyncUserDataSuccessWithoutProvider() {
    Map<String, Object> request = new HashMap<String, Object>();
    request.put(JsonKey.USERNAME, userName);
    request.put(JsonKey.PASSWORD, "password");
    request.put(JsonKey.FIRST_NAME, "A");
    request.put(JsonKey.LAST_NAME, "B");
    request.put(JsonKey.PHONE, "9870060000");
    request.put(JsonKey.EMAIL, userName.substring(0, 10));
    request.put(JsonKey.USER_ID, userId.get(JsonKey.USER_ID));
    String response = keyCloakService.syncUserData(request);
    Assert.assertEquals(JsonKey.SUCCESS, response);
  }

  @Test
  public void testSyncUserDataSuccessWithoInvalidUser() {
    Map<String, Object> request = new HashMap<String, Object>();
    request.put(JsonKey.USERNAME, userName);
    request.put(JsonKey.PASSWORD, "password");
    request.put(JsonKey.FIRST_NAME, "A");
    request.put(JsonKey.LAST_NAME, "B");
    request.put(JsonKey.PHONE, "9870060000");
    request.put(JsonKey.EMAIL, userName.substring(0, 10));
    request.put(JsonKey.USER_ID, "xey123-23sss-cbdsgdgdg");
    try {
      keyCloakService.syncUserData(request);
    } catch (ProjectCommonException e) {
      Assert.assertEquals(ResponseCode.invalidUsrData.getErrorCode(), e.getCode());
      Assert.assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
    }
  }

  @Test
  public void testUpdatePasswordSuccess() {
    boolean response = keyCloakService.doPasswordUpdate(userId.get(JsonKey.USER_ID), "password");
    Assert.assertEquals(true, response);
  }
}
