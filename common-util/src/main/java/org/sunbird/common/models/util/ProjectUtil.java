/**
 *
 */
package org.sunbird.common.models.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * This class will contains all the common utility methods.
 *
 * @author Manzarul
 */
public class ProjectUtil {

  /**
   * format the date in YYYY-MM-DD hh:mm:ss:SSZ
   */

  private static AtomicInteger atomicInteger = new AtomicInteger();

  public static final long BACKGROUND_ACTOR_WAIT_TIME = 30;
  public static final String YEAR_MONTH_DATE_FORMAT = "yyyy-MM-dd";
  private static Map<String, String> templateMap = new HashMap<>();
  private static final int randomPasswordLength = 9;
  protected static final String FILE_NAME[] = {"cassandratablecolumn.properties",
      "elasticsearch.config.properties", "cassandra.config.properties", "dbconfig.properties",
      "externalresource.properties", "sso.properties", "userencryption.properties",
      "profilecompleteness.properties", "mailTemplates.properties"};
  public static PropertiesCache propertiesCache;
  private static Pattern pattern;
  private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
      + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  public static final String[] excludes =
      new String[] {JsonKey.COMPLETENESS, JsonKey.MISSING_FIELDS, JsonKey.PROFILE_VISIBILITY,
          JsonKey.USERNAME, JsonKey.LOGIN_ID, JsonKey.USER_ID};

  public static final String[] defaultPrivateFields = new String[] {JsonKey.EMAIL, JsonKey.PHONE};

  static {
    pattern = Pattern.compile(EMAIL_PATTERN);
    initializeMailTemplateMap();
    propertiesCache = PropertiesCache.getInstance();
  }

  /**
   * @author Manzarul
   */
  public enum Environment {
    dev(1), qa(2), prod(3);
    int value;

    private Environment(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

  }

  /**
   * @author Amit Kumar
   */
  public enum Status {
    ACTIVE(1), INACTIVE(0);

    private int value;

    Status(int value) {
      this.value = value;
    }

    public int getValue() {
      return this.value;
    }
  }


  public enum BulkProcessStatus {
    NEW(0), IN_PROGRESS(1), INTERRUPT(2), COMPLETED(3), FAILED(9);

    private int value;

    BulkProcessStatus(int value) {
      this.value = value;
    }

    public int getValue() {
      return this.value;
    }
  }

  public enum OrgStatus {
    INACTIVE(0), ACTIVE(1), BLOCKED(2), RETIRED(3);

    private Integer value;

    OrgStatus(Integer value) {
      this.value = value;
    }

    public Integer getValue() {
      return this.value;
    }
  }

  /**
   * @author Amit Kumar
   */
  public enum ProgressStatus {
    NOT_STARTED(0), STARTED(1), COMPLETED(2);

    private int value;

    ProgressStatus(int value) {
      this.value = value;
    }

    public int getValue() {
      return this.value;
    }
  }

  /**
   * @author Amit Kumar
   */
  public enum ActiveStatus {
    ACTIVE(true), INACTIVE(false);

    private boolean value;

    ActiveStatus(boolean value) {
      this.value = value;
    }

    public boolean getValue() {
      return this.value;
    }
  }

  /**
   * @author Amit Kumar
   */
  public enum CourseMgmtStatus {
    DRAFT("draft"), LIVE("live"), RETIRED("retired");

    private String value;

    CourseMgmtStatus(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }
  }

  /**
   * @author Amit Kumar
   */
  public enum Source {
    WEB("web"), ANDROID("android"), IOS("ios");

    private String value;

    Source(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }
  }

  /**
   * @author Amit Kumar
   */
  public enum UserRole {
    PUBLIC("PUBLIC"), CONTENT_CREATOR("CONTENT_CREATOR"), CONTENT_REVIEWER(
        "CONTENT_REVIEWER"), ORG_ADMIN("ORG_ADMIN"), ORG_MEMBER("ORG_MEMBER");

    private String value;

    UserRole(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }
  }

  /**
   * This method will check incoming value is null or empty it will do empty check by doing trim
   * method. in case of null or empty it will return true else false.
   *
   * @param value
   * @return
   */
  public static boolean isStringNullOREmpty(String value) {
    return (value == null || "".equals(value.trim()));
  }

  /**
   * This method will provide formatted date
   *
   * @return
   */
  public static String getFormattedDate() {
    return getDateFormatter().format(new Date());
  }

  /**
   * This method will provide formatted date
   *
   * @return
   */
  public static String formatDate(Date date) {
    if (null != date)
      return getDateFormatter().format(date);
    else
      return null;
  }

