/**
 * 
 */
package org.sunbird.common.quartz.scheduler;

import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * This class will update course batch count in EKStep.
 * @author Manzarul
 *
 */
public class ManageCourseBatchCount implements Job {
  public void execute(JobExecutionContext ctx) throws JobExecutionException {
    System.out.println("Executing at: " + Calendar.getInstance().getTime() + " triggered by: " + ctx.getJobDetail().toString());
    //Collect all those batches from ES whose start date is today and countIncrementStatus value is false.
    //or all those batches whose end date was yesterday and countDecrementStatus value is false.
    //update the countIncrement or decrement status value as true , countIncrement or decrement date as today date.
    //make the status based on course start or end - in case of start make it 1 , for end make it 2. 
    // now update the data into cassandra plus ES both and EKStep content with count increment and decrement value.
    
    
    
    
  }
}
