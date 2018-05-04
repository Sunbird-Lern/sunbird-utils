package org.sunbird.common.validator.location;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.GeoLocationJsonKey;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.Request;

/**
 * Test case for Base Location request validator.
 *
 * @author arvind on 30/4/18.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(PowerMockRunner.class)
public class BaseLocationRequestValidatorTest {

  private String LOCATION_NAME = "location-name";
  private String LOCATION_CODE = "location_code";
  private String LOC_TYPE_STATE = "STATE";
  BaseLocationRequestValidator validator = new BaseLocationRequestValidator();

  @Test
  public void testValidateCreateLocationWithProperData() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> locationData = new HashMap<>();
    locationData.put(JsonKey.NAME, LOCATION_NAME);
    locationData.put(JsonKey.CODE, LOCATION_CODE);
    locationData.put(GeoLocationJsonKey.LOCATION_TYPE, LOC_TYPE_STATE);
    request.setRequest(locationData);
    try {
      validator.validateCreateLocationRequest(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test(expected = ProjectCommonException.class)
  public void testValidateCreateLocationWithMandatoryParamTypeMissing() {
    Request request = new Request();
    Map<String, Object> requestBody = new HashMap<>();
    Map<String, Object> locationData = new HashMap<>();
    locationData.put(JsonKey.NAME, LOCATION_NAME);
    locationData.put(JsonKey.CODE, LOCATION_CODE);
    request.setRequest(locationData);

    validator.validateCreateLocationRequest(request);
  }

  @Test(expected = ProjectCommonException.class)
  public void testValidateCreateLocationWithEmptyData() {
    Request request = new Request();
    validator.validateCreateLocationRequest(request);
  }

  @Test
  public void testValidateUpdateLocationWithProperData() {
    Request request = new Request();
    boolean response = false;
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put(JsonKey.ID, "123");
    requestBody.put(JsonKey.CODE, LOCATION_CODE);
    request.setRequest(requestBody);
    try {
      validator.validateUpdateLocationRequest(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test(expected = ProjectCommonException.class)
  public void testValidateUpdateLocationWithInvalidAttriuteType() {
    Request request = new Request();
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put(GeoLocationJsonKey.LOCATION_TYPE, LOC_TYPE_STATE);
    request.setRequest(requestBody);
    validator.validateUpdateLocationRequest(request);
  }

  @Test(expected = ProjectCommonException.class)
  public void testValidateUpdateLocationWithEmptyData() {
    Request request = new Request();
    validator.validateUpdateLocationRequest(request);
  }

  @Test
  public void testValidateDeleteLocationWithProperData() {
    boolean response = false;
    try {
      validator.validateDeleteLocationRequest("123");
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test(expected = ProjectCommonException.class)
  public void testValidateDeleteLocationWithMandatoryPaamMissingId() {
    validator.validateDeleteLocationRequest("");
  }

  @Test
  public void testValidateSearchLocationWithProperData() {
    Request request = new Request();
    Map<String, Object> requestBody = new HashMap<>();
    boolean response = false;
    Map<String, Object> filters = new HashMap<>();
    filters.put(JsonKey.NAME, LOCATION_NAME);
    filters.put(JsonKey.CODE, LOCATION_CODE);
    filters.put(JsonKey.TYPE, LOC_TYPE_STATE);
    requestBody.put(JsonKey.FILTERS, filters);
    request.setRequest(requestBody);
    try {
      validator.validateSearchLocationRequest(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
    assertEquals(true, response);
  }

  @Test(expected = ProjectCommonException.class)
  public void testValidateSearchLocationWithInvalidDataFilterAsNotMap() {
    Request request = new Request();
    Map<String, Object> requestBody = new HashMap<>();
    List<String> filters = new ArrayList<>();
    filters.add(LOCATION_NAME);
    requestBody.put(JsonKey.FILTERS, filters);
    request.setRequest(requestBody);
    validator.validateSearchLocationRequest(request);
  }

  @Test(expected = ProjectCommonException.class)
  public void testValidateSearchLocationWithEmptyBody() {
    Request request = new Request();
    validator.validateSearchLocationRequest(request);
  }
}
