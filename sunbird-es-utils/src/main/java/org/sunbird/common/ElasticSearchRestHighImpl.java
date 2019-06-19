package org.sunbird.common;

import akka.dispatch.Futures;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.sunbird.common.inf.ElasticService;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.HttpUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.ProjectUtil.EsType;
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
public class ElasticSearchRestHighImpl implements ElasticService {
  private static final List<String> upsertResults =
      new ArrayList<>(Arrays.asList("CREATED", "UPDATED", "NOOP"));
  private static final String ERROR = "ERROR";

  /**
   * This method will put a new data entry inside Elastic search. identifier value becomes _id
   * inside ES, so every time provide a unique value while saving it.
   *
   * @param index String ES index name
   * @param identifier ES column identifier as an String
   * @param data Map<String,Object>
   * @return Future<String> which contains identifier for created data
   */
  @Override
  public Future<String> save(
      String index, String type, String identifier, Map<String, Object> data) {
    long startTime = System.currentTimeMillis();
    Promise<String> promise = Futures.promise();
    ProjectLogger.log(
        "ElasticSearchUtilRest save method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    if (StringUtils.isBlank(identifier)
        || StringUtils.isBlank(type)
        || StringUtils.isBlank(index)) {
      ProjectLogger.log(
          "ElasticSearchRestHighImpl:save Identifier value is null or empty ,not able to save data.");
      promise.success(ERROR);
      return promise.future();
    }
    data.put("identifier", identifier);

    Map<String, String> mappedIndexAndType = ElasticSearchHelper.getMappedIndexAndType(index, type);
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
                "ElasticSearchRestHighImpl:save Success for type : "
                    + type
                    + ", identifier :"
                    + identifier,
                LoggerEnum.INFO);

            promise.success(indexResponse.getId());
            ProjectLogger.log(
                "ElasticSearchRestHighImpl:save method end at =="
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
                "ElasticSearchRestHighImpl:save "
                    + "Error while saving "
                    + type
                    + " id : "
                    + identifier
                    + " with error :"
                    + e,
                LoggerEnum.ERROR.name());
            ProjectLogger.log(
                "ElasticSearchRestHighImpl:save method end at =="
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
  /**
   * This method will update data entry inside Elastic search, using identifier and provided data .
   *
   * @param index String ES index name
   * @param identifier ES column identifier as an String
   * @param data Map<String,Object>
   * @return true or false
   */
  @Override
  public Future<Boolean> update(
      String index, String type, String identifier, Map<String, Object> data) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchRestHighImpl:update method started at ==" + startTime + " for Type " + type,
        LoggerEnum.PERF_LOG);
    Promise<Boolean> promise = Futures.promise();
    ;

