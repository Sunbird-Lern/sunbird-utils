package org.sunbird.common.validator.location;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.actorUtil.InterServiceCommunication;
import org.sunbird.actorUtil.InterServiceCommunicationFactory;
import org.sunbird.actorUtil.impl.InterServiceCommunicationImpl;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.GeoLocationJsonKey;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LocationActorOperation;
import org.sunbird.common.request.Request;

/**
 * Test case for Base Location request validator.
 *
 * @author arvind on 30/4/18.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(PowerMockRunner.class)
@PrepareForTest({
  InterServiceCommunicationFactory.class,
  InterServiceCommunication.class,
  InterServiceCommunicationImpl.class,
  BaseLocationRequestValidator.class
})
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*", "javax.security.*"})
public class BaseLocationRequestValidatorTest {

  private String LOCATION_NAME = "location-name";
  private String LOCATION_CODE = "location_code";
  private String LOC_TYPE_STATE = "STATE";
  BaseLocationRequestValidator validator = new BaseLocationRequestValidator();

  private static InterServiceCommunication interServiceCommunication = null;
  private static InterServiceCommunicationFactory factory = null;

  private static Map<String, Object> block = new HashMap<>();
  private static Map<String, Object> district = new HashMap<>();
  private static Map<String, Object> state = new HashMap<>();
  private static Request request = new Request();
  private static List<Map<String, Object>> locationList = new ArrayList<>();
  private static List<String> codeList = new ArrayList<>();
  private static Set<String> locationIdsList = new HashSet<>();

  @BeforeClass
  public static void setUp() {
    block.put(JsonKey.TYPE, "block");
    block.put(JsonKey.NAME, "block_name");
    block.put(JsonKey.CODE, "blk1");
    block.put(GeoLocationJsonKey.PARENT_CODE, "dis1");

    district.put(JsonKey.TYPE, "district");
    district.put(JsonKey.NAME, "district_name");
    district.put(JsonKey.CODE, "dis1");
    district.put(GeoLocationJsonKey.PARENT_CODE, "state1");

    state.put(JsonKey.TYPE, "state");
    state.put(JsonKey.NAME, "state_name");
    state.put(JsonKey.CODE, "state1");

    locationList.add(block);
    locationList.add(district);
    locationList.add(state);

    codeList.add("blk1");

    locationIdsList.addAll(Arrays.asList("blk1", "dis1", "state1"));

    Map<String, Object> filters = new HashMap<>();
    filters.put(GeoLocationJsonKey.CODE, codeList);
    Map<String, Object> locMap = new HashMap<>();
    locMap.put(JsonKey.FILTERS, filters);
    request.setOperation(LocationActorOperation.SEARCH_LOCATION.getValue());
    request.getRequest().putAll(locMap);

    PowerMockito.mockStatic(InterServiceCommunicationFactory.class);
    interServiceCommunication = PowerMockito.mock(InterServiceCommunication.class);
    factory = PowerMockito.mock(InterServiceCommunicationFactory.class);
    PowerMockito.when(InterServiceCommunicationFactory.getInstance()).thenReturn(factory);
    PowerMockito.when(factory.getCommunicationPath("actorCommunication"))
        .thenReturn(interServiceCommunication);
    PowerMockito.when(interServiceCommunication.getResponse(Mockito.any(), Mockito.any()))
        .thenReturn(locationList);

    PowerMockito.mockStatic(BaseLocationRequestValidator.class);
    PowerMockito.when(
            BaseLocationRequestValidator.validateLocationHierarchy(
                Mockito.anyList(), Mockito.any()))
        .thenReturn(locationIdsList);
  }

  @Test
  public void testValidateLocationCode() {
    List<String> list = null;
    list = BaseLocationRequestValidator.validateLocationCode(codeList, null);
    Assert.assertNotNull(list);
  }

  @Test(expected = ProjectCommonException.class)
  public void testValidateLocationCodeFail() {
    BaseLocationRequestValidator.validateLocationCode(null, null);
  }

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
