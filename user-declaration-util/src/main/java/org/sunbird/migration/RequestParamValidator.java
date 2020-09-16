package org.sunbird.migration;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.sunbird.migration.constants.EnvConstants;

public class RequestParamValidator {

  private static Logger logger =
      LoggerFactory.getLoggerInstance(UserDeclarationMigrations.class.getName());
  private RequestParams requestParams;

  private RequestParamValidator(RequestParams requestParams) {
    this.requestParams = requestParams;
  }

  /**
   * this method should used to get the instance of the class.
   *
   * @param requestParams
   * @return
   */
  public static RequestParamValidator getInstance(RequestParams requestParams) {
    return new RequestParamValidator(requestParams);
  }

  /** this method is used to validate the request Params (env) values... */
  public void validate() {
    isEnvValidated(requestParams);
    logger.info(
        String.format("%s:%s:env variables verified", this.getClass().getSimpleName(), "validate"));
  }

  /**
   * this method will only check weather no env should be null or empty
   *
   * @param params
   * @return boolean
   */
  private static boolean isInvalidEnv(String params) {
    return TextUtils.isEmpty(params) ? true : false;
  }

  /**
   * this method will validate the env variable. all the variables are mandatory...
   *
   * @param requestParams
   * @return
   */
  private static boolean isEnvValidated(RequestParams requestParams) {

    if (isInvalidEnv(requestParams.getCassandraHost())) {
      throw new IllegalArgumentException(
          String.format("Valid %s is required", EnvConstants.SUNBIRD_CASSANDRA_HOST));
    }
    if (isInvalidEnv(requestParams.getCassandraKeyspaceName())) {
      throw new IllegalArgumentException(
          String.format("Valid %s is required", EnvConstants.SUNBIRD_CASSANDRA_KEYSPACENAME));
    }
    if (isInvalidEnv(requestParams.getCassandraPort())) {
      throw new IllegalArgumentException(
          String.format("Valid %s is required", EnvConstants.SUNBIRD_CASSANDRA_PORT));
    }

    return true;
  }
}
