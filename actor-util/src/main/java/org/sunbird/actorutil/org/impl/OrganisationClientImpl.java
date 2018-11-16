package org.sunbird.actorutil.org.impl;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.sunbird.actorutil.InterServiceCommunication;
import org.sunbird.actorutil.InterServiceCommunicationFactory;
import org.sunbird.actorutil.org.OrganisationClient;
import org.sunbird.common.ElasticSearchUtil;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ActorOperations;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.dto.SearchDTO;
import org.sunbird.models.organisation.Organisation;

public class OrganisationClientImpl implements OrganisationClient {

  private static InterServiceCommunication interServiceCommunication =
      InterServiceCommunicationFactory.getInstance();
  ObjectMapper objectMapper = new ObjectMapper();

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

      // Convert contact details (received from ES) format from map to
      // JSON string (as in Cassandra contact details are stored as text)
      Map<String, Object> map = (Map) response.get(JsonKey.RESPONSE);
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

  @Override
  public Organisation esGetOrgByExternalId(String externalId, String provider) {
    Organisation organisation = null;
    Map<String, Object> map = null;
    SearchDTO searchDto = new SearchDTO();
    Map<String, Object> filter = new HashMap<>();
    filter.put(JsonKey.EXTERNAL_ID, externalId);
    filter.put(JsonKey.PROVIDER, provider);
    searchDto.getAdditionalProperties().put(JsonKey.FILTERS, filter);
    Map<String, Object> esResponse =
        ElasticSearchUtil.complexSearch(
            searchDto,
            ProjectUtil.EsIndex.sunbird.getIndexName(),
            ProjectUtil.EsType.organisation.getTypeName());
    List<Map<String, Object>> list = (List<Map<String, Object>>) esResponse.get(JsonKey.CONTENT);
    if (!list.isEmpty()) {
      map = list.get(0);
      map.put(JsonKey.CONTACT_DETAILS, String.valueOf(map.get(JsonKey.CONTACT_DETAILS)));
      organisation = objectMapper.convertValue(map, Organisation.class);
    }
    return organisation;
  }

  @Override
  public Organisation esGetOrgById(String id) {
    Map<String, Object> map = null;
    map =
        ElasticSearchUtil.getDataByIdentifier(
            ProjectUtil.EsIndex.sunbird.getIndexName(),
            ProjectUtil.EsType.organisation.getTypeName(),
            id);
    if (MapUtils.isEmpty(map)) {
      return null;
    } else {
      return objectMapper.convertValue(map, Organisation.class);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Organisation> esSearchOrgByFilter(Map<String, Object> filter) {
    List<Organisation> orgList = new ArrayList<>();
    SearchDTO searchDto = new SearchDTO();
    searchDto.getAdditionalProperties().put(JsonKey.FILTERS, filter);
    Map<String, Object> result =
        ElasticSearchUtil.complexSearch(
            searchDto,
            ProjectUtil.EsIndex.sunbird.getIndexName(),
            ProjectUtil.EsType.organisation.getTypeName());
    List<Map<String, Object>> orgMapList = (List<Map<String, Object>>) result.get(JsonKey.CONTENT);
    if (CollectionUtils.isNotEmpty(orgMapList)) {
      for (Map<String, Object> orgMap : orgMapList) {
        orgList.add(objectMapper.convertValue(orgMap, Organisation.class));
      }
      return orgList;
    } else {
      return null;
    }
  }
}
