package org.sunbird.actor.core;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
import org.sunbird.actorutil.InterServiceCommunication;
import org.sunbird.actorutil.InterServiceCommunicationFactory;
import org.sunbird.cassandraimpl.CassandraOperationImpl;
import org.sunbird.common.ElasticSearchUtil;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.KeyCloakConnectionProvider;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.dto.SearchDTO;
import org.sunbird.helper.ServiceFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
  ElasticSearchUtil.class,
  ServiceFactory.class,
  RequestRouter.class,
  InterServiceCommunicationFactory.class,
  KeyCloakConnectionProvider.class
})
@SuppressStaticInitializationFor({"util.AuthenticationHelper", "util.Global"})
@PowerMockIgnore("javax.management.*")
@Ignore
public abstract class BaseActorTest {

  private static CassandraOperationImpl cassandraOperation;
  private static InterServiceCommunication interServiceCommunication =
      Mockito.mock(InterServiceCommunication.class);

  @BeforeClass
  public static void beforeClass() {
    PowerMockito.mockStatic(ServiceFactory.class);
    cassandraOperation = mock(CassandraOperationImpl.class);
    when(ServiceFactory.getInstance()).thenReturn(cassandraOperation);
  }

  @Before
  public void before() {

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
    updateRecord();
    getAllRecords();
  }

  // ElasticSearch

  protected void complexSearch() {

    when(ElasticSearchUtil.complexSearch(
            Mockito.any(SearchDTO.class),
            Mockito.eq(ProjectUtil.EsIndex.sunbird.getIndexName()),
            Mockito.anyVararg()))
        .thenReturn(esComplexSearchResponse());
  }

  protected void getDataByIdentifier(boolean isSuccess, String userId) {

    when(ElasticSearchUtil.getDataByIdentifier(
            ProjectUtil.EsIndex.sunbird.getIndexName(),
            ProjectUtil.EsType.user.getTypeName(),
            userId))
        .thenReturn(esGetResponse(isSuccess));
  }

  private Response getSuccessResponse() {
    Response response = new Response();
    response.put(JsonKey.RESPONSE, JsonKey.SUCCESS);
    return response;
  }

  // Cassandra

  protected void getRecordById(Response response) {

    when(cassandraOperation.getRecordById(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(response);
  }

  protected void updateRecord() {

    when(cassandraOperation.updateRecord(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyMap()))
        .thenReturn(getSuccessResponse());
  }

  protected void getAllRecords() {

    when(cassandraOperation.getAllRecords(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(getAllRecordResponse());
  }

  private Map<String, Object> esComplexSearchResponse() {
    return null;
  }

  private Response getAllRecordResponse() {

    Response response = new Response();
    List<Map<String, Object>> resMapList = new ArrayList<>();
    Map<String, Object> map = new HashMap<>();
    resMapList.add(map);
    response.put(JsonKey.RESPONSE, resMapList);
    return response;
  }

  private Map<String, Object> esGetResponse(boolean isSuccess) {

    HashMap<String, Object> response = new HashMap<>();
    if (isSuccess) response.put(JsonKey.CONTENT, "Any-content");
    return response;
  }
}
