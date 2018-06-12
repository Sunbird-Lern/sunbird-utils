package org.sunbird.util;

import java.util.HashMap;
import java.util.Map;
import org.sunbird.learner.util.AuditOperation;

/**
 * Utility class to perform audit logging
 *
 * @author arvind
 */
public class AuditLogUtil {

  // Map with key ActorOperationType and value of type AuditOperation
  private static Map<String, AuditOperation> initAuditLogUrlMap = new HashMap<>();

  private AuditLogUtil() {}

  public static Map<String, AuditOperation> getInitAuditLogUrlMap() {
    return initAuditLogUrlMap;
  }

  public static void setInitAuditLogUrlMap(Map<String, AuditOperation> initAuditLogUrlMap) {
    AuditLogUtil.initAuditLogUrlMap = initAuditLogUrlMap;
  }
}
