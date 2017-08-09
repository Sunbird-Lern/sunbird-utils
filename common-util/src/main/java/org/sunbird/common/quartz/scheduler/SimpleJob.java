package org.sunbird.common.quartz.scheduler;

import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SimpleJob implements Job {
  public void execute(JobExecutionContext ctx) throws JobExecutionException {
    System.out.println("Executing at: " + Calendar.getInstance().getTime() + " triggered by: " + ctx.getTrigger().getDescription());
  }
}
