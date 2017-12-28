package org.sunbird.common.models.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sunbird.common.exception.ProjectCommonException;
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
    String msg = ProjectUtil.getSMSBody("AMIT@BLR", "sunbird.org", "sunbird.com");
    Assert.assertTrue(msg.contains("sunbird.org"));
  }
  
  @Test
  public void testsendSMS2(){
    String msg = ProjectUtil.getSMSBody("", "", "");
    Assert.assertTrue(msg.contains("user_name"));
  }
  
  @Test
  public void testisDateValidFormat(){
    boolean bool = ProjectUtil.isDateValidFormat("yyyy-MM-dd HH:mm:ss:SSSZ", "2017-12-18 10:47:30:707+0530");
    Assert.assertTrue(bool);
  }
  
  @Test
  public void testisDateValidFormat2(){
    boolean bool = ProjectUtil.isDateValidFormat("yyyy-MM-dd", "");
    Assert.assertFalse(bool);
  }
  
  @Test
  public void testisDateValidFormat3(){
    boolean bool = ProjectUtil.isDateValidFormat("yyyy-MM-dd", "2017-12-18");
    Assert.assertTrue(bool);
  }
  
  @Test
  public void testisDateValidFormat4(){
    boolean bool = ProjectUtil.isDateValidFormat("yyyy-MM-dd HH:mm:ss:SSSZ", "");
    Assert.assertFalse(bool);
  }
  
  @Test
  public void getEkstepHeaderTest () {
	 Map<String,String> map = ProjectUtil.getEkstepHeader();
	 Assert.assertEquals(map.get("Content-Type"), "application/json");
	 Assert.assertNotNull(map.get(JsonKey.AUTHORIZATION));
  }
  
  @Test
	public void registertagTest() {
		String response = null;
		try {
			response = ProjectUtil.registertag("testTag", "{}", ProjectUtil.getEkstepHeader());
		} catch (IOException e) {

		}
		Assert.assertNotNull(response);
	}
  
  @Test
	public void reportTrackingStatusTest() {
		Assert.assertEquals(0, ProjectUtil.ReportTrackingStatus.NEW.getValue());
		Assert.assertEquals(1, ProjectUtil.ReportTrackingStatus.GENERATING_DATA.getValue());
		Assert.assertEquals(2, ProjectUtil.ReportTrackingStatus.UPLOADING_FILE.getValue());
		Assert.assertEquals(3, ProjectUtil.ReportTrackingStatus.UPLOADING_FILE_SUCCESS.getValue());
		Assert.assertEquals(4, ProjectUtil.ReportTrackingStatus.SENDING_MAIL.getValue());
		Assert.assertEquals(5, ProjectUtil.ReportTrackingStatus.SENDING_MAIL_SUCCESS.getValue());
		Assert.assertEquals(9, ProjectUtil.ReportTrackingStatus.FAILED.getValue());
	} 
 
  @Test
  public void getDefaultTemplateTest () {
	  Map<String,Object> map = new HashMap<>();
	  String template = ProjectUtil.getTemplate(map);
	  Assert.assertEquals("/emailtemplate.vm", template);
  }
  
  @Test
  public void getTemplateTest () {
	  Map<String,Object> map = new HashMap<>();
	  map.put(JsonKey.EMAIL_TEMPLATE_TYPE, "/test.vm");
	  String template = ProjectUtil.getTemplate(map);
	  Assert.assertEquals("/emailtemplate.vm", template);
  }
  
  @Test
  public void getEsTypeTest () {
	  Assert.assertEquals("content", ProjectUtil.EsType.content.getTypeName()); 
	  Assert.assertEquals("course", ProjectUtil.EsType.course.getTypeName()); 
	  Assert.assertEquals("user", ProjectUtil.EsType.user.getTypeName());
	  Assert.assertEquals("org", ProjectUtil.EsType.organisation.getTypeName());
	  Assert.assertEquals("usercourses", ProjectUtil.EsType.usercourses.getTypeName());
	  Assert.assertEquals("usernotes", ProjectUtil.EsType.usernotes.getTypeName());
	  Assert.assertEquals("history", ProjectUtil.EsType.history.getTypeName());
	  Assert.assertEquals("userprofilevisibility", ProjectUtil.EsType.userprofilevisibility.getTypeName());
  }
 
  @Test
  public void getEsIndexTest () {
	  Assert.assertEquals("searchindex", ProjectUtil.EsIndex.sunbird.getIndexName()); 
	  Assert.assertEquals("sunbirddataaudit", ProjectUtil.EsIndex.sunbirdDataAudit.getIndexName()); 
  }
  
  @Test
  public void getUserRoleTest () {
	  Assert.assertEquals("PUBLIC", ProjectUtil.UserRole.PUBLIC.getValue()); 
	  Assert.assertEquals("CONTENT_CREATOR", ProjectUtil.UserRole.CONTENT_CREATOR.getValue()); 
	  Assert.assertEquals("CONTENT_REVIEWER", ProjectUtil.UserRole.CONTENT_REVIEWER.getValue()); 
	  Assert.assertEquals("ORG_ADMIN", ProjectUtil.UserRole.ORG_ADMIN.getValue());
	  Assert.assertEquals("ORG_MEMBER", ProjectUtil.UserRole.ORG_MEMBER.getValue());
  }
 
  @Test
  public void getBulkProcessStatusTest () {
	  Assert.assertEquals(0, ProjectUtil.BulkProcessStatus.NEW.getValue()); 
	  Assert.assertEquals(1, ProjectUtil.BulkProcessStatus.IN_PROGRESS.getValue()); 
	  Assert.assertEquals(2, ProjectUtil.BulkProcessStatus.INTERRUPT.getValue()); 
	  Assert.assertEquals(3, ProjectUtil.BulkProcessStatus.COMPLETED.getValue()); 
	  Assert.assertEquals(9, ProjectUtil.BulkProcessStatus.FAILED.getValue());
  }
  
  @Test
  public void getOrgStatusTest () {
	  Assert.assertEquals(new Integer(0), ProjectUtil.OrgStatus.INACTIVE.getValue());
	  Assert.assertEquals(new Integer(1), ProjectUtil.OrgStatus.ACTIVE.getValue());
	  Assert.assertEquals(new Integer(2), ProjectUtil.OrgStatus.BLOCKED.getValue());
	  Assert.assertEquals(new Integer(3), ProjectUtil.OrgStatus.RETIRED.getValue());
  }
  
  @Test
  public void getCourseMngTest () {
	  Assert.assertEquals("draft", ProjectUtil.CourseMgmtStatus.DRAFT.getValue());
	  Assert.assertEquals("live", ProjectUtil.CourseMgmtStatus.LIVE.getValue());
	  Assert.assertEquals("retired", ProjectUtil.CourseMgmtStatus.RETIRED.getValue());
  }
  
  @Test
  public void getProgressStatusTest () {
	  Assert.assertEquals(0, ProjectUtil.ProgressStatus.NOT_STARTED.getValue());
	  Assert.assertEquals(1, ProjectUtil.ProgressStatus.STARTED.getValue());
	  Assert.assertEquals(2, ProjectUtil.ProgressStatus.COMPLETED.getValue());
  }
  
  @Test
  public void getEnvTest () {
	  Assert.assertEquals(1, ProjectUtil.Environment.dev.getValue());
	  Assert.assertEquals(2, ProjectUtil.Environment.qa.getValue());
	  Assert.assertEquals(3, ProjectUtil.Environment.prod.getValue());
  }
  
  @Test
  public void getObjectType () {
	  Assert.assertEquals("batch", ProjectUtil.ObjectTypes.batch.getValue());
	  Assert.assertEquals("user", ProjectUtil.ObjectTypes.user.getValue());
	  Assert.assertEquals("organisation", ProjectUtil.ObjectTypes.organisation.getValue());
  }
  
  @Test
  public void getSourceTest () {
	  Assert.assertEquals("web", ProjectUtil.Source.WEB.getValue());
	  Assert.assertEquals("android", ProjectUtil.Source.ANDROID.getValue());
	  Assert.assertEquals("ios", ProjectUtil.Source.IOS.getValue());
  }
  
  @Test
  public void getSectionDataType () {
	  Assert.assertEquals("course", ProjectUtil.SectionDataType.course.getTypeName());
	  Assert.assertEquals("content", ProjectUtil.SectionDataType.content.getTypeName());
  }
  
  @Test
  public void getStatusTest () {
	  Assert.assertEquals(1, ProjectUtil.Status.ACTIVE.getValue());
	  Assert.assertEquals(0, ProjectUtil.Status.INACTIVE.getValue());
	  Assert.assertEquals(false, ProjectUtil.ActiveStatus.INACTIVE.getValue());
	  Assert.assertEquals(true, ProjectUtil.ActiveStatus.ACTIVE.getValue());
	  Assert.assertEquals("orgimg", ProjectUtil.AzureContainer.orgImage.getName());
	  Assert.assertEquals("userprofileimg", ProjectUtil.AzureContainer.userProfileImg.getName());
  }
  
  @Test
  public void serverExceptionTest () {
	  try {
		  ProjectUtil.createAndThrowServerError(); 
	  } catch (ProjectCommonException e) {
		  assertEquals(ResponseCode.SERVER_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.SERVER_ERROR.getErrorCode(), e.getCode());
	}
  }
  
  @Test
  public void invalidUserDataExceptionTest () {
	  try {
		  ProjectUtil.createAndThrowInvalidUserDataException(); 
	  } catch (ProjectCommonException e) {
		  assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
			assertEquals(ResponseCode.invalidUsrData.getErrorCode(), e.getCode());
	}
  }
  
}
