package org.sunbird.actor.core;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.actor.router.RequestRouter;
import org.sunbird.cassandraimpl.CassandraOperationImpl;
import org.sunbird.common.ElasticSearchUtil;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.KeyCloakConnectionProvider;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.dto.SearchDTO;
import org.sunbird.helper.ServiceFactory;
import org.sunbird.learner.util.Util;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
  ElasticSearchUtil.class,
  ServiceFactory.class,
  RequestRouter.class,
  KeyCloakConnectionProvider.class,
  Util.class
})
@SuppressStaticInitializationFor({"util.AuthenticationHelper", "util.Global"})
@PowerMockIgnore("javax.management.*")
public abstract class BaseActorTest {

  private static CassandraOperationImpl cassandraOperation = mock(CassandraOperationImpl.class);

  @Before
  public void before() {

    PowerMockito.mockStatic(ServiceFactory.class);
    when(ServiceFactory.getInstance()).thenReturn(cassandraOperation);
    PowerMockito.mockStatic(ElasticSearchUtil.class);
    Keycloak keycloak = mock(Keycloak.class);
    PowerMockito.mockStatic(KeyCloakConnectionProvider.class);
    when(KeyCloakConnectionProvider.getConnection()).thenReturn(keycloak);

    RealmResource realmResource = mock(RealmResource.class);
    UsersResource usersResource = mock(UsersResource.class);
    when(keycloak.realm(Mockito.anyString())).thenReturn(realmResource);
    when(realmResource.users()).thenReturn(usersResource);

    UserResource userResource = mock(UserResource.class);
    when(usersResource.get(Mockito.any())).thenReturn(userResource);

    UserRepresentation userRepresentation = mock(UserRepresentation.class);
    when(userResource.toRepresentation()).thenReturn(userRepresentation);
    cassandraMock();
    elasticSearchMock();
  }

  private void elasticSearchMock() {

    when(ElasticSearchUtil.complexSearch(
            Mockito.any(SearchDTO.class),
            Mockito.eq(ProjectUtil.EsIndex.sunbird.getIndexName()),
            Mockito.anyVararg()))
        .thenReturn(null);

    when(ElasticSearchUtil.getDataByIdentifier(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(getAbstractMethod());
  }

  protected abstract Map<String, Object> getAbstractMethod();

  private void cassandraMock() {

    when(cassandraOperation.updateRecord(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyMap()))
        .thenReturn(getSuccessResponse());

    when(cassandraOperation.getAllRecords(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(getAllRecordResponse());

    when(cassandraOperation.insertRecord(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyMap()))
        .thenReturn(getSuccessResponse());

    when(cassandraOperation.getRecordById(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(getRecordByIdResponse());

    when(cassandraOperation.getRecordById(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList()))
        .thenReturn(getCassandraRecordByIdForBulkUploadResponse());
  }

  protected abstract Response getCassandraRecordByIdForBulkUploadResponse();

  public abstract Response getRecordByIdResponse();

  private static Response getSuccessResponse() {
    Response response = new Response();
    response.put(JsonKey.RESPONSE, JsonKey.SUCCESS);
    return response;
  }

  private static Response getAllRecordResponse() {

    Response response = new Response();
    List<Map<String, Object>> resMapList = new ArrayList<>();
    Map<String, Object> map = new HashMap<>();
    resMapList.add(map);
    response.put(JsonKey.RESPONSE, resMapList);
    return response;
  }
}
