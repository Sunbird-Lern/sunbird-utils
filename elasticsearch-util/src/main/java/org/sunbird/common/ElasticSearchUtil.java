/**
 *
 */
package org.sunbird.common;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.sunbird.common.models.util.LogHelper;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.dto.SearchDTO;
import org.sunbird.helper.ConnectionManager;
import org.sunbird.util.ESOperation;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

/**
 * This class will provide all required operation
 * for elastic search.
 *
 * @author Manzarul
 */
public class ElasticSearchUtil {
    private static final LogHelper LOGGER = LogHelper.getInstance(ElasticSearchUtil.class.getName());
    public static final String INDEX_NAME = "sunbird";
    public enum IndexType {course,content,user}
    /**
     * This method will put a new data entry inside Elastic search. identifier
     * value becomes _id inside ES, so every time provide a unique value while
     * saving it.
     *
     * @param index      String  ES index name
     * @param type       String  ES type name
     * @param identifier ES column identifier as an String
     * @param data       Map<String,Object>
     * @param
     */
    public static String createData(String index, String type, String identifier, Map<String, Object> data) {
        if (ProjectUtil.isStringNullOREmpty(identifier) || ProjectUtil.isStringNullOREmpty(type)
                || ProjectUtil.isStringNullOREmpty(index)) {
            LOGGER.info("Identifier value is null or empty ,not able to save data.");
            return "ERROR";
        }
        data.put("identifier", identifier);
        IndexResponse response = ConnectionManager.getClient().prepareIndex(index, type, identifier).setSource(data)
                .get();
        LOGGER.info("Save value==" + response.getId() + " " + response.status());
        return response.getId();
    }

    /**
     * This method will provide data form ES based on incoming identifier.
     * we can get data by passing index and  identifier values , or all the three
     *
     * @param type       String
     * @param identifier String
     * @return Map<String,Object>
     */
    public static Map<String, Object> getDataByIdentifier(String index, String type, String identifier) {
        GetResponse response = null;
        if (ProjectUtil.isStringNullOREmpty(index) && ProjectUtil.isStringNullOREmpty(type)
                && ProjectUtil.isStringNullOREmpty(identifier)) {
            LOGGER.info("Invalid request is coming.");
        } else if (ProjectUtil.isStringNullOREmpty(index)) {
            LOGGER.info("Please provide index value.");
        } else if (ProjectUtil.isStringNullOREmpty(type)) {
            response = ConnectionManager.getClient().prepareGet().setIndex(index).setId(identifier).get();
        } else {
            response = ConnectionManager.getClient().prepareGet(index, type, identifier).get();
        }
        if (response == null) {
            return null;
        }
        return response.getSource();
    }

