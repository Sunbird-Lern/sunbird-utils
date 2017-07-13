package org.sunbird.common.models.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ProjectLogger {

  private static String eVersion = "1.0";
  private static String pVersion = "1.0";
  private static String dataId = "Sunbird";
  private static ObjectMapper mapper = new ObjectMapper();
  private static Logger rootLogger = (Logger) LogManager.getLogger("defaultLogger");

  /**
   * To log only message.
   */
  public static void log(String message) {
    log(message, null, LoggerEnum.DEBUG.name());
  }

  public static void log(String message, Throwable e) {
    log(message, null, e);
  }

  public static void log(String message, String logLevel) {
    log(message, null, logLevel);
  }
  
  /**
   * To log message, data in used defined log level.
   */
  public static void log(String message, Object data, String logLevel) {
    backendLog(message, data, null, logLevel);
  }

  /**
   * To log exception with message and data.
   */
  public static void log(String message, Object data, Throwable e) {
    backendLog(message, data, e, LoggerEnum.ERROR.name());
  }

  /**
   * To log exception with message and data for user specific log level.
   */
  public static void log(String message, Object data, Throwable e, String logLevel) {
    backendLog(message, data, e, logLevel);
  }

  private static void info(String message, Object data) {
    rootLogger.info(getBELogEvent(LoggerEnum.INFO.name(), message, data));
  }

  private static void debug(String message, Object data) {
    rootLogger.debug(getBELogEvent(LoggerEnum.DEBUG.name(), message, data));
  }

  private static void error(String message, Object data, Throwable exception) {
    rootLogger.error(getBELogEvent(LoggerEnum.ERROR.name(), message, data, exception));
  }

  private static void warn(String message, Object data, Throwable exception) {
    rootLogger.warn(getBELogEvent(LoggerEnum.WARN.name(), message, data, exception));
  }

  private static void backendLog(String message, Object data, Throwable e, String logLevel) {
    if (!ProjectUtil.isStringNullOREmpty(logLevel)) {
      switch (logLevel) {
        case "INFO":
          info(message, data);
          break;
        case "DEBUG":
          debug(message, data);
          break;
        case "WARN":
          warn(message, data, e);
          break;
        case "ERROR":
          error(message, data, e);
          break;
      }
    }
  }

  private static String getBELogEvent(String logLevel, String message, Object data) {
    String logData = getBELog(logLevel, message, data, null);
    return logData;
  }

  private static String getBELogEvent(String logLevel, String message, Object data, Throwable e) {
    String logData = getBELog(logLevel, message, data, e);
    return logData;
  }

  private static String getBELog(String logLevel, String message, Object data,
      Throwable exception) {
    String mid = dataId + "." + System.currentTimeMillis() + "." + UUID.randomUUID();
    long unixTime = System.currentTimeMillis();
    LogEvent te = new LogEvent();
    Map<String, Object> eks = new HashMap<String, Object>();
    eks.put("level", logLevel);
    eks.put("message", message);
    if (data != null) {
      eks.put("data", data);
    }
    if (exception != null) {
      eks.put("stacktrace", ExceptionUtils.getStackTrace(exception));
    }
    te.setEid(LoggerEnum.BE_LOG.name());
    te.setEts(unixTime);
    te.setMid(mid);
    te.setVer(eVersion);
    te.setContext(dataId, pVersion);
    String jsonMessage = null;
    try {
      te.setEdata(eks);
      jsonMessage = mapper.writeValueAsString(te);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return jsonMessage;
  }

}