  private static void initializeMailTemplateMap() {
    templateMap.put("contentFlagged", "/contentFlaggedMailTemplate.vm");
    templateMap.put("acceptFlag", "/acceptFlagMailTemplate.vm");
    templateMap.put("rejectFlag", "/rejectFlagMailTemplate.vm");
    templateMap.put("publishContent", "/publishContentMailTemplate.vm");
    templateMap.put("rejectContent", "/rejectContentMailTemplate.vm");
    templateMap.put("welcome", "/welcomeMailTemplate.vm");
    templateMap.put("unlistedPublishContent", "/unlistedPublishContentMailTemplate.vm");
  }

  /**
   * Validate email with regular expression
   *
   * @param email
   * @return true valid email, false invalid email
   */
  public static boolean isEmailvalid(final String email) {
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();

  }

  /**
   * This method will generate auth token based on name , source and timestamp
   *
   * @param name String
   * @param source String
   * @return String
   */
  public static String createAuthToken(String name, String source) {
    String data = name + source + System.currentTimeMillis();
    UUID authId = UUID.nameUUIDFromBytes(data.getBytes(StandardCharsets.UTF_8));
    return authId.toString();
  }

  /**
   * This method will generate unique id based on current time stamp and some random value mixed up.
   *
   * @param environmentId int
   * @return String
   */
  public static String getUniqueIdFromTimestamp(int environmentId) {
    Random random = new Random();
    long env = (environmentId + random.nextInt(99999)) / 10000000;
    long uid = System.currentTimeMillis() + random.nextInt(999999);
    uid = uid << 13;
    return env + "" + uid + "" + atomicInteger.getAndIncrement();
  }

  /**
   * This method will generate the unique id .
   *
   * @return
   */
  public synchronized static String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  public enum Method {
    GET, POST, PUT, DELETE, PATCH
  }
  /**
   * Enum to hold the index name for Elastic search.
   * 
   * @author Manzarul
   *
   */
  public enum EsIndex {
    sunbird("searchindex"), sunbirdDataAudit("sunbirddataaudit");
    private String indexName;

    private EsIndex(String name) {
      this.indexName = name;
    }

    public String getIndexName() {
      return indexName;
    }

  }

  /**
   * This enum will hold all the ES type name.
   * 
   * @author Manzarul
   *
   */
  public enum EsType {
    course("course"), content("content"), user("user"), organisation("org"), usercourses(
        "usercourses"), usernotes(
            "usernotes"), history("history"), userprofilevisibility("userprofilevisibility");
    private String typeName;

    private EsType(String name) {
      this.typeName = name;
    }

    public String getTypeName() {
      return typeName;
    }

  }

  /**
   * This enum will hold all the Section data type name.
   * 
   * @author Amit Kumar
   *
   */
  public enum SectionDataType {
    course("course"), content("content");
    private String typeName;

    private SectionDataType(String name) {
      this.typeName = name;
    }

    public String getTypeName() {
      return typeName;
    }

  }


  /**
   * This enum will hold all the Address type name.
   * 
   * @author Amit Kumar
   *
   */
  public enum AddressType {
    permanent("permanent"), current("current"), office("office"), home("home");
    private String typeName;

    private AddressType(String name) {
      this.typeName = name;
    }

    public String getTypeName() {
      return typeName;
    }

  }
  public enum AssessmentResult {
    gradeA("A", "Pass"), gradeB("B", "Pass"), gradeC("C", "Pass"), gradeD("D", "Pass"), gradeE("E",
        "Pass"), gradeF("F", "Fail");
    private String grade;
    private String result;

    private AssessmentResult(String grade, String result) {
      this.grade = grade;
      this.result = result;
    }

    public String getGrade() {
      return grade;
    }


    public String getResult() {
      return result;
    }

  }

  /**
   * This method will calculate the percentage
   * 
   * @param score double
   * @param maxScore double
   * @return double
   */
  public static double calculatePercentage(double score, double maxScore) {
    double percentage = (score * 100) / (maxScore * 1.0);
    return (double) Math.round(percentage);
  }

  /**
   * This method will calculate grade based on percentage marks.
   * 
   * @param percentage double
   * @return AssessmentResult
   */
  public static AssessmentResult calcualteAssessmentResult(double percentage) {
    switch ((int) (Math.round(Float.valueOf(String.valueOf(percentage))) / 10)) {
      case 10:
        return AssessmentResult.gradeA;
      case 9:
        return AssessmentResult.gradeA;
      case 8:
        return AssessmentResult.gradeB;
      case 7:
        return AssessmentResult.gradeC;
      case 6:
        return AssessmentResult.gradeD;
      case 5:
        return AssessmentResult.gradeE;
      default:
        return AssessmentResult.gradeF;
    }
  }