    /**
     * This method will do the data search inside ES. based on incoming search data.
     *
     * @param index      String
     * @param type       String
     * @param searchData Map<String,Object>
     * @return Map<String,Object>
     */
    public static Map<String, Object> searchData(String index, String type, Map<String, Object> searchData) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        Iterator<Entry<String, Object>> itr = searchData.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, Object> entry = itr.next();
            sourceBuilder.query(QueryBuilders.commonTermsQuery(entry.getKey(), entry.getValue()));
        }
        SearchResponse sr = null;
        try {
            sr = ConnectionManager.getClient().search(new SearchRequest(index).types(type).source(sourceBuilder)).get();
        } catch (InterruptedException e) {
            LOGGER.error(e);
        } catch (ExecutionException e) {
            LOGGER.error(e);
        }
        sr.getHits().getAt(0).getSource();

        return sr.getAggregations().asList().get(0).getMetaData();
    }

    /**
     * This method will update data based on identifier.take the data based on identifier and merge with
     * incoming data then update it.
     *
     * @param index      String
     * @param type       String
     * @param identifier String
     * @param data       Map<String,Object>
     * @return boolean
     */
    public static boolean updateData(String index, String type, String identifier, Map<String, Object> data) {
        if (!ProjectUtil.isStringNullOREmpty(index) && !ProjectUtil.isStringNullOREmpty(type)
                && !ProjectUtil.isStringNullOREmpty(identifier) && data != null) {
            UpdateResponse response = ConnectionManager.getClient().prepareUpdate(index, type, identifier).setDoc(data)
                    .get();
            LOGGER.info("updated response==" + response.getResult().name());
            if (response.getResult().name().equals("UPDATED")) {
                return true;
            }
        } else {
            LOGGER.info("Requested data is invalid.");
        }
        return false;
    }

    /**
     * This method will remove data from ES based on identifier.
     *
     * @param index      String
     * @param type       String
     * @param identifier String
     */
    public static void removeData(String index, String type, String identifier) {
        if (!ProjectUtil.isStringNullOREmpty(index) && !ProjectUtil.isStringNullOREmpty(type)
                && !ProjectUtil.isStringNullOREmpty(identifier)) {
            DeleteResponse response = ConnectionManager.getClient().prepareDelete(index, type, identifier).get();
            LOGGER.info("delete info ==" + response.getResult().name() + " " + response.getId());
        } else {
            LOGGER.info("Data can not be deleted due to invalid input.");
        }
    }

    /**
     * This method will create index  , type ,setting and mapping.
     *
     * @param index    String index name
     * @param type     String type name
     * @param mappings String
     * @param settings String
     * @return boolean
     */
    @SuppressWarnings("deprecation")
    public static boolean createIndex(String index, String type, String mappings, String settings) {
        boolean response = false;
        if (ProjectUtil.isStringNullOREmpty(index)) {
            return response;
        }
        CreateIndexResponse createIndexResponse = null;
        TransportClient client = ConnectionManager.getClient();
        @SuppressWarnings("deprecation")
        CreateIndexRequestBuilder createIndexBuilder = client.admin().indices().prepareCreate(index);
        if (!ProjectUtil.isStringNullOREmpty(settings)) {
            createIndexResponse = createIndexBuilder.setSettings(settings).get();
        } else {
            createIndexResponse = createIndexBuilder.get();
        }
        if (createIndexResponse != null && createIndexResponse.isAcknowledged()) {
            response = true;
            if (!ProjectUtil.isStringNullOREmpty(mappings) && !ProjectUtil.isStringNullOREmpty(type)) {
                PutMappingResponse mappingResponse = client.admin().indices().preparePutMapping(index).setType(type)
                        .setSource(mappings).get();
                if (mappingResponse.isAcknowledged()) {
                    response = true;
                } else {
                    response = false;
                }
            } else if (!ProjectUtil.isStringNullOREmpty(type)) {
                PutMappingResponse mappingResponse = client.admin().indices().preparePutMapping(index).setType(type)
                        .get();
                if (mappingResponse.isAcknowledged()) {
                    response = true;
                } else {
                    response = false;
                }
            }
        }
        LOGGER.info("Index creation status==" + response);
        return response;
    }


    /**
     * This method will type and mapping under already created index.
     *
     * @param indexName String
     * @param typeName  String
     * @param mapping   String
     * @return boolean
     */
    public static boolean addOrUpdateMapping(String indexName, String typeName, String mapping) {
        @SuppressWarnings("deprecation")
        PutMappingResponse response = ConnectionManager.getClient().admin().indices().preparePutMapping(indexName)
                .setType(typeName).setSource(mapping).get();
        if (response.isAcknowledged()) {
            return true;
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
        DeleteIndexResponse deleteResponse = ConnectionManager.getClient().admin().indices().prepareDelete(index).get();
        if (deleteResponse != null && deleteResponse.isAcknowledged()) {
            response = true;
        }
        return response;
    }

    public static void main(String[] args) {
        //searchQuery("bookdb_index" , "book",null);
        SearchDTO searchDTO = new SearchDTO();
        //searchDTO.setSampleStringQuery("guide");
        //TODO:delete
        searchDTO.getSortBy().put("courseDuration", "asc");
        //searchDTO.getSortBy().put("publish_date" , "desc");
        //TODO:delete
        List<String> fields = new ArrayList<>();
        fields.add("lastPublishedOn");
        fields.add("size");
        fields.add("courseDuration");
        //fields.add("publish_date");
        fields.add("courseAddedByName");
        searchDTO.setFields(fields);
        //searchDTO.setOffset(1);
        //searchDTO.setLimit(2);
        ESOperation operation1 = new ESOperation();
        operation1.setType(ESOperation.Operations.RANGE_QUERY.getValue());
        Map<String, Integer> rangeMap = new HashMap<String, Integer>();
        rangeMap.put("from", 5);
        rangeMap.put("to", 7);
        operation1.setValue(rangeMap);
        searchDTO.getAdditionalProperties().put("courseDuration", operation1);

        ESOperation dateOperation = new ESOperation();
        dateOperation.setType(ESOperation.Operations.RANGE_QUERY.getValue());
        Map<String, String> daterangeMap = new HashMap<String, String>();
        daterangeMap.put("from", "36");
        dateOperation.setValue(daterangeMap);
        searchDTO.getAdditionalProperties().put("noOfLecture", dateOperation);

        ESOperation existsOperation = new ESOperation();
        existsOperation.setType(ESOperation.Operations.SHOULD_EXISTS_FIELD.getValue());
        searchDTO.getAdditionalProperties().put("owner", existsOperation);

        List<String> facetList = new ArrayList<String>();
        facetList.add("noOfLecture");
        facetList.add("pkgVersion");
        searchDTO.setFacets(facetList);

        complexSearch(searchDTO, "sunbird-inx5", "course");

    }

    private static SearchResponse getSearchResponse(String index, String type, SearchSourceBuilder sourceBuilder, TransportClient client) {
        SearchResponse searchResponse = null;
        try {
            if (ProjectUtil.isStringNullOREmpty(type)) {
                searchResponse = client.search(new SearchRequest(index).source(sourceBuilder)).get();
            } else {
                searchResponse = client.search(new SearchRequest(index).types(type).source(sourceBuilder)).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return searchResponse;
    }

    public static SearchResponse complexSearch(SearchDTO searchDTO, String index, String type) {

        SearchRequestBuilder searchRequestBuilder = getSearchBuilder(ConnectionManager.getClient(), index, type);

        BoolQueryBuilder query = new BoolQueryBuilder();

        //apply simple query string
        if (null != searchDTO.getSampleStringQuery()) {
            query.should(QueryBuilders.simpleQueryStringQuery(searchDTO.getSampleStringQuery()));
        }
        //apply the sorting
        for (Map.Entry<String, String> entry : searchDTO.getSortBy().entrySet()) {
            searchRequestBuilder.addSort(entry.getKey(), getSortOrder(entry.getValue()));
        }

        //apply the fields filter
        searchRequestBuilder.setFetchSource(searchDTO.getFields().stream().toArray(String[]::new), null);

        // setting the offset
        if (searchDTO.getOffset() != null) {
            searchRequestBuilder.setFrom(searchDTO.getOffset());
        }

        //setting the limit
        if (searchDTO.getLimit() != null) {
            searchRequestBuilder.setSize(searchDTO.getLimit());
        }
        //apply additional properties
        for (Map.Entry<String, Object> entry : searchDTO.getAdditionalProperties().entrySet()) {
            addAdditionalProperties(query, entry);
        }

        //set final query to search request builder
        searchRequestBuilder.setQuery(query);

        for(String facets : searchDTO.getFacets()){
            searchRequestBuilder.addAggregation(AggregationBuilders.terms(facets).field(facets));
        }

        SearchResponse response = searchRequestBuilder.execute().actionGet();

        return response;

    }

    private static SearchRequestBuilder getSearchBuilder(TransportClient client, String index, String type) {

        if (ProjectUtil.isStringNullOREmpty(type)) {
            return client.prepareSearch()
                    .setIndices(index);
        } else {
            return client.prepareSearch()
                    .setIndices(index)
                    .setTypes(type);
        }
    }

    private static void addAdditionalProperties(BoolQueryBuilder query, Map.Entry<String, Object> entry) {

        String key = entry.getKey();
        ESOperation operation = (ESOperation) entry.getValue();

        if (operation.getType().equalsIgnoreCase(ESOperation.Operations.RANGE_QUERY.getValue())) {

            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(key);
            Map<String, Object> range = (Map<String, Object>) operation.getValue();
            if (range.containsKey("from")) {
                rangeQueryBuilder.gte(range.get("from"));
            }
            if (range.containsKey("to")) {
                rangeQueryBuilder.lte(range.get("to"));
            }
            query.must(rangeQueryBuilder);
        } else if (operation.getType().equalsIgnoreCase(ESOperation.Operations.SIMPLE_FIELD_QUERY.getValue())) {
            query.should(QueryBuilders.commonTermsQuery(key, operation.getValue()));
        } else if (operation.getType().equalsIgnoreCase(ESOperation.Operations.SHOULD_EXISTS_FIELD.getValue())) {
            ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(key);
            query.must(existsQuery);
        }

    }

    private static SortOrder getSortOrder(String value) {
        return value.equalsIgnoreCase("ASC") ? SortOrder.ASC : SortOrder.DESC;
    }
}


