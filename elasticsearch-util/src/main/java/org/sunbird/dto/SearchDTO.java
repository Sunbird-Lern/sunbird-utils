/**
 * 
 */
package org.sunbird.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * This class will take input for elastic search query
 * @author Manzarul
 *
 */
public class SearchDTO {

	@SuppressWarnings("rawtypes")
	private List<Map> properties;
	private List<String> facets;
	private List<String> fields;
	private Map<String, String> sortBy = new HashMap<String, String>();
	private String operation;
	private String query;
	private Integer limit=20;
	private Integer offset=0;
	boolean fuzzySearch = false;
	//additional properties will hold , filters, exist , not exist
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private Map<String, Object> softConstraints = new HashMap<String, Object>();
	private List<Map<String , Object>> groupQuery = new ArrayList<Map<String , Object>>();

	public List<Map<String, Object>> getGroupQuery() {
		return groupQuery;
	}

	public void setGroupQuery(List<Map<String, Object>> groupQuery) {
		this.groupQuery = groupQuery;
	}

	public SearchDTO() {
		super();
	}
	@SuppressWarnings("rawtypes")
	public SearchDTO(List<Map> properties, String operation, int limit) {
		super();
		this.properties = properties;
		this.operation = operation;
		this.limit = limit;
	}
	@SuppressWarnings("rawtypes")
	public List<Map> getProperties() {
		return properties;
	}
	@SuppressWarnings("rawtypes")
	public void setProperties(List<Map> properties) {
		this.properties = properties;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public List<String> getFacets() {
		return facets;
	}
	public void setFacets(List<String> facets) {
		this.facets = facets;
	}
	public Map<String, String> getSortBy() {
		return sortBy;
	}
	public void setSortBy(Map<String, String> sortBy) {
		this.sortBy = sortBy;
	}
	
	public boolean isFuzzySearch() {
		return fuzzySearch;
	}
	public void setFuzzySearch(boolean fuzzySearch) {
		this.fuzzySearch = fuzzySearch;
	}
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	
	public Object getAdditionalProperty(String key) {
		return additionalProperties.get(key);
	}
	public void addAdditionalProperty(String key, Object value) {
		this.additionalProperties.put(key, value);
	}
	public List<String> getFields() {
		return fields;
	}
	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Map<String, Object> getSoftConstraints() {
		return softConstraints;
	}
	public void setSoftConstraints(Map<String, Object> softConstraints) {
		this.softConstraints = softConstraints;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
}
