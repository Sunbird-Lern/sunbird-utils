package org.sunbird.statevalidateutil;

import org.apache.log4j.Logger;

public class LoggerFactory {
  public static Logger getLoggerInstance(String tag) {
    return Logger.getLogger(tag);
  }

  private LoggerFactory() {}
}