  public static boolean isNull(Object obj) {
    return null == obj ? true : false;
  }

  public static boolean isNotNull(Object obj) {
    return null != obj ? true : false;
  }

  public static String formatMessage(String exceptionMsg, Object... fieldValue) {
    return MessageFormat.format(exceptionMsg, fieldValue);
  }

  /**
   * This method will make some requested key value as lower case.
   * 
   * @param reqObj Request
   */
  public static void updateMapSomeValueTOLowerCase(Request reqObj) {
    if (reqObj.getRequest().get(JsonKey.SOURCE) != null) {
      reqObj.getRequest().put(JsonKey.SOURCE,
          ((String) reqObj.getRequest().get(JsonKey.SOURCE)).toLowerCase());
    }
    if (reqObj.getRequest().get(JsonKey.EXTERNAL_ID) != null) {
      reqObj.getRequest().put(JsonKey.EXTERNAL_ID,
          ((String) reqObj.getRequest().get(JsonKey.EXTERNAL_ID)).toLowerCase());
    }
    if (reqObj.getRequest().get(JsonKey.USERNAME) != null) {
      reqObj.getRequest().put(JsonKey.USERNAME,
          ((String) reqObj.getRequest().get(JsonKey.USERNAME)).toLowerCase());
    }
    if (reqObj.getRequest().get(JsonKey.USER_NAME) != null) {
      reqObj.getRequest().put(JsonKey.USER_NAME,
          ((String) reqObj.getRequest().get(JsonKey.USER_NAME)).toLowerCase());
    }
    if (reqObj.getRequest().get(JsonKey.PROVIDER) != null) {
      reqObj.getRequest().put(JsonKey.PROVIDER,
          ((String) reqObj.getRequest().get(JsonKey.PROVIDER)).toLowerCase());
    }
    if (reqObj.getRequest().get(JsonKey.LOGIN_ID) != null) {
      reqObj.getRequest().put(JsonKey.LOGIN_ID,
          ((String) reqObj.getRequest().get(JsonKey.LOGIN_ID)).toLowerCase());
    }

  }

  public static SimpleDateFormat getDateFormatter() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSZ");
    simpleDateFormat.setLenient(false);
    return simpleDateFormat;
  }

  /**
   *
   * @author Manzarul
   *
   */
  public enum EnrolmentType {
    open("open"), inviteOnly("invite-only");
    private String val;

    EnrolmentType(String val) {
      this.val = val;
    }

    public String getVal() {
      return val;
    }

  }

  /**
   * 
   * @author Manzarul
   *
   */
  public enum AzureContainer {
    userProfileImg("userprofileimg"), orgImage("orgimg");
    private String name;

    private AzureContainer(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }


  }

  public static VelocityContext getContext(Map<String, Object> map) {
    propertiesCache = PropertiesCache.getInstance();
    VelocityContext context = new VelocityContext();
    if (!ProjectUtil.isStringNullOREmpty((String) map.get(JsonKey.ACTION_URL))) {
      context.put(JsonKey.ACTION_URL, getValue(map, JsonKey.ACTION_URL));
    }
    if (!ProjectUtil.isStringNullOREmpty((String) map.get(JsonKey.NAME))) {
      context.put(JsonKey.NAME, getValue(map, JsonKey.NAME));
    }
    context.put(JsonKey.BODY, getValue(map, JsonKey.BODY));
    context.put(JsonKey.FROM_EMAIL, getFromEmail());
    context.put(JsonKey.ORG_NAME, getValue(map, JsonKey.ORG_NAME));
    String logoUrl = getSunbirdLogoUrl();
    if (!ProjectUtil.isStringNullOREmpty(logoUrl)) {
      context.put(JsonKey.ORG_IMAGE_URL, logoUrl);
    }
    context.put(JsonKey.ACTION_NAME, getValue(map, JsonKey.ACTION_NAME));
    context.put(JsonKey.USERNAME, getValue(map, JsonKey.USERNAME));
    context.put(JsonKey.TEMPORARY_PASSWORD, getValue(map, JsonKey.TEMPORARY_PASSWORD));


    for (Map.Entry<String, Object> entry : map.entrySet()) {
      context.put(entry.getKey(), entry.getValue());
    }
    return context;
  }

