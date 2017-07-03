package org.sunbird.cassandra;

import java.util.List;
import java.util.Map;

import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;

/**
 * @desc this interface will hold functions for cassandra db interaction
 * 
 * @author Amit Kumar
 *
 */
public interface CassandraOperation {
	/**
	 * @desc This method is used to insert/update record in cassandra db (if primary key exist in request ,it will update  
	 * else will insert the record in cassandra db.
	 * By default cassandra insert operation does upsert operation.
	 * Upsert means that Cassandra will insert a row if a primary key does not exist already otherwise 
	 * if primary key already exists, it will update that row.)
	 * 
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param request Map<String,Object>(i.e map of column name and their value)
	 * @return Response Response
	 * @throws ProjectCommonException (throws exception if it fails to do upsert operation.) 
	 */
	public Response upsertRecord(String keyspaceName,String tableName,Map<String,Object> request) throws ProjectCommonException;

	/**
	 * @desc This method is used to insert record in cassandra db 
	 * 
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param request Map<String,Object>(i.e map of column name and their value)
	 * @return Response Response
	 * @throws ProjectCommonException (throws exception if it fails to do insert operation.) 
	 */
	public Response insertRecord(String keyspaceName,String tableName,Map<String,Object> request) throws ProjectCommonException;

	/**
	 * @desc This method is used to update record in cassandra db 
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param request Map<String,Object>(i.e map of column name and their value)
	 * @return Response Response
	 * @throws ProjectCommonException (throws exception if it fails to do update operation.) 
	 */
	public Response updateRecord(String keyspaceName,String tableName,Map<String,Object> request) throws ProjectCommonException;
	
	/**
	 * @desc This method is used to delete record in cassandra db by their primary key(identifier)
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param identifier String
	 * @return Response Response
	 * @throws ProjectCommonException (throws exception if it fails to do delete operation.) 
	 */
	public Response deleteRecord(String keyspaceName,String tableName,String identifier) throws ProjectCommonException;
	
	/**
	 * @desc This method is used to fetch record based on primary key
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param identifier String
	 * @return Response  Response
	 * @throws ProjectCommonException  
	 */
	public Response  getRecordById(String keyspaceName,String tableName,String identifier) throws ProjectCommonException;
	
	/**
	 * @desc This method is used to fetch record based on given parameter and it's value
	 * (it only fetch the record based on property or column name if you have created index on that column otherwise 
	 * it will throw exception.)
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param propertyName String
	 * @param propertyValue Object
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response  getRecordsByProperty(String keyspaceName,String tableName,String propertyName,Object propertyValue) throws ProjectCommonException;
	
	/**
	 * @desc This method is used to fetch record based on given parameter and it's list of value
	 * (for In Query , for example : SELECT * FROM mykeyspace.mytable WHERE id IN (‘A’,’B’,C’) )
	 * 
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param propertyName String
	 * @param propertyValueList List<Object> 
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response  getRecordsByProperty(String keyspaceName,String tableName,String propertyName,List<Object> propertyValueList) throws ProjectCommonException;
	
	/**
	 * @desc This method is used to fetch record based on given parameter list and their values
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param propertyMap Map<String,Object> propertyMap)(i.e map of column name and their value)
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response  getRecordsByProperties(String keyspaceName,String tableName,Map<String,Object> propertyMap) throws ProjectCommonException;
	
	/**
	 * @desc This method is used to fetch properties value based on id 
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @param id  String
	 * @param properties String varargs
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response  getPropertiesValueById(String keyspaceName,String tableName,String id,String... properties) throws ProjectCommonException;
	
	/**
	 * @desc This method is used to fetch all records for table(i.e Select * from tableName)
	 * @param keyspaceName String (data base keyspace name)
	 * @param tableName String
	 * @return Response Response
	 * @throws ProjectCommonException 
	 */
	public Response  getAllRecords(String keyspaceName,String tableName) throws ProjectCommonException;
}
