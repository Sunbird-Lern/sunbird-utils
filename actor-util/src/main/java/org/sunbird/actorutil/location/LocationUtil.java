package org.sunbird.actorutil.location;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.actorutil.location.impl.LocationClientImpl;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.GeoLocationJsonKey;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;

public class LocationUtil {

  private LocationClient locationClient = new LocationClientImpl();
  private ObjectMapper mapper = new ObjectMapper();

  /**
   * This method will validate the list of location code whether its valid or not. If valid will
   * return the locationId List.
   *
   * @param List of location code.
   * @param Actor reference.
   * @return List of location id.
   */
  public List<String> getValidatedLocationIds(List<String> codeList, ActorRef actorRef) {
    Set<String> locationIds = null;
    List<Map<String, Object>> locationList = null;
    locationList = locationClient.getLocationsByCodes(actorRef, codeList);
    List<String> locationIdList = new ArrayList<>();
    if (!CollectionUtils.isEmpty(locationList)) {
      locationIds = getValidatedLocationSet(locationList, actorRef);
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
   * @desc This method will validate the location hierarchy and return the locationIds list.
   * @param LocationList list of location.
   * @param ActorRef actor reference.
   * @return Set of locationId.
   */
  public Set<String> getValidatedLocationSet(
      List<Map<String, Object>> locationList, ActorRef actorRef) {
    Set<Map<String, Object>> locationSet = new HashSet<>();
    for (Map<String, Object> location : locationList) {
      Set<Map<String, Object>> parentLocnSet = getParentLocations(location, actorRef);
      addLocations(locationSet, parentLocnSet, (String) location.get(JsonKey.CODE));
    }
    return locationSet.stream().map(s -> ((String) s.get(JsonKey.ID))).collect(Collectors.toSet());
  }

  private void addLocations(
      Set<Map<String, Object>> locationSet,
      Set<Map<String, Object>> parentLocnSet,
      String requestedLocationCode) {
    if (CollectionUtils.sizeIsEmpty(locationSet)) {
      locationSet.addAll(parentLocnSet);
    } else {
      for (Object obj : parentLocnSet) {
        Map<String, Object> currentLocation = (Map<String, Object>) obj;
        String type = (String) currentLocation.get(JsonKey.TYPE);
        Predicate<Map<String, Object>> predicate =
            location ->
                type.equalsIgnoreCase((String) location.get(JsonKey.TYPE))
                    && !(currentLocation.get(JsonKey.ID).equals(location.get(JsonKey.ID)));
        List<String> codeList =
            locationSet
                .stream()
                .filter(predicate)
                .map(location -> (String) location.get(JsonKey.CODE))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(codeList)) {
          throw new ProjectCommonException(
              ResponseCode.conflictingOrgLocations.getErrorCode(),
              ProjectUtil.formatMessage(
                  ResponseCode.conflictingOrgLocations.getErrorMessage(),
                  requestedLocationCode,
                  codeList.get(0),
                  type),
              ResponseCode.CLIENT_ERROR.getResponseCode());
        }
        locationSet.add(currentLocation);
      }
    }
  }

  private Set<Map<String, Object>> getParentLocations(
      Map<String, Object> location, ActorRef actorRef) {
    Set<Map<String, Object>> locSet = new LinkedHashSet<>();
    Map<String, Integer> orderMap = getOrderMap();
    int count = orderMap.get(location.get(JsonKey.TYPE)) + 1;
    while (count != 0) {
      if (MapUtils.isNotEmpty(location)) {
        Map<String, Object> parent = new HashMap<>();
        locSet.add(location);
        if (orderMap.get(location.get(JsonKey.TYPE)) == 0
            && StringUtils.isNotEmpty((String) location.get(JsonKey.ID))) {
          parent = getParentLocation((String) location.get(JsonKey.ID), actorRef);
        } else if (StringUtils.isNotEmpty((String) location.get(JsonKey.PARENT_ID))) {
          parent = getParentLocation((String) location.get(JsonKey.PARENT_ID), actorRef);
        }
        if (MapUtils.isNotEmpty(parent)) {
          locSet.add(parent);
          location = parent;
        }
      }
      count--;
    }
    return locSet;
  }

  private Map<String, Integer> getOrderMap() {
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

  private Map<String, Object> getParentLocation(String locationId, ActorRef actorRef) {
    Map<String, Object> location = locationClient.getLocationById(actorRef, locationId);
    if (MapUtils.isNotEmpty(location)) {
      return location;
    } else {
      return new HashMap<>();
    }
  }
}
