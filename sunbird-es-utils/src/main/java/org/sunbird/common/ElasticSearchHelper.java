package org.sunbird.common;

import static org.sunbird.common.models.util.ProjectUtil.isNotNull;

import akka.util.Timeout;
import com.typesafe.config.Config;
import java.math.BigInteger;
import java.util.ArrayList;
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
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortOrder;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.responsecode.ResponseCode;
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

  private ElasticSearchHelper() {}

  @SuppressWarnings("unchecked")
  public static Object getObjectFromFuture(Future future) {
    try {
      Object result = Await.result(future, timeout.duration());
      return result;
    } catch (Exception e) {
      ProjectLogger.log(
          "ElasticSearchUtil : getObjectFromFuture : error occured " + e, LoggerEnum.ERROR);
    }
    return null;
  }

  public static void addAggregations(
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

  public static SearchRequestBuilder getSearchBuilder(
      TransportClient client, String[] index, String... type) {

    if (type == null || type.length == 0) {
      return client.prepareSearch().setIndices(index);
    } else {
      return client.prepareSearch().setIndices(index).setTypes(type);
    }
  }

  /** Method to add the additional search query like range query , exists - not exist filter etc. */
  @SuppressWarnings("unchecked")
  public static void addAdditionalProperties(
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
  public static SortOrder getSortOrder(String value) {
    return value.equalsIgnoreCase(ASC_ORDER) ? SortOrder.ASC : SortOrder.DESC;
  }

  public static MatchQueryBuilder createMatchQuery(String name, Object text, Float boost) {
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

  public static QueryBuilder createLexicalQuery(
      String key, Map<String, Object> rangeOperation, Float boost) {
    QueryBuilder queryBuilder = null;
    for (Map.Entry<String, Object> it : rangeOperation.entrySet()) {
      if (it.getKey().equalsIgnoreCase(STARTS_WITH)) {
        String startsWithVal = (String) it.getValue();
        if (StringUtils.isNotBlank(startsWithVal)) {
          startsWithVal = startsWithVal.toLowerCase();
        }
        if (isNotNull(boost)) {
          queryBuilder = QueryBuilders.prefixQuery(key + RAW_APPEND, startsWithVal).boost(boost);
        }
        queryBuilder = QueryBuilders.prefixQuery(key + RAW_APPEND, startsWithVal);
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
   * this method will take start time and subtract with current time to get the time spent in
   * millis.
   *
   * @param startTime long
   * @return long
   */
  public static long calculateEndTime(long startTime) {
    return System.currentTimeMillis() - startTime;
  }

  public static SearchDTO createSearchDTO(Map<String, Object> searchQueryMap) {
    SearchDTO search = new SearchDTO();
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
    if (searchQueryMap.containsKey(JsonKey.OFFSET)) {
      if ((searchQueryMap.get(JsonKey.OFFSET)) instanceof Integer) {
        search.setOffset((int) searchQueryMap.get(JsonKey.OFFSET));
      } else {
        search.setOffset(((BigInteger) searchQueryMap.get(JsonKey.OFFSET)).intValue());
      }
    }
    if (searchQueryMap.containsKey(JsonKey.LIMIT)) {
      if ((searchQueryMap.get(JsonKey.LIMIT)) instanceof Integer) {
        search.setLimit((int) searchQueryMap.get(JsonKey.LIMIT));
      } else {
        search.setLimit(((BigInteger) searchQueryMap.get(JsonKey.LIMIT)).intValue());
      }
    }
    if (searchQueryMap.containsKey(JsonKey.GROUP_QUERY)) {
      search
          .getGroupQuery()
          .addAll(
              (Collection<? extends Map<String, Object>>) searchQueryMap.get(JsonKey.GROUP_QUERY));
    }
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

  public static Map<String, String> getMappedIndexAndType(String sunbirdIndex, String sunbirdType) {
    String mappedIndexAndType = "mapping." + sunbirdIndex + "." + sunbirdType;
    Map<String, String> mappedIndexAndTypeResult = new HashMap<>();
    if (config.hasPath(mappedIndexAndType)) {
      mappedIndexAndTypeResult = (Map<String, String>) config.getAnyRef(mappedIndexAndType);
    } else {
      ProjectCommonException.throwServerErrorException(ResponseCode.SERVER_ERROR);
    }
    ProjectLogger.log(
        "Elasticsearch input index "
            + sunbirdIndex
            + " types "
            + sunbirdType
            + " output "
            + mappedIndexAndTypeResult,
        LoggerEnum.DEBUG);
    return mappedIndexAndTypeResult;
  }

  public static List<Map<String, String>> getMappedIndexesAndTypes(
      String sunbirdIndex, String... sunbirdTypes) {
    List<Map<String, String>> mappedIndexesAndTypes = new ArrayList<>();
    for (String sunbirdType : sunbirdTypes) {
      mappedIndexesAndTypes.add(getMappedIndexAndType(sunbirdIndex, sunbirdType));
    }
    return mappedIndexesAndTypes;
  }
}
