package org.sunbird.common.audit;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuditUtil {

  private static final Map<String, List<String>> tablePrimaryKeyMap = new HashMap<>();

  static {
    tablePrimaryKeyMap.put("assessment_eval", Arrays.asList("id"));
    tablePrimaryKeyMap.put("bulk_upload_process_task", Arrays.asList("processid,", "sequenceid"));
    tablePrimaryKeyMap.put("course_management", Arrays.asList("id"));
    tablePrimaryKeyMap.put("url_action", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user_auth", Arrays.asList("id"));
    tablePrimaryKeyMap.put("system_settings", Arrays.asList("id"));
    tablePrimaryKeyMap.put("client_info", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user_courses", Arrays.asList("id"));
    tablePrimaryKeyMap.put("subject", Arrays.asList("id"));
    tablePrimaryKeyMap.put("content_consumption", Arrays.asList("id"));
    tablePrimaryKeyMap.put("role_group", Arrays.asList("id"));
    tablePrimaryKeyMap.put("assessment_item", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user_badge_assertion", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user_skills", Arrays.asList("id"));
    tablePrimaryKeyMap.put("geo_location", Arrays.asList("id"));
    tablePrimaryKeyMap.put("location", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user_org", Arrays.asList("id"));
    tablePrimaryKeyMap.put("media_type", Arrays.asList("id"));
    tablePrimaryKeyMap.put("badge", Arrays.asList("id"));
    tablePrimaryKeyMap.put("master_action", Arrays.asList("id"));
    tablePrimaryKeyMap.put("badge_class_extension", Arrays.asList("id"));
    tablePrimaryKeyMap.put("cassandra_migration_version", Arrays.asList("version"));
    tablePrimaryKeyMap.put("page_management", Arrays.asList("id"));
    tablePrimaryKeyMap.put("course_enrollment", Arrays.asList("id"));
    tablePrimaryKeyMap.put("org_external_identity", Arrays.asList("provider", "externalid"));
    tablePrimaryKeyMap.put("email_template", Arrays.asList("name"));
    tablePrimaryKeyMap.put(
        "usr_external_identity", Arrays.asList("provider", "idtype", "externalid"));
    tablePrimaryKeyMap.put("user_job_profile", Arrays.asList("id"));
    tablePrimaryKeyMap.put("bulk_upload_process", Arrays.asList("id"));
    tablePrimaryKeyMap.put("org_mapping", Arrays.asList("id"));
    tablePrimaryKeyMap.put("address", Arrays.asList("id"));
    tablePrimaryKeyMap.put("report_tracking", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user_example", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user_external_identity", Arrays.asList("id"));
    tablePrimaryKeyMap.put("tenant_preference", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user_action_role", Arrays.asList("id"));
    tablePrimaryKeyMap.put("cassandra_migration_version_counts", Arrays.asList("name"));
    tablePrimaryKeyMap.put("skills", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user_notes", Arrays.asList("id"));
    tablePrimaryKeyMap.put("rate_limit", Arrays.asList("key", "unit"));
    tablePrimaryKeyMap.put("course_publish_status", Arrays.asList("id"));
    tablePrimaryKeyMap.put("action_group", Arrays.asList("id"));
    tablePrimaryKeyMap.put("org_type", Arrays.asList("id"));
    tablePrimaryKeyMap.put("course_batch", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user_education", Arrays.asList("id"));
    tablePrimaryKeyMap.put("content_badge_association", Arrays.asList("id"));
    tablePrimaryKeyMap.put("user_badge", Arrays.asList("id"));
    tablePrimaryKeyMap.put("organisation", Arrays.asList("id"));
    tablePrimaryKeyMap.put("page_section", Arrays.asList("id"));
  }

  public static Map<String, Object> getAuditEvent(Map<String, Object> triggerMap) {
    Map<String, Object> resultMap = getIdentifier(triggerMap);
    resultMap.put("ets", System.currentTimeMillis());
    String operationType = (String) triggerMap.remove("operationType");
    String objectType = (String) triggerMap.remove("objectType");
    getFormattedEvent(triggerMap, resultMap);

    resultMap.put("operationType", operationType);
    resultMap.put("eventType", "transactional");
    resultMap.put("userId", "ANONYMOUS");
    resultMap.put("createdOn", Instant.now().toString());
    resultMap.put("objectType", objectType);
    return resultMap;
  }

  private static Map<String, Object> getIdentifier(Map<String, Object> triggerMap) {
    Map<String, Object> resultMap = new HashMap<>();
    String tableName = (String) triggerMap.get("objectType");
    if (tablePrimaryKeyMap.get(tableName) != null) {
      List<String> primaryKeyList = tablePrimaryKeyMap.get(tableName);
      if (primaryKeyList.size() == 1) {
        resultMap.put("identifier", triggerMap.get(primaryKeyList.get(0)));
      } else {
        Map<String, Object> hashMap = new HashMap<>();
        for (int i = 0; i < primaryKeyList.size(); i++) {
          String key = primaryKeyList.get(i);
          if (triggerMap.get(key) != null) {
            hashMap.put(key, triggerMap.get(key));
          }
        }
        resultMap.put("identifier", hashMap);
      }
    }
    return resultMap;
  }

  private static void getFormattedEvent(
      Map<String, Object> triggerMap, Map<String, Object> resultMap) {
    Map<String, Object> newMap = new HashMap<>();
    for (Map.Entry<String, Object> set : triggerMap.entrySet()) {
      Map<String, Object> newValueMap = new HashMap<>();
      newValueMap.put("nv", set.getValue());
      newMap.put(set.getKey(), newValueMap);
    }
    Map<String, Object> eventMap = new HashMap<>();
    eventMap.put("properties", newMap);
    resultMap.put("event", eventMap);
  }
}
