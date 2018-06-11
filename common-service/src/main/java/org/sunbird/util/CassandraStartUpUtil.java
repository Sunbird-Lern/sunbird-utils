package org.sunbird.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.helper.CassandraConnectionManager;
import org.sunbird.helper.CassandraConnectionMngrFactory;

/**
 * Utility class for cassandra startup
 *
 * @author arvind .
 */
public final class CassandraStartUpUtil {

  private static Properties dbConfigProperties = new Properties();
  private static final String DB_CONFIG_PROPERTIES = "dbconfig.properties";

  static {
    loadDbPropertiesFile();
  }

  private CassandraStartUpUtil() {}

  /**
   * This method will check the cassandra data base connection. first it will try to established the
   * data base connection from provided environment variable , if environment variable values are
   * not set then connection will be established from property file.
   */
  public static void checkCassandraDbConnections(String keySpace) {

    PropertiesCache propertiesCache = PropertiesCache.getInstance();

    String cassandraMode = propertiesCache.getProperty(JsonKey.SUNBIRD_CASSANDRA_MODE);
    if (StringUtils.isBlank(cassandraMode)
        || cassandraMode.equalsIgnoreCase(JsonKey.EMBEDDED_MODE)) {

      // configure the Embedded mode and return true here ....
      CassandraConnectionManager cassandraConnectionManager =
          CassandraConnectionMngrFactory.getObject(cassandraMode);
      boolean result =
          cassandraConnectionManager.createConnection(null, null, null, null, keySpace);
      if (result) {
        ProjectLogger.log(
            "CassandraStartUpUtil:checkCassandraDbConnections: CONNECTION CREATED SUCCESSFULLY FOR IP:"
                + " : KEYSPACE :"
                + keySpace,
            LoggerEnum.INFO.name());
      } else {
        ProjectLogger.log(
            "CassandraStartUpUtil:checkCassandraDbConnections: CONNECTION CREATION FAILED FOR IP: "
                + " : KEYSPACE :"
                + keySpace);
      }

    } else if (cassandraMode.equalsIgnoreCase(JsonKey.STANDALONE_MODE)) {
      if (createCassandraConnection(keySpace)) {
        ProjectLogger.log(
            "CassandraStartUpUtil:checkCassandraDbConnections: db connection is created from System env variable.");
        return;
      }
      CassandraConnectionManager cassandraConnectionManager =
          CassandraConnectionMngrFactory.getObject(JsonKey.STANDALONE_MODE);
      String[] ipList = dbConfigProperties.getProperty(JsonKey.DB_IP).split(",");
      String[] portList = dbConfigProperties.getProperty(JsonKey.DB_PORT).split(",");

      String userName = dbConfigProperties.getProperty(JsonKey.DB_USERNAME);
      String password = dbConfigProperties.getProperty(JsonKey.DB_PASSWORD);
      for (int i = 0; i < ipList.length; i++) {
        String ip = ipList[i];
        String port = portList[i];

        try {

          boolean result =
              cassandraConnectionManager.createConnection(ip, port, userName, password, keySpace);
          if (result) {
            ProjectLogger.log(
                "CassandraStartUpUtil:checkCassandraDbConnections: CONNECTION CREATED SUCCESSFULLY FOR IP: "
                    + ip
                    + " : KEYSPACE :"
                    + keySpace,
                LoggerEnum.INFO.name());
          } else {
            ProjectLogger.log(
                "CassandraStartUpUtil:checkCassandraDbConnections: CONNECTION CREATION FAILED FOR IP: "
                    + ip
                    + " : KEYSPACE :"
                    + keySpace);
          }

        } catch (ProjectCommonException ex) {
          ProjectLogger.log(ex.getMessage(), ex);
        }
      }
    }
  }

  /**
   * Method to read the configuration from System variable.
   *
   * @return boolean
   */
  private static boolean createCassandraConnection(String keyspace) {
    boolean response = false;
    String ips = System.getenv(JsonKey.SUNBIRD_CASSANDRA_IP);
    String envPort = System.getenv(JsonKey.SUNBIRD_CASSANDRA_PORT);
    CassandraConnectionManager cassandraConnectionManager =
        CassandraConnectionMngrFactory.getObject(JsonKey.STANDALONE_MODE);

    if (StringUtils.isBlank(ips) || StringUtils.isBlank(envPort)) {
      ProjectLogger.log(
          "CassandraStartUpUtil:createCassandraConnection: Configuration value is not coming form System variable.");
      return response;
    }
    String[] ipList = ips.split(",");
    String[] portList = envPort.split(",");
    String userName = System.getenv(JsonKey.SUNBIRD_CASSANDRA_USER_NAME);
    String password = System.getenv(JsonKey.SUNBIRD_CASSANDRA_PASSWORD);
    for (int i = 0; i < ipList.length; i++) {
      String ip = ipList[i];
      String port = portList[i];
      try {
        boolean result =
            cassandraConnectionManager.createConnection(ip, port, userName, password, keyspace);
        if (result) {
          ProjectLogger.log(
              "CassandraStartUpUtil:createCassandraConnection: CONNECTION CREATED SUCCESSFULLY FOR IP: "
                  + ip
                  + " : KEYSPACE :"
                  + keyspace,
              LoggerEnum.INFO.name());
        } else {
          ProjectLogger.log(
              "CassandraStartUpUtil:createCassandraConnection: CONNECTION CREATION FAILED FOR IP: "
                  + ip
                  + " : KEYSPACE :"
                  + keyspace,
              LoggerEnum.INFO.name());
        }
        response = true;
      } catch (ProjectCommonException ex) {
        ProjectLogger.log(ex.getMessage(), ex);
        throw new ProjectCommonException(
            ResponseCode.invaidConfiguration.getErrorCode(),
            ResponseCode.invaidConfiguration.getErrorCode(),
            ResponseCode.SERVER_ERROR.hashCode());
      }
    }
    return response;
  }

  /** Method to load the db config properties file. */
  private static void loadDbPropertiesFile() {
    InputStream input = null;
    Properties prop = new Properties();
    try {
      input = CassandraStartUpUtil.class.getClassLoader().getResourceAsStream(DB_CONFIG_PROPERTIES);
      // load properties file
      prop.load(input);
    } catch (IOException ex) {
      ProjectLogger.log(ex.getMessage(), ex);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          ProjectLogger.log(e.getMessage(), e);
        }
      }
    }
  }
}
