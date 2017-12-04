/**
 * 
 */
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

/**
 * @author Manzarul
 *
 */
public class CommonRequestValidatorTest {
  
  @Test
  public void enrollCourseValidationSuccess() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.COURSE_ID, "do_1233343");
    request.setRequest(requestObj);
    try {
    RequestValidator.validateEnrollCourse(request);
     response = true;
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals(true, response);
  }

  @Test
  public void enrollCourseValidationWithOutCourseId() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    request.setRequest(requestObj);
    request.setRequest(requestObj);
    try {
    RequestValidator.validateEnrollCourse(request);
  } catch (ProjectCommonException e) {
    assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
    assertEquals(ResponseCode.courseIdRequiredError.getErrorCode(), e.getCode());
  }
  }
  
  @Test
  public void validateUpdateContentSuccess() {
    Request request = new Request();
    boolean response = false;
    List<Map<String,Object>> listOfMap = new ArrayList<>();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.CONTENT_ID, "do_1233343");
    requestObj.put(JsonKey.STATUS, "Completed");
    listOfMap.add(requestObj);
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put(JsonKey.CONTENTS, listOfMap);
    request.setRequest(innerMap);
    try {
    RequestValidator.validateUpdateContent(request);
    response = true;
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals(true, response);
  }
 
  @Test
  public void validateUpdateContentWithOutContentId() {
    Request request = new Request();
    List<Map<String, Object>> listOfMap = new ArrayList<>();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.STATUS, "Completed");
    listOfMap.add(requestObj);
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put(JsonKey.CONTENTS, listOfMap);
    request.setRequest(innerMap);
    try {
      RequestValidator.validateUpdateContent(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.contentIdRequiredError.getErrorCode(),
          e.getCode());
    }
  }
 
  @Test
  public void validateUpdateContentWithOutStatus() {
    Request request = new Request();
    List<Map<String, Object>> listOfMap = new ArrayList<>();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.CONTENT_ID, "do_1233343");
    listOfMap.add(requestObj);
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put(JsonKey.CONTENTS, listOfMap);
    request.setRequest(innerMap);
    try {
      RequestValidator.validateUpdateContent(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.contentStatusRequired.getErrorCode(),
          e.getCode());
    }
  }
 
  @Test
  public void validateUpdateContentWithWrongType() {
    Request request = new Request();
    List<Map<String, Object>> listOfMap = new ArrayList<>();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.CONTENT_ID, "do_1233343");
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put(JsonKey.CONTENTS, listOfMap);
    request.setRequest(innerMap);
    try {
      RequestValidator.validateUpdateContent(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.contentIdRequiredError.getErrorCode(),
          e.getCode());
    }
  }
  
}
