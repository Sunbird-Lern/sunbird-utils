package org.sunbird.services.sso.impl;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.services.sso.SSOManager;
import org.sunbird.services.sso.SSOServiceFactory;

/**
 * @author arvind
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KeyCloakServiceImplTest {

    SSOManager keyCloakService = SSOServiceFactory.getInstance();

    private static Map<String,String> userId ;
    final static String userName = UUID.randomUUID().toString().replaceAll("-", "");
    static Class t  = null;    
    @BeforeClass
    public static void setUp() {
      try {
        t  = Class.forName("org.sunbird.services.sso.SSOServiceFactory");
      } catch (ClassNotFoundException e) {
      }
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void instanceCreationTest() {
      Exception exp = null ;
      try {
        Constructor<SSOServiceFactory> constructor =
            t.getDeclaredConstructor();
        constructor.setAccessible(true);
        SSOServiceFactory application = constructor.newInstance();
        org.junit.Assert.assertNotNull(application);
      } catch (Exception e) {
        exp = e;
      }
      org.junit.Assert.assertNull(exp);
    }

    @Test
    public void createUserTest(){

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USERNAME , userName);
        request.put(JsonKey.PASSWORD , "password");
        request.put(JsonKey.FIRST_NAME , "A");
        request.put(JsonKey.LAST_NAME , "B");
        request.put(JsonKey.PHONE , "9870060000");
        request.put(JsonKey.EMAIL , userName.substring(0,10));
        userId = keyCloakService.createUser(request);
        Assert.assertNotNull(userId);
    }
   
    @Test(expected = ProjectCommonException.class)
    public void createUserTestWithSameEmailUserName(){

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USERNAME , userName);
        request.put(JsonKey.PASSWORD , "password");
        request.put(JsonKey.FIRST_NAME , "A");
        request.put(JsonKey.LAST_NAME , "B");
        request.put(JsonKey.EMAIL_VERIFIED , true);
        request.put(JsonKey.EMAIL , userName.substring(0,10));
        userId = keyCloakService.createUser(request);
        Assert.assertNotNull(userId);
    }

    @Test(expected = ProjectCommonException.class)
    public void createUserTestWithSameEmailDiffUserName(){

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USERNAME , userName+"01abc");
        request.put(JsonKey.PASSWORD , "password");
        request.put(JsonKey.FIRST_NAME , "A");
        request.put(JsonKey.LAST_NAME , "B");
        request.put(JsonKey.EMAIL_VERIFIED , true);
        request.put(JsonKey.EMAIL , userName.substring(0,10));
        userId = keyCloakService.createUser(request);
        Assert.assertNotNull(userId);
    }

    @Test
    public void updateUserTest(){
        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USER_ID , userId.get(JsonKey.USER_ID));
        request.put(JsonKey.FIRST_NAME , userName);
        request.put(JsonKey.PHONE, "9870060000");
        request.put(JsonKey.EMAIL , userName.substring(0,10));
        request.put(JsonKey.USERNAME , userName);
        request.put(JsonKey.PROVIDER, "ntp");
        String result = keyCloakService.updateUser(request);
        Assert.assertNotNull(result);
    }
   
    @Test
    public void updateUserWithOutProviderTest(){
        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USER_ID , userId.get(JsonKey.USER_ID));
        request.put(JsonKey.FIRST_NAME , userName);
        request.put(JsonKey.PHONE, "9870060000");
        request.put(JsonKey.COUNTRY_CODE, "+91");
        request.put(JsonKey.EMAIL , userName.substring(0,10));
        request.put(JsonKey.USERNAME , userName);
        String result = keyCloakService.updateUser(request);
        Assert.assertNotNull(result);
    }
    
    
    @Test
    public void updateUserWithOutProviderAndCountryCodeTest(){
        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USER_ID , userId.get(JsonKey.USER_ID));
        request.put(JsonKey.FIRST_NAME , userName);
        request.put(JsonKey.PHONE, "9870060000");
        request.put(JsonKey.EMAIL , userName.substring(0,10));
        request.put(JsonKey.USERNAME , userName);
        String result = keyCloakService.updateUser(request);
        Assert.assertNotNull(result);
    }
    
    @Test
    public void updateUserTestWithOutPassingAnyField(){

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USER_ID , userId.get(JsonKey.USER_ID));
        String result = keyCloakService.updateUser(request);
        Assert.assertNotNull(result);
    }
    
    @Test
    public void updateUserTestWithAllFieldsExceptProvider(){

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USER_ID , userId.get(JsonKey.USER_ID));
        request.put(JsonKey.PASSWORD , "password");
        request.put(JsonKey.FIRST_NAME , "A");
        request.put(JsonKey.LAST_NAME , "B");
        request.put(JsonKey.EMAIL_VERIFIED , true);
        request.put(JsonKey.EMAIL , userName.substring(0,10));
        String result = keyCloakService.updateUser(request);
        Assert.assertNotNull(result);
    }
    
    
    @Test
    public void vdeactivateUserTest(){

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USER_ID , userId.get(JsonKey.USER_ID));
        request.put(JsonKey.FIRST_NAME , userName);
        String result = keyCloakService.deactivateUser(request);
        Assert.assertNotNull(result);
    }

    @Test
    public void zremoveUserTest(){

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USER_ID , userId.get(JsonKey.USER_ID));
        String result = keyCloakService.removeUser(request);
        Assert.assertNotNull(result);
    }
    
    @Test (expected = ProjectCommonException.class)
    public void verifyAuthTOken() {
      keyCloakService.verifyToken("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI5emhhVnZDbl81OEtheHpldHBzYXNZQ2lEallkemJIX3U2LV93SDk4SEc0In0.eyJqdGkiOiI5ZmQzNzgzYy01YjZmLTQ3OWQtYmMzYy0yZWEzOGUzZmRmYzgiLCJleHAiOjE1MDUxMTQyNDYsIm5iZiI6MCwiaWF0IjoxNTA1MTEzNjQ2LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoic2VjdXJpdHktYWRtaW4tY29uc29sZSIsInN1YiI6ImIzYTZkMTY4LWJjZmQtNDE2MS1hYzVmLTljZjYyODIyNzlmMyIsInR5cCI6IkJlYXJlciIsImF6cCI6InNlY3VyaXR5LWFkbWluLWNvbnNvbGUiLCJub25jZSI6ImMxOGVlMDM2LTAyMWItNGVlZC04NWVhLTc0MjMyYzg2ZmI4ZSIsImF1dGhfdGltZSI6MTUwNTExMzY0Niwic2Vzc2lvbl9zdGF0ZSI6ImRiZTU2NDlmLTY4MDktNDA3NS05Njk5LTVhYjIyNWMwZTkyMiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOltdLCJyZXNvdXJjZV9hY2Nlc3MiOnt9LCJuYW1lIjoiTWFuemFydWwgaGFxdWUiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0MTIzNDU2NyIsImdpdmVuX25hbWUiOiJNYW56YXJ1bCBoYXF1ZSIsImVtYWlsIjoidGVzdDEyM0B0LmNvbSJ9.Xdjqe16MSkiR94g-Uj_pVZ2L3gnIdKpkJ6aB82W_w_c3yEmx1mXYBdkxe4zMz3ks4OX_PWwSFEbJECHcnujUwF6Ula0xtXTfuESB9hFyiWHtVAhuh5UlCCwPnsihv5EqK6u-Qzo0aa6qZOiQK3Zo7FLpnPUDxn4yHyo3mRZUiWf76KTl8PhSMoXoWxcR2vGW0b-cPixILTZPV0xXUZoozCui70QnvTgOJDWqr7y80EWDkS4Ptn-QM3q2nJlw63mZreOG3XTdraOlcKIP5vFK992dyyHlYGqWVzigortS9Ah4cprFVuLlX8mu1cQvqHBtW-0Dq_JlcTMaztEnqvJ6XA");
      }
    
    
    @Test
    public void xaddLoginTimeTest () {
       boolean response = keyCloakService.addUserLoginTime(userId.get(JsonKey.USER_ID));
       Assert.assertEquals(true, response);
    }
    
    @Test
    public void yverifyLastLoginTime () {
      String lastLoginTime = keyCloakService.getLastLoginTime(userId.get(JsonKey.USER_ID));
      Assert.assertNull(lastLoginTime);
    }
  
    @Test
    public void yxddLoginTimeTest () {
       boolean response = keyCloakService.addUserLoginTime(userId.get(JsonKey.USER_ID));
       Assert.assertEquals(true, response);
    }
    
    @Test
    public void yyerifyLastLoginTime () {
      String lastLoginTime = keyCloakService.getLastLoginTime(userId.get(JsonKey.USER_ID));
      Assert.assertNotNull(lastLoginTime);
    }
    
  @Test
  public void makeUserActive() {
    Map<String, Object> reqMap = new HashMap<>();
    reqMap.put(JsonKey.USER_ID, userId.get(JsonKey.USER_ID));
    String response = keyCloakService.activateUser(reqMap);
    Assert.assertEquals(JsonKey.SUCCESS, response);
  }
  
  @Test
  public void makeUserActiveFailure() {
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
  public void loginSuccessTest() {
   String authKey = keyCloakService.login(userName, "password");
   Assert.assertNotEquals("", authKey);
  }
  
  @Test
  public void loginFailureTest() {
    String authKey = keyCloakService.login(userName, "password123");
    Assert.assertEquals("", authKey);
   }
  
  @Test
  public void emailVerifiedTest() {
    boolean response = keyCloakService.isEmailVerified(userId.get(JsonKey.USER_ID));
    Assert.assertEquals(false, response);
  }
  
  
  @Test
  public void setEmailVerifiedAsFalseTest() {
    keyCloakService.setEmailVerifiedAsFalse(userId.get(JsonKey.USER_ID));
    boolean response = keyCloakService.isEmailVerified(userId.get(JsonKey.USER_ID));
    Assert.assertNotEquals(true, response);
  }
  
  @Test
  public void setEmailVerifiedUpdatedFlagWithFalse() {
    keyCloakService.setEmailVerifiedUpdatedFlag(userId.get(JsonKey.USER_ID),"false");
    String response = keyCloakService.getEmailVerifiedUpdatedFlag(userId.get(JsonKey.USER_ID));
    Assert.assertEquals(false+"", response);
  }
  
  @Test
	public void setEmailVerifiedUpdatedFlagWithTrue() {
		keyCloakService.setEmailVerifiedUpdatedFlag(userId.get(JsonKey.USER_ID), "true");
		String response = keyCloakService.getEmailVerifiedUpdatedFlag(userId.get(JsonKey.USER_ID));
		Assert.assertEquals(true + "", response);
	}
  
  @Test
	public void syncUserDataSuccess() {
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
 	public void syncUserDataSuccessWithOutCountryCode() {
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
	public void syncUserDataSuccessWithOutProvider() {
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
	public void syncUserDataWithInvalidUser() {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put(JsonKey.USERNAME, userName);
		request.put(JsonKey.PASSWORD, "password");
		request.put(JsonKey.FIRST_NAME, "A");
		request.put(JsonKey.LAST_NAME, "B");
		request.put(JsonKey.PHONE, "9870060000");
		request.put(JsonKey.EMAIL, userName.substring(0, 10));
		request.put(JsonKey.USER_ID, "xey123-23sss-cbdsgdgdg");
		try {
			String response = keyCloakService.syncUserData(request);
		} catch (ProjectCommonException e) {
			Assert.assertEquals(ResponseCode.invalidUsrData.getErrorCode(), e.getCode());
			Assert.assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
		}
	}
  
  @Test
  public void passwordUppdateTest() {
	   boolean response = keyCloakService.doPasswordUpdate(userId.get(JsonKey.USER_ID), "password");
	   Assert.assertEquals(true, response);
  }
  
  
}
