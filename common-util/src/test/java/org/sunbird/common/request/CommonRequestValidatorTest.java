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
  public void validateUpdateContentWithContentIdAsNull() {
    Request request = new Request();
    boolean response = false;
    List<Map<String,Object>> listOfMap = new ArrayList<>();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.CONTENT_ID, null);
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
  
  @Test
  public void validateRegisterClientTest(){
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.CLIENT_NAME, "");
    request.setRequest(requestObj);
    try{
      RequestValidator.validateRegisterClient(request);
    }catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.invalidClientName.getErrorCode(),
          e.getCode());
    }
  }
  
  @Test
  public void validateRegisterClientSuccessTest(){
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.CLIENT_NAME, "1234");
    request.setRequest(requestObj);
    try{
      RequestValidator.validateRegisterClient(request);
    }catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.invalidClientName.getErrorCode(),
          e.getCode());
    }
  }
  
  @Test
  public void validateUpdateClientKeyTest(){
    try{
      RequestValidator.validateUpdateClientKey("1234", "");
    }catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.invalidRequestData.getErrorCode(),
          e.getCode());
    }
  }
  
  @Test
  public void validateUpdateClientKeyWithSuccessTest(){
    try{
      RequestValidator.validateUpdateClientKey("1234", "test123");
    }catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.invalidRequestData.getErrorCode(),
          e.getCode());
    }
  }
  
  @Test
  public void validateClientIdTest(){
    try{
      RequestValidator.validateClientId("");
    }catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.invalidClientId.getErrorCode(),
          e.getCode());
    }
  }
  
  
  @Test
  public void validateFileUploadTest(){
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.CONTAINER, "");
    request.setRequest(requestObj);
    try{
      RequestValidator.validateFileUpload(request);
    }catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.storageContainerNameMandatory.getErrorCode(),
          e.getCode());
    }
  }
  
  @Test
  public void validateSendMailRecipientUserTest() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SUBJECT, "test123");
    requestObj.put(JsonKey.BODY, "test");
    List<String> data = new ArrayList<>();
    data.add("test123@gmail.com");
    requestObj.put(JsonKey.RECIPIENT_EMAILS, data);
    requestObj.put(JsonKey.RECIPIENT_USERIDS, new ArrayList<>());
    request.setRequest(requestObj);
    try{
      RequestValidator.validateSendMail(request);
    }catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.recipientAddressError.getErrorCode(),
          e.getCode());
    }
  }
  
  @Test
  public void validateSendMailRecipientEmailTest() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SUBJECT, "test123");
    requestObj.put(JsonKey.BODY, "test");
    requestObj.put(JsonKey.RECIPIENT_EMAILS, null);
    request.setRequest(requestObj);
    try{
      RequestValidator.validateSendMail(request);
    }catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.recipientAddressError.getErrorCode(),
          e.getCode());
    }
  }
  
  @Test
  public void validateSendMailBodyTest() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SUBJECT, "test123");
    requestObj.put(JsonKey.BODY, "");
    request.setRequest(requestObj);
    try{
      RequestValidator.validateSendMail(request);
    }catch (ProjectCommonException e) {
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
          e.getResponseCode());
      assertEquals(ResponseCode.emailBodyError.getErrorCode(),
          e.getCode());
    }
  }
    
    @Test
    public void validateSendMailSubjectTest() {
      Request request = new Request();
      Map<String, Object> requestObj = new HashMap<>();
      requestObj.put(JsonKey.SUBJECT, "");
      request.setRequest(requestObj);
      try{
        RequestValidator.validateSendMail(request);
      }catch (ProjectCommonException e) {
        assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(),
            e.getResponseCode());
        assertEquals(ResponseCode.emailSubjectError.getErrorCode(),
            e.getCode());
      }
  }
}
