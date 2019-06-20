package org.sunbird.common;

import static org.sunbird.common.models.util.ProjectUtil.isNotNull;

import akka.util.Timeout;
import com.typesafe.config.Config;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
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
import org.elasticsearch.search.sort.SortOrder;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.util.ConfigUtil;
import org.sunbird.dto.SearchDTO;
import scala.concurrent.Await;
import scala.concurrent.Future;

/**
 * This class will provide all required operation for elastic search.
 *
 * @author arvind
 * @author Manzarul
 */
public class ElasticSearchHelper {

  public static final String LTE = "<=";
  public static final String LT = "<";
  public static final String GTE = ">=";
  public static final String GT = ">";
  public static final String ASC_ORDER = "ASC";
  public static final String STARTS_WITH = "startsWith";
  public static final String ENDS_WITH = "endsWith";
  public static final String SOFT_MODE = "soft";
  public static final String RAW_APPEND = ".raw";
  protected static Map<String, Boolean> indexMap = new HashMap<>();
  protected static Map<String, Boolean> typeMap = new HashMap<>();
  protected static final String ES_CONFIG_FILE = "elasticsearch.conf";
  private static Config config = ConfigUtil.getConfig(ES_CONFIG_FILE);
  public static final int WAIT_TIME = 30;
  public static Timeout timeout = new Timeout(WAIT_TIME, TimeUnit.SECONDS);
  public static final List<String> upsertResults =
      new ArrayList<>(Arrays.asList("CREATED", "UPDATED", "NOOP"));

  private ElasticSearchHelper() {}

  /**
   * This method will return the object after getting complete future.
   *
   * @param future
   * @return Object which future inherites
   */
  @SuppressWarnings("unchecked")
  public static Object getResponseFromFuture(Future future) {
    try {
      Object result = Await.result(future, timeout.duration());
      return result;
    } catch (Exception e) {
      ProjectLogger.log(
          "ElasticSearchHelper:getResponseFromFuture : error occured " + e, LoggerEnum.ERROR);
    }
    return null;
  }

  /**
   * This method adds aggregations to the incoming SearchRequestBuilder object
   *
   * @param searchRequestBuilder
   * @param facets
   * @return SearchRequestBuilder
   */
  public static SearchRequestBuilder addAggregations(
      SearchRequestBuilder searchRequestBuilder, List<Map<String, String>> facets) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchHelper:addAggregations method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    if (facets != null && !facets.isEmpty()) {
      Map<String, String> map = facets.get(0);
      if (!MapUtils.isEmpty(map)) {
        for (Map.Entry<String, String> entry : map.entrySet()) {

          String key = entry.getKey();
          String value = entry.getValue();
          if (JsonKey.DATE_HISTOGRAM.equalsIgnoreCase(value)) {
            searchRequestBuilder.addAggregation(
                AggregationBuilders.dateHistogram(key)
                    .field(key + RAW_APPEND)
                    .dateHistogramInterval(DateHistogramInterval.days(1)));

          } else if (null == value) {
            searchRequestBuilder.addAggregation(
                AggregationBuilders.terms(key).field(key + RAW_APPEND));
          }
        }
      }
      long stopTime = System.currentTimeMillis();
      long elapsedTime = stopTime - startTime;
      ProjectLogger.log(
          "ElasticSearchHelper:addAggregations method end at =="
              + stopTime
              + " ,Total time elapsed = "
              + elapsedTime,
          LoggerEnum.PERF_LOG);
    }

