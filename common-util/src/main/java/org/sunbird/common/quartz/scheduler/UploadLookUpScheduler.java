/**
 * 
 */
package org.sunbird.common.quartz.scheduler;

import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * This class will lookup into bulk process table.
 * if process type is new or in progress (more than x hours) then
 * take the process id and do the re-process of job.
 * @author Manzarul
 *
 */
public class UploadLookUpScheduler implements Job {
  public void execute(JobExecutionContext ctx) throws JobExecutionException {
    System.out.println("Running Upload Scheduler Job at: " + Calendar.getInstance().getTime() + " triggered by: " + ctx.getJobDetail().toString());
  }
}
