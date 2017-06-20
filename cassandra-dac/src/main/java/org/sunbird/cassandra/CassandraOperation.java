package org.sunbird.cassandra;

import java.util.List;
import java.util.Map;

import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;

/**
 * This interface provides method to interact with cassandra database
 * @author Amit Kumar
 *
 */
public interface CassandraOperation {
	/**
	 * This method is used to insert/update record in cassandra db 
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param request Map<String,Object>(i.e map of column name and their value)
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response upsertRecord(String keyspaceName,String tableName,Map<String,Object> request) throws ProjectCommonException;

	/**
	 * This method is used to insert record in cassandra db 
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param request Map<String,Object>(i.e map of column name and their value)
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response insertRecord(String keyspaceName,String tableName,Map<String,Object> request) throws ProjectCommonException;

	/**
	 * This method is used to update record in cassandra db 
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param request Map<String,Object>(i.e map of column name and their value)
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response updateRecord(String keyspaceName,String tableName,Map<String,Object> request) throws ProjectCommonException;
	
	/**
	 * This method is used to delete record in cassandra db
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param identifier String
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response deleteRecord(String keyspaceName,String tableName,String identifier) throws ProjectCommonException;
	
	/**
	 * This method is used to fetch record based on primary key
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param identifier String
	 * @return Response  Response
	 * @throws ProjectCommonException 
	 */
	public Response  getRecordById(String keyspaceName,String tableName,String identifier) throws ProjectCommonException;
	
	/**
	 * This method is used to fetch record based on given parameter and it's value
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param propertyName String
	 * @param propertyValue Object
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response  getRecordsByProperty(String keyspaceName,String tableName,String propertyName,Object propertyValue) throws ProjectCommonException;
	
	/**
	 * used to fetch record based on given parameter and it's value
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param propertyName String
	 * @param propertyValueList List<Object> 
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response  getRecordsByProperty(String keyspaceName,String tableName,String propertyName,List<Object> propertyValueList) throws ProjectCommonException;
	
	/**
	 * This method is used to fetch record based on given parameter list and their values
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param propertyMap Map<String,Object> propertyMap)(i.e map of column name and their value)
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response  getRecordsByProperties(String keyspaceName,String tableName,Map<String,Object> propertyMap) throws ProjectCommonException;
	
	/**
	 * This method is used to fetch properties value based on id 
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param id  String
	 * @param properties String varargs
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response  getPropertiesValueById(String keyspaceName,String tableName,String id,String... properties) throws ProjectCommonException;
	
	/**
	 * This method is used to fetch all records for table
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response  getAllRecords(String keyspaceName,String tableName) throws ProjectCommonException;
}
