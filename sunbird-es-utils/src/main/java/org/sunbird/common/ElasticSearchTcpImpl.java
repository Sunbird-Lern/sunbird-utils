package org.sunbird.common;

import akka.dispatch.Futures;
import akka.util.Timeout;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.inf.ElasticSearchUtil;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.dto.SearchDTO;
import org.sunbird.helper.ConnectionManager;
import scala.concurrent.Future;
import scala.concurrent.Promise;

public class ElasticSearchTcpImpl implements ElasticSearchUtil {
  private static final List<String> upsertResults =
      new ArrayList<>(Arrays.asList("CREATED", "UPDATED", "NOOP"));
  public static final int WAIT_TIME = 30;
  public static Timeout timeout = new Timeout(WAIT_TIME, TimeUnit.SECONDS);

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
  @Override
  public Future<String> createData(
      String index, String type, String identifier, Map<String, Object> data) {
    long startTime = System.currentTimeMillis();
    Promise<String> promise = Futures.promise();
    ProjectLogger.log(
        "ElasticSearchUtil createData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    if (StringUtils.isBlank(identifier)
        || StringUtils.isBlank(type)
        || StringUtils.isBlank(index)) {
      ProjectLogger.log("Identifier value is null or empty ,not able to save data.");
      promise.success("ERROR");
      return promise.future();
    }
    Map<String, String> mappedIndexAndType = ElasticSearchHelper.getMappedIndexAndType(index, type);
    try {
      data.put("identifier", identifier);
      IndexResponse response =
          ConnectionManager.getClient()
              .prepareIndex(
                  mappedIndexAndType.get(JsonKey.INDEX),
                  mappedIndexAndType.get(JsonKey.TYPE),
                  identifier)
              .setSource(data)
              .get();
      ProjectLogger.log(
          "Save value==" + response.getId() + " " + response.status(), LoggerEnum.INFO.name());
      ProjectLogger.log(
          "ElasticSearchUtil createData method end at =="
              + System.currentTimeMillis()
              + " for Type "
              + type
              + " ,Total time elapsed = "
              + ElasticSearchHelper.calculateEndTime(startTime),
          LoggerEnum.PERF_LOG);
      promise.success(response.getId());
      return promise.future();
    } catch (Exception e) {
      ProjectLogger.log("Error while saving " + type + " id : " + identifier, e);
      ProjectLogger.log(
          "ElasticSearchUtil createData method end at =="
              + System.currentTimeMillis()
              + " for Type "
              + type
              + " ,Total time elapsed = "
              + ElasticSearchHelper.calculateEndTime(startTime),
          LoggerEnum.PERF_LOG);
      promise.failure(e);
      promise.success("");
      return promise.future();
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
  @Override
  public Future<Map<String, Object>> getDataByIdentifier(
      String index, String type, String identifier) {
    long startTime = System.currentTimeMillis();
    Promise<Map<String, Object>> promise = Futures.promise();
    ProjectLogger.log(
        "ElasticSearchUtil getDataByIdentifier method started at =="
            + startTime
            + " for Type "
            + type,
        LoggerEnum.PERF_LOG);
    Map<String, String> mappedIndexAndType = ElasticSearchHelper.getMappedIndexAndType(index, type);
    GetResponse response = null;
    if (StringUtils.isBlank(index) || StringUtils.isBlank(identifier)) {
      ProjectLogger.log("Invalid request is coming.");
      promise.success(new HashMap<>());
      return promise.future();
    } else if (StringUtils.isBlank(type)) {
      response =
          ConnectionManager.getClient()
              .prepareGet()
              .setIndex(mappedIndexAndType.get(JsonKey.INDEX))
              .setId(identifier)
              .get();
    } else {
      response =
          ConnectionManager.getClient()
              .prepareGet(
                  mappedIndexAndType.get(JsonKey.INDEX),
                  mappedIndexAndType.get(JsonKey.TYPE),
                  identifier)
              .get();
    }
    if (response == null || null == response.getSource()) {
      promise.success(new HashMap<>());
      return promise.future();
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
    promise.success(response.getSource());
    return promise.future();
  }

  /**
   * This method will do the data search inside ES. based on incoming search data.
   *
   * @param index String
   * @param type String
   * @param searchData Map<String,Object>
   * @return Map<String,Object>
   */
  @Override
  public Future<Map<String, Object>> searchData(
      String index, String type, Map<String, Object> searchData) {
    long startTime = System.currentTimeMillis();
    Promise<Map<String, Object>> promise = Futures.promise();
    ProjectLogger.log(
        "ElasticSearchUtil searchData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    Iterator<Entry<String, Object>> itr = searchData.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<String, Object> entry = itr.next();
      sourceBuilder.query(QueryBuilders.commonTermsQuery(entry.getKey(), entry.getValue()));
    }
    Map<String, String> mappedIndexAndType = ElasticSearchHelper.getMappedIndexAndType(index, type);
    SearchResponse sr = null;
    try {
      sr =
          ConnectionManager.getClient()
              .search(
                  new SearchRequest(mappedIndexAndType.get(JsonKey.INDEX))
                      .types(mappedIndexAndType.get(JsonKey.TYPE))
                      .source(sourceBuilder))
              .get();
    } catch (InterruptedException e) {
      ProjectLogger.log("Error, interrupted while connecting to Elasticsearch", e);
      Thread.currentThread().interrupt();

    } catch (ExecutionException e) {
      ProjectLogger.log("Error while execution in Elasticsearch", e);
      promise.failure(e);
    }
    if (sr.getHits() == null || sr.getHits().getTotalHits() == 0) {
      promise.success(new HashMap<>());
      return promise.future();
    }
    sr.getHits().getAt(0);
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
    promise.success(sr.getAggregations().asList().get(0).getMetaData());
    return promise.future();
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
  @Override
  public Future<Boolean> updateData(
      String index, String type, String identifier, Map<String, Object> data) {
    long startTime = System.currentTimeMillis();
    Promise<Boolean> promise = Futures.promise();
    ProjectLogger.log(
        "ElasticSearchUtil updateData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    if (!StringUtils.isBlank(index)
        && !StringUtils.isBlank(type)
        && !StringUtils.isBlank(identifier)
        && data != null) {
      Map<String, String> mappedIndexAndType =
          ElasticSearchHelper.getMappedIndexAndType(index, type);
      try {
        UpdateResponse response =
            ConnectionManager.getClient()
                .prepareUpdate(
                    mappedIndexAndType.get(JsonKey.INDEX),
                    mappedIndexAndType.get(JsonKey.TYPE),
                    identifier)
                .setDoc(data)
                .get();
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
          promise.success(true);
          return promise.future();
        } else {
          ProjectLogger.log(
              "ElasticSearchUtil:updateData update was not success:" + response.getResult(),
              LoggerEnum.INFO.name());
        }
      } catch (Exception e) {
        ProjectLogger.log(
            "ElasticSearchUtil:updateData exception occured:" + e.getMessage(),
            LoggerEnum.ERROR.name());
        promise.failure(e);
      }
    } else {
      ProjectLogger.log(
          "ElasticSearchUtil:updateData Requested data is invalid.", LoggerEnum.INFO.name());
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
    promise.success(false);
    return promise.future();
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
  @Override
  public Future<Boolean> upsertData(
      String index, String type, String identifier, Map<String, Object> data) {
    long startTime = System.currentTimeMillis();
    Promise<Boolean> promise = Futures.promise();
    ProjectLogger.log(
        "ElasticSearchUtil upsertData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    if (!StringUtils.isBlank(index)
        && !StringUtils.isBlank(type)
        && !StringUtils.isBlank(identifier)
        && data != null
        && data.size() > 0) {
      Map<String, String> mappedIndexAndType =
          ElasticSearchHelper.getMappedIndexAndType(index, type);
      IndexRequest indexRequest =
          new IndexRequest(
                  mappedIndexAndType.get(JsonKey.INDEX),
                  mappedIndexAndType.get(JsonKey.TYPE),
                  identifier)
              .source(data);
      UpdateRequest updateRequest =
          new UpdateRequest(
                  mappedIndexAndType.get(JsonKey.INDEX),
                  mappedIndexAndType.get(JsonKey.TYPE),
                  identifier)
              .doc(data)
              .upsert(indexRequest);
      UpdateResponse response = null;
      try {
        response = ConnectionManager.getClient().update(updateRequest).get();
      } catch (InterruptedException | ExecutionException e) {
        ProjectLogger.log(e.getMessage(), e);
        promise.failure(e);
        promise.success(false);
        return promise.future();
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
        promise.success(true);
        return promise.future();
      }
    } else {
      ProjectLogger.log("Requested data is invalid.");
      promise.success(false);
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
    return promise.future();
  }

  /**
   * This method will remove data from ES based on identifier.
   *
   * @param index String
   * @param type String
   * @param identifier String
   */
  @Override
  public Future<Boolean> removeData(String index, String type, String identifier) {
    long startTime = System.currentTimeMillis();
    Promise<Boolean> promise = Futures.promise();
    ProjectLogger.log(
        "ElasticSearchUtil removeData method started at ==" + startTime, LoggerEnum.PERF_LOG);
    DeleteResponse deleteResponse = null;
    if (!StringUtils.isBlank(index)
        && !StringUtils.isBlank(type)
        && !StringUtils.isBlank(identifier)) {
      Map<String, String> mappedIndexAndType =
          ElasticSearchHelper.getMappedIndexAndType(index, type);
      try {
        deleteResponse =
            ConnectionManager.getClient()
                .prepareDelete(
                    mappedIndexAndType.get(JsonKey.INDEX),
                    mappedIndexAndType.get(JsonKey.TYPE),
                    identifier)
                .get();
        ProjectLogger.log(
            "delete info ==" + deleteResponse.getResult().name() + " " + deleteResponse.getId());
      } catch (Exception e) {
        promise.failure(e);
        ProjectLogger.log(e.getMessage(), e);
      }
    } else {
      ProjectLogger.log("Data can not be deleted due to invalid input.");
      promise.success(false);
      return promise.future();
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtil removeData method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    promise.success(deleteResponse.getResult().name().equalsIgnoreCase("DELETED"));
    return promise.future();
  }

  /**
   * Method to perform the elastic search on the basis of SearchDTO . SearchDTO contains the search
   * criteria like fields, facets, sort by , filters etc. here user can pass single type to search
   * or multiple type or null
   *
   * @param type var arg of String
   * @return search result as Map.
   */
  @Override
  public Future<Map<String, Object>> complexSearch(
      SearchDTO searchDTO, String index, String... type) {

    long startTime = System.currentTimeMillis();
    Promise<Map<String, Object>> promise = Futures.promise();
    List<Map<String, String>> indicesAndTypesMapping =
        ElasticSearchHelper.getMappedIndexesAndTypes(index, type);
    String[] indices =
        indicesAndTypesMapping
            .stream()
            .map(indexMap -> indexMap.get(JsonKey.INDEX))
            .toArray(String[]::new);
    String[] types =
        indicesAndTypesMapping
            .stream()
            .map(indexMap -> indexMap.get(JsonKey.TYPE))
            .distinct()
            .toArray(String[]::new);
    ProjectLogger.log(
        "ElasticSearchUtil complexSearch method started at ==" + startTime, LoggerEnum.PERF_LOG);
    SearchRequestBuilder searchRequestBuilder =
        ElasticSearchHelper.getSearchBuilder(ConnectionManager.getClient(), indices, types);
    // check mode and set constraints
    Map<String, Float> constraintsMap = ElasticSearchHelper.getConstraints(searchDTO);

    BoolQueryBuilder query = new BoolQueryBuilder();

    // add channel field as mandatory
    String channel = PropertiesCache.getInstance().getProperty(JsonKey.SUNBIRD_ES_CHANNEL);
    if (!(StringUtils.isBlank(channel) || JsonKey.SUNBIRD_ES_CHANNEL.equals(channel))) {
      query.must(
          ElasticSearchHelper.createMatchQuery(
              JsonKey.CHANNEL, channel, constraintsMap.get(JsonKey.CHANNEL)));
    }

    // apply simple query string
    if (!StringUtils.isBlank(searchDTO.getQuery())) {
      SimpleQueryStringBuilder sqsb = QueryBuilders.simpleQueryStringQuery(searchDTO.getQuery());
      if (CollectionUtils.isEmpty(searchDTO.getQueryFields())) {
        query.must(sqsb.field("all_fields"));
      } else {
        Map<String, Float> searchFields =
            searchDTO
                .getQueryFields()
                .stream()
                .collect(Collectors.<String, String, Float>toMap(s -> s, v -> 1.0f));
        query.must(sqsb.fields(searchFields));
      }
    }
    // apply the sorting
    if (searchDTO.getSortBy() != null && searchDTO.getSortBy().size() > 0) {
      for (Map.Entry<String, Object> entry : searchDTO.getSortBy().entrySet()) {
        if (!entry.getKey().contains(".")) {
          searchRequestBuilder.addSort(
              entry.getKey() + ElasticSearchHelper.RAW_APPEND,
              ElasticSearchHelper.getSortOrder((String) entry.getValue()));
        } else {
          Map<String, Object> map = (Map<String, Object>) entry.getValue();
          Map<String, String> dataMap = (Map) map.get(JsonKey.TERM);
          for (Map.Entry<String, String> dateMapEntry : dataMap.entrySet()) {
            FieldSortBuilder mySort =
                SortBuilders.fieldSort(entry.getKey() + ElasticSearchHelper.RAW_APPEND)
                    .setNestedFilter(
                        new TermQueryBuilder(dateMapEntry.getKey(), dateMapEntry.getValue()))
                    .sortMode(SortMode.MIN)
                    .order(ElasticSearchHelper.getSortOrder((String) map.get(JsonKey.ORDER)));
            searchRequestBuilder.addSort(mySort);
          }
        }
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
        ElasticSearchHelper.addAdditionalProperties(query, entry, constraintsMap);
      }
    }

    // set final query to search request builder
    searchRequestBuilder.setQuery(query);
    List finalFacetList = new ArrayList();

    if (null != searchDTO.getFacets() && !searchDTO.getFacets().isEmpty()) {
      ElasticSearchHelper.addAggregations(searchRequestBuilder, searchDTO.getFacets());
    }
    ProjectLogger.log(
        "calling search builder======" + searchRequestBuilder.toString(), LoggerEnum.INFO.name());
    SearchResponse response = null;
    try {
      response = searchRequestBuilder.execute().actionGet();
    } catch (SearchPhaseExecutionException e) {
      promise.failure(e);
      ProjectCommonException.throwClientErrorException(
          ResponseCode.invalidValue, e.getRootCause().getMessage());
    }

    List<Map<String, Object>> esSource = new ArrayList<>();
    Map<String, Object> responsemap = new HashMap<>();
    long count = 0;
    if (response != null) {
      SearchHits hits = response.getHits();
      count = hits.getTotalHits();

      for (SearchHit hit : hits) {
        esSource.add(hit.getSourceAsMap());
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
    promise.success(responsemap);
    return promise.future();
  }
  /**
   * @param ids List of ids of document
   * @param fields List of fields which needs to captured
   * @param typeToSearch type of ES
   * @return Map<String,Map<String,Objec>> It will return a map with id as key and the data from ES
   *     as value
   */
  @Override
  public Future<Map<String, Map<String, Object>>> getEsResultByListOfIds(
      List<String> ids, List<String> fields, ProjectUtil.EsType typeToSearch) {

    Map<String, Object> filters = new HashMap<>();
    filters.put(JsonKey.ID, ids);

    SearchDTO searchDTO = new SearchDTO();
    searchDTO.getAdditionalProperties().put(JsonKey.FILTERS, filters);
    searchDTO.setFields(fields);

    Future<Map<String, Object>> resultF =
        complexSearch(
            searchDTO, ProjectUtil.EsIndex.sunbird.getIndexName(), typeToSearch.getTypeName());
    Map<String, Object> result =
        (Map<String, Object>) ElasticSearchHelper.getObjectFromFuture(resultF);
    List<Map<String, Object>> esContent = (List<Map<String, Object>>) result.get(JsonKey.CONTENT);
    Promise<Map<String, Map<String, Object>>> promise = Futures.promise();
    promise.success(
        esContent
            .stream()
            .collect(
                Collectors.toMap(
                    obj -> {
                      return (String) obj.get("id");
                    },
                    val -> val)));
    return promise.future();
  }

  @Override
  public Future<Map<String, Object>> doAsyncSearch(String index, String type, SearchDTO searchDTO) {
    Map<String, String> indexTypeMap = ElasticSearchHelper.getMappedIndexAndType(index, type);
    Promise<Map<String, Object>> promise = Futures.promise();
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    if (!StringUtils.isBlank(searchDTO.getQuery())) {
      SimpleQueryStringBuilder sqsb = QueryBuilders.simpleQueryStringQuery(searchDTO.getQuery());
      if (CollectionUtils.isEmpty(searchDTO.getQueryFields())) {
        boolQueryBuilder.must(sqsb.field("all_fields"));
      } else {
        Map<String, Float> searchFields =
            searchDTO
                .getQueryFields()
                .stream()
                .collect(Collectors.<String, String, Float>toMap(s -> s, v -> 1.0f));
        boolQueryBuilder.must(sqsb.fields(searchFields));
      }
    }
    sourceBuilder.from(searchDTO.getOffset() != null ? searchDTO.getOffset() : 0);
    sourceBuilder.size(searchDTO.getLimit() != null ? searchDTO.getLimit() : 250);
    // check mode and set constraints
    Map<String, Float> constraintsMap = ElasticSearchHelper.getConstraints(searchDTO);
    // apply additional properties
    if (searchDTO.getAdditionalProperties() != null
        && searchDTO.getAdditionalProperties().size() > 0) {
      for (Map.Entry<String, Object> entry : searchDTO.getAdditionalProperties().entrySet()) {
        ElasticSearchHelper.addAdditionalProperties(boolQueryBuilder, entry, constraintsMap);
      }
    }
    sourceBuilder.query(boolQueryBuilder);
    SearchRequest searchRequest = new SearchRequest(indexTypeMap.get(JsonKey.INDEX));
    searchRequest.source(sourceBuilder);
    ActionListener<SearchResponse> listener =
        new ActionListener<SearchResponse>() {
          @Override
          public void onResponse(SearchResponse searchResponse) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<String, Object> responseMap = new HashMap<>();
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits.getHits()) {
              mapList.add(hit.getSourceAsMap());
            }
            responseMap.put(JsonKey.CONTENT, mapList);
            responseMap.put(JsonKey.COUNT, hits.getTotalHits());
            promise.success(responseMap);
          }

          @Override
          public void onFailure(Exception e) {
            promise.failure(e);
          }
        };
    ConnectionManager.getRestClient().searchAsync(searchRequest, listener);
    return promise.future();
  }
  /**
   * This method will do the bulk data insertion.
   *
   * @param index String index name
   * @param type String type name
   * @param dataList List<Map<String, Object>>
   * @return boolean
   */
  @Override
  public Future<Boolean> bulkInsertData(
      String index, String type, List<Map<String, Object>> dataList) {
    Promise<Boolean> promise = Futures.promise();
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil bulkInsertData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    promise.success(true);
    Map<String, String> mappedIndexAndType = ElasticSearchHelper.getMappedIndexAndType(index, type);
    try {
      BulkProcessor bulkProcessor =
          BulkProcessor.builder(
                  ConnectionManager.getClient(),
                  new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId, BulkRequest request) {}

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
            new IndexRequest(
                    mappedIndexAndType.get(JsonKey.INDEX),
                    mappedIndexAndType.get(JsonKey.TYPE),
                    (String) map.get(JsonKey.IDENTIFIER))
                .source(map);
        bulkProcessor.add(request);
      }
      // Flush any remaining requests
      bulkProcessor.flush();

      // Or close the bulkProcessor if you don't need it anymore
      bulkProcessor.close();

      // Refresh your indices
      ConnectionManager.getClient().admin().indices().prepareRefresh().get();
    } catch (Exception e) {
      promise.failure(e);
      promise.success(false);
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
    return promise.future();
  }

  /**
   * This method will do the health check of elastic search.
   *
   * @return boolean
   */
  @Override
  public Future<Boolean> healthCheck() {
    Promise<Boolean> promise = Futures.promise();

    boolean indexResponse = false;
    Map<String, String> mappedIndexAndType =
        ElasticSearchHelper.getMappedIndexAndType(
            ProjectUtil.EsIndex.sunbird.getIndexName(), ProjectUtil.EsType.user.getTypeName());
    try {
      indexResponse =
          ConnectionManager.getClient()
              .admin()
              .indices()
              .exists(Requests.indicesExistsRequest(mappedIndexAndType.get(JsonKey.INDEX)))
              .get()
              .isExists();
    } catch (Exception e) {
      ProjectLogger.log("ElasticSearchUtil:healthCheck error " + e.getMessage(), e);
      promise.failure(e);
    }
    promise.success(indexResponse);
    return promise.future();
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
  @Override
  public Response searchMetricsData(String index, String type, String rawQuery) {
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
    Map<String, String> mappedIndexAndType = ElasticSearchHelper.getMappedIndexAndType(index, type);
    String requestURL =
        baseUrl
            + "/"
            + mappedIndexAndType.get(JsonKey.INDEX)
            + "/"
            + mappedIndexAndType.get(JsonKey.TYPE)
            + "/"
            + "_search";
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
}
