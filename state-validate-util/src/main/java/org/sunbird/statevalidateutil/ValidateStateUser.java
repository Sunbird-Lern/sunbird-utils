package org.sunbird.statevalidateutil;

import org.apache.log4j.Logger;
import org.sunbird.statevalidateutil.connection.factory.CassandraConnectionFactory;
import org.sunbird.statevalidateutil.connection.factory.ConnectionFactory;
import org.sunbird.statevalidateutil.constants.EnvConstants;

import java.io.IOException;

public class ValidateStateUser {

  static Logger logger = LoggerFactory.getLoggerInstance(ValidateStateUser.class.getName());
  private static String className = ValidateStateUser.class.getSimpleName();

  public static void main(String[] args) throws IOException {
    RequestParams requestParams = prepareRequestParams();
    RequestParamValidator.getInstance(requestParams).validate();
    ConnectionFactory connectionFactory = new CassandraConnectionFactory();
    RecordProcessor recordProcessor = RecordProcessor.getInstance(connectionFactory, requestParams);
    recordProcessor.startProcessingStateValidationAndFlags();
  }

  /**
   * this method will prepare RequestParams object while reading constants from env
   *
   * @return RequestParams
   */
  private static RequestParams prepareRequestParams() {
    RequestParams requestParams = new RequestParams();
    requestParams.setCassandraHost(System.getenv(EnvConstants.SUNBIRD_CASSANDRA_HOST));
    requestParams.setCassandraKeyspaceName(
            System.getenv(EnvConstants.SUNBIRD_CASSANDRA_KEYSPACENAME));
    requestParams.setCassandraKeyspaceName(
            "sunbird");
    requestParams.setCassandraPort(System.getenv(EnvConstants.SUNBIRD_CASSANDRA_PORT));
    logger.info(
            String.format(
                    "%s:%s:env variable got %s",
                    className, "prepareRequestParams", requestParams.toString()));
    return requestParams;
  }
}