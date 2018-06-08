package org.sunbird.util;

import java.util.HashMap;
import java.util.Map;

/** @autho arvind. */
public class AuditLogUrlUtil {

  private static Map<String, Object> auditLogUrlMap = new HashMap<>();

  private AuditLogUrlUtil() {}

  public static Map<String, Object> getAuditLogUrlMap() {
    return auditLogUrlMap;
  }

  public static void initAuditLogUrl(Map<String, Object> map) {
    auditLogUrlMap = map;
  }
}
