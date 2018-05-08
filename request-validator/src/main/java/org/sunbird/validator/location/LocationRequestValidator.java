package org.sunbird.validator.location;

import akka.actor.ActorRef;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.actorutil.location.LocationClient;
import org.sunbird.actorutil.location.impl.LocationClientImpl;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.GeoLocationJsonKey;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.models.location.Location;

/**
 * This class will contains method to validate the location api request.
 *
 * @author Amit Kumar
 */
public class LocationRequestValidator extends BaseLocationRequestValidator {

  private LocationClient locationClient = new LocationClientImpl();
  private static Map<String, Integer> orderMap = new HashMap<>();

  static {
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
  }

  /**
   * This method will validate the list of location code whether its valid or not. If valid will
   * return the locationId List.
   *
   * @param actorRef Actor reference.
   * @param codeList List of location code.
   * @return List of location id.
   */
  public List<String> getValidatedLocationIds(ActorRef actorRef, List<String> codeList) {
    Set<String> locationIds = null;
    List<Location> locationList = locationClient.getLocationsByCodes(actorRef, codeList);
    List<String> locationIdList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(locationList)) {
      if (locationList.size() != codeList.size()) {
        List<String> invalidCodeList =
            locationList
                .stream()
                .filter(s -> !codeList.contains(s.getCode()))
                .map(Location::getCode)
                .collect(Collectors.toList());
        throwInvalidParameterValueException(invalidCodeList);
      } else {
        locationIds = getValidatedLocationSet(actorRef, locationList);
      }
    } else {
      throwInvalidParameterValueException(codeList);
    }
    locationIdList.addAll(locationIds);
    return locationIdList;
  }

  private void throwInvalidParameterValueException(List<String> codeList) {
    throw new ProjectCommonException(
        ResponseCode.invalidParameterValue.getErrorCode(),
        ProjectUtil.formatMessage(
            ResponseCode.invalidParameterValue.getErrorMessage(), codeList, JsonKey.LOCATION_CODE),
        ResponseCode.CLIENT_ERROR.getResponseCode());
  }

  /**
   * This method will validate the location hierarchy and return the locationIds list.
   *
   * @param actorRef Actor reference.
   * @param locationList List of location.
   * @return Set of locationId.
   */
  public Set<String> getValidatedLocationSet(ActorRef actorRef, List<Location> locationList) {
    Set<Location> locationSet = new HashSet<>();
    for (Location requestedLocation : locationList) {
      Set<Location> parentLocnSet = getParentLocations(actorRef, requestedLocation);
      if (CollectionUtils.sizeIsEmpty(locationSet)) {
        locationSet.addAll(parentLocnSet);
      } else {
        for (Location currentLocation : parentLocnSet) {
          String type = currentLocation.getType();
          locationSet
              .stream()
              .forEach(
                  location -> {
                    if (type.equalsIgnoreCase(location.getType())
                        && !(currentLocation.getId().equals(location.getId()))) {
                      throw new ProjectCommonException(
                          ResponseCode.conflictingOrgLocations.getErrorCode(),
                          ProjectUtil.formatMessage(
                              ResponseCode.conflictingOrgLocations.getErrorMessage(),
                              requestedLocation.getCode(),
                              location.getCode(),
                              type),
                          ResponseCode.CLIENT_ERROR.getResponseCode());
                    }
                  });
          locationSet.add(currentLocation);
        }
      }
    }
    return locationSet.stream().map(Location::getId).collect(Collectors.toSet());
  }

  private Set<Location> getParentLocations(ActorRef actorRef, Location locationObj) {
    Set<Location> locationSet = new LinkedHashSet<>();
    Location location = locationObj;
    int count = getOrder(location.getType());
    locationSet.add(location);
    while (count > 0) {
      Location parent = null;
      if (getOrder(location.getType()) == 0 && StringUtils.isNotEmpty(location.getId())) {
        parent = locationClient.getLocationById(actorRef, location.getId());
      } else if (StringUtils.isNotEmpty(location.getParentId())) {
        parent = locationClient.getLocationById(actorRef, location.getParentId());
      }
      if (null != parent) {
        locationSet.add(parent);
        location = parent;
      }
      count--;
    }
    return locationSet;
  }

  public int getOrder(String type) {
    return orderMap.get(type);
  }
}