    return searchRequestBuilder;
  }

  /**
   * This method returns any constraints defined in searchDto object
   *
   * @param searchDTO
   * @return Map
   */
  public static Map<String, Float> getConstraints(SearchDTO searchDTO) {
    if (null != searchDTO.getSoftConstraints() && !searchDTO.getSoftConstraints().isEmpty()) {
      return searchDTO
          .getSoftConstraints()
          .entrySet()
          .stream()
          .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().floatValue()));
    }
    return Collections.emptyMap();
  }

  /**
   * This method return SearchRequestBuilder for transport client
   *
   * @param client
   * @param index
   * @param type
   * @return SearchRequestBuilder
   */
  public static SearchRequestBuilder getTransportSearchBuilder(
      TransportClient client, String[] index, String... type) {

    if (type == null || type.length == 0) {
      return client.prepareSearch().setIndices(index);
    } else {
      return client.prepareSearch().setIndices(index).setTypes(type);
    }
  }

  /**
   * Method to add the additional search query like range query , exists - not exist filter etc.
   *
   * @param query
   * @param entry
   * @param constraintsMap
   */
  @SuppressWarnings("unchecked")
  public static void addAdditionalProperties(
      BoolQueryBuilder query, Entry<String, Object> entry, Map<String, Float> constraintsMap) {
    long startTime = System.currentTimeMillis();
    ProjectLogger.log(
        "ElasticSearchHelper:addAdditionalProperties method started at ==" + startTime,
        LoggerEnum.PERF_LOG);
    String key = entry.getKey();
    if (JsonKey.FILTERS.equalsIgnoreCase(key)) {

      Map<String, Object> filters = (Map<String, Object>) entry.getValue();
      for (Map.Entry<String, Object> en : filters.entrySet()) {
        query = createFilterESOpperation(en, query, constraintsMap);
      }
    } else if (JsonKey.EXISTS.equalsIgnoreCase(key) || JsonKey.NOT_EXISTS.equalsIgnoreCase(key)) {
      query = createESOpperation(entry, query, constraintsMap);
    }
    long elapsedTime = calculateEndTime(startTime);
    ProjectLogger.log(
        "ElasticSearchHelper:addAdditionalProperties method end =="
            + " ,Total time elapsed = "
            + elapsedTime,
        LoggerEnum.PERF_LOG);
  }

  /**
   * Method to create CommonTermQuery , multimatch and Range Query.
   *
   * @param entry
   * @param query
   * @param constraintsMap
   * @return BoolQueryBuilder
   */
  @SuppressWarnings("unchecked")
  private static BoolQueryBuilder createFilterESOpperation(
      Entry<String, Object> entry, BoolQueryBuilder query, Map<String, Float> constraintsMap) {

    String key = entry.getKey();
    Object val = entry.getValue();
    if (val instanceof List && val != null) {
      query = getTermQueryFromList(val, key, query, constraintsMap);
    } else if (val instanceof Map) {
      query = getTermQueryFromMap(val, key, query, constraintsMap);
    } else if (val instanceof String) {
      query.must(
          createTermQuery(key + RAW_APPEND, ((String) val).toLowerCase(), constraintsMap.get(key)));
    } else {
      query.must(createTermQuery(key + RAW_APPEND, val, constraintsMap.get(key)));
    }
    return query;
  }

  /**
   * This method returns termQuery if any present in map provided
   *
   * @param val
   * @param key
   * @param query
   * @param constraintsMap
   * @return BoolQueryBuilder
   */
  private static BoolQueryBuilder getTermQueryFromMap(
      Object val, String key, BoolQueryBuilder query, Map<String, Float> constraintsMap) {
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

    return query;
  }

  /**
   * This method returns termQuery if any present in List provided
   *
   * @param val
   * @param key
   * @param query
   * @param constraintsMap
   * @return BoolQueryBuilder
   */
  private static BoolQueryBuilder getTermQueryFromList(
      Object val, String key, BoolQueryBuilder query, Map<String, Float> constraintsMap) {
    if (!((List) val).isEmpty()) {
      if (((List) val).get(0) instanceof String) {
        ((List<String>) val).replaceAll(String::toLowerCase);
        query.must(createTermsQuery(key + RAW_APPEND, (List<String>) val, constraintsMap.get(key)));
      } else {
        query.must(createTermsQuery(key, (List) val, constraintsMap.get(key)));
      }
    }
    return null;
  }

  /** Method to create EXISTS and NOT EXIST FILTER QUERY . */
  @SuppressWarnings("unchecked")
  private static BoolQueryBuilder createESOpperation(
      Entry<String, Object> entry, BoolQueryBuilder query, Map<String, Float> constraintsMap) {

    String operation = entry.getKey();
    if (entry.getValue() != null && entry.getValue() instanceof List) {
      List<String> existsList = (List<String>) entry.getValue();

      if (JsonKey.EXISTS.equalsIgnoreCase(operation)) {
        for (String name : existsList) {
          query.must(createExistQuery(name, constraintsMap.get(name)));
        }
      } else if (JsonKey.NOT_EXISTS.equalsIgnoreCase(operation)) {
        for (String name : existsList) {
          query.mustNot(createExistQuery(name, constraintsMap.get(name)));
        }
      }
    }
    return query;
  }

  /** Method to return the sorting order on basis of string param . */
  public static SortOrder getSortOrder(String value) {
    return ASC_ORDER.equalsIgnoreCase(value) ? SortOrder.ASC : SortOrder.DESC;
  }

  /**
   * This method return MatchQueryBuilder Object with boosts if any provided
   *
   * @param name of the attribute
   * @param value of the attribute
   * @param boost
   * @return MatchQueryBuilder
   */
  public static MatchQueryBuilder createMatchQuery(String name, Object value, Float boost) {
    if (isNotNull(boost)) {
      return QueryBuilders.matchQuery(name, value).boost(boost);
    } else {
      return QueryBuilders.matchQuery(name, value);
    }
  }

  /**
   * This method returns TermsQueryBuilder with boosts if any provided
   *
   * @param key
   * @param values
   * @param boost
   * @return TermsQueryBuilder
   */
  private static TermsQueryBuilder createTermsQuery(String key, List values, Float boost) {
    if (isNotNull(boost)) {
      return QueryBuilders.termsQuery(key, (values).stream().toArray(Object[]::new)).boost(boost);
    } else {
      return QueryBuilders.termsQuery(key, (values).stream().toArray(Object[]::new));
    }
  }

  /**
   * This method returns RangeQueryBuilder with boosts if any provided
   *
   * @param name
   * @param rangeOperation
   * @param boost
   * @return RangeQueryBuilder
   */
  private static RangeQueryBuilder createRangeQuery(
      String name, Map<String, Object> rangeOperation, Float boost) {

    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name + RAW_APPEND);
    for (Map.Entry<String, Object> it : rangeOperation.entrySet()) {
      switch (it.getKey()) {
        case LTE:
          rangeQueryBuilder.lte(it.getValue());
        case LT:
          rangeQueryBuilder.lt(it.getValue());
        case GTE:
          rangeQueryBuilder.gte(it.getValue());
        case GT:
          rangeQueryBuilder.gt(it.getValue());
      }
    }
    if (isNotNull(boost)) {
      return rangeQueryBuilder.boost(boost);
    }
    return rangeQueryBuilder;
  }

  /**
   * This method returns TermQueryBuilder with boosts if any provided
   *
   * @param name
   * @param text
   * @param boost
   * @return TermQueryBuilder
   */
  private static TermQueryBuilder createTermQuery(String name, Object text, Float boost) {
    if (isNotNull(boost)) {
      return QueryBuilders.termQuery(name, text).boost(boost);
    } else {
      return QueryBuilders.termQuery(name, text);
    }
  }

  /**
   * this method return ExistsQueryBuilder with boosts if any provided
   *
   * @param name
   * @param boost
   * @return ExistsQueryBuilder
   */
  private static ExistsQueryBuilder createExistQuery(String name, Float boost) {
    if (isNotNull(boost)) {
      return QueryBuilders.existsQuery(name).boost(boost);
    } else {
      return QueryBuilders.existsQuery(name);
    }
  }

  /**
   * This method create lexical query with boosts if any provided
   *
   * @param key
   * @param rangeOperation
   * @param boost
   * @return QueryBuilder
   */
  public static QueryBuilder createLexicalQuery(
      String key, Map<String, Object> rangeOperation, Float boost) {
    QueryBuilder queryBuilder = null;
    for (Map.Entry<String, Object> it : rangeOperation.entrySet()) {
      switch (it.getKey()) {
        case STARTS_WITH:
          {
            String startsWithVal = (String) it.getValue();
            if (StringUtils.isNotBlank(startsWithVal)) {
              startsWithVal = startsWithVal.toLowerCase();
            }
            if (isNotNull(boost)) {
              queryBuilder =
                  QueryBuilders.prefixQuery(key + RAW_APPEND, startsWithVal).boost(boost);
            }
            queryBuilder = QueryBuilders.prefixQuery(key + RAW_APPEND, startsWithVal);
          }
        case ENDS_WITH:
          {
            String endsWithRegex = "~" + it.getValue();
            if (isNotNull(boost)) {
              queryBuilder =
                  QueryBuilders.regexpQuery(key + RAW_APPEND, endsWithRegex).boost(boost);
            }
            queryBuilder = QueryBuilders.regexpQuery(key + RAW_APPEND, endsWithRegex);
          }
      }
    }
    return queryBuilder;
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

  /**
   * This method will create search dto on this of searchquery provided
   *
   * @param searchQueryMap Map<String,Object>
   * @return SearchDto
   */
  public static SearchDTO createSearchDTO(Map<String, Object> searchQueryMap) {
    SearchDTO search = new SearchDTO();
    search = getBasicBuiders(search, searchQueryMap);
    search = setOffset(search, searchQueryMap);
    search = getLimits(search, searchQueryMap);
    if (searchQueryMap.containsKey(JsonKey.GROUP_QUERY)) {
      search
          .getGroupQuery()
          .addAll(
              (Collection<? extends Map<String, Object>>) searchQueryMap.get(JsonKey.GROUP_QUERY));
    }
    search = getSoftConstraints(search, searchQueryMap);
    return search;
  }

  /**
   * This method add any softconstraints present in seach query to search DTo
   *
   * @param searchDto serach
   * @param Map searchQueryMap
   * @return SearchDTO
   */
  private static SearchDTO getSoftConstraints(
      SearchDTO search, Map<String, Object> searchQueryMap) {
    if (searchQueryMap.containsKey(JsonKey.SOFT_CONSTRAINTS)) {
      // Play is converting int value to bigInt so need to cnvert back those data to iny
      // SearchDto soft constraints expect Map<String, Integer>
      Map<String, Integer> constraintsMap = new HashMap<>();
      Set<Entry<String, BigInteger>> entrySet =
          ((Map<String, BigInteger>) searchQueryMap.get(JsonKey.SOFT_CONSTRAINTS)).entrySet();
      Iterator<Entry<String, BigInteger>> itr = entrySet.iterator();
      while (itr.hasNext()) {
        Entry<String, BigInteger> entry = itr.next();
        constraintsMap.put(entry.getKey(), entry.getValue().intValue());
      }
      search.setSoftConstraints(constraintsMap);
    }
    return search;
  }

  /**
   * This method adds any limits present in the search query
   *
   * @param SearchDTO search
   * @param Map searchQueryMap
   * @return SearchDTO
   */
  private static SearchDTO getLimits(SearchDTO search, Map<String, Object> searchQueryMap) {
    if (searchQueryMap.containsKey(JsonKey.LIMIT)) {
      if ((searchQueryMap.get(JsonKey.LIMIT)) instanceof Integer) {
        search.setLimit((int) searchQueryMap.get(JsonKey.LIMIT));
      } else {
        search.setLimit(((BigInteger) searchQueryMap.get(JsonKey.LIMIT)).intValue());
      }
    }
    return search;
  }

  /**
   * This method adds offset if any present in the searchQuery
   *
   * @param SearchDTO search
   * @param map searchQueryMap
   * @return SearchDTO
   */
  private static SearchDTO setOffset(SearchDTO search, Map<String, Object> searchQueryMap) {
    if (searchQueryMap.containsKey(JsonKey.OFFSET)) {
      if ((searchQueryMap.get(JsonKey.OFFSET)) instanceof Integer) {
        search.setOffset((int) searchQueryMap.get(JsonKey.OFFSET));
      } else {
        search.setOffset(((BigInteger) searchQueryMap.get(JsonKey.OFFSET)).intValue());
      }
    }
    return search;
  }

  /**
   * This method adds basic query parameter to SearchDTO if any provided
   *
   * @param SearchDTO search
   * @param Map searchQueryMap
   * @return SearchDTO
   */
  private static SearchDTO getBasicBuiders(SearchDTO search, Map<String, Object> searchQueryMap) {
    if (searchQueryMap.containsKey(JsonKey.QUERY)) {
      search.setQuery((String) searchQueryMap.get(JsonKey.QUERY));
    }
    if (searchQueryMap.containsKey(JsonKey.QUERY_FIELDS)) {
      search.setQueryFields((List<String>) searchQueryMap.get(JsonKey.QUERY_FIELDS));
    }
    if (searchQueryMap.containsKey(JsonKey.FACETS)) {
      search.setFacets((List<Map<String, String>>) searchQueryMap.get(JsonKey.FACETS));
    }
    if (searchQueryMap.containsKey(JsonKey.FIELDS)) {
      search.setFields((List<String>) searchQueryMap.get(JsonKey.FIELDS));
    }
    if (searchQueryMap.containsKey(JsonKey.FILTERS)) {
      search.getAdditionalProperties().put(JsonKey.FILTERS, searchQueryMap.get(JsonKey.FILTERS));
    }
    if (searchQueryMap.containsKey(JsonKey.EXISTS)) {
      search.getAdditionalProperties().put(JsonKey.EXISTS, searchQueryMap.get(JsonKey.EXISTS));
    }
    if (searchQueryMap.containsKey(JsonKey.NOT_EXISTS)) {
      search
          .getAdditionalProperties()
          .put(JsonKey.NOT_EXISTS, searchQueryMap.get(JsonKey.NOT_EXISTS));
    }
    if (searchQueryMap.containsKey(JsonKey.SORT_BY)) {
      search
          .getSortBy()
          .putAll((Map<? extends String, ? extends String>) searchQueryMap.get(JsonKey.SORT_BY));
    }
    return search;
  }

  /**
   * Method returns map which contains all the request data from elasticsearch
   *
   * @param SearchResponse
   * @param searchDTO
   * @param finalFacetList
   * @return
   */
  public static Map<String, Object> getSearchResponseMap(
      SearchResponse response, SearchDTO searchDTO, List finalFacetList) {
    Map<String, Object> responseMap = new HashMap<>();
    List<Map<String, Object>> esSource = new ArrayList<>();
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
    return responseMap;
  }
}
