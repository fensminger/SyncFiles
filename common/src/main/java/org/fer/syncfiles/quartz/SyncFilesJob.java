package org.fer.syncfiles.quartz;

import org.apache.log4j.Logger;
import org.fer.syncfiles.bus.SyncFileMgr;
import org.fer.syncfiles.model.Param;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

@DisallowConcurrentExecution
public class SyncFilesJob implements Job {
	private static final Logger log = Logger.getLogger(SyncFilesJob.class);
	
	private Param param;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		@SuppressWarnings("unused")
		JobKey key = context.getJobDetail().getKey();
		@SuppressWarnings("unused")
		JobDataMap dataMap = context.getMergedJobDataMap(); // Note the difference from the previous state.add(new Date());
//		Injector injector = App.getInjector();
//		try {
//			SyncFileMgr syncFileMgr = injector.getInstance(SyncFileMgr.class);
//
//			log.info("Schedule task : start syncfiles for param -> " + param);
//			syncFileMgr.launchSyncFiles(param, null, false);
//			SyncFilesDbService syncFilesDbService = injector.getInstance(SyncFilesDbService.class);
//			syncFilesDbService.save(syncFileMgr.getResultSync());
//		} catch (Throwable e) {
//			log.error("Echec dans le backup SyncFilesJob : " + e.getMessage(), e);
//		}
	}

	public Param getParam() {
		return param;
	}

	public void setParam(Param param) {
		this.param = param;
	}

}
