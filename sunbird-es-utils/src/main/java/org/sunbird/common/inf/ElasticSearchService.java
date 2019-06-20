package org.sunbird.common.inf;

import java.util.List;
import java.util.Map;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ProjectUtil.EsType;
import org.sunbird.dto.SearchDTO;
import scala.concurrent.Future;

public interface ElasticSearchService {
  /**
   * This method will put a new data entry inside Elastic search. identifier value becomes _id
   * inside ES, so every time provide a unique value while saving it.
   *
   * @param index String ES index name
   * @param identifier ES column identifier as an String
   * @param data Map<String,Object>
   * @return String identifier for created data
   */
  public Future<String> save(String index, String identifier, Map<String, Object> data);

  /**
   * This method will update data based on identifier.take the data based on identifier and merge
   * with incoming data then update it.
   *
   * @param index String
   * @param identifier String
   * @param data Map<String,Object>
   * @return boolean
   */
  public Future<Boolean> update(String index, String identifier, Map<String, Object> data);

  /**
   * This method will provide data form ES based on incoming identifier. we can get data by passing
   * index and identifier values , or all the three index, identifier and type
   *
   * @param index String
   * @param identifier String
   * @return Map<String,Object> or null
   */
  public Future<Map<String, Object>> getDataByIdentifier(String index, String identifier);

  /**
   * This method will remove data from ES based on identifier.
   *
   * @param index String
   * @param identifier String
   */
  public Future<Boolean> delete(String index, String identifier);

  /**
   * Method to perform the elastic search on the basis of SearchDTO . SearchDTO contains the search
   * criteria like fields, facets, sort by , filters etc. here user can pass single type to search
   * or multiple type or null
   *
   * @param type var arg of String
   * @return search result as Map.
   */
  public Future<Map<String, Object>> search(SearchDTO searchDTO, String index);

  /**
   * Method to execute ES raw query with the limitation of size set to 0 Currently, This is a not a
   * tcp call.
   *
   * @param index ES indexName
   * @param rawQuery actual query to be executed
   * @return Response Object from elastic Search
   */
  public Response searchMetricsData(String index, String rawQuery);

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
   * @param dataList List<Map<String, Object>>
   * @return boolean
   */
  public Future<Boolean> bulkInsert(String index, List<Map<String, Object>> dataList);

  /**
   * This method will upsert data based on identifier.take the data based on identifier and merge
   * with incoming data then update it or if not present already will create it.
   *
   * @param index String
   * @param identifier String
   * @param data Map<String,Object>
   * @return boolean
   */
  public Future<Boolean> upsert(String index, String identifier, Map<String, Object> data);
  /**
   * @param ids List of ids of document
   * @param fields List of fields which needs to captured
   * @return Map<String,Map<String,Object>> It will return a map with id as key and the data from ES
   *     as value
   */
  public Future<Map<String, Map<String, Object>>> getEsResultByListOfIds(
      List<String> organisationIds, List<String> fields, EsType organisation);
}
