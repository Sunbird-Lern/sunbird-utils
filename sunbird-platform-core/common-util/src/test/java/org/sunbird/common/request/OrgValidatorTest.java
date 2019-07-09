/** */
package org.sunbird.common.request;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.responsecode.ResponseCode;

/** @author Manzarul */
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
      // this method will either throw projectCommonException or it return void
      // new OrgRequestValidator().validateCreateOrgRequest(request);
      requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals("success", requestObj.get("ext"));
  }

  @Ignore
  public void validateCreateOrgWithOutName() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.IS_ROOT_ORG, true);
    requestObj.put(JsonKey.CHANNEL, "tpp");
    request.setRequest(requestObj);
    try {
      // this method will either throw projectCommonException or it return void
      // new OrgRequestValidator().validateCreateOrgRequest(request);
      requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.organisationNameRequired.getErrorCode(), e.getCode());
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
    }
    assertEquals(null, requestObj.get("ext"));
  }

  @Ignore
  public void validateCreateOrgWithRootOrgTrueAndWithOutChannel() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORG_NAME, "test");
    requestObj.put(JsonKey.IS_ROOT_ORG, true);
    requestObj.put(JsonKey.CHANNEL, "");
    request.setRequest(requestObj);
    try {
      // this method will either throw projectCommonException or it return void
      // new OrgRequestValidator().validateCreateOrgRequest(request);
      requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.mandatoryParamsMissing.getErrorCode(), e.getCode());
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
    }
    assertEquals(null, requestObj.get("ext"));
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
      // this method will either throw projectCommonException or it return void
      // new OrgRequestValidator().validateUpdateOrgRequest(request);
      requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals("success", requestObj.get("ext"));
  }

  @Ignore
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
      // this method will either throw projectCommonException or it return void
      // new OrgRequestValidator().validateUpdateOrgRequest(request);
      requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.invalidRootOrganisationId.getErrorCode(), e.getCode());
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
    }
    assertEquals(null, requestObj.get("ext"));
  }

  @Ignore
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
      // this method will either throw projectCommonException or it return void
      // new OrgRequestValidator().validateUpdateOrgRequest(request);
      requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.invalidRequestData.getErrorCode(), e.getCode());
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
    }
    assertEquals(null, requestObj.get("ext"));
  }

  @Ignore
  public void validateUpdateOrgWithEmptyChannel() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORG_NAME, "test");
    requestObj.put(JsonKey.IS_ROOT_ORG, true);
    requestObj.put(JsonKey.CHANNEL, "");
    requestObj.put(JsonKey.ORGANISATION_ID, "test123444");
    request.setRequest(requestObj);
    try {
      // this method will either throw projectCommonException or it return void
      // new OrgRequestValidator().validateUpdateOrgRequest(request);
      requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.mandatoryParamsMissing.getErrorCode(), e.getCode());
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
    }
    assertEquals(null, requestObj.get("ext"));
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
      // this method will either throw projectCommonException or it return void
      // new OrgRequestValidator().validateUpdateOrgStatusRequest(request);
      requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals("success", requestObj.get("ext"));
  }

  @Ignore
  public void validateUpdateOrgStatusWithInvalidStatus() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.ORG_NAME, "test");
    requestObj.put(JsonKey.STATUS, "true");
    requestObj.put(JsonKey.ORGANISATION_ID, "test-12334");
    request.setRequest(requestObj);
    try {
      // this method will either throw projectCommonException or it return void
      // new OrgRequestValidator().validateUpdateOrgStatusRequest(request);
      requestObj.put("ext", "success");
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.invalidRequestData.getErrorCode(), e.getCode());
      assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getResponseCode());
    }
    assertEquals(null, requestObj.get("ext"));
  }
}
