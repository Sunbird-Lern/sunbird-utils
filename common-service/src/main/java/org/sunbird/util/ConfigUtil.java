package org.sunbird.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * This util class for providing type safe config to any service that requires it.
 *
 * @author Manzarul
 */
public class ConfigUtil {

  private static Config config;
  private static final String DEFAULT_TYPE_SAFE_CONFIG_FILE_NAME = "service.conf";
  private static final String INVALID_FILE_NAME = "Please provide a valid file name.";

  /** Private default constructor. */
  private ConfigUtil() {}

  /**
   * This method will create a type safe config object and return to caller. It will read the config
   * value from System env first and as a fall back it will use service.conf file.
   *
   * @return Type safe config object
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
   * This method will create a type safe config object and return to caller. It will read the config
   * value from System env first and as a fall back it will use provided file name. If file name is
   * null or empty then it will throw ProjectCommonException with status code as 500.
   *
   * @return Type safe config object
   */
  public static Config getConfig(String fileName) {
    if (StringUtils.isBlank(fileName)) {
      ProjectLogger.log(
          "ConfigUtil:getConfigWithFilename: Given file name is null or empty: " + fileName,
          LoggerEnum.INFO.name());
      throw new ProjectCommonException(
          ResponseCode.internalError.getErrorCode(),
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

  private static Config createConfig(String fileName) {
    Config defaultConf = ConfigFactory.load(fileName);
    Config envConf = ConfigFactory.systemEnvironment();
    return envConf.withFallback(defaultConf);
  }
}
