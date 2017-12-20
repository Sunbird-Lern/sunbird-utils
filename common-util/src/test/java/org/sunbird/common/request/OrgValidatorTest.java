/**
 * 
 */
package org.sunbird.common.request;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
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
public class OrgValidatorTest {

 @Test
  public void validateCreateOrgSuccess() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORG_NAME, "test");
    requestObj.put(JsonKey.IS_ROOT_ORG, true);
    requestObj.put(JsonKey.CHANNEL, "tpp");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateCreateOrg(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals("success", (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateCreateOrgWithOutName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.IS_ROOT_ORG, true);
    requestObj.put(JsonKey.CHANNEL, "tpp");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateCreateOrg(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.organisationNameRequired.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
     assertEquals(null, (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateCreateOrgWithRootOrgTrueAndWithOutChannel() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORG_NAME, "test");
    requestObj.put(JsonKey.IS_ROOT_ORG, true);
    requestObj.put(JsonKey.CHANNEL, "");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateCreateOrg(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.channelIdRequiredForRootOrg.getErrorCode(), e.getCode());
    	assertEquals( ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
     assertEquals(null, (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateOrgWithOrgId() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORGANISATION_ID, "test-120");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateOrg(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
     assertEquals("success", (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateOrgWithExtAndProvider() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.PROVIDER, "tpt");
    requestObj.put(JsonKey.EXTERNAL_ID, "2344");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateOrg(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
     assertEquals("success", (String)requestObj.get("ext"));
  }
  
  @Test
	public void validateOrgWithExtAndWithOutProvider() {
		Request request = new Request();
		Map<String, Object> requestObj = new HashMap<>();
		requestObj.put(JsonKey.EXTERNAL_ID, "2344");
		request.setRequest(requestObj);
		try {
			// this method will either throw projectCommonException or it return
			// void
			RequestValidator.validateOrg(request);
			requestObj.put("ext", "success");
		} catch (ProjectCommonException e) {
			assertEquals(ResponseCode.sourceAndExternalIdValidationError.getErrorCode(), e.getCode());
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
		}
		assertEquals(null, (String) requestObj.get("ext"));
	}
  
  @Test
 	public void validateOrgWithProviderAndWithOutExt() {
 		Request request = new Request();
 		Map<String, Object> requestObj = new HashMap<>();
 		requestObj.put(JsonKey.PROVIDER, "tpt");
 		request.setRequest(requestObj);
 		try {
 			// this method will either throw projectCommonException or it return
 			// void
 			RequestValidator.validateOrg(request);
 			requestObj.put("ext", "success");
 		} catch (ProjectCommonException e) {
 			assertEquals(ResponseCode.sourceAndExternalIdValidationError.getErrorCode(), e.getCode());
 			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
 		}
 		assertEquals(null, (String) requestObj.get("ext"));
 	}
  
  @Test
  public void validateUpdateCreateOrgSuccess() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORG_NAME, "test");
    requestObj.put(JsonKey.ORGANISATION_ID, "test12344");
    requestObj.put(JsonKey.IS_ROOT_ORG, true);
    requestObj.put(JsonKey.CHANNEL, "tpp");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdateOrg(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals("success", (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateUpdateOrgFailure() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORGANISATION_ID, "test2344");
    requestObj.put(JsonKey.ROOT_ORG_ID, "");
    requestObj.put(JsonKey.ORG_NAME, "test");
    requestObj.put(JsonKey.IS_ROOT_ORG, true);
    requestObj.put(JsonKey.CHANNEL, "tpp");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdateOrg(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.invalidRootOrganisationId.getErrorCode(), e.getCode());
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateUpdateOrgWithStatus() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.STATUS, "true");
    requestObj.put(JsonKey.ORG_NAME, "test");
    requestObj.put(JsonKey.IS_ROOT_ORG, true);
    requestObj.put(JsonKey.CHANNEL, "tpp");
    requestObj.put(JsonKey.ORGANISATION_ID, "test123444");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdateOrg(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.invalidRequestData.getErrorCode(), e.getCode());
			assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
 
  @Test
  public void validateUpdateOrgWithEmptyChannel() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORG_NAME, "test");
    requestObj.put(JsonKey.IS_ROOT_ORG, true);
    requestObj.put(JsonKey.CHANNEL, "");
    requestObj.put(JsonKey.ORGANISATION_ID, "test123444");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdateOrg(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.channelIdRequiredForRootOrg.getErrorCode(), e.getCode());
	    assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateUpdateOrgStatus() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORG_NAME, "test");
    requestObj.put(JsonKey.STATUS, new BigInteger("2"));
    requestObj.put(JsonKey.ORGANISATION_ID, "test-12334");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdateOrgStatus(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	Assert.assertNull(e);
	}
    assertEquals("success", (String)requestObj.get("ext"));
  }
  
  @Test
  public void validateUpdateOrgStatusWithInvalidStatus() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORG_NAME, "test");
    requestObj.put(JsonKey.STATUS, "true");
    requestObj.put(JsonKey.ORGANISATION_ID, "test-12334");
    request.setRequest(requestObj);
    try {
    //this method will either throw projectCommonException or it return void	
       RequestValidator.validateUpdateOrgStatus(request);
       requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
    	assertEquals(ResponseCode.invalidRequestData.getErrorCode(), e.getCode());
	    assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
	}
    assertEquals(null, (String)requestObj.get("ext"));
  }
  
}
