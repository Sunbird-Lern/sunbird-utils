package org.sunbird.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.helper.ServiceFactory;

public class CacheLoadingService {
  private CassandraOperation cassandraOperation = ServiceFactory.getInstance();
  private static final String KEY_SPACE_NAME = "sunbird";
  
  
  @SuppressWarnings("unchecked")
  public Map<String, Map<String, Object>> cacheLoader(String tableName) {
    Map<String, Map<String, Object>> map = new HashMap<>();
    try {
      Response response = cassandraOperation.getAllRecords(KEY_SPACE_NAME, tableName);
      List<Map<String, Object>> responseList =
          (List<Map<String, Object>>) response.get(JsonKey.RESPONSE);
      if (null != responseList && !responseList.isEmpty()) {
        for (Map<String, Object> resultMap : responseList) {
          if (tableName.equalsIgnoreCase(JsonKey.PAGE_SECTION)) {
            map.put((String) resultMap.get(JsonKey.ID), resultMap);
          } else {
            String orgId =
                (((String) resultMap.get(JsonKey.ORGANISATION_ID)) == null
                    ? "NA"
                    : (String) resultMap.get(JsonKey.ORGANISATION_ID));
            map.put(orgId + ":" + ((String) resultMap.get(JsonKey.PAGE_NAME)), resultMap);
          }
        }
      }
    } catch (Exception e) {
      ProjectLogger.log(
          "CacheLoadingService:cacheLoader: Exception in retrieving page section " + e.getMessage(), e);
    }
    return map;
  }

}
