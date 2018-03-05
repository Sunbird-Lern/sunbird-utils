package org.sunbird.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

/**
 * @desc This class will provide all required helper method for cassandra db
 *       operation.
 * @author Amit Kumar
 */
public final class CassandraUtil {

	private static final PropertiesCache instance = PropertiesCache.getInstance();

	private CassandraUtil() {
	}

	/**
	 * @desc This method is used to create prepared statement based on table name
	 *       and column name provided in request
	 * @param keyspaceName
	 *            String (data base keyspace name)
	 * @param tableName
	 *            String
	 * @param map
	 *            is key value pair (key is column name and value is value of
	 *            column)
	 * @return String String
	 */
	public static String getPreparedStatement(String keyspaceName, String tableName, Map<String, Object> map) {
		StringBuilder query = new StringBuilder();
		query.append(Constants.INSERT_INTO + keyspaceName + Constants.DOT + tableName + Constants.OPEN_BRACE);
		Set<String> keySet = map.keySet();
		query.append(String.join(",", keySet) + Constants.VALUES_WITH_BRACE);
		StringBuilder commaSepValueBuilder = new StringBuilder();
		for (int i = 0; i < keySet.size(); i++) {
			commaSepValueBuilder.append(Constants.QUE_MARK);
			if (i != keySet.size() - 1) {
				commaSepValueBuilder.append(Constants.COMMA);
			}
		}
		query.append(commaSepValueBuilder + ")" + Constants.IF_NOT_EXISTS);
		ProjectLogger.log(query.toString());
		return query.toString();

	}

	/**
	 * @desc This method is used for creating response from the resultset i.e return
	 *       map <String,Object> or map<columnName,columnValue>
	 * @param results
	 *            ResultSet
	 * @return Response Response
	 */
	public static Response createResponse(ResultSet results) {
		Response response = new Response();
		List<Row> rows = results.all();
		Map<String, Object> map = null;
		List<Map<String, Object>> responseList = new ArrayList<>();
		String str = results.getColumnDefinitions().toString().substring(8,
				results.getColumnDefinitions().toString().length() - 1);
		String[] keyArray = str.split("\\), ");
		for (Row row : rows) {
			map = new HashMap<>();
			for (int i = 0; i < keyArray.length; i++) {
				int pos = keyArray[i].indexOf(Constants.OPEN_BRACE);
				String column = instance.getProperty(keyArray[i].substring(0, pos).trim());
				map.put(column, row.getObject(column));
			}
			responseList.add(map);
		}
		ProjectLogger.log(responseList.toString());
		response.put(Constants.RESPONSE, responseList);
		return response;
	}

	/**
	 * @desc This method is used to create update query statement based on table
	 *       name and column name provided
	 * @param keyspaceName
	 *            String (data base keyspace name)
	 * @param tableName
	 *            String
	 * @param map
	 *            Map<String, Object>
	 * @return String String
	 */
	public static String getUpdateQueryStatement(String keyspaceName, String tableName, Map<String, Object> map) {
		StringBuilder query = new StringBuilder(
				Constants.UPDATE + keyspaceName + Constants.DOT + tableName + Constants.SET);
		Set<String> key = new HashSet<>(map.keySet());
		key.remove(Constants.IDENTIFIER);
		query.append(String.join(" = ? ,", key));
		query.append(Constants.EQUAL_WITH_QUE_MARK + Constants.WHERE_ID + Constants.EQUAL_WITH_QUE_MARK);
		ProjectLogger.log(query.toString());
		return query.toString();
	}

	/**
	 * @desc This method is used to create prepared statement based on table name
	 *       and column name provided as varargs
	 * @param keyspaceName
	 *            String (data base keyspace name)
	 * @param tableName
	 *            String
	 * @param properties(String
	 *            varargs)
	 * @return String String
	 */
	public static String getSelectStatement(String keyspaceName, String tableName, String... properties) {
		StringBuilder query = new StringBuilder(Constants.SELECT);
		query.append(String.join(",", properties));
		query.append(Constants.FROM + keyspaceName + Constants.DOT + tableName + Constants.WHERE + Constants.IDENTIFIER
				+ Constants.EQUAL + " ?; ");
		ProjectLogger.log(query.toString());
		return query.toString();

	}

	/**
	 * @desc This method is used to create prepared statement based on table name
	 *       and column name provided
	 * @param keyspaceName
	 *            String (data base keyspace name)
	 * @param tableName
	 *            String
	 * @param map
	 *            is key value pair (key is column name and value is value of
	 *            column)
	 * @return String String
	 */
	public static String getPreparedStatementFrUpsert(String keyspaceName, String tableName, Map<String, Object> map) {
		StringBuilder query = new StringBuilder();
		query.append(Constants.INSERT_INTO + keyspaceName + Constants.DOT + tableName + Constants.OPEN_BRACE);
		Set<String> keySet = map.keySet();
		query.append(String.join(",", keySet) + Constants.VALUES_WITH_BRACE);
		StringBuilder commaSepValueBuilder = new StringBuilder();
		for (int i = 0; i < keySet.size(); i++) {
			commaSepValueBuilder.append(Constants.QUE_MARK);
			if (i != keySet.size() - 1) {
				commaSepValueBuilder.append(Constants.COMMA);
			}
		}
		query.append(commaSepValueBuilder + Constants.CLOSING_BRACE);
		ProjectLogger.log(query.toString());
		return query.toString();

	}

	public static String processExceptionForUnknownIdentifier(Exception e) {
		// Unknown identifier
		return ProjectUtil.formatMessage(ResponseCode.invalidPropertyError.getErrorMessage(),
				e.getMessage().replace(JsonKey.UNKNOWN_IDENTIFIER, "").replace(JsonKey.UNDEFINED_IDENTIFIER, ""))
				.trim();
	}
}
