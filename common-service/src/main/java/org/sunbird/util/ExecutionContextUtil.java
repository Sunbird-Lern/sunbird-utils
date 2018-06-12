package org.sunbird.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.ElasticSearchUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.ProjectUtil.EsType;
import org.sunbird.common.request.ExecutionContext;
import org.sunbird.common.request.Request;

/**
 * Class to provide utility for ExecutionContext like initialize context for various telemetry
 * generation environment like telemetry for actors, telemetry for schedulers.
 *
 * @author arvind
 */
public class ExecutionContextUtil {

  /**
   * Method to initialize the execution context.
   *
   * @param actorMessage represents the request received by the actor
   * @param env represents on which entity operation is processing like user, organisation etc
   */
  public static void initializeContext(Request actorMessage, String env) {

    ExecutionContext context = ExecutionContext.getCurrent();
    Map<String, Object> requestContext = null;
    if (actorMessage.getContext().get(JsonKey.TELEMETRY_CONTEXT) != null) {
      // means request context is already set by some other actor
      requestContext =
          (Map<String, Object>) actorMessage.getContext().get(JsonKey.TELEMETRY_CONTEXT);
    } else {
      requestContext = new HashMap<>();
      // request level info
      Map<String, Object> req = actorMessage.getRequest();
      String requestedBy = (String) req.get(JsonKey.REQUESTED_BY);
      // getting context from request context set y controller read from header
      String channel = (String) actorMessage.getContext().get(JsonKey.CHANNEL);
      requestContext.put(JsonKey.CHANNEL, channel);
      requestContext.put(JsonKey.ACTOR_ID, actorMessage.getContext().get(JsonKey.ACTOR_ID));
      requestContext.put(JsonKey.ACTOR_TYPE, actorMessage.getContext().get(JsonKey.ACTOR_TYPE));
      requestContext.put(JsonKey.ENV, env);
      requestContext.put(JsonKey.REQUEST_ID, actorMessage.getRequestId());
      requestContext.put(JsonKey.REQUEST_TYPE, JsonKey.API_CALL);
      if (JsonKey.USER.equalsIgnoreCase(
          (String) actorMessage.getContext().get(JsonKey.ACTOR_TYPE))) {
        // assign rollup of user ...
        Map<String, Object> result =
            ElasticSearchUtil.getDataByIdentifier(
                ProjectUtil.EsIndex.sunbird.getIndexName(), EsType.user.getTypeName(), requestedBy);
        if (result != null) {
          String rootOrgId = (String) result.get(JsonKey.ROOT_ORG_ID);

          if (StringUtils.isNotBlank(rootOrgId)) {
            Map<String, String> rollup = new HashMap<>();

            rollup.put("l1", rootOrgId);
            requestContext.put(JsonKey.ROLLUP, rollup);
          }
        }
      }
    }
    context.setRequestContext(requestContext);
    // and global context will be set at the time of creation of thread local automatically
  }

  /**
   * Method to initialize the execution context for the scheduler jobs.
   *
   * @param actorType represents the type of actor(requester)
   * @param actorId represents the identity of actor(requester)
   * @param environment env represents on which entity operation is processing like user ,
   *     organisation etc
   */
  public static void initializeContextForSchedulerJob(
      String actorType, String actorId, String environment) {

    ExecutionContext context = ExecutionContext.getCurrent();
    Map<String, Object> requestContext = new HashMap<>();
    requestContext.put(JsonKey.CHANNEL, JsonKey.DEFAULT_ROOT_ORG_ID);
    requestContext.put(JsonKey.ACTOR_ID, actorId);
    requestContext.put(JsonKey.ACTOR_TYPE, actorType);
    requestContext.put(JsonKey.ENV, environment);
    context.setRequestContext(requestContext);
  }
}
