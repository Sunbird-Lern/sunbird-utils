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
import org.sunbird.models.organization.Organization;

/** Created by rajatgupta on 11/10/18. */
public class OrganisationClientImpl implements OrganisationClient {

  private static InterServiceCommunication interServiceCommunication =
      InterServiceCommunicationFactory.getInstance();

  @Override
  public String createOrg(ActorRef actorRef, Map<String, Object> orgMap) {
    Request request = new Request();
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put(JsonKey.ORGANISATION, orgMap);
    request.setRequest(requestMap);
    request.setOperation(ActorOperations.CREATE_ORG.getValue());
    ProjectLogger.log("LocationClientImpl : callCreateOrganisation ", LoggerEnum.INFO);
    Object obj = interServiceCommunication.getResponse(actorRef, request);
    checOrganisationResponseForException(obj);
    String orgId = null;
    if (obj instanceof Response) {
      Response response = (Response) obj;
      orgId = (String) response.get(JsonKey.ORGANISATION_ID);
    }
    return orgId;
  }

  @Override
  public String updateOrg(ActorRef actorRef, Map<String, Object> orgMap) {
    Request request = new Request();
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put(JsonKey.ORGANISATION, orgMap);
    request.setRequest(requestMap);
    request.setOperation(ActorOperations.UPDATE_ORG.getValue());
    ProjectLogger.log("LocationClientImpl : callCreateOrganisation ", LoggerEnum.INFO);
    Object obj = interServiceCommunication.getResponse(actorRef, request);
    checOrganisationResponseForException(obj);
    String orgId = null;
    if (obj instanceof Response) {
      Response response = (Response) obj;
      orgId = (String) response.get(JsonKey.ORGANISATION_ID);
    }
    return orgId;
  }

  @Override
  public Organization getOrgById(ActorRef actorRef, String orgId) {
    Request request = new Request();
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put(JsonKey.ORGANISATION_ID, orgId);
    request.setRequest(requestMap);
    request.setOperation(ActorOperations.GET_ORG_DETAILS.getValue());
    ProjectLogger.log("LocationClientImpl : callGetOrganisation ", LoggerEnum.INFO);
    Object obj = interServiceCommunication.getResponse(actorRef, request);
    checOrganisationResponseForException(obj);
    Organization organization = null;
    if (obj instanceof Response) {
      ObjectMapper objectMapper = new ObjectMapper();
      Response response = (Response) obj;
      organization =
          objectMapper.convertValue(
              (Map<String, Object>) response.get(JsonKey.RESPONSE), Organization.class);
    }

    return organization;
  }

  private void checOrganisationResponseForException(Object obj) {
    if (obj instanceof ProjectCommonException) {
      throw (ProjectCommonException) obj;
    } else if (obj instanceof Exception) {
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
  }
}
