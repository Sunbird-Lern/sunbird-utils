package org.sunbird.actor.core;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
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
import org.sunbird.actor.service.BaseMWService;
import org.sunbird.actorutil.InterServiceCommunication;
import org.sunbird.actorutil.InterServiceCommunicationFactory;
import org.sunbird.cassandraimpl.CassandraOperationImpl;
import org.sunbird.common.ElasticSearchUtil;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.KeyCloakConnectionProvider;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.datasecurity.DecryptionService;
import org.sunbird.dto.SearchDTO;
import org.sunbird.helper.ServiceFactory;
import org.sunbird.learner.actors.role.dao.impl.RoleDaoImpl;
import org.sunbird.user.dao.UserOrgDao;
import org.sunbird.user.dao.impl.UserOrgDaoImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
  ElasticSearchUtil.class,
  ServiceFactory.class,
  BaseMWService.class,
  RequestRouter.class,
  InterServiceCommunicationFactory.class,
  UserOrgDaoImpl.class,
  RoleDaoImpl.class,
  //        UserServiceImpl.class,
  KeyCloakConnectionProvider.class
})
@SuppressStaticInitializationFor({"util.AuthenticationHelper", "util.Global"})
@PowerMockIgnore("javax.management.*")
@Ignore
public abstract class BaseActorTest {

  private static CassandraOperationImpl cassandraOperation;
  private static InterServiceCommunication interServiceCommunication =
      Mockito.mock(InterServiceCommunication.class);
  private static DecryptionService decryptionService;
  private static Response response;
  private static RoleDaoImpl roleDao;
  private static UserOrgDao userOrgDao;
  private static ActorSelection actorSelection;
  private static ActorRef actorRef;
  //  protected static User user = mock(User.class);

  @BeforeClass
  public static void beforeClass() {
    PowerMockito.mockStatic(ServiceFactory.class);
    cassandraOperation = mock(CassandraOperationImpl.class);
    when(ServiceFactory.getInstance()).thenReturn(cassandraOperation);
  }

  @Before
  public void before() {

    PowerMockito.mockStatic(BaseMWService.class);
    PowerMockito.mockStatic(InterServiceCommunicationFactory.class);
    when(InterServiceCommunicationFactory.getInstance()).thenReturn(interServiceCommunication);
    response = Mockito.mock(Response.class);
    when(interServiceCommunication.getResponse(Mockito.anyObject(), Mockito.anyObject()))
        .thenReturn(response);
    when(response.get(Mockito.anyString())).thenReturn(new HashMap<>());

    PowerMockito.mockStatic(RoleDaoImpl.class);
    roleDao = Mockito.mock(RoleDaoImpl.class);
    when(RoleDaoImpl.getInstance()).thenReturn(roleDao);

    PowerMockito.mockStatic(UserOrgDaoImpl.class);
    userOrgDao = Mockito.mock(UserOrgDaoImpl.class);
    when(UserOrgDaoImpl.getInstance()).thenReturn(userOrgDao);
    when(userOrgDao.updateUserOrg(Mockito.anyObject())).thenReturn(getSuccessResponse());

    decryptionService = Mockito.mock(DecryptionService.class);
    when(decryptionService.decryptData(Mockito.anyMap())).thenReturn(getOrganisationsMap());

    actorSelection = Mockito.mock(ActorSelection.class);
    when(BaseMWService.getRemoteRouter(Mockito.anyString())).thenReturn(actorSelection);

    PowerMockito.mockStatic(RequestRouter.class);
    actorRef = Mockito.mock(ActorRef.class);
    when(RequestRouter.getActor(Mockito.anyString())).thenReturn(actorRef);

    PowerMockito.mockStatic(ElasticSearchUtil.class);
    when(cassandraOperation.getAllRecords(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(getCassandraResponse());

    //    when(cassandraOperation.getRecordById(Mockito.anyString(), Mockito.anyString(),
    // Mockito.anyString()))
    //            .thenReturn(getIdCassandraResponse());

    when(cassandraOperation.updateRecord(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyMap()))
        .thenReturn(getSuccessResponse());

    when(ElasticSearchUtil.complexSearch(
            Mockito.any(SearchDTO.class),
            Mockito.eq(ProjectUtil.EsIndex.sunbird.getIndexName()),
            Mockito.anyVararg()))
        .thenReturn(createResponseGet());

    //    PowerMockito.mockStatic(UserServiceImpl.class);
    //    UserService userService = mock(UserService.class);
    //    when(UserServiceImpl.getInstance()).thenReturn(userService);

    //    when(userService.getUserById(Mockito.anyString())).thenReturn(user);

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
  }

  protected Response getIdCassandraResponse(boolean isDeleted) {

    Response response = new Response();
    List<Map<String, Object>> resMapList = new ArrayList<>();
    Map<String, Object> map = getMapResponse(isDeleted);
    resMapList.add(map);
    response.put(JsonKey.RESPONSE, resMapList);
    return response;
  }

  protected void getCassandraResponseForId(boolean isDeleted) {

    when(cassandraOperation.getRecordById(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(getIdCassandraResponse(isDeleted));
  }

  protected abstract Map<String, Object> getMapResponse(boolean isDeleted);

  protected void resetAllMocks() {
    Mockito.reset(cassandraOperation);
    Mockito.reset(interServiceCommunication);
    Mockito.reset(decryptionService);
    Mockito.reset(response);
    Mockito.reset(roleDao);
    Mockito.reset(userOrgDao);
    Mockito.reset(actorSelection);
    Mockito.reset(actorRef);
    when(ElasticSearchUtil.complexSearch(
            Mockito.any(SearchDTO.class),
            Mockito.eq(ProjectUtil.EsIndex.sunbird.getIndexName()),
            Mockito.anyVararg()))
        .thenReturn(getFailureResponse());
  }

  private Map<String, Object> getFailureResponse() {

    HashMap<String, Object> response = new HashMap<>();
    List<Map<String, Object>> content = new ArrayList<>();
    response.put(JsonKey.CONTENT, content);
    return response;
  }

  protected abstract Map<String, Object> getOrganisationsMap();

  protected abstract Map<String, Object> createResponseGet();

  private Response getCassandraResponse() {
    Response response = new Response();
    List<Map<String, Object>> resMapList = new ArrayList<>();
    Map<String, Object> map = new HashMap<>();
    resMapList.add(map);
    response.put(JsonKey.RESPONSE, resMapList);
    return response;
  }

  private Response getSuccessResponse() {
    Response response = new Response();
    response.put(JsonKey.RESPONSE, JsonKey.SUCCESS);
    return response;
  }
}
