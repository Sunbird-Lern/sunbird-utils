/**
 * 
 */
package org.sunbird.common.quartz.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
     String identifier = "NetOps-PC1502295457753";
   // 1- create a job and bind with class which is implementing Job
      // interface.
      JobDetail job = JobBuilder.newJob(ManageCourseBatchCount.class).requestRecovery(true).withIdentity("schedulerJob", identifier).build();
      
      // 2- Create a trigger object that will define frequency of run.
      Trigger trigger = TriggerBuilder.newTrigger().withIdentity("schedulertrigger", identifier)
          .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(2).repeatForever()).build();
      try {
         if (scheduler.checkExists(job.getKey())){
          scheduler.deleteJob(job.getKey());
         }
          scheduler.scheduleJob(job, trigger);
          scheduler.start();
      } catch (Exception e) {
        ProjectLogger.log(e.getMessage(), e);
      }
      
    // add another job for verifying the bulk upload part.
      // 1- create a job and bind with class which is implementing Job
      // interface.
      JobDetail uploadVerifyJob = JobBuilder.newJob(UploadLookUpScheduler.class).requestRecovery(true).withIdentity("uploadVerifyScheduler", identifier).build();
      
      // 2- Create a trigger object that will define frequency of run.
      Trigger uploadTrigger = TriggerBuilder.newTrigger().withIdentity("uploadVerifyTrigger", identifier)
          .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(3).repeatForever()).build();
      try {
         if (scheduler.checkExists(uploadVerifyJob.getKey())){
          scheduler.deleteJob(uploadVerifyJob.getKey());
         }
          scheduler.scheduleJob(uploadVerifyJob, uploadTrigger);
          scheduler.start();
      } catch (Exception e) {
        ProjectLogger.log(e.getMessage(), e);
      }
    } catch (IOException | SchedulerException e ) {
      ProjectLogger.log("Error in properties cache", e);
    }
  }
  
  public static void main(String[] args) {
    new SchedulerManager().schedule();
  }

}
