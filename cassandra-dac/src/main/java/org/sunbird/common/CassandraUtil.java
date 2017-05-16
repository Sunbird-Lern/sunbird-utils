package org.sunbird.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.LogHelper;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

/**
 * This class will provide all required operation
 * for cassandra db. 
 * @author Manzarul
 * @author Amit Kumar
 */
public final class CassandraUtil{

	private final static LogHelper LOGGER = LogHelper.getInstance(CassandraUtil.class.getName());
	
	/**
	 * this method is used to create prepared statement based on table name and column name provided
	 * @param keyspaceName
	 * @param tableName
	 * @param map is key value pair (key is column name and value is value of column)
	 * @return String
	 * @author Amit Kumar
	 */
	public static String getPreparedStatement(String keyspaceName, String tableName, Map<String, Object> map){
		StringBuilder query=new StringBuilder();
		query.append("INSERT INTO "+keyspaceName+"."+tableName+" (");
		Set<String> keySet= map.keySet();
		String keyStmt = String.join(",", keySet);
		query.append(keyStmt+") VALUES (");
		StringBuilder commaSepValueBuilder = new StringBuilder();
	    for ( int i = 0; i< keySet.size(); i++){
	      //append the value into the builder
	      commaSepValueBuilder.append("?");
	      //if the value is not the last element of the list
	      //then append the comma(,) as well
	      if ( i != keySet.size()-1){
	        commaSepValueBuilder.append(", ");
	      }
	    }
	    query.append(commaSepValueBuilder+");");
	    LOGGER.debug(query.toString());
		return query.toString();
		
	}
	/**
	 * this method is used for creating response from the resultset
	 * i.e creating map<String,Object> or map<columnName,columnValue>
	 * @param results
	 * @return Response
	 * @author Amit Kumar
	 */
	public static Response createResponse(ResultSet results){
		System.out.println(" inside create Result");
		Response response = new Response();
		List<Row> rows =results.all();
		Map<String, Object> map=null;
		List<Map<String, Object>> responseList= new ArrayList<>();
		String[] keyArray =results.getColumnDefinitions().toString().substring(8, results.getColumnDefinitions().toString().length()-1).split(",");
		for(Row row :rows){
			map=new HashMap<>();
			String[] valueArray =row.toString().substring(4, row.toString().length()-1).split(",");
			for(int i=0;i<keyArray.length;i++){
				int pos= keyArray[i].indexOf("(");
				map.put(keyArray[i].substring(0,pos),valueArray[i]);
			}
			responseList.add(map);
			System.out.println(Arrays.toString(valueArray));
		}
		
		LOGGER.debug(responseList.toString());
		response.put("response", responseList);
		return response;
	}
	
	/**
	 * this method is used to create update query statement based on table name and column name provided
	 * @param keyspaceName
	 * @param tableName
	 * @param map
	 * @return String
	 * @author Amit Kumar
	 */
	public static String getUpdateQueryStatement(String keyspaceName, String tableName, Map<String, Object> map){
		StringBuilder query=new StringBuilder("UPDATE "+keyspaceName+"."+tableName+" SET ");
		Set<String> keySet= map.keySet();
		Iterator<String> itr = keySet.iterator();
		int i=0;
		while(itr.hasNext()){
			query.append(itr.next() +" = ? ");
			if ( i != keySet.size()-1){
				query.append(", ");
		      }
			i++;
		}
		query.append(" where "+Constants.IDENTIFIER +"= ? ;");
	    LOGGER.debug(query.toString());
	    System.out.println(query.toString());
		return query.toString();
		
	}

}
