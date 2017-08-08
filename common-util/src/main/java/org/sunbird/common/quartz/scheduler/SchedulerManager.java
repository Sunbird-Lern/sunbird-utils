/**
 * 
 */
package org.sunbird.common.quartz.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.sunbird.common.models.util.ProjectLogger;

/**
 * @author Manzarul
 *
 */
public class SchedulerManager {
  private static final String file = "quartz.properties";

  public void schedule() {
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
    Properties configProp = new Properties();
    try {
      configProp.load(in);
      Scheduler schedulerManager = new StdSchedulerFactory(configProp).getScheduler();
    } catch (IOException  | SchedulerException e ) {
      ProjectLogger.log("Error in properties cache", e);
    }
    
  }

}
