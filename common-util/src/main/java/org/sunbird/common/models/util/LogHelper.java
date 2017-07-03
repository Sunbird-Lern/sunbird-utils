package org.sunbird.common.models.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.sunbird.common.request.ExecutionContext;

/**
 * This class will use internally log4j logger.
 * here we can add some extra data while logging the request.
 * @author Manzarul
 *
 */
public class LogHelper {
     
    private Logger LOGGER = null;
    
    /**
     * private 1-arg constructor.
     * @param logger
     */
    private LogHelper(Logger logger) {
        this.LOGGER = logger;
    }
    
    /**
     * This method will provide  LogHelper class instance.
     * @param classname String
     * @return LogHelper
     */
    public static LogHelper getInstance(String classname) {
        Logger logger = LogManager.getLogger(classname);
        LogHelper helper = new LogHelper(logger);
        return helper;
    }
   
    /**
     * 
     * @param msg String
     */
    public void info(String msg) {
        LOGGER.info(ExecutionContext.getRequestId() + " | " +  msg);
    }
    
    /**
     * 
     * @param msg String
     */
    public void debug(String msg) {
        LOGGER.debug(ExecutionContext.getRequestId() + " | " +  msg);
    }
    
    /**
     * add custom message and exception object.
     * @param msg String
     * @param e Throwable
     */
    public void error(String msg, Throwable e) {
        LOGGER.error(ExecutionContext.getRequestId() + " | " +  msg, e);
    }
    
    /**
     * send the complete exception object for logging.
     * @param e Throwable
     */
    public void error(Throwable e) {
        LOGGER.error(ExecutionContext.getRequestId() + " | " +  e.getMessage(), e);
    }
}
