package org.sunbird.common.models;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;

/** Unit test for simple App. */
public class AppTest {
  private static final String data =
      "{\"request\": { \"search\": {\"contentType\": [\"Story\"] }}}";
  private static Map<String, String> headers = new HashMap<String, String>();

  @BeforeClass
  public static void init() {
    headers.put("content-type", "application/json");
    headers.put("accept", "application/json");
    headers.put("user-id", "mahesh");
    String header = System.getenv(JsonKey.EKSTEP_AUTHORIZATION);
    if (StringUtils.isBlank(header)) {
      header = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_AUTHORIZATION);
    }
    headers.put("authorization", "Bearer " + header);
  }

  @Test
  public void testGetResourceMethod() throws Exception {
    String ekStepBaseUrl = System.getenv(JsonKey.EKSTEP_BASE_URL);
    if (StringUtils.isBlank(ekStepBaseUrl)) {
      ekStepBaseUrl = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_BASE_URL);
    }
    String response = HttpUtil.sendGetRequest(ekStepBaseUrl + "/search/health", headers);
    Assert.assertNotNull(response);
  }

  @Test
  @Ignore
  public void testPostResourceMethod() throws Exception {
    String ekStepBaseUrl = System.getenv(JsonKey.EKSTEP_BASE_URL);
    if (StringUtils.isBlank(ekStepBaseUrl)) {
      ekStepBaseUrl = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_BASE_URL);
    }
    String response = HttpUtil.sendPostRequest(ekStepBaseUrl + "/content/v3/list", data, headers);
    Assert.assertNotNull(response);
  }

  @Test()
  public void testPostFailureResourceMethod() {
    // passing wrong url
    String ekStepBaseUrl = System.getenv(JsonKey.EKSTEP_BASE_URL);
    if (StringUtils.isBlank(ekStepBaseUrl)) {
      ekStepBaseUrl = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_BASE_URL);
    }
    String response = null;
    try {
      Map<String, String> data = new HashMap<>();
      data.put("search", "\"contentType\": [\"Story\"]");
      response = HttpUtil.sendPostRequest(ekStepBaseUrl + "/content/wrong/v3/list", data, headers);
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    Assert.assertNull(response);
  }

  @Test()
  public void testPatchMatch() {
    String response = null;
    try {
      String ekStepBaseUrl = System.getenv(JsonKey.EKSTEP_BASE_URL);
      if (StringUtils.isBlank(ekStepBaseUrl)) {
        ekStepBaseUrl = PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_BASE_URL);
      }
      response =
          HttpUtil.sendPatchRequest(
              ekStepBaseUrl
                  + PropertiesCache.getInstance().getProperty(JsonKey.EKSTEP_TAG_API_URL)
                  + "/"
                  + "testt123",
              "{}",
              headers);
    } catch (Exception e) {
    }
    Assert.assertNotNull(response);
  }

  @Test
  public void testEmailValidation() {
    boolean bool = ProjectUtil.isEmailvalid("amit.kumar@tarento.com");
    assertTrue(bool);
  }

  @Test
  public void testEmailFailureValidation() {
    boolean bool = ProjectUtil.isEmailvalid("amit.kumartarento.com");
    Assert.assertFalse(bool);
  }

  private Map<String, List<Map<String, Object>>> createEvaluateAssessmentRequest() {

    Map<String, Object> assmntMap1 = new HashMap<>();
    assmntMap1.put(JsonKey.ASSESSMENT_SCORE, "4");
    assmntMap1.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
    assmntMap1.put(JsonKey.ASSESSMENT_ITEM_ID, "1");
    assmntMap1.put(JsonKey.COURSE_ID, "CSR1");
    assmntMap1.put(JsonKey.CONTENT_ID, "CON1");
    assmntMap1.put(JsonKey.USER_ID, "USR1");

    Map<String, Object> assmntMap2 = new HashMap<>();
    assmntMap2.put(JsonKey.ASSESSMENT_SCORE, "8");
    assmntMap2.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
    assmntMap2.put(JsonKey.ASSESSMENT_ITEM_ID, "2");
    assmntMap2.put(JsonKey.COURSE_ID, "CSR1");
    assmntMap2.put(JsonKey.CONTENT_ID, "CON1");
    assmntMap2.put(JsonKey.USER_ID, "USR1");

    Map<String, Object> assmntMap3 = new HashMap<>();
    assmntMap3.put(JsonKey.ASSESSMENT_SCORE, "8");
    assmntMap3.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
    assmntMap3.put(JsonKey.ASSESSMENT_ITEM_ID, "3");
    assmntMap3.put(JsonKey.COURSE_ID, "CSR1");
    assmntMap3.put(JsonKey.CONTENT_ID, "CON1");
    assmntMap3.put(JsonKey.USER_ID, "USR1");

    Map<String, Object> assmntMap4 = new HashMap<>();
    assmntMap4.put(JsonKey.ASSESSMENT_SCORE, "5");
    assmntMap4.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
    assmntMap4.put(JsonKey.ASSESSMENT_ITEM_ID, "4");
    assmntMap4.put(JsonKey.COURSE_ID, "CSR1");
    assmntMap4.put(JsonKey.CONTENT_ID, "CON1");
    assmntMap4.put(JsonKey.USER_ID, "USR1");

    Map<String, Object> assmntMap5 = new HashMap<>();
    assmntMap5.put(JsonKey.ASSESSMENT_SCORE, "5");
    assmntMap5.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
    assmntMap5.put(JsonKey.ASSESSMENT_ITEM_ID, "5");
    assmntMap5.put(JsonKey.COURSE_ID, "CSR1");
    assmntMap5.put(JsonKey.CONTENT_ID, "CON1");
    assmntMap5.put(JsonKey.USER_ID, "USR1");

    Map<String, Object> assmntMap6 = new HashMap<>();
    assmntMap6.put(JsonKey.ASSESSMENT_SCORE, "6");
    assmntMap6.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
    assmntMap6.put(JsonKey.ASSESSMENT_ITEM_ID, "6");
    assmntMap6.put(JsonKey.COURSE_ID, "CSR1");
    assmntMap6.put(JsonKey.CONTENT_ID, "CON1");
    assmntMap6.put(JsonKey.USER_ID, "USR1");

    Map<String, Object> assmntMap7 = new HashMap<>();
    assmntMap7.put(JsonKey.ASSESSMENT_SCORE, "8");
    assmntMap7.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
    assmntMap7.put(JsonKey.ASSESSMENT_ITEM_ID, "7");
    assmntMap7.put(JsonKey.COURSE_ID, "CSR1");
    assmntMap7.put(JsonKey.CONTENT_ID, "CON1");
    assmntMap7.put(JsonKey.USER_ID, "USR1");

    Map<String, Object> assmntMap8 = new HashMap<>();
    assmntMap8.put(JsonKey.ASSESSMENT_SCORE, "9");
    assmntMap8.put(JsonKey.ASSESSMENT_MAX_SCORE, "10");
    assmntMap8.put(JsonKey.ASSESSMENT_ITEM_ID, "8");
    assmntMap8.put(JsonKey.COURSE_ID, "CSR1");
    assmntMap8.put(JsonKey.CONTENT_ID, "CON1");
    assmntMap8.put(JsonKey.USER_ID, "USR1");

    Map<String, List<Map<String, Object>>> map = new HashMap<>();

    List<Map<String, Object>> list = new ArrayList<>();
    list.add(assmntMap1);
    list.add(assmntMap2);
    list.add(assmntMap3);
    list.add(assmntMap4);
    list.add(assmntMap5);
    list.add(assmntMap6);
    list.add(assmntMap7);
    list.add(assmntMap8);

    map.put("USR1", list);

    return map;
  }
}
