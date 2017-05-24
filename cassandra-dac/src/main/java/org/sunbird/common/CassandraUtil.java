package org.sunbird.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
		query.append(Constants.INSERT_INTO+keyspaceName+Constants.DOT+tableName+Constants.OPEN_BRACE);
		Set<String> keySet= map.keySet();
		String keyStmt = String.join(",", keySet);
		query.append(keyStmt+Constants.VALUES_WITH_BRACE);
		StringBuilder commaSepValueBuilder = new StringBuilder();
	    for ( int i = 0; i< keySet.size(); i++){
	      commaSepValueBuilder.append(Constants.QUE_MARK);
	      if ( i != keySet.size()-1){
	        commaSepValueBuilder.append(Constants.COMMA);
	      }
	    }
	    query.append(commaSepValueBuilder+Constants.CLOSING_BRACE);
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
		Response response = new Response();
		List<Row> rows =results.all();
		Map<String, Object> map=null;
		List<Map<String, Object>> responseList= new ArrayList<>();
		String str =results.getColumnDefinitions().toString().substring(8, results.getColumnDefinitions().toString().length()-1);
		 String[] keyArray = str.split("\\), ");
			for(int i=0;i<keyArray.length;i++){
				int pos= keyArray[i].indexOf("(");
				System.out.println((keyArray[i].substring(0,pos).trim()));
			}
		
		for(Row row :rows){
			map=new HashMap<>();
			for(int i=0;i<keyArray.length;i++){
				int pos= keyArray[i].indexOf(Constants.OPEN_BRACE);
				map.put(keyArray[i].substring(0,pos).trim(),row.getObject(i));
			}
			responseList.add(map);
		}
		LOGGER.debug(responseList.toString());
		response.put(Constants.RESPONSE, responseList);
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
		StringBuilder query=new StringBuilder(Constants.UPDATE+keyspaceName+Constants.DOT+tableName+Constants.SET);
		Iterator<Entry<String, Object>> itr1 = map.entrySet().iterator();
		  int i=0;
		  boolean found = false;
		  while (itr1.hasNext()) {
		   String key = itr1.next().getKey();
		   if(!key.equalsIgnoreCase(Constants.IDENTIFIER)) {
		    if(i<map.size()-1 || found) {
		     if(found){
		      if(i<map.size()-1 ) { 
		          query.append( key +" = ? , ");
		          } else {
		           query.append( key +" = ? ");  
		          } 
		     }else{
		       if(i<map.size()-2 ) { 
		       query.append( key +" = ? , ");
		       } else {
		        query.append( key +" = ? ");  
		       }
		    }
		    } 
		   }else {
		    found = true;
		   }
		   i++;  
		  }
		query.append(Constants.WHERE+Constants.IDENTIFIER +"= ? ;");
	    LOGGER.debug(query.toString());
	    System.out.println(query.toString());
		return query.toString();
		
	}

}
