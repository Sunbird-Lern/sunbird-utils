package org.sunbird.util;

import com.typesafe.config.Config;
import java.text.MessageFormat;
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
   * Creates Cassandra DB connection(s) based on configured mode (embedded or standalone)
   *
   * @param keySpace Keyspace (sunbird, sunbirdplugin) of DB for which connection needs to be
   *     established
   */
  public static void createCassandraConnection(String keySpace) {

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
              "CassandraStartUpUtil:checkCassandraDbConnections: Connection created successfully for mode : {0} for keyspace : {1}",
              JsonKey.EMBEDDED_MODE, keySpace),
          LoggerEnum.INFO.name());
    } else {
      ProjectLogger.log(
          MessageFormat.format(
              "CassandraStartUpUtil:checkCassandraDbConnections: Connection creation failed for mode : {0} for keyspace : {1}",
              JsonKey.EMBEDDED_MODE, keySpace),
          LoggerEnum.ERROR.name());
      throw new ProjectCommonException(
          ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
          ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
          ResponseCode.SERVER_ERROR.getResponseCode(),
          JsonKey.EMBEDDED_MODE);
    }
    return true;
  }

  private static boolean createCassandraConnectionForStandAloneMode(String keyspace) {
    String ips = config.getString(JsonKey.SUNBIRD_CASSANDRA_IP);
    String envPort = config.getString(JsonKey.SUNBIRD_CASSANDRA_PORT);
    String userName = config.getString(JsonKey.SUNBIRD_CASSANDRA_USER_NAME);
    String password = config.getString(JsonKey.SUNBIRD_CASSANDRA_PASSWORD);

    ConfigUtil.validateMandatoryConfigValue(ips);
    ConfigUtil.validateMandatoryConfigValue(envPort);

    CassandraConnectionManager cassandraConnectionManager =
        CassandraConnectionMngrFactory.getObject(JsonKey.STANDALONE_MODE);

    String[] ipList = ips.split(",");
    String[] portList = envPort.split(",");

    for (int i = 0; i < ipList.length; i++) {
      String ip = ipList[i];
      String port = portList[i];
      try {
        boolean result =
            cassandraConnectionManager.createConnection(ip, port, userName, password, keyspace);
        if (result) {
          ProjectLogger.log(
              MessageFormat.format(
                  "CassandraStartUpUtil:createCassandraConnectionForStandAloneMode: Connection created successfully in mode: {0} for IP: {1} and keyspace: {2}",
                  JsonKey.STANDALONE_MODE, ip, keyspace),
              LoggerEnum.INFO.name());
        } else {
          ProjectLogger.log(
              MessageFormat.format(
                  "CassandraStartUpUtil:createCassandraConnectionForStandAloneMode: Connection creation failed in mode: {0} for IP: {1} and keyspace: {2}",
                  JsonKey.STANDALONE_MODE, ip, keyspace),
              LoggerEnum.ERROR.name());
          throw new ProjectCommonException(
              ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
              ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
              ResponseCode.SERVER_ERROR.getResponseCode());
        }
      } catch (Exception ex) {
        ProjectLogger.log(
            "CassandraStartUpUtil:createCassandraConnectionForStandAloneMode: Generic exception occurred with error = "
                + ex.getMessage(),
            ex);
        throw new ProjectCommonException(
            ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
            ResponseCode.cassandraConnectionEstablishmentFailed.getErrorCode(),
            ResponseCode.SERVER_ERROR.getResponseCode());
      }
    }
    return true;
  }
}
