package org.sunbird.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Util class to configure the audit log means what operations have to log for auditing purpose.
 *
 * @author arvind
 */
public class AuditLogUrlUtil {

  // This map will hold ActorOperationType as key and AuditOperation Object as value which contains
  // operation Type and Object Type info.
  private static Map<String, Object> auditLogUrlMap = new HashMap<>();

  private AuditLogUrlUtil() {}

  public static Map<String, Object> getAuditLogUrlMap() {
    return auditLogUrlMap;
  }

  public static void initAuditLogUrl(Map<String, Object> map) {
    auditLogUrlMap = map;
  }
}
