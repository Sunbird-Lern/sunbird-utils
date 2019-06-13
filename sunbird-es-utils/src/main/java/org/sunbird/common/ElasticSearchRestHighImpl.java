package org.sunbird.common;

import akka.dispatch.Futures;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortMode;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.inf.ElasticSearchClientInf;
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

/**
 * This class will provide all required operation for elastic search.
 *
 * @author github.com/iostream04
 */
public class ElasticSearchRestHighImpl implements ElasticSearchClientInf {

  public Future<Map<String, Object>> doAsyncSearch(String index, String type, SearchDTO searchDTO) {
    Map<String, String> indexTypeMap = ElasticSearchUtil.getMappedIndexAndType(index, type);
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
    Map<String, Float> constraintsMap = ElasticSearchUtil.getConstraints(searchDTO);
    // apply additional properties
    if (searchDTO.getAdditionalProperties() != null
        && searchDTO.getAdditionalProperties().size() > 0) {
      for (Map.Entry<String, Object> entry : searchDTO.getAdditionalProperties().entrySet()) {
        ElasticSearchUtil.addAdditionalProperties(boolQueryBuilder, entry, constraintsMap);
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
      String index, String type, String identifier, Map<String, Object> data) {
    long startTime = System.currentTimeMillis();
    Promise<String> promise = Futures.promise();
    ProjectLogger.log(
        "ElasticSearchUtilRest createData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    if (StringUtils.isBlank(identifier)
        || StringUtils.isBlank(type)
        || StringUtils.isBlank(index)) {
      ProjectLogger.log("Identifier value is null or empty ,not able to save data.");
      promise.success("ERROR");
      return promise.future();
    }
    data.put("identifier", identifier);

    Map<String, String> mappedIndexAndType = ElasticSearchUtil.getMappedIndexAndType(index, type);
    IndexRequest indexRequest =
        new IndexRequest(
                mappedIndexAndType.get(JsonKey.INDEX),
                mappedIndexAndType.get(JsonKey.TYPE),
                identifier)
            .source(data);

    ActionListener<IndexResponse> listener =
        new ActionListener<IndexResponse>() {
          @Override
          public void onResponse(IndexResponse indexResponse) {
            ProjectLogger.log(
                "ElasticSearchUtilRest createData Success for type : "
                    + type
                    + ", identifier :"
                    + identifier,
                LoggerEnum.INFO);

            promise.success(indexResponse.getId());
            ProjectLogger.log(
                "ElasticSearchUtilRest createData method end at =="
                    + System.currentTimeMillis()
                    + " for Type "
                    + type
                    + " ,Total time elapsed = "
                    + calculateEndTime(startTime),
                LoggerEnum.PERF_LOG);
          }

          @Override
          public void onFailure(Exception e) {
            promise.failure(e);
            ProjectLogger.log(
                "Error while saving " + type + " id : " + identifier + " with error :" + e,
                LoggerEnum.ERROR.name());
            ProjectLogger.log(
                "ElasticSearchUtilRest createData method end at =="
                    + System.currentTimeMillis()
                    + " for Type "
                    + type
                    + " ,Total time elapsed = "
                    + calculateEndTime(startTime),
                LoggerEnum.PERF_LOG);
          }
        };

    ConnectionManager.getRestClient().indexAsync(indexRequest, listener);

    return promise.future();
  }

  public Future<Boolean> updateData(
      String index, String type, String identifier, Map<String, Object> data) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtilRest updateData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    Promise<Boolean> promise = Futures.promise();
    ;

    if (!StringUtils.isBlank(index)
        && !StringUtils.isBlank(type)
        && !StringUtils.isBlank(identifier)
        && data != null) {
      Map<String, String> mappedIndexAndType = ElasticSearchUtil.getMappedIndexAndType(index, type);
      UpdateRequest updateRequest =
          new UpdateRequest(
                  mappedIndexAndType.get(JsonKey.INDEX),
                  mappedIndexAndType.get(JsonKey.TYPE),
                  identifier)
              .doc(data);

      ActionListener<UpdateResponse> listener =
          new ActionListener<UpdateResponse>() {
            @Override
            public void onResponse(UpdateResponse updateResponse) {
              promise.success(true);
              if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {
                ProjectLogger.log(
                    "ElasticSearchUtilRest updateData  Success with upsert for index : "
                        + index
                        + ",type : "
                        + type
                        + ",identifier : "
                        + identifier,
                    LoggerEnum.INFO);
              } else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                ProjectLogger.log(
                    "ElasticSearchUtilRest updateData  Success for index : "
                        + index
                        + ",type : "
                        + type
                        + ",identifier : "
                        + identifier,
                    LoggerEnum.INFO);
              } else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
                promise.success(false);
                ProjectLogger.log(
                    "ElasticSearchUtilRest updateData  falied for index : "
                        + index
                        + ",type : "
                        + type
                        + ",identifier : "
                        + identifier,
                    LoggerEnum.INFO);
              }

              long stopTime = System.currentTimeMillis();
              long elapsedTime = stopTime - startTime;
              ProjectLogger.log(
                  "ElasticSearchUtilRest updateData method end at =="
                      + stopTime
                      + " for Type "
                      + type
                      + " ,Total time elapsed = "
                      + elapsedTime,
                  LoggerEnum.PERF_LOG);
            }

            @Override
            public void onFailure(Exception e) {
              ProjectLogger.log(
                  "ElasticSearchUtilRest : updateData exception occured:" + e.getMessage(),
                  LoggerEnum.ERROR.name());
              promise.failure(e);
            }
          };
      ConnectionManager.getRestClient().updateAsync(updateRequest, listener);

    } else {
      ProjectLogger.log(
          "ElasticSearchUtilRest : updateData Requested data is invalid.", LoggerEnum.INFO.name());
    }
    return promise.future();
  }

  /**
   * This method will provide data form ES based on incoming identifier. we can get data by passing
   * index and identifier values , or all the three
   *
   * @param type String
   * @param identifier String
   * @return Map<String,Object> or null
   */
  public Future<Map<String, Object>> getDataByIdentifier(
      String index, String type, String identifier) {
    long startTime = System.currentTimeMillis();
    Promise<Map<String, Object>> promise = Futures.promise();
    ProjectLogger.log(
        "ElasticSearchUtilRest getDataByIdentifier method started at =="
            + startTime
            + " for Type "
            + type,
        LoggerEnum.PERF_LOG);

    Map<String, String> mappedIndexAndType = ElasticSearchUtil.getMappedIndexAndType(index, type);

    GetRequest getRequest =
        new GetRequest(
            mappedIndexAndType.get(JsonKey.INDEX),
            mappedIndexAndType.get(JsonKey.TYPE),
            identifier);

    ActionListener<GetResponse> listener =
        new ActionListener<GetResponse>() {
          @Override
          public void onResponse(GetResponse getResponse) {
            if (getResponse.isExists()) {
              Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
              if (MapUtils.isNotEmpty(sourceAsMap)) {
                promise.success(sourceAsMap);
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                ProjectLogger.log(
                    "ElasticSearchUtilRest getDataByIdentifier method end at =="
                        + stopTime
                        + " for Type "
                        + type
                        + " ,Total time elapsed = "
                        + elapsedTime,
                    LoggerEnum.PERF_LOG);
              } else {
                promise.success(new HashMap<>());
              }
            }
          }

          @Override
          public void onFailure(Exception e) {
            ProjectLogger.log(
                "ElasticSearchUtilRest getDataByIdentifier method Failed with error == " + e,
                LoggerEnum.INFO);
            promise.failure(e);
          }
        };

    ConnectionManager.getRestClient().getAsync(getRequest, listener);
    return promise.future();
  }

  /**
   * This method will remove data from ES based on identifier.
   *
   * @param index String
   * @param type String
   * @param identifier String
   */
  public Future<Boolean> removeData(String index, String type, String identifier) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtilRest removeData method started at ==" + startTime, LoggerEnum.PERF_LOG);
    Promise<Boolean> promise = Futures.promise();
    Map<String, String> mappedIndexAndType = ElasticSearchUtil.getMappedIndexAndType(index, type);
    DeleteRequest delRequest =
        new DeleteRequest(
            mappedIndexAndType.get(JsonKey.INDEX),
            mappedIndexAndType.get(JsonKey.TYPE),
            identifier);
    ActionListener<DeleteResponse> listener =
        new ActionListener<DeleteResponse>() {
          @Override
          public void onResponse(DeleteResponse deleteResponse) {
            if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
              ProjectLogger.log(
                  "ElasticSearchUtilRest removeData Async : Document  not found for index : "
                      + index
                      + ", Type : "
                      + type
                      + " , identifier : "
                      + identifier,
                  LoggerEnum.INFO);
              promise.success(false);
            } else {
              promise.success(true);
            }
          }

          @Override
          public void onFailure(Exception e) {
            ProjectLogger.log(
                "ElasticSearchUtilRest removeData Async Failed due to error :" + e,
                LoggerEnum.INFO);
            promise.failure(e);
          }
        };

    ConnectionManager.getRestClient().deleteAsync(delRequest, listener);

    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtilRest removeData method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
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
  public Future<Map<String, Object>> searchData(
      String index, String type, Map<String, Object> searchData) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtilRest searchData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    Promise<Map<String, Object>> promise = Futures.promise();
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    Iterator<Entry<String, Object>> itr = searchData.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<String, Object> entry = itr.next();
      sourceBuilder.query(QueryBuilders.commonTermsQuery(entry.getKey(), entry.getValue()));
    }
    Map<String, String> mappedIndexAndType = ElasticSearchUtil.getMappedIndexAndType(index, type);

    SearchRequest searchRequest = new SearchRequest(mappedIndexAndType.get(JsonKey.INDEX));
    searchRequest.types(mappedIndexAndType.get(JsonKey.TYPE));
    searchRequest.source(sourceBuilder);

    ActionListener<SearchResponse> listener =
        new ActionListener<SearchResponse>() {
          @Override
          public void onResponse(SearchResponse searchResponse) {
            if (searchResponse.getHits() == null || searchResponse.getHits().getTotalHits() == 0) {
              promise.success(new HashMap<>());
            }
            searchResponse.getHits().getAt(0);
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            ProjectLogger.log(
                "ElasticSearchUtilRest searchData method end at =="
                    + stopTime
                    + " for Type "
                    + type
                    + " ,Total time elapsed = "
                    + elapsedTime,
                LoggerEnum.PERF_LOG);
            promise.success(searchResponse.getAggregations().asList().get(0).getMetaData());
          }

          @Override
          public void onFailure(Exception e) {
            promise.failure(e);

            long elapsedTime = calculateEndTime(startTime);
            ProjectLogger.log(
                "ElasticSearchUtilRest searchData method end   for Type "
                    + type
                    + " ,Total time elapsed = "
                    + elapsedTime,
                LoggerEnum.PERF_LOG);
            ProjectLogger.log(
                "ElasticSearchUtilRest searchData method Failed with error :" + e,
                LoggerEnum.ERROR);
          }
        };

    ConnectionManager.getRestClient().searchAsync(searchRequest, listener);
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
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Future<Map<String, Object>> complexSearch(
      SearchDTO searchDTO, String index, String... type) {
    long startTime = System.currentTimeMillis();
    List<Map<String, String>> indicesAndTypesMapping =
        ElasticSearchUtil.getMappedIndexesAndTypes(index, type);
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
        "ElasticSearchUtilRest complexSearch method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    SearchRequest searchRequest = new SearchRequest(indices);
    searchRequest.types(types);

    // check mode and set constraints
    Map<String, Float> constraintsMap = ElasticSearchUtil.getConstraints(searchDTO);

    BoolQueryBuilder query = new BoolQueryBuilder();

    // add channel field as mandatory
    String channel = PropertiesCache.getInstance().getProperty(JsonKey.SUNBIRD_ES_CHANNEL);
    if (!(StringUtils.isBlank(channel) || JsonKey.SUNBIRD_ES_CHANNEL.equals(channel))) {
      query.must(
          ElasticSearchUtil.createMatchQuery(
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
          searchSourceBuilder.sort(
              entry.getKey() + ElasticSearchUtil.RAW_APPEND,
              ElasticSearchUtil.getSortOrder((String) entry.getValue()));
        } else {
          Map<String, Object> map = (Map<String, Object>) entry.getValue();
          Map<String, String> dataMap = (Map) map.get(JsonKey.TERM);
          for (Map.Entry<String, String> dateMapEntry : dataMap.entrySet()) {
            /* FieldSortBuilder mySort =
            SortBuilders.fieldSort(entry.getKey() + ElasticSearchUtil.RAW_APPEND)
                .setNestedFilter(
                    new TermQueryBuilder(dateMapEntry.getKey(), dateMapEntry.getValue()))
                .sortMode(SortMode.MIN)
                .order(ElasticSearchUtil.getSortOrder((String) map.get(JsonKey.ORDER)));*/
            FieldSortBuilder mySort =
                new FieldSortBuilder(entry.getKey() + ElasticSearchUtil.RAW_APPEND)
                    .setNestedFilter(
                        new TermQueryBuilder(dateMapEntry.getKey(), dateMapEntry.getValue()))
                    .sortMode(SortMode.MIN)
                    .order(ElasticSearchUtil.getSortOrder((String) map.get(JsonKey.ORDER)));
            searchSourceBuilder.sort(mySort);
          }
        }
      }
    }

    // apply the fields filter
    searchSourceBuilder.fetchSource(
        searchDTO.getFields() != null
            ? searchDTO.getFields().stream().toArray(String[]::new)
            : null,
        searchDTO.getExcludedFields() != null
            ? searchDTO.getExcludedFields().stream().toArray(String[]::new)
            : null);

    // setting the offset
    if (searchDTO.getOffset() != null) {
      searchSourceBuilder.from(searchDTO.getOffset());
    }

    // setting the limit
    if (searchDTO.getLimit() != null) {
      searchSourceBuilder.size(searchDTO.getLimit());
    }
    // apply additional properties
    if (searchDTO.getAdditionalProperties() != null
        && searchDTO.getAdditionalProperties().size() > 0) {
      for (Map.Entry<String, Object> entry : searchDTO.getAdditionalProperties().entrySet()) {
        ElasticSearchUtil.addAdditionalProperties(query, entry, constraintsMap);
      }
    }

    // set final query to search request builder
    searchSourceBuilder.query(query);
    List finalFacetList = new ArrayList();

    if (null != searchDTO.getFacets() && !searchDTO.getFacets().isEmpty()) {
      addAggregations(searchSourceBuilder, searchDTO.getFacets());
    }
    ProjectLogger.log(
        "calling search builder======" + searchSourceBuilder.toString(), LoggerEnum.INFO.name());

    searchRequest.source(searchSourceBuilder);
    Promise<Map<String, Object>> promise = Futures.promise();

    ActionListener<SearchResponse> listener =
        new ActionListener<SearchResponse>() {
          @Override
          public void onResponse(SearchResponse response) {
            if (response.getHits() == null || response.getHits().getTotalHits() == 0) {
              promise.success(new HashMap<>());
            } else {
              List<Map<String, Object>> esSource = new ArrayList<>();
              Map<String, Object> responseMap = new HashMap<>();
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
              responseMap.put(JsonKey.CONTENT, esSource);
              if (!(finalFacetList.isEmpty())) {
                responseMap.put(JsonKey.FACETS, finalFacetList);
              }
              responseMap.put(JsonKey.COUNT, count);
              long stopTime = System.currentTimeMillis();
              long elapsedTime = stopTime - startTime;
              ProjectLogger.log(
                  "ElasticSearchUtilRest complexSearch method end at =="
                      + stopTime
                      + " ,Total time elapsed = "
                      + elapsedTime,
                  LoggerEnum.PERF_LOG);
              promise.success(responseMap);
            }
          }

          @Override
          public void onFailure(Exception e) {
            promise.failure(e);

            long elapsedTime = calculateEndTime(startTime);
            ProjectLogger.log(
                "ElasticSearchUtilRest searchData method end   for Type "
                    + type
                    + " ,Total time elapsed = "
                    + elapsedTime,
                LoggerEnum.PERF_LOG);
            ProjectLogger.log(
                "ElasticSearchUtilRest searchData method Failed with error :" + e,
                LoggerEnum.ERROR);
          }
        };

    ConnectionManager.getRestClient().searchAsync(searchRequest, listener);
    return promise.future();
  }

  /**
   * This method will do the health check of elastic search.
   *
   * @return boolean
   */
  public Future<Boolean> healthCheck() {

    Map<String, String> mappedIndexAndType =
        ElasticSearchUtil.getMappedIndexAndType(
            ProjectUtil.EsIndex.sunbird.getIndexName(), ProjectUtil.EsType.user.getTypeName());

    GetIndexRequest indexRequest =
        new GetIndexRequest().indices(mappedIndexAndType.get(JsonKey.INDEX));
    Promise<Boolean> promise = Futures.promise();
    ActionListener<Boolean> listener =
        new ActionListener<Boolean>() {
          @Override
          public void onResponse(Boolean getResponse) {
            if (getResponse) {
              promise.success(getResponse);
            } else {
              promise.success(false);
            }
          }

          @Override
          public void onFailure(Exception e) {
            promise.failure(e);
            ProjectLogger.log("ElasticSearchUtil:healthCheck error " + e.getMessage(), e);
          }
        };
    ConnectionManager.getRestClient().indices().existsAsync(indexRequest, listener);

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
    Map<String, String> mappedIndexAndType = ElasticSearchUtil.getMappedIndexAndType(index, type);
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

  /**
   * This method will do the bulk data insertion.
   *
   * @param index String index name
   * @param type String type name
   * @param dataList List<Map<String, Object>>
   * @return boolean
   */
  public Future<Boolean> bulkInsertData(
      String index, String type, List<Map<String, Object>> dataList) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtil bulkInsertData method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    Map<String, String> mappedIndexAndType = ElasticSearchUtil.getMappedIndexAndType(index, type);
    BulkRequest request = new BulkRequest();
    Promise<Boolean> promise = Futures.promise();
    for (Map<String, Object> data : dataList) {
      request.add(
          new IndexRequest(
                  mappedIndexAndType.get(JsonKey.INDEX), mappedIndexAndType.get(JsonKey.TYPE))
              .source(data));
    }
    ActionListener<BulkResponse> listener =
        new ActionListener<BulkResponse>() {
          @Override
          public void onResponse(BulkResponse bulkResponse) {
            Iterator<BulkItemResponse> responseItr = bulkResponse.iterator();
            if (responseItr != null) {
              promise.success(true);
              while (responseItr.hasNext()) {

                BulkItemResponse bResponse = responseItr.next();
                ProjectLogger.log(
                    "Bulk insert api response===" + bResponse.getId() + " " + bResponse.isFailed());
              }
            }
          }

          @Override
          public void onFailure(Exception e) {
            ProjectLogger.log("Bulk upload error block", e);
            promise.success(false);
          }
        };
    ConnectionManager.getRestClient().bulkAsync(request, listener);

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

  private static long calculateEndTime(long startTime) {
    return System.currentTimeMillis() - startTime;
  }

  private static void addAggregations(
      SearchSourceBuilder searchRequestBuilder, List<Map<String, String>> facets) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtilRest addAggregations method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    Map<String, String> map = facets.get(0);
    for (Map.Entry<String, String> entry : map.entrySet()) {

      String key = entry.getKey();
      String value = entry.getValue();
      if (JsonKey.DATE_HISTOGRAM.equalsIgnoreCase(value)) {
        searchRequestBuilder.aggregation(
            AggregationBuilders.dateHistogram(key)
                .field(key + ElasticSearchUtil.RAW_APPEND)
                .dateHistogramInterval(DateHistogramInterval.days(1)));

      } else if (null == value) {
        searchRequestBuilder.aggregation(
            AggregationBuilders.terms(key).field(key + ElasticSearchUtil.RAW_APPEND));
      }
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtilRest addAggregations method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
  }
}
