package org.sunbird.common.inf;

import java.util.List;
import java.util.Map;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ProjectUtil.EsType;
import org.sunbird.dto.SearchDTO;
import scala.concurrent.Future;

public interface ElasticSearchUtil {
  /**
   * This method will put a new data entry inside Elastic search. identifier value becomes _id
   * inside ES, so every time provide a unique value while saving it.
   *
   * @param index String ES index name
   * @param type String ES type name
   * @param identifier ES column identifier as an String
   * @param data Map<String,Object>
   * @return String identifier for created data
   */
  public Future<String> createData(
      String index, String type, String identifier, Map<String, Object> data);

  /**
   * This method will update data based on identifier.take the data based on identifier and merge
   * with incoming data then update it.
   *
   * @param index String
   * @param type String
   * @param identifier String
   * @param data Map<String,Object>
   * @return boolean
   */
  public Future<Boolean> updateData(
      String index, String type, String identifier, Map<String, Object> data);

  /**
   * This method will provide data form ES based on incoming identifier. we can get data by passing
   * index and identifier values , or all the three
   *
   * @param type String
   * @param identifier String
   * @return Map<String,Object> or null
   */
  public Future<Map<String, Object>> getDataByIdentifier(
      String index, String type, String identifier);

  /**
   * This method will remove data from ES based on identifier.
   *
   * @param index String
   * @param type String
   * @param identifier String
   */
  public Future<Boolean> removeData(String index, String type, String identifier);

  /**
   * This method will do the data search inside ES. based on incoming search data.
   *
   * @param index String
   * @param type String
   * @param searchData Map<String,Object>
   * @return Map<String,Object>
   */
  public Future<Map<String, Object>> searchData(
      String index, String type, Map<String, Object> searchData);

  /**
   * Method to perform the elastic search on the basis of SearchDTO . SearchDTO contains the search
   * criteria like fields, facets, sort by , filters etc. here user can pass single type to search
   * or multiple type or null
   *
   * @param type var arg of String
   * @return search result as Map.
   */
  public Future<Map<String, Object>> complexSearch(
      SearchDTO searchDTO, String index, String... type);

  /**
   * Method to execute ES query with the limitation of size set to 0 Currently this is a rest call
   *
   * @param index ES indexName
   * @param type ES type
   * @param rawQuery actual query to be executed
   * @return ES response for the query
   */
  public Response searchMetricsData(String index, String type, String rawQuery);

  /**
   * This method will do the health check of elastic search.
   *
   * @return boolean
   */
  public Future<Boolean> healthCheck();

  /**
   * This method will do the bulk data insertion.
   *
   * @param index String index name
   * @param type String type name
   * @param dataList List<Map<String, Object>>
   * @return boolean
   */
  public Future<Boolean> bulkInsertData(
      String index, String type, List<Map<String, Object>> dataList);

  public Future<Map<String, Object>> doAsyncSearch(String index, String type, SearchDTO searchDTO);

  public Future<Boolean> upsertData(
      String index, String type, String identifier, Map<String, Object> data);

  public Future<Map<String, Map<String, Object>>> getEsResultByListOfIds(
      List<String> organisationIds, List<String> fields, EsType organisation);
}