  private static String getSunbirdLogoUrl() {
    if (!ProjectUtil.isStringNullOREmpty(System.getenv(JsonKey.SUNBIRD_ENV_LOGO_URL))) {
      return System.getenv(JsonKey.SUNBIRD_ENV_LOGO_URL);
    } else if (!ProjectUtil
        .isStringNullOREmpty(propertiesCache.getProperty(JsonKey.SUNBIRD_ENV_LOGO_URL))) {
      return propertiesCache.getProperty(JsonKey.SUNBIRD_ENV_LOGO_URL);
    }
    return "";
  }

  private static String getFromEmail() {
    if (!ProjectUtil.isStringNullOREmpty(System.getenv(JsonKey.EMAIL_SERVER_FROM))) {
      return System.getenv(JsonKey.EMAIL_SERVER_FROM);
    } else if (!ProjectUtil
        .isStringNullOREmpty(propertiesCache.getProperty(JsonKey.EMAIL_SERVER_FROM))) {
      return propertiesCache.getProperty(JsonKey.EMAIL_SERVER_FROM);
    }
    return "";
  }

  private static Object getValue(Map<String, Object> map, String key) {
    Object value = map.get(key);
    map.remove(key);
    return value;
  }

  public static String getTemplate(Map<String, Object> map) {
    if (ProjectUtil.isStringNullOREmpty(templateMap.get(map.get(JsonKey.EMAIL_TEMPLATE_TYPE)))) {
      return "/emailtemplate.vm";
    }
    return templateMap.get(map.get(JsonKey.EMAIL_TEMPLATE_TYPE));
  }

  /**
   * @author Arvind
   */
  public enum ReportTrackingStatus {
    NEW(0), GENERATING_DATA(1), UPLOADING_FILE(2), UPLOADING_FILE_SUCCESS(3), SENDING_MAIL(
        4), SENDING_MAIL_SUCCESS(5), FAILED(9);

    private int value;

    ReportTrackingStatus(int value) {
      this.value = value;
    }

    public int getValue() {
      return this.value;
    }
  }

  /**
   * 
   * @param serviceName
   * @param isError
   * @param e
   * @return
   */
  public static Map<String, Object> createCheckResponse(String serviceName, boolean isError,
      Exception e) {
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put(JsonKey.NAME, serviceName);
    if (!isError) {
      responseMap.put(JsonKey.Healthy, true);
      responseMap.put(JsonKey.ERROR, "");
      responseMap.put(JsonKey.ERRORMSG, "");
    } else {
      responseMap.put(JsonKey.Healthy, false);
      if (e != null && e instanceof ProjectCommonException) {
        ProjectCommonException commonException = (ProjectCommonException) e;
        responseMap.put(JsonKey.ERROR, commonException.getResponseCode());
        responseMap.put(JsonKey.ERRORMSG, commonException.getMessage());
      } else {
        responseMap.put(JsonKey.ERROR, e != null ? e.getMessage() : "connection Error");
        responseMap.put(JsonKey.ERRORMSG, e != null ? e.getMessage() : "connection Error");
      }
    }
    return responseMap;
  }


