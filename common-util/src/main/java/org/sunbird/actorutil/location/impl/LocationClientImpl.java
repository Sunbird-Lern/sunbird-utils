package org.sunbird.actorutil.location.impl;

import akka.actor.ActorRef;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.sunbird.actorutil.InterServiceCommunication;
import org.sunbird.actorutil.InterServiceCommunicationFactory;
import org.sunbird.actorutil.location.LocationClient;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.GeoLocationJsonKey;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LocationActorOperation;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.Request;

public class LocationClientImpl implements LocationClient {

  private static InterServiceCommunication interServiceCommunication =
      InterServiceCommunicationFactory.getInstance();

  @Override
  public List<Map<String, Object>> getLocationsByCodes(ActorRef actorRef, List<String> codeList) {
    String param = GeoLocationJsonKey.CODE;
    Object value = codeList;
    Response response = getSearchResponse(actorRef, param, value);
    return (List<Map<String, Object>>) response.getResult().get(JsonKey.RESPONSE);
  }

  @Override
  public Map<String, Object> getLocationById(ActorRef actorRef, String id) {
    String param = JsonKey.ID;
    Object value = id;
    Map<String, Object> location = new HashMap<>();
    Response response = getSearchResponse(actorRef, param, value);
    if (null != response.getResult()) {
      List<Map<String, Object>> locationList =
          (List<Map<String, Object>>) response.getResult().get(JsonKey.RESPONSE);
      if (CollectionUtils.isNotEmpty(locationList)) {
        location = locationList.get(0);
      }
    }
    return location;
  }

  private Response getSearchResponse(ActorRef actorRef, String param, Object value) {
    Response response = null;
    Map<String, Object> filters = new HashMap<>();
    Map<String, Object> locMap = new HashMap<>();
    filters.put(param, value);
    locMap.put(JsonKey.FILTERS, filters);
    Request request = new Request();
    request.setOperation(LocationActorOperation.SEARCH_LOCATION.getValue());
    request.getRequest().putAll(locMap);
    ProjectLogger.log(
        "LocationClientImpl : callSearchLocation - "
            + "Operation -"
            + LocationActorOperation.SEARCH_LOCATION.getValue(),
        LoggerEnum.INFO);
    Object obj = interServiceCommunication.getResponse(actorRef, request);
    if (obj instanceof Response) {
      response = (Response) obj;
    } else {
      response = new Response();
    }
    return response;
  }

  @Override
  public Response getLocationByCode(ActorRef actorRef, String locationCode) {
    String param = GeoLocationJsonKey.CODE;
    Object value = locationCode;
    return getSearchResponse(actorRef, param, value);
  }

  @Override
  public Response createLocation(ActorRef actorRef, Map<String, Object> location) {
    Request request = new Request();
    request.getRequest().putAll(location);
    request.setOperation(LocationActorOperation.CREATE_LOCATION.getValue());
    ProjectLogger.log(
        "LocationClientImpl : callCreateLocation - "
            + "Operation -"
            + LocationActorOperation.CREATE_LOCATION.getValue(),
        LoggerEnum.INFO);

    Object obj = interServiceCommunication.getResponse(actorRef, request);
    if (obj instanceof Response) {
      return (Response) obj;
    }
    return null;
  }

  @Override
  public Response updateLocation(ActorRef actorRef, Map<String, Object> location) {
    Request request = new Request();
    request.getRequest().putAll(location);
    request.setOperation(LocationActorOperation.UPDATE_LOCATION.getValue());
    ProjectLogger.log(
        "LocationClientImpl : callUpdateLocation - "
            + "Operation -"
            + LocationActorOperation.UPDATE_LOCATION.getValue(),
        LoggerEnum.INFO);
    Object obj = interServiceCommunication.getResponse(actorRef, request);
    if (obj instanceof Response) {
      return (Response) obj;
    }
    return null;
  }
}
