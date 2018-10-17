// package org.sunbird.actorutil.org.impl;
//
// import akka.actor.ActorRef;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import java.util.HashMap;
// import java.util.Map;
// import org.sunbird.actorutil.InterServiceCommunication;
// import org.sunbird.actorutil.InterServiceCommunicationFactory;
// import org.sunbird.actorutil.org.OrganisationClient;
// import org.sunbird.common.exception.ProjectCommonException;
// import org.sunbird.common.models.response.Response;
// import org.sunbird.common.models.util.*;
// import org.sunbird.common.request.Request;
// import org.sunbird.common.responsecode.ResponseCode;
// import org.sunbird.models.organization.Organization;
//
// public class OrganisationClientImpl implements OrganisationClient {
//
//  private static InterServiceCommunication interServiceCommunication =
//      InterServiceCommunicationFactory.getInstance();
//
//  @Override
//  public String createOrg(ActorRef actorRef, Map<String, Object> orgMap) {
//    ProjectLogger.log("OrganisationClientImpl: createOrg called", LoggerEnum.INFO);
//    return upsertOrg(actorRef, orgMap, ActorOperations.CREATE_ORG.getValue());
//  }
//
//  @Override
//  public void updateOrg(ActorRef actorRef, Map<String, Object> orgMap) {
//    ProjectLogger.log("OrganisationClientImpl: updateOrg called", LoggerEnum.INFO);
//    upsertOrg(actorRef, orgMap, ActorOperations.UPDATE_ORG.getValue());
//  }
//
//  private String upsertOrg(ActorRef actorRef, Map<String, Object> orgMap, String operation) {
//    String orgId = null;
//
//    Request request = new Request();
//    Map<String, Object> requestMap = new HashMap<>();
//    requestMap.put(JsonKey.ORGANISATION, orgMap);
//    request.setRequest(requestMap);
//    request.setOperation(operation);
//
//    Object obj = interServiceCommunication.getResponse(actorRef, request);
//
//    if (obj instanceof Response) {
//      Response response = (Response) obj;
//      orgId = (String) response.get(JsonKey.ORGANISATION_ID);
// <<<<<<< HEAD
//    }
//    return orgId;
//  }
//

//
//  private void checOrganisationResponseForException(Object obj) {
//    if (obj instanceof ProjectCommonException) {
// =======
//    } else if (obj instanceof ProjectCommonException) {
// >>>>>>> 2ad1aaa0a386456310a5c98d00d882ba9954ccb1
//      throw (ProjectCommonException) obj;
//    } else if (obj instanceof Exception) {
//      throw new ProjectCommonException(
//          ResponseCode.SERVER_ERROR.getErrorCode(),
//          ResponseCode.SERVER_ERROR.getErrorMessage(),
//          ResponseCode.SERVER_ERROR.getResponseCode());
//    }
//
//    return orgId;
//  }
// }
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
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put(JsonKey.ORGANISATION, orgMap);
    request.setRequest(requestMap);
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
  public Organization getOrgById(ActorRef actorRef, String orgId) {
    Request request = new Request();
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put(JsonKey.ORGANISATION_ID, orgId);
    request.setRequest(requestMap);
    request.setOperation(ActorOperations.GET_ORG_DETAILS.getValue());
    ProjectLogger.log("OrganisationClientImpl : callGetOrgById ", LoggerEnum.INFO);
    Object obj = interServiceCommunication.getResponse(actorRef, request);
    Organization organization = null;
    if (obj instanceof Response) {
      ObjectMapper objectMapper = new ObjectMapper();
      Response response = (Response) obj;
      organization = objectMapper.convertValue(response.get(JsonKey.RESPONSE), Organization.class);
    } else if (obj instanceof ProjectCommonException) {
      throw (ProjectCommonException) obj;
    } else if (obj instanceof Exception) {
      throw new ProjectCommonException(
          ResponseCode.SERVER_ERROR.getErrorCode(),
          ResponseCode.SERVER_ERROR.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }

    return organization;
  }
}
