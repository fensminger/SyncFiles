package org.fer.syncfiles.service;

import org.apache.log4j.Logger;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.quartz.SyncFilesJob;
import org.fer.syncfiles.quartz.SyncToolTipJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerMgr {
	private static final Logger log = Logger.getLogger(SchedulerMgr.class);
	
	private Scheduler scheduler = null;
	private final String GROUP_NAME = "SyncFilesGroup";
	private final String JOB_PARAM = "param";

	public void start() {
		try {
			// Grab the Scheduler instance from the Factory
			scheduler = StdSchedulerFactory.getDefaultScheduler();

			scheduler.start();

		} catch (SchedulerException se) {
			se.printStackTrace();
		}

	}
	
	public void stop() {
		if (scheduler!=null) {
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void insertOrUpdateJob(Param p) throws SchedulerException {
		Param param = new Param(p);
		
		deleteJobIfExist(param);
		
		JobDetail job;
		try {
			job = scheduler.getJobDetail(JobKey.jobKey(param.getJobName(), GROUP_NAME));
			log.info("On a retrouvé ce job dans le scheduler.");
		} catch (Exception e) {
			log.info("Ce job n'a pas encore été schedulé.");
			job = null;
		}
		
		if (job==null) {
			JobDataMap map = new JobDataMap();
			map.put(JOB_PARAM, param);
			
			job = JobBuilder.newJob(SyncFilesJob.class)
					.withIdentity(param.getJobName(), GROUP_NAME)
					.usingJobData(map)
					.build();
		} else {
			job.getJobDataMap().put(JOB_PARAM, param);
		}
		
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(param), GROUP_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(param.getCronExp()))
				.forJob(job)
				.build();
		
		scheduler.scheduleJob(job, trigger);
	}
	
	public void deleteJobIfExist(Param param) throws SchedulerException {
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(getTriggerName(param), GROUP_NAME);
			if (scheduler.checkExists(triggerKey)) {
				scheduler.unscheduleJob(triggerKey);
				log.info("Job unscheduled");
			}
		} catch (SchedulerException e) {
			log.error("Unable to unschedule job");
			throw e;
		}
		//scheduler.deleteJob(JobKey.jobKey(param.getJobName(), GROUP_NAME));
	}
	
	private String getTriggerName(Param param) {
		return "trigger_"+param.getJobName();
	}
	
	public void deleteAll() throws SchedulerException {
		scheduler.clear();
		
		final String JOB_NAME = "JOB_MISFIRED_SYNC";
		final String TRIGGER_NAME = "trigger_"+JOB_NAME;
		final String CRON_EXP = "0 0 0/6 * * ?";
		JobDetail job;
		try {
			job = scheduler.getJobDetail(JobKey.jobKey(JOB_NAME, GROUP_NAME));
			log.info("On a retrouvé ce job dans le scheduler.");
		} catch (Exception e) {
			log.info("Ce job n'a pas encore été schedulé.");
			job = null;
		}
		
		if (job==null) {
			job = JobBuilder.newJob(SyncToolTipJob.class)
					.withIdentity(JOB_NAME, GROUP_NAME)
					.build();
		}
		
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(TRIGGER_NAME, GROUP_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(CRON_EXP))
				.forJob(job)
				.build();
		
		scheduler.scheduleJob(job, trigger);
		
	}
}
