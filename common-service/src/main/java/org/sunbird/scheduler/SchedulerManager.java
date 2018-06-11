/** */
package org.sunbird.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

/**
 * This class will manage all the Quartz scheduler. We need to call the schedule method at one time.
 *
 * @author Manzarul
 */
public abstract class SchedulerManager {

  private static final String FILE = "quartz.properties";
  public Scheduler scheduler = null;
  public static SchedulerManager schedulerManager = null;

  public SchedulerManager() {
    initScheduler();
  }

  private void initScheduler() {
    try {
      Thread.sleep(240000);
      boolean isEmbedded = false;
      Properties configProp = null;
      String embeddVal = System.getenv(JsonKey.SUNBIRD_QUARTZ_MODE);
      if (JsonKey.EMBEDDED.equalsIgnoreCase(embeddVal)) {
        isEmbedded = true;
      } else {
        configProp = setUpClusterMode();
      }
      if (!isEmbedded && configProp != null) {
        ProjectLogger.log(
            "SchedulerManager:initScheduler: Quartz scheduler is running in cluster mode.");
        scheduler = new StdSchedulerFactory(configProp).getScheduler();
      } else {
        ProjectLogger.log(
            "SchedulerManager:initScheduler: Quartz scheduler is running in embedded mode.");
        scheduler = new StdSchedulerFactory().getScheduler();
      }
    } catch (Exception e) {
      ProjectLogger.log("SchedulerManager:initScheduler: Error in starting scheduler jobs.", e);
    } finally {
      registerShutDownHook();
    }
    ProjectLogger.log("SchedulerManager:initScheduler: started scheduler jobs.");
  }

  /** This method will register the quartz scheduler job. */
  public abstract void schedule();

  /**
   * Method to read the system properties and load to the Properties file that will be used to setup
   * the quartz scheduler in distributed mode
   *
   * @return Properties file contains key(attributes) and values related to Postgres database
   * @throws IOException
   */
  private Properties setUpClusterMode() throws IOException {
    Properties configProp = new Properties();
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(FILE);
    String host = System.getenv(JsonKey.SUNBIRD_PG_HOST);
    String port = System.getenv(JsonKey.SUNBIRD_PG_PORT);
    String db = System.getenv(JsonKey.SUNBIRD_PG_DB);
    String username = System.getenv(JsonKey.SUNBIRD_PG_USER);
    String password = System.getenv(JsonKey.SUNBIRD_PG_PASSWORD);
    ProjectLogger.log(
        "SchedulerManager:setUpClusterMode: Environment variable value for PostGress SQl= host, port,db,username,password "
            + host
            + " ,"
            + port
            + ","
            + db
            + ","
            + username
            + ","
            + password,
        LoggerEnum.INFO.name());
    if (!StringUtils.isBlank(host)
        && !StringUtils.isBlank(port)
        && !StringUtils.isBlank(db)
        && !StringUtils.isBlank(username)
        && !StringUtils.isBlank(password)) {
      ProjectLogger.log(
          "SchedulerManager:setUpClusterMode: Taking Postgres value from Environment variable...",
          LoggerEnum.INFO.name());
      configProp.load(in);
      configProp.put(
          "org.quartz.dataSource.MySqlDS.URL", "jdbc:postgresql://" + host + ":" + port + "/" + db);
      configProp.put("org.quartz.dataSource.MySqlDS.user", username);
      configProp.put("org.quartz.dataSource.MySqlDS.password", password);
    } else {
      ProjectLogger.log(
          "SchedulerManager:setUpClusterMode: Environment variable is not set for postgres SQl.",
          LoggerEnum.INFO.name());
      configProp = null;
    }
    return configProp;
  }

  /**
   * Method to get the instance of the scheduler manager
   *
   * @return SchedulerManager instance
   */
  public abstract SchedulerManager getInstance();

  /**
   * This class will be called by registerShutDownHook to register the call inside jvm , when jvm
   * terminate it will call the run method to clean up the resource.
   *
   * @author Manzarul
   */
  class ResourceCleanUp extends Thread {
    @Override
    public void run() {
      ProjectLogger.log(
          "SchedulerManager:ResourceCleanUp: started resource cleanup for Quartz job.");
      try {
        scheduler.shutdown();
      } catch (SchedulerException e) {
        ProjectLogger.log(e.getMessage(), e);
      }
      ProjectLogger.log("SchedulerManager:ResourceCleanUp: completed resource cleanup Quartz job.");
    }
  }

  /** Register the hook for resource clean up. this will be called when jvm shut down. */
  public void registerShutDownHook() {
    Runtime runtime = Runtime.getRuntime();
    runtime.addShutdownHook(new ResourceCleanUp());
    ProjectLogger.log(
        "SchedulerManager:registerShutDownHook: ShutDownHook registered for Quartz scheduler.");
  }
}
