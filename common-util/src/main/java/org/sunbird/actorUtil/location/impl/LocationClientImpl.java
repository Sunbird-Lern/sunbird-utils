package org.sunbird.actorUtil.location.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.sunbird.actorUtil.InterServiceCommunication;
import org.sunbird.actorUtil.InterServiceCommunicationFactory;
import org.sunbird.actorUtil.location.LocationClient;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.GeoLocationJsonKey;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LocationActorOperation;
import org.sunbird.common.request.Request;

public class LocationClientImpl implements LocationClient {

  private static InterServiceCommunication interServiceCommunication =
      InterServiceCommunicationFactory.getInstance().getCommunicationPath("actorCommunication");

  @Override
  public List<Map<String, Object>> getLocationsByCodes(List<String> codeList, Object actorRef) {
    List<Map<String, Object>> locationList = null;
    Map<String, Object> filters = new HashMap<>();
    filters.put(GeoLocationJsonKey.CODE, codeList);
    Map<String, Object> locMap = new HashMap<>();
    locMap.put(JsonKey.FILTERS, filters);
    Request request = new Request();
    request.setOperation(LocationActorOperation.SEARCH_LOCATION.getValue());
    request.getRequest().putAll(locMap);
    Object obj = interServiceCommunication.getResponse(request, actorRef);
    if (obj instanceof Response) {
      Response response = (Response) obj;
      locationList = (List<Map<String, Object>>) response.getResult().get(JsonKey.RESPONSE);
    }
    return locationList;
  }

  @Override
  public Map<String, Object> getLocationById(String id, Object actorRef) {
    List<Map<String, Object>> locationList = null;
    Map<String, Object> filters = new HashMap<>();
    Map<String, Object> location = new HashMap<>();
    Map<String, String> requestData = new HashMap<>();
    requestData.put(JsonKey.ID, id);
    filters.put(JsonKey.FILTERS, requestData);
    Request req = new Request();
    req.setOperation(LocationActorOperation.SEARCH_LOCATION.getValue());
    req.getRequest().putAll(filters);
    Object obj = interServiceCommunication.getResponse(req, actorRef);
    if (obj instanceof Response) {
      Response response = (Response) obj;
      locationList = (List<Map<String, Object>>) response.getResult().get(JsonKey.RESPONSE);
    }
    if (CollectionUtils.isNotEmpty(locationList)) {
      location = locationList.get(0);
    }
    return location;
  }
}
