package org.sunbird.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * This util class will handle type safe config.
 *
 * @author Manzarul
 */
public class ConfigUtil {

  private static Config config;
  private static final String DEFAULT_TYPE_SAFE_CONFIG_FILE_NAME = "service.conf";
  private static final String INVALID_FILE_NAME = "Please provide a valid file name.";

  /** private default constructor. */
  private ConfigUtil() {}

  /**
   * This method will create a type safe config object and return to caller. it will read the config
   * value from System env first and as a fall back it will used service.conf file.
   *
   * @return com.typesafe.config.Config
   */
  public static Config getConfig() {
    if (config == null) {
      synchronized (ConfigUtil.class) {
        config = createConfig(DEFAULT_TYPE_SAFE_CONFIG_FILE_NAME);
      }
    }
    return config;
  }

  /**
   * This method will create a type safe config object and return to caller. it will read the config
   * value from System env first and as a fall back it will used provided by name. if file name is
   * null or empty then it will throw ProjectCommonException with status code as 400.
   *
   * @return com.typesafe.config.Config
   */
  public static Config getConfig(String fileName) {
    if (StringUtils.isBlank(fileName)) {
      ProjectLogger.log(
          "ConfigUtil:getConfigWithFilename: file name is coming as null or empty " + fileName,
          LoggerEnum.INFO.name());
      throw new ProjectCommonException(
          ResponseCode.invalidData.getErrorCode(),
          INVALID_FILE_NAME,
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    if (config == null) {
      synchronized (ConfigUtil.class) {
        config = createConfig(fileName);
      }
    }
    return config;
  }

  /**
   * @param fileName
   * @return
   */
  private static Config createConfig(String fileName) {
    Config defaultConf = ConfigFactory.load(fileName);
    Config envConf = ConfigFactory.systemEnvironment();
    return envConf.withFallback(defaultConf);
  }
}
