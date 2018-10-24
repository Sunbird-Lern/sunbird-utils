package org.sunbird.actorutil.org.impl;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.sunbird.actorutil.InterServiceCommunication;
import org.sunbird.actorutil.InterServiceCommunicationFactory;
import org.sunbird.actorutil.org.OrganisationClient;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.*;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.models.organisation.Organisation;

public class OrganisationClientImpl implements OrganisationClient {

  private static InterServiceCommunication interServiceCommunication =
      InterServiceCommunicationFactory.getInstance();

  @Override
  public String createOrg(ActorRef actorRef, Map<String, Object> orgMap) {
    ProjectLogger.log("OrganisationClientImpl: createOrg called", LoggerEnum.INFO);
    return upsertOrg(actorRef, orgMap, ActorOperations.CREATE_ORG.getValue());
  }

  @Override
  public void updateOrg(ActorRef actorRef, Map<String, Object> orgMap) {
    ProjectLogger.log("OrganisationClientImpl: updateOrg called", LoggerEnum.INFO);
    upsertOrg(actorRef, orgMap, ActorOperations.UPDATE_ORG.getValue());
  }

  private String upsertOrg(ActorRef actorRef, Map<String, Object> orgMap, String operation) {
    String orgId = null;

    Request request = new Request();
    request.setRequest(orgMap);
    request.setOperation(operation);

    Object obj = interServiceCommunication.getResponse(actorRef, request);

    if (obj instanceof Response) {
      Response response = (Response) obj;
      orgId = (String) response.get(JsonKey.ORGANISATION_ID);
    } else if (obj instanceof ProjectCommonException) {
      throw (ProjectCommonException) obj;
    } else if (obj instanceof Exception) {
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }

    return orgId;
  }

  @Override
  public Organisation getOrgById(ActorRef actorRef, String orgId) {
    ProjectLogger.log("OrganisationClientImpl: getOrgById called", LoggerEnum.INFO);
    Organisation organisation = null;
    
    Request request = new Request();
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put(JsonKey.ORGANISATION_ID, orgId);
    request.setRequest(requestMap);
    request.setOperation(ActorOperations.GET_ORG_DETAILS.getValue());
    
    Object obj = interServiceCommunication.getResponse(actorRef, request);
    
    if (obj instanceof Response) {
      ObjectMapper objectMapper = new ObjectMapper();
      Response response = (Response) obj;
      //Temp Fix as this result is coming from elastic-search and ORganisation object is built based on cassandra definition.
      Map<String,Object> map = (Map)response.get(JsonKey.RESPONSE);
      map.put(JsonKey.CONTACT_DETAILS, String.valueOf(map.get(JsonKey.CONTACT_DETAILS)));
      organisation = objectMapper.convertValue(map, Organisation.class);
    } else if (obj instanceof ProjectCommonException) {
      throw (ProjectCommonException) obj;
    } else if (obj instanceof Exception) {
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }

    return organisation;
  }
}
