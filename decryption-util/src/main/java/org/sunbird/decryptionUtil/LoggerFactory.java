package org.sunbird.decryptionUtil;

import org.apache.log4j.Logger;

public class LoggerFactory {
  public static Logger getLoggerInstance(String tag) {
    return Logger.getLogger(tag);
  }

  private LoggerFactory() {}
}