    if (!StringUtils.isBlank(index)
        && !StringUtils.isBlank(type)
        && !StringUtils.isBlank(identifier)
        && data != null) {
      Map<String, String> mappedIndexAndType =
          ElasticSearchHelper.getMappedIndexAndType(index, type);
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
              if (updateResponse.getResult() == DocWriteResponse.Result.CREATED
                  || updateResponse.getResult() == DocWriteResponse.Result.UPDATED
                  || updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
                ProjectLogger.log(
                    "ElasticSearchRestHighImpl:update  Success with upsert for index : "
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
                  "ElasticSearchRestHighImpl:update method end at =="
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
                  "ElasticSearchRestHighImpl:update exception occured:" + e.getMessage(),
                  LoggerEnum.ERROR.name());
              promise.failure(e);
            }
          };
      ConnectionManager.getRestClient().updateAsync(updateRequest, listener);

    } else {
      ProjectLogger.log(
          "ElasticSearchRestHighImpl:update Requested data is invalid.", LoggerEnum.INFO.name());
      promise.failure(
          new ProjectCommonException(
              "", "Requested data is invalid", ResponseCode.invalidData.getResponseCode()));
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
  @Override
  public Future<Map<String, Object>> getDataByIdentifier(
      String index, String type, String identifier) {
    long startTime = System.currentTimeMillis();
    Promise<Map<String, Object>> promise = Futures.promise();
    ProjectLogger.log(
        "ElasticSearchRestHighImpl:getDataByIdentifier method started at =="
            + startTime
            + " for Type "
            + type,
        LoggerEnum.PERF_LOG);

    Map<String, String> mappedIndexAndType = ElasticSearchHelper.getMappedIndexAndType(index, type);

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
                    "ElasticSearchRestHighImpl:getDataByIdentifier method end at =="
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
                "ElasticSearchRestHighImpl:getDataByIdentifier method Failed with error == " + e,
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
  @Override
  public Future<Boolean> delete(String index, String type, String identifier) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchRestHighImpl:delete method started at ==" + startTime, LoggerEnum.PERF_LOG);
    Promise<Boolean> promise = Futures.promise();
    Map<String, String> mappedIndexAndType = ElasticSearchHelper.getMappedIndexAndType(index, type);
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
                  "ElasticSearchRestHighImpl:delete Async : Document  not found for index : "
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
                "ElasticSearchRestHighImpl:delete Async Failed due to error :" + e,
                LoggerEnum.INFO);
            promise.failure(e);
          }
        };

    ConnectionManager.getRestClient().deleteAsync(delRequest, listener);

    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchRestHighImpl:delete method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
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
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Future<Map<String, Object>> search(SearchDTO searchDTO, String index, String... type) {
    long startTime = System.currentTimeMillis();
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
        "ElasticSearchRestHighImpl:search method started at ==" + startTime, LoggerEnum.PERF_LOG);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    SearchRequest searchRequest = new SearchRequest(indices);
    searchRequest.types(types);

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
          searchSourceBuilder.sort(
              entry.getKey() + ElasticSearchHelper.RAW_APPEND,
              ElasticSearchHelper.getSortOrder((String) entry.getValue()));
        } else {
          Map<String, Object> map = (Map<String, Object>) entry.getValue();
          Map<String, String> dataMap = (Map) map.get(JsonKey.TERM);
          for (Map.Entry<String, String> dateMapEntry : dataMap.entrySet()) {
            FieldSortBuilder mySort =
                new FieldSortBuilder(entry.getKey() + ElasticSearchHelper.RAW_APPEND)
                    .setNestedFilter(
                        new TermQueryBuilder(dateMapEntry.getKey(), dateMapEntry.getValue()))
                    .sortMode(SortMode.MIN)
                    .order(ElasticSearchHelper.getSortOrder((String) map.get(JsonKey.ORDER)));
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
        ElasticSearchHelper.addAdditionalProperties(query, entry, constraintsMap);
      }
    }

    // set final query to search request builder
    searchSourceBuilder.query(query);
    List finalFacetList = new ArrayList();

    if (null != searchDTO.getFacets() && !searchDTO.getFacets().isEmpty()) {
      searchSourceBuilder = addAggregations(searchSourceBuilder, searchDTO.getFacets());
    }
    ProjectLogger.log(
        "ElasticSearchRestHighImpl:search calling search builder======"
            + searchSourceBuilder.toString(),
        LoggerEnum.INFO.name());

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
                  "ElasticSearchRestHighImpl:search method end at =="
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
                "ElasticSearchRestHighImpl:search method end   for Type "
                    + type
                    + " ,Total time elapsed = "
                    + elapsedTime,
                LoggerEnum.PERF_LOG);
            ProjectLogger.log(
                "ElasticSearchRestHighImpl:search method Failed with error :" + e,
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
  @Override
  public Future<Boolean> healthCheck() {

    Map<String, String> mappedIndexAndType =
        ElasticSearchHelper.getMappedIndexAndType(
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
            ProjectLogger.log("ElasticSearchRestHighImpl:healthCheck error " + e.getMessage(), e);
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
  @Override
  public Response searchMetricsData(String index, String type, String rawQuery) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchRestHighImpl:searchMetricsData method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
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
      ProjectLogger.log(
          "ElasticSearchRestHighImpl:searchMerticData, ES URL from Properties file",
          LoggerEnum.INFO);
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
        "ElasticSearchRestHighImpl:searchMetricsData search method end at == "
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
  @Override
  public Future<Boolean> bulkInsert(String index, String type, List<Map<String, Object>> dataList) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchRestHighImpl:bulkInsert method started at =="
            + startTime
            + " for Type "
            + type,
        LoggerEnum.PERF_LOG);
    Map<String, String> mappedIndexAndType = ElasticSearchHelper.getMappedIndexAndType(index, type);
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
                    "ElasticSearchRestHighImpl:Bulkinsert api response==="
                        + bResponse.getId()
                        + " "
                        + bResponse.isFailed(),
                    LoggerEnum.ERROR);
              }
            }
          }

          @Override
          public void onFailure(Exception e) {
            ProjectLogger.log("ElasticSearchRestHighImpl:Bulkinsert Bulk upload error block", e);
            promise.success(false);
          }
        };
    ConnectionManager.getRestClient().bulkAsync(request, listener);

    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchRestHighImpl:bulkInsert method end at =="
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

  private static SearchSourceBuilder addAggregations(
      SearchSourceBuilder searchSourceBuilder, List<Map<String, String>> facets) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchUtilRest:addAggregations method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    Map<String, String> map = facets.get(0);
    for (Map.Entry<String, String> entry : map.entrySet()) {

      String key = entry.getKey();
      String value = entry.getValue();
      if (JsonKey.DATE_HISTOGRAM.equalsIgnoreCase(value)) {
        searchSourceBuilder.aggregation(
            AggregationBuilders.dateHistogram(key)
                .field(key + ElasticSearchHelper.RAW_APPEND)
                .dateHistogramInterval(DateHistogramInterval.days(1)));

      } else if (null == value) {
        searchSourceBuilder.aggregation(
            AggregationBuilders.terms(key).field(key + ElasticSearchHelper.RAW_APPEND));
      }
    }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    ProjectLogger.log(
        "ElasticSearchUtilRest:addAggregations method end at =="
            + stopTime
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
    return searchSourceBuilder;
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
  public Future<Boolean> upsert(
      String index, String type, String identifier, Map<String, Object> data) {
    long startTime = System.currentTimeMillis();
    Promise<Boolean> promise = Futures.promise();
    ProjectLogger.log(
        "ElasticSearchUtil upsert method started at ==" + startTime + " for Type " + type,
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
              .upsert(indexRequest);

      ActionListener<UpdateResponse> listener =
          new ActionListener<UpdateResponse>() {
            @Override
            public void onResponse(UpdateResponse updateResponse) {
              promise.success(true);
              if (updateResponse.getResult() == DocWriteResponse.Result.CREATED
                  || updateResponse.getResult() == DocWriteResponse.Result.UPDATED
                  || updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
                ProjectLogger.log(
                    "ElasticSearchUtilRest updateData  Success for index : "
                        + index
                        + ",type : "
                        + type
                        + ",identifier : "
                        + identifier,
                    LoggerEnum.INFO);
              }
              long elapsedTime = calculateEndTime(startTime);
              ProjectLogger.log(
                  "ElasticSearchUtilRest upsert method end =="
                      + " for Type "
                      + type
                      + " ,Total time elapsed = "
                      + elapsedTime,
                  LoggerEnum.PERF_LOG);
            }

            @Override
            public void onFailure(Exception e) {
              ProjectLogger.log(
                  "ElasticSearchRestHighImpl:upsert exception occured:" + e.getMessage(),
                  LoggerEnum.ERROR.name());
              promise.failure(e);
            }
          };
      ConnectionManager.getRestClient().updateAsync(updateRequest, listener);
      return promise.future();
    } else {
      ProjectLogger.log(
          "ElasticSearchRestHighImpl:upsert Requested data is invalid.", LoggerEnum.ERROR);
      return promise.future();
    }
  }

  /**
   * This method will return map of objects on the basis of ids and type provided.
   *
   * @param ids List of String
   * @param fields List of String
   * @param Estype type
   * @param data Map<String,Object>
   * @return boolean
   */
  @Override
  public Future<Map<String, Map<String, Object>>> getEsResultByListOfIds(
      List<String> ids, List<String> fields, EsType typeToSearch) {
    Map<String, Object> filters = new HashMap<>();
    filters.put(JsonKey.ID, ids);

    SearchDTO searchDTO = new SearchDTO();
    searchDTO.getAdditionalProperties().put(JsonKey.FILTERS, filters);
    searchDTO.setFields(fields);

    Future<Map<String, Object>> resultF =
        search(searchDTO, ProjectUtil.EsIndex.sunbird.getIndexName(), typeToSearch.getTypeName());
    Map<String, Object> result =
        (Map<String, Object>) ElasticSearchHelper.getResponseFromFuture(resultF);
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
}
