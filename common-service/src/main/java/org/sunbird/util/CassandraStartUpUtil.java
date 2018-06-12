package org.sunbird.util;

import com.typesafe.config.Config;
import java.text.MessageFormat;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.helper.CassandraConnectionManager;
import org.sunbird.helper.CassandraConnectionMngrFactory;

/**
 * Utility class for cassandra startup
 *
 * @author arvind .
 */
public final class CassandraStartUpUtil {

  private static final Config config = ConfigUtil.getConfig();

  private CassandraStartUpUtil() {}

  /**
   * This method will check the cassandra data base connection. first it will try to established the
   * data base connection from provided environment variable , if environment variable values are
   * not set then connection will be established from property file.
   */
  public static void checkCassandraDbConnections(String keySpace) {

    String cassandraMode = config.getString(JsonKey.SUNBIRD_CASSANDRA_MODE);
    if (JsonKey.EMBEDDED_MODE.equalsIgnoreCase(cassandraMode)) {
      createCassandraConnectionForEmbeddedMode(keySpace);
    } else if (JsonKey.STANDALONE_MODE.equalsIgnoreCase(cassandraMode)) {
      createCassandraConnectionForStandAloneMode(keySpace);
    }
  }

  private static boolean createCassandraConnectionForEmbeddedMode(String keySpace) {
    CassandraConnectionManager cassandraConnectionManager =
        CassandraConnectionMngrFactory.getObject(JsonKey.EMBEDDED_MODE);
    boolean result = cassandraConnectionManager.createConnection(null, null, null, null, keySpace);
    if (result) {
      ProjectLogger.log(
          MessageFormat.format(
              "CassandraStartUpUtil:checkCassandraDbConnections: connection created successfully for mode : {0} for keyspace : {1}",
              JsonKey.EMBEDDED_MODE, keySpace),
          LoggerEnum.INFO.name());
    } else {
      ProjectLogger.log(
          MessageFormat.format(
              "CassandraStartUpUtil:checkCassandraDbConnections: Cconnection creation failed for mode : {0} for keyspace : {1}",
              JsonKey.EMBEDDED_MODE, keySpace),
          LoggerEnum.ERROR.name());
      throw new ProjectCommonException(
          ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
          ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
          ResponseCode.SERVER_ERROR.hashCode(),
          JsonKey.EMBEDDED_MODE);
    }
    return true;
  }

  private static boolean createCassandraConnectionForStandAloneMode(String keyspace) {
    String ips = config.getString(JsonKey.SUNBIRD_CASSANDRA_IP);
    String envPort = config.getString(JsonKey.SUNBIRD_CASSANDRA_PORT);
    CassandraConnectionManager cassandraConnectionManager =
        CassandraConnectionMngrFactory.getObject(JsonKey.STANDALONE_MODE);

    checkMandatoryConfigValue(ips);
    checkMandatoryConfigValue(envPort);
    String[] ipList = ips.split(",");
    String[] portList = envPort.split(",");
    String userName = config.getString(JsonKey.SUNBIRD_CASSANDRA_USER_NAME);
    String password = config.getString(JsonKey.SUNBIRD_CASSANDRA_PASSWORD);
    for (int i = 0; i < ipList.length; i++) {
      String ip = ipList[i];
      String port = portList[i];
      try {
        boolean result =
            cassandraConnectionManager.createConnection(ip, port, userName, password, keyspace);
        if (result) {
          ProjectLogger.log(
              MessageFormat.format(
                  "CassandraStartUpUtil:createCassandraConnection: Connection created successfully in mode: {0} for  IP: {1} and keyspace: {2}",
                  JsonKey.STANDALONE_MODE, ip, keyspace),
              LoggerEnum.INFO.name());
        } else {
          ProjectLogger.log(
              MessageFormat.format(
                  "CassandraStartUpUtil:createCassandraConnection: Connection creation failed in mode: {0} for  IP: {1} and keyspace: {2}",
                  JsonKey.STANDALONE_MODE, ip, keyspace),
              LoggerEnum.ERROR.name());
          throw new ProjectCommonException(
              ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
              ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
              ResponseCode.SERVER_ERROR.hashCode());
        }
      } catch (Exception ex) {
        ProjectLogger.log(
            "CassandraStartUpUtil:createCassandraConnection: failed with error = "
                + ex.getMessage(),
            ex);
        throw new ProjectCommonException(
            ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
            ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
            ResponseCode.SERVER_ERROR.hashCode());
      }
    }
    return true;
  }

  private static void checkMandatoryConfigValue(String configParameter) {

    if (StringUtils.isBlank(configParameter)) {
      ProjectLogger.log(
          "SchedulerManager:checkMandatoryConfigValues: missing mandatory configuration parameter : "
              + configParameter,
          LoggerEnum.ERROR.name());
      throw new ProjectCommonException(
          ResponseCode.mandatoryConfigParamsMissing.getErrorCode(),
          ResponseCode.mandatoryConfigParamsMissing.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode(),
          configParameter);
    }
  }
}
