package org.sunbird.services.sso.impl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.services.sso.SSOManager;
import org.sunbird.services.sso.SSOServiceFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author arvind
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KeyCloakServiceImplTest {

    SSOManager keyCloakService = SSOServiceFactory.getInstance();

    private static Map<String,String> userId ;
    final static String userName = UUID.randomUUID().toString().replaceAll("-", "");

    @BeforeClass
    public static void setup(){

    }

    @Test
    public void createUserTest(){

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
        request.put(JsonKey.USER_ID , userId);
        request.put(JsonKey.FIRST_NAME , userName);
        String result = keyCloakService.updateUser(request);
        Assert.assertNotNull(result);
    }

    @Test
    public void vdeactivateUserTest(){

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USER_ID , userId);
        request.put(JsonKey.FIRST_NAME , userName);
        String result = keyCloakService.deactivateUser(request);
        Assert.assertNotNull(result);
    }

    @Test
    public void xremoveUserTest(){

        Map<String , Object> request = new HashMap<String , Object>();
        request.put(JsonKey.USER_ID , userId);
        String result = keyCloakService.removeUser(request);
        Assert.assertNotNull(result);
    }
    
    @Test(expected = javax.ws.rs.ForbiddenException.class) 
    public void verifyAuthTOken() {
      keyCloakService.verifyToken("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI5emhhVnZDbl81OEtheHpldHBzYXNZQ2lEallkemJIX3U2LV93SDk4SEc0In0.eyJqdGkiOiI5ZmQzNzgzYy01YjZmLTQ3OWQtYmMzYy0yZWEzOGUzZmRmYzgiLCJleHAiOjE1MDUxMTQyNDYsIm5iZiI6MCwiaWF0IjoxNTA1MTEzNjQ2LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoic2VjdXJpdHktYWRtaW4tY29uc29sZSIsInN1YiI6ImIzYTZkMTY4LWJjZmQtNDE2MS1hYzVmLTljZjYyODIyNzlmMyIsInR5cCI6IkJlYXJlciIsImF6cCI6InNlY3VyaXR5LWFkbWluLWNvbnNvbGUiLCJub25jZSI6ImMxOGVlMDM2LTAyMWItNGVlZC04NWVhLTc0MjMyYzg2ZmI4ZSIsImF1dGhfdGltZSI6MTUwNTExMzY0Niwic2Vzc2lvbl9zdGF0ZSI6ImRiZTU2NDlmLTY4MDktNDA3NS05Njk5LTVhYjIyNWMwZTkyMiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOltdLCJyZXNvdXJjZV9hY2Nlc3MiOnt9LCJuYW1lIjoiTWFuemFydWwgaGFxdWUiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0MTIzNDU2NyIsImdpdmVuX25hbWUiOiJNYW56YXJ1bCBoYXF1ZSIsImVtYWlsIjoidGVzdDEyM0B0LmNvbSJ9.Xdjqe16MSkiR94g-Uj_pVZ2L3gnIdKpkJ6aB82W_w_c3yEmx1mXYBdkxe4zMz3ks4OX_PWwSFEbJECHcnujUwF6Ula0xtXTfuESB9hFyiWHtVAhuh5UlCCwPnsihv5EqK6u-Qzo0aa6qZOiQK3Zo7FLpnPUDxn4yHyo3mRZUiWf76KTl8PhSMoXoWxcR2vGW0b-cPixILTZPV0xXUZoozCui70QnvTgOJDWqr7y80EWDkS4Ptn-QM3q2nJlw63mZreOG3XTdraOlcKIP5vFK992dyyHlYGqWVzigortS9Ah4cprFVuLlX8mu1cQvqHBtW-0Dq_JlcTMaztEnqvJ6XA");
    }
}
