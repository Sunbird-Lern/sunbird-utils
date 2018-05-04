package org.sunbird.common.validator.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.actorUtil.location.LocationClient;
import org.sunbird.actorUtil.location.impl.LocationClientImpl;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.GeoLocationJsonKey;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.request.BaseRequestValidator;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

/** Created by arvind on 25/4/18. */
public class BaseLocationRequestValidator extends BaseRequestValidator {

  private static LocationClient locationClient = new LocationClientImpl();
  /**
   * Method to validate the create location request . Mandatory fields are as - name , type, code.
   *
   * @param req Request .
   */
  public void validateCreateLocationRequest(Request req) {
    checkMandatoryFieldsPresent(
        req.getRequest(), JsonKey.NAME, JsonKey.CODE, GeoLocationJsonKey.LOCATION_TYPE);
  }

  /**
   * Method to validate the update location request . Mandatory fields are as - id, type.
   *
   * @param req Request.
   */
  public void validateUpdateLocationRequest(Request req) {
    checkMandatoryFieldsPresent(req.getRequest(), JsonKey.ID);
    checkReadOnlyAttributesAbsent(req.getRequest(), GeoLocationJsonKey.LOCATION_TYPE);
  }

  /**
   * Method to validate the delete location request . Mandatory field - locationId.
   *
   * @param locationId
   */
  public void validateDeleteLocationRequest(String locationId) {
    if (StringUtils.isEmpty(locationId)) {
      throw new ProjectCommonException(
          ResponseCode.locationIdRequired.getErrorCode(),
          ResponseCode.locationIdRequired.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }

  /**
   * Method to validate the search location request . Request body can not be null and If filter is
   * coming in request body it should be of type map.
   *
   * @param req
   */
  public void validateSearchLocationRequest(Request req) {
    Map<String, Object> requestBody = req.getRequest();
    if (MapUtils.isEmpty(requestBody)) {
      throw new ProjectCommonException(
          ResponseCode.invalidRequestData.getErrorCode(),
          ResponseCode.invalidRequestData.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    if (requestBody.containsKey(JsonKey.FILTERS)
        && !(requestBody.get(JsonKey.FILTERS) instanceof Map)) {
      throw new ProjectCommonException(
          ResponseCode.invalidRequestData.getErrorCode(),
          ResponseCode.invalidRequestData.getErrorMessage(),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
  }

  /**
   * This method will validate the list of location code whether its valid or not. If valid will
   * return the locationId List.
   *
   * @param codeList list of location code
   * @param actorRef
   * @return List<String> list of locationId
   */
  public static List<String> validateLocationCode(List<String> codeList, Object actorRef) {
    Set<String> locationIds = null;
    List<Map<String, Object>> locationList = null;
    locationList = locationClient.getLocationsByCodes(codeList, actorRef);
    List<String> locationIdList = new ArrayList<>();
    if (!CollectionUtils.isEmpty(locationList)) {
      locationIds = BaseLocationRequestValidator.validateLocationHierarchy(locationList, actorRef);
    } else {
      throw new ProjectCommonException(
          ResponseCode.invalidParameterValue.getErrorCode(),
          ProjectUtil.formatMessage(
              ResponseCode.invalidParameterValue.getErrorMessage(),
              codeList,
              JsonKey.LOCATION_CODE),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    locationIdList.addAll(locationIds);
    return locationIdList;
  }

  /**
   * This method will validate the location hierarchy and return the locationIds list
   *
   * @param locationList list of location
   * @param actorRef actor reference
   * @return list of locationId
   */
  public static Set<String> validateLocationHierarchy(
      List<Map<String, Object>> locationList, Object actorRef) {
    Set<String> locnIds = new HashSet<>();
    Map<String, Object> responseLocnMapList = new HashMap<>();
    for (Map<String, Object> location : locationList) {
      Map<String, Object> parentLocnList = getParentLocations(location, actorRef);
      addLocations(responseLocnMapList, parentLocnList);
    }
    locnIds.addAll(responseLocnMapList.keySet());
    return locnIds;
  }

  private static void addLocations(
      Map<String, Object> responseLocnMapList, Map<String, Object> parentLocnList) {
    if (MapUtils.isEmpty(responseLocnMapList)) {
      responseLocnMapList.putAll(parentLocnList);
    } else {
      checkLocationAlreadyExist(responseLocnMapList, parentLocnList);
    }
  }

  private static void checkLocationAlreadyExist(
      Map<String, Object> responseLocnMapList, Map<String, Object> parentLocnList) {
    for (Object obj : parentLocnList.values()) {
      Map<String, Object> currentLocation = (Map<String, Object>) obj;
      String type = (String) currentLocation.get(JsonKey.TYPE);
      for (Object object : responseLocnMapList.values()) {
        Map<String, Object> location = (Map<String, Object>) object;
        if (type.equalsIgnoreCase((String) location.get(JsonKey.TYPE))
            && !(currentLocation.get(JsonKey.ID).equals(location.get(JsonKey.ID)))) {
          throw new ProjectCommonException(
              ResponseCode.validateLocationCode.getErrorCode(),
              ProjectUtil.formatMessage(
                  ResponseCode.validateLocationCode.getErrorMessage(),
                  currentLocation.get(JsonKey.CODE),
                  location.get(JsonKey.CODE),
                  type),
              ResponseCode.CLIENT_ERROR.getResponseCode());
        }
      }
      responseLocnMapList.put((String) currentLocation.get(JsonKey.ID), currentLocation);
    }
  }

  private static Map<String, Object> getParentLocations(
      Map<String, Object> location, Object actorRef) {
    Map<String, Object> locMap = new HashMap<>();
    Map<String, Integer> orderMap = getOrderMap();
    int count = orderMap.get(location.get(JsonKey.TYPE)) + 1;
    while (count != 0) {
      if (MapUtils.isNotEmpty(location)) {
        Map<String, Object> parent = new HashMap<>();
        locMap.put((String) location.get(JsonKey.ID), location);
        if (orderMap.get(location.get(JsonKey.TYPE)) == 0
            && StringUtils.isNotEmpty((String) location.get(JsonKey.ID))) {
          parent = getParentLocation((String) location.get(JsonKey.ID), actorRef);
        } else if (StringUtils.isNotEmpty((String) location.get(JsonKey.PARENT_ID))) {
          parent = getParentLocation((String) location.get(JsonKey.PARENT_ID), actorRef);
        }
        if (MapUtils.isNotEmpty(parent)) {
          locMap.put((String) parent.get(JsonKey.ID), parent);
          location = parent;
        }
      }
      count--;
    }
    return locMap;
  }

  private static Map<String, Integer> getOrderMap() {
    Map<String, Integer> orderMap = new HashMap<>();
    List<String> subTypeList =
        Arrays.asList(
            ProjectUtil.getConfigValue(GeoLocationJsonKey.SUNBIRD_VALID_LOCATION_TYPES).split(";"));
    for (String str : subTypeList) {
      List<String> typeList =
          (((Arrays.asList(str.split(","))).stream().map(String::toLowerCase))
              .collect(Collectors.toList()));
      for (int i = 0; i < typeList.size(); i++) {
        orderMap.put(typeList.get(i), i);
      }
    }
    return orderMap;
  }

  private static Map<String, Object> getParentLocation(String locationId, Object actorRef) {
    Map<String, Object> location = locationClient.getLocationById(locationId, actorRef);
    if (MapUtils.isNotEmpty(location)) {
      return location;
    } else {
      return new HashMap<>();
    }
  }
}
