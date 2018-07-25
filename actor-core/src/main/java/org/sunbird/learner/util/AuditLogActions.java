package org.sunbird.learner.util;

import java.util.HashMap;
import java.util.Map;
import org.sunbird.common.models.util.ActorOperations;
import org.sunbird.common.models.util.JsonKey;

public class AuditLogActions {

  public static final Map<String, Object> auditLogUrlMap = new HashMap<String, Object>();

  static {
    auditLogUrlMap.put(
        ActorOperations.CREATE_USER.getValue(), new AuditOperation(JsonKey.USER, JsonKey.CREATE));
    auditLogUrlMap.put(
        ActorOperations.UPDATE_USER.getValue(), new AuditOperation(JsonKey.USER, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.CREATE_ORG.getValue(),
        new AuditOperation(JsonKey.ORGANISATION, JsonKey.CREATE));
    auditLogUrlMap.put(
        ActorOperations.UPDATE_ORG.getValue(),
        new AuditOperation(JsonKey.ORGANISATION, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.UPDATE_ORG_STATUS.getValue(),
        new AuditOperation(JsonKey.ORGANISATION, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.APPROVE_ORGANISATION.getValue(),
        new AuditOperation(JsonKey.ORGANISATION, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.ADD_MEMBER_ORGANISATION.getValue(),
        new AuditOperation(JsonKey.ORGANISATION, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.REMOVE_MEMBER_ORGANISATION.getValue(),
        new AuditOperation(JsonKey.ORGANISATION, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.BLOCK_USER.getValue(), new AuditOperation(JsonKey.USER, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.UNBLOCK_USER.getValue(), new AuditOperation(JsonKey.USER, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.ASSIGN_ROLES.getValue(), new AuditOperation(JsonKey.USER, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.CREATE_BATCH.getValue(), new AuditOperation(JsonKey.BATCH, JsonKey.CREATE));
    auditLogUrlMap.put(
        ActorOperations.UPDATE_BATCH.getValue(), new AuditOperation(JsonKey.BATCH, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.REMOVE_BATCH.getValue(), new AuditOperation(JsonKey.BATCH, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.ADD_USER_TO_BATCH.getValue(),
        new AuditOperation(JsonKey.BATCH, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.REMOVE_USER_FROM_BATCH.getValue(),
        new AuditOperation(JsonKey.BATCH, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.CREATE_NOTE.getValue(), new AuditOperation(JsonKey.USER, JsonKey.CREATE));
    auditLogUrlMap.put(
        ActorOperations.UPDATE_NOTE.getValue(), new AuditOperation(JsonKey.USER, JsonKey.UPDATE));
    auditLogUrlMap.put(
        ActorOperations.DELETE_NOTE.getValue(), new AuditOperation(JsonKey.USER, JsonKey.UPDATE));
  }
}
