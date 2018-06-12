/** */
package org.sunbird.common;

import static org.sunbird.common.models.util.ProjectUtil.isNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.ProjectUtil.EsIndex;
import org.sunbird.common.models.util.ProjectUtil.EsType;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.dto.SearchDTO;
import org.sunbird.helper.ConnectionManager;
import org.sunbird.helper.ElasticSearchMapping;
import org.sunbird.helper.ElasticSearchSettings;

/**
 * This class will provide all required operation for elastic search.
 *
 * @author arvind
 * @author Manzarul
 */
public class ElasticSearchUtil {

  private static final String LTE = "<=";
  private static final String LT = "<";
  private static final String GTE = ">=";
  private static final String GT = ">";
  private static final String ASC_ORDER = "ASC";
  private static final String STARTS_WITH = "startsWith";
  private static final String ENDS_WITH = "endsWith";
  private static final List<String> upsertResults =
      new ArrayList<>(Arrays.asList("CREATED", "UPDATED", "NOOP"));
  private static final String SOFT_MODE = "soft";
  private static final String RAW_APPEND = ".raw";
  private static Map<String, Boolean> indexMap = new HashMap<>();
  private static Map<String, Boolean> typeMap = new HashMap<>();

  private ElasticSearchUtil() {}
  
  static {
	  createIndices();
	  createIndexTypes();
  }

  
  private static void createIndices() {
	  try {
		  for (EsIndex index: EsIndex.values()) {
			boolean isExist = ConnectionManager.getClient().admin().indices().exists(Requests.indicesExistsRequest(index.getIndexName())).get()
			  .isExists();
			if (!isExist) {
	            indexMap.put(index.getIndexName(), true);
			} else {
	            boolean created =
	                createIndex(index.getIndexName(), null, null, ElasticSearchSettings.createSettingsForIndex());
	            if (created) {
	              indexMap.put(index.getIndexName(), true);
	            }
	          }
		  } 
	  } catch (Exception e) {
			e.printStackTrace();
	  }
  }
  
