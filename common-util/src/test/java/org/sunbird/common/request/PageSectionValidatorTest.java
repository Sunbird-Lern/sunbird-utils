/**
 * 
 */
package org.sunbird.common.request;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
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
public class PageSectionValidatorTest {

  @Test
  public void validategetPageData() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SOURCE, "web");
    requestObj.put(JsonKey.PAGE_NAME, "resource");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateGetPageData(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals("success", (String)requestObj.get("ext"));
  }
  
  @Test
  public void validategetPageDataWithOutSource() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PAGE_NAME, "resource");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateGetPageData(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.sourceRequired.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
  @Test
  public void validategetPageDataWithSourceWithoutPageName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SOURCE, "web");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateGetPageData(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.pageNameRequired.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
  
  @Test
  public void validateCreateSectionData() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SECTION_NAME, "latest resource");
    requestObj.put(JsonKey.SECTION_DATA_TYPE, "{\"request\": { \"search\": {\"contentType\": [\"Story\"] }}}");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateCreateSection(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals("success", (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateCreateSectionDataWithOutName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SECTION_DATA_TYPE, "{\"request\": { \"search\": {\"contentType\": [\"Story\"] }}}");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateCreateSection(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.sectionNameRequired.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
 
  
  @Test
  public void validateCreateSectionDataWithOutSectionData() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SECTION_NAME, "latest resource");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateCreateSection(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.sectionDataTypeRequired.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateUpdateSectionData() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SECTION_NAME, "latest resource");
    requestObj.put(JsonKey.SECTION_DATA_TYPE, "{\"request\": { \"search\": {\"contentType\": [\"Story\"] }}}");
    requestObj.put(JsonKey.ID, "some section id");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdateSection(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals("success", (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateUpdateSectionWithOutId() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SECTION_NAME, "latest resource");
    requestObj.put(JsonKey.SECTION_DATA_TYPE, "{\"request\": { \"search\": {\"contentType\": [\"Story\"] }}}");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdateSection(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.sectionIdRequired.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateUpdateSectionWithOutSectionName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SECTION_DATA_TYPE, "{\"request\": { \"search\": {\"contentType\": [\"Story\"] }}}");
    requestObj.put(JsonKey.ID, "some section id");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdateSection(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.sectionNameRequired.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
  
  @Test
  public void validateUpdateSectionWithOutSectionData() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.SECTION_NAME, "latest resource");
    requestObj.put(JsonKey.ID, "some section id");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdateSection(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.sectionDataTypeRequired.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateCreatePageSuccess() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PAGE_NAME, "some page name that need to be build");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateCreatePage(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals("success", (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateCreatePageWithOutPageName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateCreatePage(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.pageNameRequired.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateUpdatePage() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PAGE_NAME, "some page name that need to be build");
    requestObj.put(JsonKey.ID, "identifier of the page");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdatepage(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals("success", (String)requestObj.get("ext"));
  }
  
  
  @Test
  public void validateUpdatePageWithOutPageName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ID, "identifier of the page");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdatepage(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.pageNameRequired.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateUpdatePageWithOutPageId() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PAGE_NAME, "some page name that need to be build");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdatepage(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.pageIdRequired.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
}
