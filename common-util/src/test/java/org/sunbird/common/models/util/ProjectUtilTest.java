package org.sunbird.common.models.util;

import com.google.i18n.phonenumbers.NumberParseException;
import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.VelocityContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * Created by arvind on 6/10/17.
 */
public class ProjectUtilTest {

  private  PropertiesCache propertiesCache = ProjectUtil.propertiesCache;

  @BeforeClass
  public static void setUp(){

  }

  @Test
  public void testMailTemplateContextNameAsent(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");

    VelocityContext context = ProjectUtil.getContext(templateMap);
    Assert.assertEquals(false ,context.internalContainsKey(JsonKey.NAME));

  }

  @Test
  public void testMailTemplateContextActionUrlAbsent(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.NAME, "userName");

    VelocityContext context = ProjectUtil.getContext(templateMap);
    Assert.assertEquals(false ,context.internalContainsKey(JsonKey.ACTION_URL));

  }

  @Test
  public void testMailTemplateContextCheckFromMail(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");
    templateMap.put(JsonKey.NAME, "userName");


    boolean envVal = !ProjectUtil.isStringNullOREmpty(System.getenv(JsonKey.EMAIL_SERVER_FROM));
    boolean cacheVal = propertiesCache.getProperty(JsonKey.EMAIL_SERVER_FROM)!=null;

    VelocityContext context = ProjectUtil.getContext(templateMap);
    if(envVal){
      Assert.assertEquals(System.getenv(JsonKey.EMAIL_SERVER_FROM) , (String)context.internalGet(JsonKey.FROM_EMAIL));
    }else if(cacheVal){
      Assert.assertEquals(propertiesCache.getProperty(JsonKey.EMAIL_SERVER_FROM) , (String)context.internalGet(JsonKey.FROM_EMAIL));
    }

  }

  @Test
  public void testMailTemplateContextCheckOrgImageUrl(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");
    templateMap.put(JsonKey.NAME, "userName");

    boolean envVal = !ProjectUtil.isStringNullOREmpty(System.getenv(JsonKey.SUNBIRD_ENV_LOGO_URL));
    boolean cacheVal = propertiesCache.getProperty(JsonKey.SUNBIRD_ENV_LOGO_URL)!=null;

    VelocityContext context = ProjectUtil.getContext(templateMap);
    if(envVal){
      Assert.assertEquals(System.getenv(JsonKey.SUNBIRD_ENV_LOGO_URL) , (String)context.internalGet(JsonKey.ORG_IMAGE_URL));
    }else if(cacheVal){
      Assert.assertEquals(propertiesCache.getProperty(JsonKey.SUNBIRD_ENV_LOGO_URL) , (String)context.internalGet(JsonKey.ORG_IMAGE_URL));
    }

  }
  
  @Test
  public void testCreateAuthToken(){
    String authToken = ProjectUtil.createAuthToken("test", "tset1234");
    Assert.assertNotNull(authToken);
  }
  
  @Test
  public void testValidateInValidPhoneNumberSuccess(){
    Assert.assertFalse(ProjectUtil.validatePhoneNumber("312"));
  }
  
  @Test
  public void testValidateValidPhoneNumber(){
    Assert.assertTrue(ProjectUtil.validatePhoneNumber("9844016699"));
  }
  
  @Test
  public void testValidateGenerateRandomPassword(){
    Assert.assertNotNull(ProjectUtil.generateRandomPassword());
  }
  
  @Test
  public void testCreateCheckResponseSuccess(){
   Map<String, Object> responseMap = ProjectUtil.createCheckResponse("LearnerService", false, null);
   Assert.assertEquals(true, responseMap.get(JsonKey.Healthy));
  }
  
  @Test
  public void testCreateCheckResponseException(){
    Map<String, Object> responseMap = ProjectUtil.createCheckResponse("LearnerService", true,
        new ProjectCommonException(ResponseCode.invalidObjectType.getErrorCode(),
            ResponseCode.invalidObjectType.getErrorMessage(),
            ResponseCode.CLIENT_ERROR.getResponseCode()));
    Assert.assertEquals(false, responseMap.get(JsonKey.Healthy));
    Assert.assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), responseMap.get(JsonKey.ERROR));
    Assert.assertEquals(ResponseCode.invalidObjectType.getErrorMessage(), responseMap.get(JsonKey.ERRORMSG));
  }
  
  @Test
  public void testUpdateMapSomeValueToLowerCase() {
    Request request = new Request();
    Map<String,Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SOURCE, "Test");
    requestObj.put(JsonKey.LOGIN_ID, "SunbirdUser");
    requestObj.put(JsonKey.EXTERNAL_ID, "testExternal");
    requestObj.put(JsonKey.USER_NAME, "username");
    requestObj.put(JsonKey.USERNAME, "userName");
    requestObj.put(JsonKey.PROVIDER, "Provider");
    requestObj.put(JsonKey.ID, "TEST123");
    request.setRequest(requestObj);
    ProjectUtil.updateMapSomeValueTOLowerCase(request);
    Assert.assertEquals("test", requestObj.get(JsonKey.SOURCE));
    Assert.assertEquals("sunbirduser", requestObj.get(JsonKey.LOGIN_ID));
    Assert.assertEquals("testexternal", requestObj.get(JsonKey.EXTERNAL_ID));
    Assert.assertEquals("username", requestObj.get(JsonKey.USER_NAME));
    Assert.assertEquals("username", requestObj.get(JsonKey.USERNAME));
    Assert.assertEquals("provider", requestObj.get(JsonKey.PROVIDER));
    Assert.assertEquals("TEST123", requestObj.get(JsonKey.ID));
  }
  
  @Test
  public void testFormatMessage(){
    String msg = ProjectUtil.formatMessage("Hello {0}", "user");
    Assert.assertEquals("Hello user", msg);
  }
  
  @Test
  public void testFormatMessageInvalid(){
    String msg = ProjectUtil.formatMessage("Hello ", "user");
    Assert.assertNotEquals("Hello user", msg);
  }

  @Test
  public void testisEmailvalid(){
    boolean msg = ProjectUtil.isEmailvalid("Hello ");
    Assert.assertFalse(msg);
  }
  
  @Test
  public void testsendSMS(){
    String msg = ProjectUtil.sendSMS("SUNBIRD", "KARNATAKA", "AMIT@BLR", "sunbird.org", "sunbird.com");
    Assert.assertTrue(msg.contains("KARNATAKA"));
  }
  
  @Test
  public void testsendSMS2(){
    String msg = ProjectUtil.sendSMS("", "", "", "", "");
    Assert.assertTrue(msg.contains("user_name"));
  }
}