  private static void createIndexTypes() {
	  String[] types = Arrays.stream(EsType.values()).map(f -> f.getTypeName()).toArray(String[]::new);
	  for (EsIndex index: EsIndex.values()) {
		  verifyOrCreatType(index.getIndexName(), types);
	  }
  }
  
  
  
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
  public static String createData(
      String index, String type, String identifier, Map<String, Object> data) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil createData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    if (StringUtils.isBlank(identifier)
        || StringUtils.isBlank(type)
        || StringUtils.isBlank(index)) {
      ProjectLogger.log("Identifier value is null or empty ,not able to save data.");
      return "ERROR";
    }
    verifyOrCreateIndexAndType(index, type);
    try {
      data.put("identifier", identifier);
      IndexResponse response =
          ConnectionManager.getClient().prepareIndex(index, type, identifier).setSource(data).get();
      ProjectLogger.log(
          "Save value==" + response.getId() + " " + response.status(), LoggerEnum.INFO.name());
      ProjectLogger.log(
          "ElasticSearchUtil createData method end at =="
              + System.currentTimeMillis()
              + " for Type "
              + type
              + " ,Total time elapsed = "
              + calculateEndTime(startTime),
          LoggerEnum.PERF_LOG);
      return response.getId();
    } catch (Exception e) {
      ProjectLogger.log("Error while saving " + type + " id : " + identifier, e);
      ProjectLogger.log(
          "ElasticSearchUtil createData method end at =="
              + System.currentTimeMillis()
              + " for Type "
              + type
              + " ,Total time elapsed = "
              + calculateEndTime(startTime),
          LoggerEnum.PERF_LOG);
      return "";
    }
  }

  /**
   * This method will provide data form ES based on incoming identifier. we can get data by passing
   * index and identifier values , or all the three
   *
   * @param type String
   * @param identifier String
   * @return Map<String,Object> or null
   */
  public static Map<String, Object> getDataByIdentifier(
      String index, String type, String identifier) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil getDataByIdentifier method started at =="
            + startTime
            + " for Type "
            + type,
        LoggerEnum.PERF_LOG);
    GetResponse response = null;
    if (StringUtils.isBlank(index) || StringUtils.isBlank(identifier)) {
      ProjectLogger.log("Invalid request is coming.");
      return new HashMap<>();
    } else if (StringUtils.isBlank(type)) {
      response = ConnectionManager.getClient().prepareGet().setIndex(index).setId(identifier).get();
    } else {
      response = ConnectionManager.getClient().prepareGet(index, type, identifier).get();
    }
    if (response == null || null == response.getSource()) {
      return new HashMap<>();
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtil getDataByIdentifier method end at =="
            + stopTime
            + " for Type "
            + type
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response.getSource();
  }

  /**
   * This method will do the data search inside ES. based on incoming search data.
   *
   * @param index String
   * @param type String
   * @param searchData Map<String,Object>
   * @return Map<String,Object>
   */
  public static Map<String, Object> searchData(
      String index, String type, Map<String, Object> searchData) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil searchData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    Iterator<Entry<String, Object>> itr = searchData.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<String, Object> entry = itr.next();
      sourceBuilder.query(QueryBuilders.commonTermsQuery(entry.getKey(), entry.getValue()));
    }
    SearchResponse sr = null;
    try {
      verifyOrCreateIndexAndType(index, type);
      sr =
          ConnectionManager.getClient()
              .search(new SearchRequest(index).types(type).source(sourceBuilder))
              .get();
    } catch (InterruptedException e) {
      ProjectLogger.log("Error, interrupted while connecting to Elasticsearch", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      ProjectLogger.log("Error while execution in Elasticsearch", e);
    }
    if (sr.getHits() == null || sr.getHits().getTotalHits() == 0) {
      return new HashMap<>();
    }
    sr.getHits().getAt(0).getSource();
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtil searchData method end at =="
            + stopTime
            + " for Type "
            + type
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return sr.getAggregations().asList().get(0).getMetaData();
  }

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
  public static boolean updateData(
      String index, String type, String identifier, Map<String, Object> data) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil updateData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    if (!StringUtils.isBlank(index)
        && !StringUtils.isBlank(type)
        && !StringUtils.isBlank(identifier)
        && data != null) {
      verifyOrCreateIndexAndType(index, type);
      try {
        UpdateResponse response =
            ConnectionManager.getClient().prepareUpdate(index, type, identifier).setDoc(data).get();
        ProjectLogger.log(
            "updated response==" + response.getResult().name(), LoggerEnum.INFO.name());
        if (response.getResult().name().equals("UPDATED")) {
          long stopTime = System.currentTimeMillis();
          long elapsedTime = stopTime - startTime;
          ProjectLogger.log(
              "ElasticSearchUtil updateData method end at =="
                  + stopTime
                  + " for Type "
                  + type
                  + " ,Total time elapsed = "
                  + elapsedTime,
              LoggerEnum.PERF_LOG);
          return true;
        }
      } catch (Exception e) {
        ProjectLogger.log(e.getMessage(), e);
      }
    } else {
      ProjectLogger.log("Requested data is invalid.");
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtil updateData method end at =="
            + stopTime
            + " for Type "
            + type
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return false;
  }

  /**
   * This method will upser data based on identifier.take the data based on identifier and merge
   * with incoming data then update it and if identifier does not exist , it will insert data .
   *
   * @param index String
   * @param type String
   * @param identifier String
   * @param data Map<String,Object>
   * @return boolean
   */
  public static boolean upsertData(
      String index, String type, String identifier, Map<String, Object> data) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil upsertData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    if (!StringUtils.isBlank(index)
        && !StringUtils.isBlank(type)
        && !StringUtils.isBlank(identifier)
        && data != null
        && data.size() > 0) {
      verifyOrCreateIndexAndType(index, type);
      IndexRequest indexRequest = new IndexRequest(index, type, identifier).source(data);
      UpdateRequest updateRequest =
          new UpdateRequest(index, type, identifier).doc(data).upsert(indexRequest);
      UpdateResponse response = null;
      try {
        response = ConnectionManager.getClient().update(updateRequest).get();
      } catch (InterruptedException | ExecutionException e) {
        ProjectLogger.log(e.getMessage(), e);
        return false;
      }
      ProjectLogger.log("updated response==" + response.getResult().name());
      if (upsertResults.contains(response.getResult().name())) {
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        ProjectLogger.log(
            "ElasticSearchUtil upsertData method end at =="
                + stopTime
                + " for Type "
                + type
                + " ,Total time elapsed = "
                + elapsedTime,
            LoggerEnum.PERF_LOG);
        return true;
      }
    } else {
      ProjectLogger.log("Requested data is invalid.");
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtil upsertData method end at =="
            + stopTime
            + " for Type "
            + type
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return false;
  }

  /**
   * This method will remove data from ES based on identifier.
   *
   * @param index String
   * @param type String
   * @param identifier String
   */
  public static boolean removeData(String index, String type, String identifier) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil removeData method started at ==" + startTime, LoggerEnum.PERF_LOG);
    DeleteResponse deleteResponse = null;
    if (!StringUtils.isBlank(index)
        && !StringUtils.isBlank(type)
        && !StringUtils.isBlank(identifier)) {
      try {
        deleteResponse = ConnectionManager.getClient().prepareDelete(index, type, identifier).get();
        ProjectLogger.log(
            "delete info ==" + deleteResponse.getResult().name() + " " + deleteResponse.getId());
      } catch (Exception e) {
        ProjectLogger.log(e.getMessage(), e);
      }
    } else {
      ProjectLogger.log("Data can not be deleted due to invalid input.");
      return false;
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtil removeData method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);

    return (deleteResponse.getResult().name().equalsIgnoreCase("DELETED"));
  }

  /**
   * This method will create index , type ,setting and mapping.
   *
   * @param index String index name
   * @param type String type name
   * @param mappings String
   * @param settings String
   * @return boolean
   */
  @SuppressWarnings("deprecation")
  public static boolean createIndex(String index, String type, String mappings, String settings) {
    boolean response = false;
    if (StringUtils.isBlank(index)) {
      return response;
    }
    CreateIndexResponse createIndexResponse = null;
    TransportClient client = ConnectionManager.getClient();
    try {
      CreateIndexRequestBuilder createIndexBuilder = client.admin().indices().prepareCreate(index);
      if (!StringUtils.isBlank(settings)) {
        createIndexResponse = createIndexBuilder.setSettings(settings).get();
      } else {
        createIndexResponse = createIndexBuilder.get();
      }
      if (createIndexResponse != null && createIndexResponse.isAcknowledged()) {
        response = true;
        if (!StringUtils.isBlank(mappings) && !StringUtils.isBlank(type)) {
          PutMappingResponse mappingResponse =
              client
                  .admin()
                  .indices()
                  .preparePutMapping(index)
                  .setType(type)
                  .setSource(mappings)
                  .get();
          if (mappingResponse.isAcknowledged()) {
            response = true;
          } else {
            response = false;
          }
        } else if (!StringUtils.isBlank(type)) {
          PutMappingResponse mappingResponse =
              client.admin().indices().preparePutMapping(index).setType(type).get();
          if (mappingResponse.isAcknowledged()) {
            response = true;
          } else {
            response = false;
          }
        }
      }
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
      response = false;
    }
    ProjectLogger.log("Index creation status==" + response, LoggerEnum.INFO.name());
    return response;
  }

  /**
   * This method will update type and mapping under already created index.
   *
   * @param indexName String
   * @param typeName String
   * @param mapping String
   * @return boolean
   */
  @SuppressWarnings("deprecation")
  public static boolean addOrUpdateMapping(String indexName, String typeName, String mapping) {
    try {
      PutMappingResponse response =
          ConnectionManager.getClient()
              .admin()
              .indices()
              .preparePutMapping(indexName)
              .setType(typeName)
              .setSource(mapping)
              .get();
      if (response.isAcknowledged()) {
        return true;
      }
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    return false;
  }

  /**
   * This method will delete the index
   *
   * @param index String name of the index which we need to delete.
   * @return boolean
   */
  public static boolean deleteIndex(String index) {
    boolean response = false;
    DeleteIndexResponse deleteResponse =
        ConnectionManager.getClient().admin().indices().prepareDelete(index).get();
    if (deleteResponse != null && deleteResponse.isAcknowledged()) {
      response = true;
    }
    return response;
  }

  /**
   * Method to perform the elastic search on the basis of SearchDTO . SearchDTO contains the search
   * criteria like fields, facets, sort by , filters etc. here user can pass single type to search
   * or multiple type or null
   *
   * @param type var arg of String
   * @return search result as Map.
   */
  public static Map<String, Object> complexSearch(
      SearchDTO searchDTO, String index, String... type) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil complexSearch method started at ==" + startTime, LoggerEnum.PERF_LOG);
    SearchRequestBuilder searchRequestBuilder =
        getSearchBuilder(ConnectionManager.getClient(), index, type);
    // check mode and set constraints
    Map<String, Float> constraintsMap = getConstraints(searchDTO);

    BoolQueryBuilder query = new BoolQueryBuilder();

    // add channel field as mandatory
    String channel = PropertiesCache.getInstance().getProperty(JsonKey.SUNBIRD_ES_CHANNEL);
    if (!(StringUtils.isBlank(channel) || JsonKey.SUNBIRD_ES_CHANNEL.equals(channel))) {
      query.must(createMatchQuery(JsonKey.CHANNEL, channel, constraintsMap.get(JsonKey.CHANNEL)));
    }

    // apply simple query string
    if (!StringUtils.isBlank(searchDTO.getQuery())) {
      query.must(QueryBuilders.simpleQueryStringQuery(searchDTO.getQuery()).field("all_fields"));
    }
    // apply the sorting
    if (searchDTO.getSortBy() != null && searchDTO.getSortBy().size() > 0) {
      for (Map.Entry<String, String> entry : searchDTO.getSortBy().entrySet()) {
        searchRequestBuilder.addSort(entry.getKey() + RAW_APPEND, getSortOrder(entry.getValue()));
      }
    }

    // apply the fields filter
    searchRequestBuilder.setFetchSource(
        searchDTO.getFields() != null
            ? searchDTO.getFields().stream().toArray(String[]::new)
            : null,
        searchDTO.getExcludedFields() != null
            ? searchDTO.getExcludedFields().stream().toArray(String[]::new)
            : null);

    // setting the offset
    if (searchDTO.getOffset() != null) {
      searchRequestBuilder.setFrom(searchDTO.getOffset());
    }

    // setting the limit
    if (searchDTO.getLimit() != null) {
      searchRequestBuilder.setSize(searchDTO.getLimit());
    }
    // apply additional properties
    if (searchDTO.getAdditionalProperties() != null
        && searchDTO.getAdditionalProperties().size() > 0) {
      for (Map.Entry<String, Object> entry : searchDTO.getAdditionalProperties().entrySet()) {
        addAdditionalProperties(query, entry, constraintsMap);
      }
    }

    // set final query to search request builder
    searchRequestBuilder.setQuery(query);
    List finalFacetList = new ArrayList();

    if (null != searchDTO.getFacets() && !searchDTO.getFacets().isEmpty()) {
      addAggregations(searchRequestBuilder, searchDTO.getFacets());
    }
    ProjectLogger.log("calling search builder======" + searchRequestBuilder.toString());
    SearchResponse response = null;
    response = searchRequestBuilder.execute().actionGet();
    List<Map<String, Object>> esSource = new ArrayList<>();
    Map<String, Object> responsemap = new HashMap<>();
    long count = 0;
    if (response != null) {
      SearchHits hits = response.getHits();
      count = hits.getTotalHits();

      for (SearchHit hit : hits) {
        esSource.add(hit.getSource());
      }

      // fetch aggregations aggregations
      if (null != searchDTO.getFacets() && !searchDTO.getFacets().isEmpty()) {
        Map<String, String> m1 = searchDTO.getFacets().get(0);
        for (Map.Entry<String, String> entry : m1.entrySet()) {
          String field = entry.getKey();
          String aggsType = entry.getValue();
          List<Object> aggsList = new ArrayList<>();
          Map facetMap = new HashMap();
          if (JsonKey.DATE_HISTOGRAM.equalsIgnoreCase(aggsType)) {
            Histogram agg = response.getAggregations().get(field);
            for (Histogram.Bucket ent : agg.getBuckets()) {
              // DateTime key = (DateTime) ent.getKey(); // Key
              String keyAsString = ent.getKeyAsString(); // Key as String
              long docCount = ent.getDocCount(); // Doc count
              Map internalMap = new HashMap();
              internalMap.put(JsonKey.NAME, keyAsString);
              internalMap.put(JsonKey.COUNT, docCount);
              aggsList.add(internalMap);
            }
          } else {
            Terms aggs = response.getAggregations().get(field);
            for (Bucket bucket : aggs.getBuckets()) {
              Map internalMap = new HashMap();
              internalMap.put(JsonKey.NAME, bucket.getKey());
              internalMap.put(JsonKey.COUNT, bucket.getDocCount());
              aggsList.add(internalMap);
            }
          }
          facetMap.put("values", aggsList);
          facetMap.put(JsonKey.NAME, field);
          finalFacetList.add(facetMap);
        }
      }
    }
    responsemap.put(JsonKey.CONTENT, esSource);
    if (!(finalFacetList.isEmpty())) {
      responsemap.put(JsonKey.FACETS, finalFacetList);
    }
    responsemap.put(JsonKey.COUNT, count);
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtil complexSearch method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return responsemap;
  }

  private static void addAggregations(
      SearchRequestBuilder searchRequestBuilder, List<Map<String, String>> facets) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil addAggregations method started at ==" + startTime, LoggerEnum.PERF_LOG);
    Map<String, String> map = facets.get(0);
    for (Map.Entry<String, String> entry : map.entrySet()) {

      String key = entry.getKey();
      String value = entry.getValue();
      if (JsonKey.DATE_HISTOGRAM.equalsIgnoreCase(value)) {
        searchRequestBuilder.addAggregation(
            AggregationBuilders.dateHistogram(key)
                .field(key + RAW_APPEND)
                .dateHistogramInterval(DateHistogramInterval.days(1)));

      } else if (null == value) {
        searchRequestBuilder.addAggregation(AggregationBuilders.terms(key).field(key + RAW_APPEND));
      }
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtil addAggregations method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
  }

  private static Map<String, Float> getConstraints(SearchDTO searchDTO) {
    if (null != searchDTO.getSoftConstraints() && !searchDTO.getSoftConstraints().isEmpty()) {
      return searchDTO
          .getSoftConstraints()
          .entrySet()
          .stream()
          .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().floatValue()));
    }
    return Collections.emptyMap();
  }

  private static SearchRequestBuilder getSearchBuilder(
      TransportClient client, String index, String... type) {

    if (type == null || type.length == 0) {
      return client.prepareSearch().setIndices(index);
    } else {
      return client.prepareSearch().setIndices(index).setTypes(type);
    }
  }

  /** Method to add the additional search query like range query , exists - not exist filter etc. */
  @SuppressWarnings("unchecked")
  private static void addAdditionalProperties(
      BoolQueryBuilder query, Entry<String, Object> entry, Map<String, Float> constraintsMap) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil addAdditionalProperties method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    String key = entry.getKey();

    if (key.equalsIgnoreCase(JsonKey.FILTERS)) {

      Map<String, Object> filters = (Map<String, Object>) entry.getValue();
      for (Map.Entry<String, Object> en : filters.entrySet()) {
        createFilterESOpperation(en, query, constraintsMap);
      }
    } else if (key.equalsIgnoreCase(JsonKey.EXISTS) || key.equalsIgnoreCase(JsonKey.NOT_EXISTS)) {
      createESOpperation(entry, query, constraintsMap);
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtil addAdditionalProperties method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
  }

  /** Method to create CommonTermQuery , multimatch and Range Query. */
  @SuppressWarnings("unchecked")
  private static void createFilterESOpperation(
      Entry<String, Object> entry, BoolQueryBuilder query, Map<String, Float> constraintsMap) {

    String key = entry.getKey();
    Object val = entry.getValue();
    if (val instanceof List) {
      if (!((List) val).isEmpty()) {
        if (((List) val).get(0) instanceof String) {
          ((List<String>) val).replaceAll(String::toLowerCase);
          query.must(
              createTermsQuery(key + RAW_APPEND, (List<String>) val, constraintsMap.get(key)));
        } else {
          query.must(createTermsQuery(key, (List) val, constraintsMap.get(key)));
        }
      }
    } else if (val instanceof Map) {
      Map<String, Object> value = (Map<String, Object>) val;
      Map<String, Object> rangeOperation = new HashMap<>();
      Map<String, Object> lexicalOperation = new HashMap<>();
      for (Map.Entry<String, Object> it : value.entrySet()) {
        String operation = it.getKey();
        if (operation.startsWith(LT) || operation.startsWith(GT)) {
          rangeOperation.put(operation, it.getValue());
        } else if (operation.startsWith(STARTS_WITH) || operation.startsWith(ENDS_WITH)) {
          lexicalOperation.put(operation, it.getValue());
        }
      }
      if (!(rangeOperation.isEmpty())) {
        query.must(createRangeQuery(key, rangeOperation, constraintsMap.get(key)));
      }
      if (!(lexicalOperation.isEmpty())) {
        query.must(createLexicalQuery(key, lexicalOperation, constraintsMap.get(key)));
      }

    } else if (val instanceof String) {
      query.must(
          createTermQuery(key + RAW_APPEND, ((String) val).toLowerCase(), constraintsMap.get(key)));
    } else {
      query.must(createTermQuery(key + RAW_APPEND, val, constraintsMap.get(key)));
    }
  }

  /** Method to create EXISTS and NOT EXIST FILTER QUERY . */
  @SuppressWarnings("unchecked")
  private static void createESOpperation(
      Entry<String, Object> entry, BoolQueryBuilder query, Map<String, Float> constraintsMap) {

    String operation = entry.getKey();
    List<String> existsList = (List<String>) entry.getValue();

    if (operation.equalsIgnoreCase(JsonKey.EXISTS)) {
      for (String name : existsList) {
        query.must(createExistQuery(name, constraintsMap.get(name)));
      }
    } else if (operation.equalsIgnoreCase(JsonKey.NOT_EXISTS)) {
      for (String name : existsList) {
        query.mustNot(createExistQuery(name, constraintsMap.get(name)));
      }
    }
  }

  /** Method to return the sorting order on basis of string param . */
  private static SortOrder getSortOrder(String value) {
    return value.equalsIgnoreCase(ASC_ORDER) ? SortOrder.ASC : SortOrder.DESC;
  }

  /**
   * This method will check indices is already created or not.
   *
   * @param indices String
   */
  private static void verifyOrCreateIndex(String... indices) {
    for (String index : indices) {
      if (!indexMap.containsKey(index)) {
        try {
          boolean indexResponse =
              ConnectionManager.getClient()
                  .admin()
                  .indices()
                  .exists(Requests.indicesExistsRequest(index))
                  .get()
                  .isExists();
          if (indexResponse) {
            indexMap.put(index, true);
          } else {
            boolean createIndexResp =
                createIndex(index, null, null, ElasticSearchSettings.createSettingsForIndex());
            if (createIndexResp) {
              indexMap.put(index, true);
            }
          }
        } catch (InterruptedException | ExecutionException e) {
          boolean createIndexResp =
              createIndex(index, null, null, ElasticSearchSettings.createSettingsForIndex());
          if (createIndexResp) {
            indexMap.put(index, true);
          }
        }
      }
    }
  }

  /**
   * This method will check types are created or not.
   *
   * @param indices String
   * @param types String var arg
   */
  private static void verifyOrCreatType(String indices, String... types) {
    for (String type : types) {
      if (!typeMap.containsKey(type)) {
        TypesExistsRequest typesExistsRequest =
            new TypesExistsRequest(new String[] {indices}, type);
        try {
          boolean typeResponse =
              ConnectionManager.getClient()
                  .admin()
                  .indices()
                  .typesExists(typesExistsRequest)
                  .get()
                  .isExists();
          if (typeResponse) {
            typeMap.put(type, true);
          } else {
            boolean response =
                addOrUpdateMapping(indices, type, ElasticSearchMapping.createMapping());
            if (response) {
              typeMap.put(type, true);
            }
          }
        } catch (InterruptedException | ExecutionException e) {
          ProjectLogger.log(e.getMessage(), e);
          boolean response =
              addOrUpdateMapping(indices, type, ElasticSearchMapping.createMapping());
          ProjectLogger.log("addOrUpdateMapping method call response ==" + response);
          if (response) {
            typeMap.put(type, true);
          }
        }
      }
    }
  }

  /**
   * Method to create the index and type with provided setting and mapping.
   *
   * @param index String
   * @param type String
   * @return boolean
   */
  private static boolean verifyOrCreateIndexAndType(String index, String type) {
//    if (indexMap.containsKey(index)) {
//      if (typeMap.containsKey(type)) {
//        return true;
//      }
//      verifyOrCreatType(index, type);
//      return true;
//    } else {
//      verifyOrCreateIndex(index);
//      verifyOrCreatType(index, type);
//      return true;
//    }
	  return true;
  }

  private static MatchQueryBuilder createMatchQuery(String name, Object text, Float boost) {
    if (isNotNull(boost)) {
      return QueryBuilders.matchQuery(name, text).boost(boost);
    } else {
      return QueryBuilders.matchQuery(name, text);
    }
  }

  private static TermsQueryBuilder createTermsQuery(String key, List values, Float boost) {
    if (isNotNull(boost)) {
      return QueryBuilders.termsQuery(key, (values).stream().toArray(Object[]::new)).boost(boost);
    } else {
      return QueryBuilders.termsQuery(key, (values).stream().toArray(Object[]::new));
    }
  }

  private static RangeQueryBuilder createRangeQuery(
      String name, Map<String, Object> rangeOperation, Float boost) {

    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name + RAW_APPEND);
    for (Map.Entry<String, Object> it : rangeOperation.entrySet()) {
      if (it.getKey().equalsIgnoreCase(LTE)) {
        rangeQueryBuilder.lte(it.getValue());
      } else if (it.getKey().equalsIgnoreCase(LT)) {
        rangeQueryBuilder.lt(it.getValue());
      } else if (it.getKey().equalsIgnoreCase(GTE)) {
        rangeQueryBuilder.gte(it.getValue());
      } else if (it.getKey().equalsIgnoreCase(GT)) {
        rangeQueryBuilder.gt(it.getValue());
      }
    }
    if (isNotNull(boost)) {
      return rangeQueryBuilder.boost(boost);
    }
    return rangeQueryBuilder;
  }

  private static TermQueryBuilder createTermQuery(String name, Object text, Float boost) {
    if (isNotNull(boost)) {
      return QueryBuilders.termQuery(name, text).boost(boost);
    } else {
      return QueryBuilders.termQuery(name, text);
    }
  }

  private static ExistsQueryBuilder createExistQuery(String name, Float boost) {
    if (isNotNull(boost)) {
      return QueryBuilders.existsQuery(name).boost(boost);
    } else {
      return QueryBuilders.existsQuery(name);
    }
  }

  private static QueryBuilder createLexicalQuery(
      String key, Map<String, Object> rangeOperation, Float boost) {
    QueryBuilder queryBuilder = null;
    for (Map.Entry<String, Object> it : rangeOperation.entrySet()) {
      if (it.getKey().equalsIgnoreCase(STARTS_WITH)) {
        if (isNotNull(boost)) {
          queryBuilder =
              QueryBuilders.prefixQuery(key + RAW_APPEND, (String) it.getValue()).boost(boost);
        }
        queryBuilder = QueryBuilders.prefixQuery(key + RAW_APPEND, (String) it.getValue());
      } else if (it.getKey().equalsIgnoreCase(ENDS_WITH)) {
        String endsWithRegex = "~" + it.getValue();
        if (isNotNull(boost)) {
          queryBuilder = QueryBuilders.regexpQuery(key + RAW_APPEND, endsWithRegex).boost(boost);
        }
        queryBuilder = QueryBuilders.regexpQuery(key + RAW_APPEND, endsWithRegex);
      }
    }
    return queryBuilder;
  }

  /**
   * This method will do the bulk data insertion.
   *
   * @param index String index name
   * @param type String type name
   * @param dataList List<Map<String, Object>>
   * @return boolean
   */
  public static boolean bulkInsertData(
      String index, String type, List<Map<String, Object>> dataList) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil bulkInsertData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    boolean response = true;
    try {
      BulkProcessor bulkProcessor =
          BulkProcessor.builder(
                  ConnectionManager.getClient(),
                  new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId, BulkRequest request) {
                      // doing the verification for index and type here.
                      verifyOrCreateIndexAndType(index, type);
                    }

                    @Override
                    public void afterBulk(
                        long executionId, BulkRequest request, BulkResponse response) {
                      Iterator<BulkItemResponse> bulkResponse = response.iterator();
                      if (bulkResponse != null) {
                        while (bulkResponse.hasNext()) {
                          BulkItemResponse bResponse = bulkResponse.next();
                          ProjectLogger.log(
                              "Bulk insert api response==="
                                  + bResponse.getId()
                                  + " "
                                  + bResponse.isFailed());
                        }
                      }
                    }

                    @Override
                    public void afterBulk(
                        long executionId, BulkRequest request, Throwable failure) {
                      ProjectLogger.log("Bulk upload error block", failure);
                    }
                  })
              .setBulkActions(10000)
              .setConcurrentRequests(0)
              .build();

      for (Map<String, Object> map : dataList) {
        map.put(JsonKey.IDENTIFIER, map.get(JsonKey.ID));
        IndexRequest request =
            new IndexRequest(index, type, (String) map.get(JsonKey.IDENTIFIER)).source(map);
        bulkProcessor.add(request);
      }
      // Flush any remaining requests
      bulkProcessor.flush();

      // Or close the bulkProcessor if you don't need it anymore
      bulkProcessor.close();

      // Refresh your indices
      ConnectionManager.getClient().admin().indices().prepareRefresh().get();
    } catch (Exception e) {
      response = false;
      ProjectLogger.log(e.getMessage(), e);
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtil bulkInsertData method end at =="
            + stopTime
            + " for Type "
            + type
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  /**
   * This method will do the health check of elastic search.
   *
   * @return boolean
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public static boolean healthCheck() throws InterruptedException, ExecutionException {
    boolean indexResponse = false;
    indexResponse =
        ConnectionManager.getClient()
            .admin()
            .indices()
            .exists(Requests.indicesExistsRequest(ProjectUtil.EsIndex.sunbird.getIndexName()))
            .get()
            .isExists();
    return indexResponse;
  }

  /**
   * Method to execute ES query with the limitation of size set to 0 Currently this is a rest call
   *
   * @param index ES indexName
   * @param type ES type
   * @param rawQuery actual query to be executed
   * @return ES response for the query
   */
  @SuppressWarnings("unchecked")
  public static Response searchMetricsData(String index, String type, String rawQuery) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log("Metrics search method started at ==" + startTime, LoggerEnum.PERF_LOG);
    String baseUrl = null;
    if (!StringUtils.isBlank(System.getenv(JsonKey.SUNBIRD_ES_IP))) {
      String envHost = System.getenv(JsonKey.SUNBIRD_ES_IP);
      String[] host = envHost.split(",");
      baseUrl =
          "http://"
              + host[0]
              + ":"
              + PropertiesCache.getInstance().getProperty(JsonKey.ES_METRICS_PORT);
    } else {
      ProjectLogger.log("ES URL from Properties file");
      baseUrl = PropertiesCache.getInstance().getProperty(JsonKey.ES_URL);
    }
    String requestURL = baseUrl + "/" + index + "/" + type + "/" + "_search";
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    Map<String, Object> responseData = new HashMap<>();
    try {
      // TODO:Currently this is making a rest call but needs to be modified to make
      // the call using
      // ElasticSearch client
      String responseStr = HttpUtil.sendPostRequest(requestURL, rawQuery, headers);
      ObjectMapper mapper = new ObjectMapper();
      responseData = mapper.readValue(responseStr, Map.class);
    } catch (IOException e) {
      throw new ProjectCommonException(
          ResponseCode.unableToConnectToES.getErrorCode(),
          ResponseCode.unableToConnectToES.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    } catch (Exception e) {
      throw new ProjectCommonException(
          ResponseCode.unableToParseData.getErrorCode(),
          ResponseCode.unableToParseData.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    Response response = new Response();
    response.put(JsonKey.RESPONSE, responseData);
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtil metrics search method end at == "
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return response;
  }

  /**
   * this method will take start time and subtract with current time to get the time spent in
   * millis.
   *
   * @param startTime long
   * @return long
   */
  public static long calculateEndTime(long startTime) {
    return System.currentTimeMillis() - startTime;
  }
}
