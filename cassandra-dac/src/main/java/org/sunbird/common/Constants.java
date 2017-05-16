package org.sunbird.common;

/*
 * @author Amit Kumar
 */
public interface Constants {
	//CASSANDRA CONFIG PROPERTIES
	public static final String CORE_CONNECTIONS_PER_HOST_FOR_LOCAL="coreConnectionsPerHostForLocal";
	public static final String CORE_CONNECTIONS_PER_HOST_FOR_REMOTE="coreConnectionsPerHostForRemote";
	public static final String MAX_CONNECTIONS_PER_HOST_FOR_LOCAl="maxConnectionsPerHostForLocal";
	public static final String MAX_CONNECTIONS_PER_HOST_FOR_REMOTE="maxConnectionsPerHostForRemote";
	public static final String MAX_REQUEST_PER_CONNECTION="maxRequestsPerConnection";
	public static final String HEARTBEAT_INTERVAL="heartbeatIntervalSeconds";
	public static final String POOL_TIMEOUT="poolTimeoutMillis";
	public static final String CONTACT_POINT="contactPoint";
	public static final String PORT="port";
	public static final String CASSANDRA_USERNAME="userName";
	public static final String CASSANDRA_PASSWORD="password";
	public static final String QUERY_LOGGER_THRESHOLD="queryLoggerConstantThreshold";
	public static final String CASSANDRA_PROPERTIES_FILE="cassandra.config.properties";
	
	
	//CONSTANT
	public static final String COURSE_ID="courseId";
	public static final String USER_ID="userId";
	public static final String CONTENT_ID="contentId";
	public static final String IDENTIFIER="id";
	public static final String CONST_VARCHAR="(varchar)";
	public static final String SUCCESS="SUCCESS";
	public static final String RESPONSE="response";
	public static final String SESSION_IS_NULL="cassandra session is null for this ";
	
	public enum LearnerStateOperation {
		NOT_STARTED("notStarted"),STARTED("started"),COMPLETED("completed");

		private String value;

		LearnerStateOperation(String value){
			this.value=value;
		}

		public String getValue(){
			return this.value;
		}
	}

}
