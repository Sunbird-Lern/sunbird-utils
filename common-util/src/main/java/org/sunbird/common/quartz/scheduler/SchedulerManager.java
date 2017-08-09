/**
 * 
 */
package org.sunbird.common.quartz.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.sunbird.common.models.util.ProjectLogger;


/**
 * @author Manzarul
 *
 */
public class SchedulerManager {
  private static final String file = "quartz.properties";

  public  void schedule() {
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
    Properties configProp = new Properties();
    try {
      configProp.load(in);
      Scheduler scheduler = new StdSchedulerFactory(configProp).getScheduler();
     String identifier = scheduler.getSchedulerInstanceId();
   // 1- create a job and bind with class which is implementing Job
      // interface.
      JobDetail job = JobBuilder.newJob(SimpleJob.class).withIdentity("testJob", identifier).build();
      
      // 2- Create a trigger object that will define frequency of run.
      Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger", identifier)
          .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever()).build();
      //CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *")
      try {
          scheduler.scheduleJob(job, trigger);
          scheduler.start();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (IOException | SchedulerException e ) {
      e.printStackTrace();
      ProjectLogger.log("Error in properties cache", e);
    }
  }
  
  public static void main(String[] args) {
    new SchedulerManager().schedule();
  }

}
