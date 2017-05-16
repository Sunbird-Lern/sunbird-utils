package org.sunbird.cassandra;

import java.util.List;
import java.util.Map;

import org.sunbird.common.models.response.Response;

public interface CassandraOperation {
	
	/**
	 * used to insert record in cassandra db 
	 * @param keyspaceName
	 * @param tableName
	 * @param request
	 * @return Response
	 */
	public Response insertRecord(String keyspaceName,String tableName,Map<String,Object> request);

	/**
	 * used to update record in cassandra db 
	 * @param keyspaceName
	 * @param tableName
	 * @param request
	 * @param identifier
	 * @return Response
	 */
	public Response updateRecord(String keyspaceName,String tableName,Map<String,Object> request,String identifier);
	
	/**
	 * used to delete record in cassandra db
	 * @param keyspaceName
	 * @param tableName
	 * @param identifier
	 * @return Response
	 */
	public Response deleteRecord(String keyspaceName,String tableName,String identifier);
	
	/**
	 * used to fetch record based on primary key
	 * @param keyspaceName
	 * @param tableName
	 * @param identifier
	 * @return Response 
	 */
	public Response  getRecordById(String keyspaceName,String tableName,String identifier);
	
	/**
	 * used to fetch record based on given parameter and it's value
	 * @param keyspaceName
	 * @param tableName
	 * @param propertyName
	 * @param propertyValue
	 * @return Response
	 */
	public Response  getRecordsByProperty(String keyspaceName,String tableName,String propertyName,String propertyValue);
	
	/**
	 * used to fetch record based on given parameter and it's value
	 * @param keyspaceName
	 * @param tableName
	 * @param propertyName
	 * @param propertyValueList
	 * @return Response
	 */
	public Response  getRecordsByProperty(String keyspaceName,String tableName,String propertyName,List<Object> propertyValueList);
	
	/**
	 * used to fetch record based on given parameter list and their values
	 * @param keyspaceName
	 * @param tableName
	 * @param propertyMap
	 * @return Response
	 */
	public Response  getRecordsByProperties(String keyspaceName,String tableName,Map<String,Object> propertyMap);
}