  /**
   * This method will make EkStep api call register the tag.
   * 
   * @param tagId String unique tag id.
   * @param body String requested body
   * @param header Map<String,String>
   * @return String
   * @throws IOException
   */
  public static String registertag(String tagId, String body, Map<String, String> header)
      throws IOException {
    String tagStatus = "";
    try {
      ProjectLogger.log("start call for registering the tag ==" + tagId);
      String ekStepBaseUrl = System.getenv(JsonKey.EKSTEP_BASE_URL);
      if (ProjectUtil.isStringNullOREmpty(ekStepBaseUrl)) {
        ekStepBaseUrl = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_BASE_URL);
      }
      tagStatus = HttpUtil.sendPostRequest(ekStepBaseUrl
          + PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_TAG_API_URL) + "/" + tagId,
          body, header);
      ProjectLogger
          .log("end call for tag registration id and status  ==" + tagId + " " + tagStatus);
    } catch (Exception e) {
      throw e;
    }
    return tagStatus;
  }

  public enum ObjectTypes {
    user("user"), organisation("organisation"), batch("batch");

    private String value;

    private ObjectTypes(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }



  }

  public static String generateRandomPassword() {
    String SALTCHARS = "abcdef12345ghijklACDEFGHmnopqrs67IJKLMNOP890tuvQRSTUwxyzVWXYZ";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < randomPasswordLength) { // length of the random string.
      int index = (int) (rnd.nextFloat() * SALTCHARS.length());
      salt.append(SALTCHARS.charAt(index));
    }
    String saltStr = salt.toString();
    return saltStr;
  }

  /**
   * This method will do the phone number validation check
   * 
   * @param phoneNo String
   * @return boolean
   */
  public static boolean validatePhoneNumber(String phone) {
    String phoneNo = "";
    phoneNo = phone.replace("+", "");
    if (phoneNo.matches("\\d{10}"))
      return true;
    else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}"))
      return true;
    else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))
      return true;
    else
      return (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}"));
  }

  public static Map<String, String> getEkstepHeader() {
    Map<String, String> headerMap = new HashMap<>();
    String header = System.getenv(JsonKey.EKSTEP_AUTHORIZATION);
    if (ProjectUtil.isStringNullOREmpty(header)) {
      header = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_AUTHORIZATION);
    } else {
      header = JsonKey.BEARER + header;
    }
    headerMap.put(JsonKey.AUTHORIZATION, header);
    headerMap.put("Content-Type", "application/json");
    return headerMap;
  }

  public static boolean validatePhone(String phone, String countryCode) {
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    String contryCode = countryCode;
    String phon = "";
    if (!ProjectUtil.isStringNullOREmpty(countryCode) && (countryCode.charAt(0) != '+')) {
      contryCode = "+" + countryCode;
    }
    try {
      if (isStringNullOREmpty(countryCode)) {
        contryCode = PropertiesCache.getInstance().getProperty("sunbird_default_country_code");
      }
      phon = contryCode + "-" + phone;
      PhoneNumber numberProto = phoneUtil.parse(phon, "");
      // phoneUtil.isValidNumber(number)
      ProjectLogger.log("Number is of region - " + numberProto.getCountryCode() + " "
          + phoneUtil.getRegionCodeForNumber(numberProto));
      ProjectLogger.log(
          "Is the input number valid - " + (phoneUtil.isValidNumber(numberProto) ? "Yes" : "No"));
      return phoneUtil.isValidNumber(numberProto);
    } catch (NumberParseException e) {
      ProjectLogger.log("Exception occurred while validating phone number : ", e);
      ProjectLogger.log(phon + "this phone no. is not a valid one.");
    }
    return false;
  }

  public static boolean validateCountryCode(String countryCode) {
    String pattern = "^(?:[+] ?){0,1}(?:[0-9] ?){1,3}";
    try {
      Pattern patt = Pattern.compile(pattern);
      Matcher matcher = patt.matcher(countryCode);
      return matcher.matches();
    } catch (RuntimeException e) {
      return false;
    }
  }

  public static String getSMSBody(String userName, String webUrl, String instanceName) {
    try {
      Properties props = new Properties();
      props.put("resource.loader", "class");
      props.put("class.resource.loader.class",
          "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

      VelocityEngine ve = new VelocityEngine();
      ve.init(props);

      Map<String, String> params = new HashMap<>();
      params.put("userName", isStringNullOREmpty(userName) ? "user_name" : userName);
      params.put("webUrl", isStringNullOREmpty(webUrl) ? "web_url" : webUrl);
      params.put("instanceName",
          isStringNullOREmpty(instanceName) ? "instance_name" : instanceName);
      Template t = ve.getTemplate("/welcomeSmsTemplate.vm");
      VelocityContext context = new VelocityContext(params);
      StringWriter writer = new StringWriter();
      t.merge(context, writer);
      return writer.toString();
    } catch (Exception ex) {
      ProjectLogger.log("Exception occurred while formating and sending SMS " + ex);
    }
    return "";
  }

  public static boolean isDateValidFormat(String format, String value) {
    Date date = null;
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      date = sdf.parse(value);
      if (!value.equals(sdf.format(date))) {
        date = null;
      }
    } catch (ParseException ex) {
      ProjectLogger.log(ex.getMessage(), ex);
    }
    return date != null;
  }

  /**
   * This method will create a new ProjectCommonException of type server Error and throws it.
   */
  public static void createAndThrowServerError() {
    throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
        ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
  }

  /**
   * This method will create ProjectCommonException of type invalidUserDate exception and throws it.
   */
  public static void createAndThrowInvalidUserDataException() {
    throw new ProjectCommonException(ResponseCode.invalidUsrData.getErrorCode(),
        ResponseCode.invalidUsrData.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
  }
  
  public static void main(String[] args) {
    System.out.println(validatePhone("8297211569", "+91"));
  }
}
