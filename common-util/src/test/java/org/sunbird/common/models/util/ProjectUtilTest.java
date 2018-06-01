package org.sunbird.common.models.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.junit.Test;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

/** Created by arvind on 6/10/17. */
public class ProjectUtilTest {

  private PropertiesCache propertiesCache = ProjectUtil.propertiesCache;

  @Test
  public void testMailTemplateContextNameAsent() {

    Map<String, Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");

    VelocityContext context = ProjectUtil.getContext(templateMap);
    assertEquals(false, context.internalContainsKey(JsonKey.NAME));
  }

  @Test
  public void testMailTemplateContextActionUrlAbsent() {

    Map<String, Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.NAME, "userName");

    VelocityContext context = ProjectUtil.getContext(templateMap);
    assertEquals(false, context.internalContainsKey(JsonKey.ACTION_URL));
  }

  @Test
  public void testMailTemplateContextCheckFromMail() {

    Map<String, Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");
    templateMap.put(JsonKey.NAME, "userName");

    boolean envVal = !StringUtils.isBlank(System.getenv(JsonKey.EMAIL_SERVER_FROM));
    boolean cacheVal = propertiesCache.getProperty(JsonKey.EMAIL_SERVER_FROM) != null;

    VelocityContext context = ProjectUtil.getContext(templateMap);
    if (envVal) {
      assertEquals(
          System.getenv(JsonKey.EMAIL_SERVER_FROM),
          (String) context.internalGet(JsonKey.FROM_EMAIL));
    } else if (cacheVal) {
      assertEquals(
          propertiesCache.getProperty(JsonKey.EMAIL_SERVER_FROM),
          (String) context.internalGet(JsonKey.FROM_EMAIL));
    }
  }

  @Test
  public void testMailTemplateContextCheckOrgImageUrl() {

    Map<String, Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");
    templateMap.put(JsonKey.NAME, "userName");

    boolean envVal = !StringUtils.isBlank(System.getenv(JsonKey.SUNBIRD_ENV_LOGO_URL));
    boolean cacheVal = propertiesCache.getProperty(JsonKey.SUNBIRD_ENV_LOGO_URL) != null;

    VelocityContext context = ProjectUtil.getContext(templateMap);
    if (envVal) {
      assertEquals(
          System.getenv(JsonKey.SUNBIRD_ENV_LOGO_URL),
          (String) context.internalGet(JsonKey.ORG_IMAGE_URL));
    } else if (cacheVal) {
      assertEquals(
          propertiesCache.getProperty(JsonKey.SUNBIRD_ENV_LOGO_URL),
          (String) context.internalGet(JsonKey.ORG_IMAGE_URL));
    }
  }

  @Test
  public void testCreateAuthToken() {
    String authToken = ProjectUtil.createAuthToken("test", "tset1234");
    assertNotNull(authToken);
  }

  @Test
  public void testValidateInValidPhoneNumberSuccess() {
    assertFalse(ProjectUtil.validatePhoneNumber("312"));
  }

  @Test
  public void testValidateValidPhoneNumber() {
    assertTrue(ProjectUtil.validatePhoneNumber("9844016699"));
  }

  @Test
  public void testValidateGenerateRandomPassword() {
    assertNotNull(ProjectUtil.generateRandomPassword());
  }

  @Test
  public void testCreateCheckResponseSuccess() {
    Map<String, Object> responseMap =
        ProjectUtil.createCheckResponse("LearnerService", false, null);
    assertEquals(true, responseMap.get(JsonKey.Healthy));
  }

  @Test
  public void testCreateCheckResponseException() {
    Map<String, Object> responseMap =
        ProjectUtil.createCheckResponse(
            "LearnerService",
            true,
            new ProjectCommonException(
                ResponseCode.invalidObjectType.getErrorCode(),
                ResponseCode.invalidObjectType.getErrorMessage(),
                ResponseCode.CLIENT_ERROR.getResponseCode()));
    assertEquals(false, responseMap.get(JsonKey.Healthy));
    assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), responseMap.get(JsonKey.ERROR));
    assertEquals(
        ResponseCode.invalidObjectType.getErrorMessage(), responseMap.get(JsonKey.ERRORMSG));
  }

  @Test
  public void testUpdateMapSomeValueToLowerCase() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SOURCE, "Test");
    requestObj.put(JsonKey.LOGIN_ID, "SunbirdUser");
    requestObj.put(JsonKey.EXTERNAL_ID, "testExternal");
    requestObj.put(JsonKey.USER_NAME, "username");
    requestObj.put(JsonKey.USERNAME, "userName");
    requestObj.put(JsonKey.PROVIDER, "Provider");
    requestObj.put(JsonKey.ID, "TEST123");
    request.setRequest(requestObj);
    ProjectUtil.updateMapSomeValueTOLowerCase(request);
    assertEquals("test", requestObj.get(JsonKey.SOURCE));
    assertEquals("sunbirduser", requestObj.get(JsonKey.LOGIN_ID));
    assertEquals("testexternal", requestObj.get(JsonKey.EXTERNAL_ID));
    assertEquals("username", requestObj.get(JsonKey.USER_NAME));
    assertEquals("username", requestObj.get(JsonKey.USERNAME));
    assertEquals("provider", requestObj.get(JsonKey.PROVIDER));
    assertEquals("TEST123", requestObj.get(JsonKey.ID));
  }

  @Test
  public void testFormatMessage() {
    String msg = ProjectUtil.formatMessage("Hello {0}", "user");
    assertEquals("Hello user", msg);
  }

  @Test
  public void testFormatMessageInvalid() {
    String msg = ProjectUtil.formatMessage("Hello ", "user");
    assertNotEquals("Hello user", msg);
  }

  @Test
  public void testisEmailvalid() {
    boolean msg = ProjectUtil.isEmailvalid("Hello ");
    assertFalse(msg);
  }

  @Test
  public void testsendSMS() {
    String msg = ProjectUtil.getSMSBody("AMIT@BLR", "www.sunbird.org", "sunbird.com");
    assertTrue(msg.contains("sunbird.org"));
  }

  @Test
  public void testsendSMS2() {
    String msg = ProjectUtil.getSMSBody("", "", "");
    assertTrue(msg.contains("user_name"));
  }

  @Test
  public void testisDateValidFormat() {
    boolean bool =
        ProjectUtil.isDateValidFormat("yyyy-MM-dd HH:mm:ss:SSSZ", "2017-12-18 10:47:30:707+0530");
    assertTrue(bool);
  }

  @Test
  public void testisDateValidFormat2() {
    boolean bool = ProjectUtil.isDateValidFormat("yyyy-MM-dd", "");
    assertFalse(bool);
  }

  @Test
  public void testisDateValidFormat3() {
    boolean bool = ProjectUtil.isDateValidFormat("yyyy-MM-dd", "2017-12-18");
    assertTrue(bool);
  }

  @Test
  public void testisDateValidFormat4() {
    boolean bool = ProjectUtil.isDateValidFormat("yyyy-MM-dd HH:mm:ss:SSSZ", "");
    assertFalse(bool);
  }

  @Test
  public void getEkstepHeaderTest() {
    Map<String, String> map = ProjectUtil.getEkstepHeader();
    assertEquals(map.get("Content-Type"), "application/json");
    assertNotNull(map.get(JsonKey.AUTHORIZATION));
  }

  @Test
  public void registertagTest() {
    String response = null;
    try {
      response = ProjectUtil.registertag("testTag", "{}", ProjectUtil.getEkstepHeader());
    } catch (IOException e) {

    }
    assertNotNull(response);
  }

  @Test
  public void reportTrackingStatusTest() {
    assertEquals(0, ProjectUtil.ReportTrackingStatus.NEW.getValue());
    assertEquals(1, ProjectUtil.ReportTrackingStatus.GENERATING_DATA.getValue());
    assertEquals(2, ProjectUtil.ReportTrackingStatus.UPLOADING_FILE.getValue());
    assertEquals(3, ProjectUtil.ReportTrackingStatus.UPLOADING_FILE_SUCCESS.getValue());
    assertEquals(4, ProjectUtil.ReportTrackingStatus.SENDING_MAIL.getValue());
    assertEquals(5, ProjectUtil.ReportTrackingStatus.SENDING_MAIL_SUCCESS.getValue());
    assertEquals(9, ProjectUtil.ReportTrackingStatus.FAILED.getValue());
  }

  @Test
  public void getDefaultTemplateTest() {
    Map<String, Object> map = new HashMap<>();
    String template = ProjectUtil.getTemplate(map);
    assertEquals("/emailtemplate.vm", template);
  }

  @Test
  public void getTemplateTest() {
    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.EMAIL_TEMPLATE_TYPE, "/test.vm");
    String template = ProjectUtil.getTemplate(map);
    assertEquals("/emailtemplate.vm", template);
  }

  @Test
  public void getEsTypeTest() {
    assertEquals("content", ProjectUtil.EsType.content.getTypeName());
    assertEquals("course", ProjectUtil.EsType.course.getTypeName());
    assertEquals("user", ProjectUtil.EsType.user.getTypeName());
    assertEquals("org", ProjectUtil.EsType.organisation.getTypeName());
    assertEquals("usercourses", ProjectUtil.EsType.usercourses.getTypeName());
    assertEquals("usernotes", ProjectUtil.EsType.usernotes.getTypeName());
    assertEquals("history", ProjectUtil.EsType.history.getTypeName());
    assertEquals("userprofilevisibility", ProjectUtil.EsType.userprofilevisibility.getTypeName());
  }

  @Test
  public void getEsIndexTest() {
    assertEquals("searchindex", ProjectUtil.EsIndex.sunbird.getIndexName());
    assertEquals("sunbirddataaudit", ProjectUtil.EsIndex.sunbirdDataAudit.getIndexName());
  }

  @Test
  public void getUserRoleTest() {
    assertEquals("PUBLIC", ProjectUtil.UserRole.PUBLIC.getValue());
    assertEquals("CONTENT_CREATOR", ProjectUtil.UserRole.CONTENT_CREATOR.getValue());
    assertEquals("CONTENT_REVIEWER", ProjectUtil.UserRole.CONTENT_REVIEWER.getValue());
    assertEquals("ORG_ADMIN", ProjectUtil.UserRole.ORG_ADMIN.getValue());
    assertEquals("ORG_MEMBER", ProjectUtil.UserRole.ORG_MEMBER.getValue());
  }

  @Test
  public void getBulkProcessStatusTest() {
    assertEquals(0, ProjectUtil.BulkProcessStatus.NEW.getValue());
    assertEquals(1, ProjectUtil.BulkProcessStatus.IN_PROGRESS.getValue());
    assertEquals(2, ProjectUtil.BulkProcessStatus.INTERRUPT.getValue());
    assertEquals(3, ProjectUtil.BulkProcessStatus.COMPLETED.getValue());
    assertEquals(9, ProjectUtil.BulkProcessStatus.FAILED.getValue());
  }

  @Test
  public void getOrgStatusTest() {
    assertEquals(new Integer(0), ProjectUtil.OrgStatus.INACTIVE.getValue());
    assertEquals(new Integer(1), ProjectUtil.OrgStatus.ACTIVE.getValue());
    assertEquals(new Integer(2), ProjectUtil.OrgStatus.BLOCKED.getValue());
    assertEquals(new Integer(3), ProjectUtil.OrgStatus.RETIRED.getValue());
  }

  @Test
  public void getCourseMngTest() {
    assertEquals("draft", ProjectUtil.CourseMgmtStatus.DRAFT.getValue());
    assertEquals("live", ProjectUtil.CourseMgmtStatus.LIVE.getValue());
    assertEquals("retired", ProjectUtil.CourseMgmtStatus.RETIRED.getValue());
  }

  @Test
  public void getProgressStatusTest() {
    assertEquals(0, ProjectUtil.ProgressStatus.NOT_STARTED.getValue());
    assertEquals(1, ProjectUtil.ProgressStatus.STARTED.getValue());
    assertEquals(2, ProjectUtil.ProgressStatus.COMPLETED.getValue());
  }

  @Test
  public void getEnvTest() {
    assertEquals(1, ProjectUtil.Environment.dev.getValue());
    assertEquals(2, ProjectUtil.Environment.qa.getValue());
    assertEquals(3, ProjectUtil.Environment.prod.getValue());
  }

  @Test
  public void getObjectType() {
    assertEquals("batch", ProjectUtil.ObjectTypes.batch.getValue());
    assertEquals("user", ProjectUtil.ObjectTypes.user.getValue());
    assertEquals("organisation", ProjectUtil.ObjectTypes.organisation.getValue());
  }

  @Test
  public void getSourceTest() {
    assertEquals("web", ProjectUtil.Source.WEB.getValue());
    assertEquals("android", ProjectUtil.Source.ANDROID.getValue());
    assertEquals("ios", ProjectUtil.Source.IOS.getValue());
  }

  @Test
  public void getSectionDataType() {
    assertEquals("course", ProjectUtil.SectionDataType.course.getTypeName());
    assertEquals("content", ProjectUtil.SectionDataType.content.getTypeName());
  }

  @Test
  public void getStatusTest() {
    assertEquals(1, ProjectUtil.Status.ACTIVE.getValue());
    assertEquals(0, ProjectUtil.Status.INACTIVE.getValue());
    assertEquals(false, ProjectUtil.ActiveStatus.INACTIVE.getValue());
    assertEquals(true, ProjectUtil.ActiveStatus.ACTIVE.getValue());
    assertEquals("orgimg", ProjectUtil.AzureContainer.orgImage.getName());
    assertEquals("userprofileimg", ProjectUtil.AzureContainer.userProfileImg.getName());
  }

  @Test
  public void serverExceptionTest() {
    try {
      ProjectUtil.createAndThrowServerError();
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.SERVER_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.SERVER_ERROR.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void invalidUserDataExceptionTest() {
    try {
      ProjectUtil.createAndThrowInvalidUserDataException();
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
      assertEquals(ResponseCode.invalidUsrData.getErrorCode(), e.getCode());
    }
  }

  @Test
  public void testValidDateRange() {
    int noOfDays = 7;
    Map<String, String> map = ProjectUtil.getDateRange(noOfDays);
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.add(Calendar.DATE, -noOfDays);
    assertEquals(map.get("startDate"), new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
    cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.add(Calendar.DATE, -1);
    assertEquals(map.get("endDate"), new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
  }

  @Test
  public void testInvalidDateRange() {
    int noOfDays = 14;
    Map<String, String> map = ProjectUtil.getDateRange(noOfDays);
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.add(Calendar.DATE, -noOfDays);
    assertEquals(map.get("startDate"), new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
    cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.add(Calendar.DATE, noOfDays);
    assertNotEquals(map.get("endDate"), new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
  }

  @Test
  public void testZeroDateRange() {
    int noOfDays = 0;
    Map<String, String> map = ProjectUtil.getDateRange(noOfDays);
    assertNull(map.get("startDate"));
    assertNull(map.get("endDate"));
  }

  @Test
  public void testNegativeDateRange() {
    int noOfDays = -100;
    Map<String, String> map = ProjectUtil.getDateRange(noOfDays);
    assertNull(map.get("startDate"));
    assertNull(map.get("endDate"));
  }
}
