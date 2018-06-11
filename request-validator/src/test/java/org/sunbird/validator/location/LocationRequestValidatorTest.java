package org.sunbird.validator.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.sunbird.actorutil.InterServiceCommunication;
import org.sunbird.actorutil.InterServiceCommunicationFactory;
import org.sunbird.actorutil.impl.InterServiceCommunicationImpl;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.GeoLocationJsonKey;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LocationActorOperation;
import org.sunbird.common.request.Request;

/** @author Amit Kumar */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(PowerMockRunner.class)
@PrepareForTest({
  InterServiceCommunicationFactory.class,
  InterServiceCommunication.class,
  InterServiceCommunicationImpl.class
})
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*", "javax.security.*"})
public class LocationRequestValidatorTest {

  private static InterServiceCommunication interServiceCommunication = null;

  private static Map<String, Object> block = new HashMap<>();
  private static Map<String, Object> district = new HashMap<>();
  private static Map<String, Object> state = new HashMap<>();
  private static Request request = new Request();
  private static List<Map<String, Object>> locationList = new ArrayList<>();
  private static List<String> codeList = new ArrayList<>();
  private static List<String> locationIdsList = new ArrayList<>();
  private static LocationRequestValidator validator = null;

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
    PowerMockito.when(InterServiceCommunicationFactory.getInstance())
        .thenReturn(interServiceCommunication);
    PowerMockito.when(interServiceCommunication.getResponse(Mockito.any(), Mockito.any()))
        .thenReturn(locationList);

    validator = PowerMockito.mock(LocationRequestValidator.class);
    PowerMockito.when(validator.getValidatedLocationIds(Mockito.any(), Mockito.any()))
        .thenReturn(locationIdsList);
  }

  /** Test to validate location code and get the corresponding location ids till top level. */
  @Test
  public void testValidateLocationCode() {
    List<String> list = null;
    list = validator.getValidatedLocationIds(Mockito.any(), codeList);
    Assert.assertNotNull(list);
  }

  @Test(expected = ProjectCommonException.class)
  public void testValidateLocationCodeFail() {
    validator.getValidatedLocationIds(null, null);
  }
}
