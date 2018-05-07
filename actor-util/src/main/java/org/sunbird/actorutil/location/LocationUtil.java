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
import org.sunbird.models.location.Location;

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
    List<Map<String, Object>> responseLocList = null;
    responseLocList = locationClient.getLocationsByCodes(actorRef, codeList);
    List<Location> locationList =
        responseLocList
            .stream()
            .map(s -> mapper.convertValue(s, Location.class))
            .collect(Collectors.toList());
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
  public Set<String> getValidatedLocationSet(List<Location> locationList, ActorRef actorRef) {
    Set<Location> locationSet = new HashSet<>();
    for (Location location : locationList) {
      Set<Location> parentLocnSet = getParentLocations(location, actorRef);
      addLocations(locationSet, parentLocnSet, location.getCode());
    }
    return locationSet.stream().map(Location::getId).collect(Collectors.toSet());
  }

  private void addLocations(
      Set<Location> locationSet, Set<Location> parentLocnSet, String requestedLocationCode) {
    if (CollectionUtils.sizeIsEmpty(locationSet)) {
      locationSet.addAll(parentLocnSet);
    } else {
      for (Location currentLocation : parentLocnSet) {
        String type = currentLocation.getType();
        Predicate<Location> predicate =
            location ->
                type.equalsIgnoreCase(location.getType())
                    && !(currentLocation.getId().equals(location.getId()));
        List<String> codeList =
            locationSet
                .stream()
                .filter(predicate)
                .map(Location::getCode)
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

  private Set<Location> getParentLocations(Location location, ActorRef actorRef) {
    Set<Location> locationSet = new LinkedHashSet<>();
    Map<String, Integer> orderMap = getOrderMap();
    int count = orderMap.get(location.getType()) + 1;
    while (count != 0) {
      Location parent = null;
      locationSet.add(location);
      if (orderMap.get(location.getType()) == 0 && StringUtils.isNotEmpty(location.getId())) {
        parent = getParentLocation(location.getId(), actorRef);
      } else if (StringUtils.isNotEmpty(location.getParentId())) {
        parent = getParentLocation(location.getParentId(), actorRef);
      }
      if (null != parent) {
        locationSet.add(parent);
        location = parent;
      }
      count--;
    }
    return locationSet;
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

  private Location getParentLocation(String locationId, ActorRef actorRef) {
    Map<String, Object> location = locationClient.getLocationById(actorRef, locationId);
    if (MapUtils.isNotEmpty(location)) {
      return mapper.convertValue(location, Location.class);
    } else {
      return new Location();
    }
  }
}
