/**
 *
 */
package org.sunbird.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LogHelper;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.dto.SearchDTO;
import org.sunbird.helper.ConnectionManager;
import org.sunbird.helper.ElasticSearchQueryBuilder;
import org.sunbird.util.ESOperation;

/**
 * This class will provide all required operation
 * for elastic search.
 *
 * @author Manzarul
 */
public class ElasticSearchUtil {
    private static final LogHelper LOGGER = LogHelper.getInstance(ElasticSearchUtil.class.getName());
    private static ConcurrentHashMap<String, Boolean> indexMap = new ConcurrentHashMap<String, Boolean>();
    private static ConcurrentHashMap<String, Boolean> typeMap = new ConcurrentHashMap<String, Boolean>();
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
        verifyOrCreateIndexAndType(index, type);
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
        	verifyOrCreateIndexAndType(index, type);
            response = ConnectionManager.getClient().prepareGet().setIndex(index).setId(identifier).get();
        } else {
        	 verifyOrCreateIndexAndType(index, type);
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
        	verifyOrCreateIndexAndType(index, type);
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
        	verifyOrCreateIndexAndType(index, type);
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
        	verifyOrCreateIndexAndType(index, type);
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
        searchDTO.getSortBy().put("courseDuration", "desc");
        searchDTO.getSortBy().put("createdOn" , "desc");
        //TODO:delete
        List<String> fields = new ArrayList<>();
        fields.add("lastPublishedOn");
        fields.add("size");
        fields.add("courseDuration");
        fields.add("createdOn");
        fields.add("courseAddedByName");
        fields.add("courseDuration");
        searchDTO.setFields(fields);
        searchDTO.setOffset(1);
        searchDTO.setLimit(30);
        ESOperation operation1 = new ESOperation();
        operation1.setType(ESOperation.Operations.RANGE_QUERY.getValue());
        Map<String, Integer> rangeMap = new HashMap<String, Integer>();
        rangeMap.put("from", 54);
        rangeMap.put("to", 70);
        operation1.setValue(rangeMap);
        searchDTO.getAdditionalProperties().put("courseDuration", operation1);

       /* ESOperation dateOperation = new ESOperation();
        dateOperation.setType(ESOperation.Operations.RANGE_QUERY.getValue());
        Map<String, String> daterangeMap = new HashMap<String, String>();
        daterangeMap.put("from", "36");
        dateOperation.setValue(daterangeMap);
        searchDTO.getAdditionalProperties().put("noOfLecture", dateOperation);*/

        ESOperation existsOperation = new ESOperation();
        existsOperation.setType(ESOperation.Operations.SHOULD_EXISTS.getValue());
        searchDTO.getAdditionalProperties().put("owner", existsOperation);

        Map<String,List<Map<String,Object>>> response = complexSearch(searchDTO, "sunbird-inx5", "course");
        System.out.println(response.get(JsonKey.RESPONSE));
        //response.getHits().getAt(0).getSource()
        //complexSearch(searchDTO, "sunbird-inx5", "course");
        //complexSearch(searchDTO, "sunbird-inx5", "course");
        List<String> facetList = new ArrayList<String>();
        facetList.add("noOfLecture");
        facetList.add("pkgVersion");
        searchDTO.setFacets(facetList);

        complexSearch(searchDTO, "sunbird-inx5", "course");

    }

    public static Map<String,List<Map<String,Object>>>  complexSearch(SearchDTO searchDTO, String index, String type) {

        SearchRequestBuilder searchRequestBuilder = getSearchBuilder(ConnectionManager.getClient(), index, type);

        BoolQueryBuilder query = new BoolQueryBuilder();

        //apply simple query string
        if (null != searchDTO.getQuery()) {
            query.should(QueryBuilders.simpleQueryStringQuery(searchDTO.getQuery()));
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
        List<Map<String,Object>> esResponse = new ArrayList<Map<String,Object>>();
        Map<String,List<Map<String,Object>>> responsemap = new HashMap<>();
        if (response != null) {
        	SearchHits hits = response.getHits();
        	for (SearchHit hit :  hits) {
        		esResponse.add(hit.getSource());
        	}
        }
        responsemap.put(JsonKey.RESPONSE, esResponse);
        return responsemap;
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


        //need to replace by FILTERS type
        if (key.equalsIgnoreCase(ESOperation.Operations.FILTERS.getValue())) {
            Map<String, Object> filters = (Map<String, Object>) entry.getValue();
            for (Map.Entry<String, Object> en : filters.entrySet()) {

                ESOperation operation = createFilterESOpperation(en , query);
                /*if (key.equalsIgnoreCase(ESOperation.Operations.SIMPLE_FIELD_QUERY.getValue())) {
                    operation = createFilterESOpperation(en);
                    query.should(QueryBuilders.commonTermsQuery(key, operation.getValue()));
                }
                if (key.equalsIgnoreCase(ESOperation.Operations.MULTI_VALUE_QUERY.getValue())) {
                    operation = createFilterESOpperation(en);
                    query.should(QueryBuilders.multiMatchQuery(key, ((List<String>)operation.getValue()).stream().toArray(String[]::new)));
                }*/
            }
        }else if (key.equalsIgnoreCase(ESOperation.Operations.SHOULD_EXISTS.getValue())) {
            createESOpperation(entry ,query);
           /* ESOperation operation = createESOpperation(entry ,query);
            query.must(existsQuery);
            ExistsQueryBuilder existsQuery1 =  QueryBuilders.existsQuery("courseId");
            query.must(existsQuery1);*/
        }else if (key.equalsIgnoreCase(ESOperation.Operations.SHOULD_NOT_EXISTS.getValue())) {
            createESOpperation(entry ,query);
            /*ESOperation operation = createESOpperation(entry, query);
        	 query.mustNot(notExistsQuery);*/
        }

    }

    private static ESOperation createFilterESOpperation(Entry<String, Object> entry, BoolQueryBuilder query) {

        String key = entry.getKey();
        Object val = entry.getValue();
        ESOperation esOperation = new ESOperation();

        if(val instanceof String){
            /*esOperation.setType(ESOperation.Operations.SIMPLE_FIELD_QUERY.getValue());
            esOperation.setValue(val);
            return esOperation;*/
            query.should(QueryBuilders.commonTermsQuery(key, val));
        }else if(val instanceof List){
            /*List list =(List) val;
            esOperation.setType(ESOperation.Operations.MULTI_VALUE_QUERY.getValue());
            esOperation.setValue(list);
            return esOperation;*/
            query.should(QueryBuilders.multiMatchQuery(key, ((List<String>)val).stream().toArray(String[]::new)));
        }else if(val instanceof Map){
            Map<String , Object> value = (Map<String , Object>) val;
            Map<String , Object> rangeOperation = new HashMap<String, Object>();
            for(Map.Entry<String , Object> it:value.entrySet()){
                String operation = it.getKey();
                if(operation.startsWith("<") || operation.startsWith(">")){
                    rangeOperation.put(operation , it.getValue());
                }
            }

            for(Map.Entry<String , Object> it : rangeOperation.entrySet()){
                if(it.getKey().equalsIgnoreCase("<=")){
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(key).lte(it.getValue());
                    query.must(rangeQueryBuilder);
                }else if(it.getKey().equalsIgnoreCase("<")){
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(key).lt(it.getValue());
                    query.must(rangeQueryBuilder);
                }
                else if(it.getKey().equalsIgnoreCase(">=")){
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(key).gte(it.getValue());
                    query.must(rangeQueryBuilder);
                }else if(it.getKey().equalsIgnoreCase(">")){
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(key).gt(it.getValue());
                    query.must(rangeQueryBuilder);
                }
            }

        }
        return null;
    }

    private static ESOperation createESOpperation(Entry<String, Object> entry, BoolQueryBuilder query) {

        String operation = entry.getKey();
        //Map<String , Object> obj = (Map<String , Object>)entry.getValue();
        ESOperation esOperation = new ESOperation();

        if(operation.equalsIgnoreCase(JsonKey.EXISTS)){
            List<String> existsList = new ArrayList<String>();
            for(String str : existsList){
                ExistsQueryBuilder existsQuery1 =  QueryBuilders.existsQuery(str);
                query.must(existsQuery1);
            }

            return esOperation;

        }else if(operation.equalsIgnoreCase(JsonKey.NOT_EXISTS)){
            List<String> existsList = new ArrayList<String>();
            for(String str : existsList){
                ExistsQueryBuilder existsQuery1 =  QueryBuilders.existsQuery(str);
                query.mustNot(existsQuery1);
            }
            return esOperation;

        }

        return null;

    }

    private static SortOrder getSortOrder(String value) {
        return value.equalsIgnoreCase("ASC") ? SortOrder.ASC : SortOrder.DESC;
    }

    /**
     * This method will check indices is already created or not.
     * @param indices String
     */
	private static void verifyOrCreateIndex(String... indices) {
		for (String index : indices) {
			if (!indexMap.containsKey(index)) {
				try {
					boolean indexResponse = ConnectionManager.getClient().admin().indices()
							.exists(Requests.indicesExistsRequest(index)).get().isExists();
					if (indexResponse) {
						indexMap.put(index, true);
					} else {
						boolean createIndexResp = createIndex(index, null, null,
								ElasticSearchQueryBuilder.createSettingsForIndex());
						if (createIndexResp) {
							indexMap.put(index, true);
						}
					}
				} catch (InterruptedException | ExecutionException e) {
					boolean createIndexResp = createIndex(index, null, null,
							ElasticSearchQueryBuilder.createSettingsForIndex());
					if (createIndexResp) {
						indexMap.put(index, true);
					}
				}
			}
		}
	}

    /**
     * This method will check types are created or not.
     * @param indices String []
     * @param types String var arg
     */
	private static void verifyOrCreatType(String indices, String... types) {
		for (String type : types) {
			if (!typeMap.containsKey(type)) {
				TypesExistsRequest typesExistsRequest = new TypesExistsRequest(new String[] { indices }, type);
				try {
					boolean typeResponse = ConnectionManager.getClient().admin().indices()
							.typesExists(typesExistsRequest).get().isExists();
					if (typeResponse) {
						typeMap.put(type, true);
					} else {
						boolean response = addOrUpdateMapping(indices, type, ElasticSearchQueryBuilder.createMapping());
						if (response) {
							typeMap.put(type, true);
						}
					}
				} catch (InterruptedException | ExecutionException e) {
					boolean response = addOrUpdateMapping(indices, type, ElasticSearchQueryBuilder.createMapping());
					if (response) {
						typeMap.put(type, true);
					}
				}
			}
		}
	}


	private static boolean verifyOrCreateIndexAndType(String index, String type) {
		if (indexMap.containsKey(index)) {
			if (typeMap.containsKey(type)) {
				return true;
			}
			verifyOrCreatType(index, type);
			return true;
		} else {
			verifyOrCreateIndex(index);
			verifyOrCreatType(index, type);
			return true;
		}
	}


}


