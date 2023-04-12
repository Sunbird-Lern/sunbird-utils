package org.sunbird.migration;

import org.apache.log4j.Logger;
import org.sunbird.migration.connection.factory.CassandraConnectionFactory;
import org.sunbird.migration.connection.factory.ConnectionFactory;
import org.sunbird.migration.constants.EnvConstants;

public class ExternalIdMigrations {
  static Logger logger = LoggerFactory.getLoggerInstance(ExternalIdMigrations.class.getName());
  private static String className = ExternalIdMigrations.class.getSimpleName();

  /**
   * the Code begins here...
   *
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) {
    try {
      RequestParams requestParams = prepareRequestParams();
      RequestParamValidator.getInstance(requestParams).validate();
      ConnectionFactory connectionFactory = new CassandraConnectionFactory();
      RecordProcessor recordProcessor =
          RecordProcessor.getInstance(connectionFactory, requestParams);
      recordProcessor.startProcessingExternalIds();
    } catch (Exception ex) {
      logger.error("Error :" + ex.getMessage());
    }
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
    requestParams.setCassandraPort(System.getenv(EnvConstants.SUNBIRD_CASSANDRA_PORT));
    logger.info(
        String.format(
            "%s:%s:env variable got %s",
            className, "prepareRequestParams", requestParams.toString()));
    return requestParams;
  }
}
