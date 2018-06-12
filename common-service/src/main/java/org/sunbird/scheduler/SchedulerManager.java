package org.sunbird.scheduler;

import com.typesafe.config.Config;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.util.ConfigUtil;

/**
 * Abstract class for managing Quartz scheduler jobs
 *
 * @author Manzarul
 */
public abstract class SchedulerManager {

  private static final String QUARTZ_CONFIG_FILE = "quartz.properties";
  public Scheduler scheduler = null;
  public static SchedulerManager schedulerManager = null;
  private static final Config config = ConfigUtil.getConfig();

  public SchedulerManager() {
    initScheduler();
  }

  /**
   * Get singleton instance of scheduler manager
   *
   * @return Singleton instance of scheduler manager
   */
  public abstract SchedulerManager getInstance();

  /** Method to schedule jobs by registering with scheduler */
  public abstract void schedule();

  private void initScheduler() {
    try {
      boolean isEmbedded = false;
      Properties configProp = null;
      String embeddedVal = config.getString(JsonKey.SUNBIRD_QUARTZ_MODE);
      if (!(JsonKey.EMBEDDED.equalsIgnoreCase(embeddedVal))) {
        configProp = setUpClusterMode();
      }
      if (configProp != null) {
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
    ProjectLogger.log("SchedulerManager:initScheduler: Started scheduler jobs.");
  }

  /**
   * Creates a properties object containing PostgreSQL configuration (host, port, db, user,
   * password)
   *
   * @return PostgreSQL properties
   * @throws IOException Exception in reading PostgreSQL configuration
   */
  private Properties setUpClusterMode() throws IOException {
    Properties configProp = new Properties();
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(QUARTZ_CONFIG_FILE);
    String host = config.getString(JsonKey.SUNBIRD_PG_HOST);
    String port = config.getString(JsonKey.SUNBIRD_PG_PORT);
    String db = config.getString(JsonKey.SUNBIRD_PG_DB);
    String username = config.getString(JsonKey.SUNBIRD_PG_USER);
    String password = config.getString(JsonKey.SUNBIRD_PG_PASSWORD);
    ProjectLogger.log(
        MessageFormat.format(
            "SchedulerManager:setUpClusterMode: PostgreSQL config = (host: {0}, port: {1}, db: {2})",
            host, port, db),
        LoggerEnum.INFO.name());
    ConfigUtil.validateMandatoryConfigValue(host);
    ConfigUtil.validateMandatoryConfigValue(port);
    ConfigUtil.validateMandatoryConfigValue(db);
    ConfigUtil.validateMandatoryConfigValue(username);
    ConfigUtil.validateMandatoryConfigValue(password);
    ProjectLogger.log(
        "SchedulerManager:setUpClusterMode: Reading PostgreSQL configuration from environment variables.",
        LoggerEnum.INFO.name());
    configProp.load(in);
    configProp.put(
        "org.quartz.dataSource.MySqlDS.URL", "jdbc:postgresql://" + host + ":" + port + "/" + db);
    configProp.put("org.quartz.dataSource.MySqlDS.user", username);
    configProp.put("org.quartz.dataSource.MySqlDS.password", password);
    return configProp;
  }

  /**
   * Clean up thread to gracefully shutdown quartz scheduler
   *
   * @author Manzarul
   */
  class ResourceCleanUp extends Thread {

    @Override
    public void run() {
      ProjectLogger.log(
          "SchedulerManager:ResourceCleanUp: Started resource cleanup for quartz scheduler.");
      try {
        if (null != scheduler) {
          scheduler.shutdown();
        }
      } catch (SchedulerException e) {
        ProjectLogger.log(
            "SchedulerManager:ResourceCleanUp: Generic exception while shutting down quartz scheduler = "
                + e.getMessage(),
            e);
      }
      ProjectLogger.log(
          "SchedulerManager:ResourceCleanUp: Completed resource cleanup for quartz scheduler");
    }
  }

  /** Register a shutdown hook to gracefully shutdown quartz scheduler */
  public void registerShutDownHook() {
    Runtime runtime = Runtime.getRuntime();
    runtime.addShutdownHook(new ResourceCleanUp());
    ProjectLogger.log(
        "SchedulerManager:registerShutDownHook: ShutDownHook registered for Quartz scheduler.");
  }
}
