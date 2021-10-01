package org.sunbird.migration;

import org.apache.log4j.Logger;
import org.sunbird.migration.connection.factory.CassandraConnectionFactory;
import org.sunbird.migration.connection.factory.ConnectionFactory;
import org.sunbird.migration.constants.EnvConstants;

import java.util.List;

public class UserFeedUtil {
  static Logger logger = LoggerFactory.getLoggerInstance(UserFeedUtil.class.getName());
  private static String className = UserFeedUtil.class.getSimpleName();

  /**
   * the Code begins here...
   *
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) {
    try {
      RequestParams requestParams = prepareRequestParamsSunbird(System.getenv(EnvConstants.SUNBIRD_CASSANDRA_LEARNER_KEYSPACENAME));
      RequestParamValidator.getInstance(requestParams).validate();
      ConnectionFactory connectionFactory = new CassandraConnectionFactory();
      RecordProcessor recordProcessor =
          RecordProcessor.getInstance(connectionFactory, requestParams);
      List<Feed> feedList = recordProcessor.getFeedLists();
      recordProcessor.connection.closeConnection();
      requestParams = prepareRequestParamsSunbird(System.getenv(EnvConstants.SUNBIRD_CASSANDRA_NOTIFICATION_KEYSPACENAME));
      recordProcessor =
              RecordProcessor.getInstance(connectionFactory, requestParams);
      recordProcessor.startProcessing(feedList);

    } catch (Exception ex) {
      logger.error("Error :" + ex.getMessage());
    }
  }

  /**
   * this method will prepare RequestParams object while reading constants from env
   *
   * @return RequestParams
   */
  private static RequestParams prepareRequestParamsSunbird(String keyspace) {
    RequestParams requestParams = new RequestParams();
    requestParams.setCassandraHost(System.getenv(EnvConstants.SUNBIRD_CASSANDRA_HOST));
    requestParams.setCassandraKeyspaceName(
       keyspace);
    requestParams.setCassandraPort(System.getenv(EnvConstants.SUNBIRD_CASSANDRA_PORT));
    logger.info(
        String.format(
            "%s:%s:env variable got %s",
            className, "prepareRequestParams", requestParams.toString()));
    return requestParams;
  }
}
