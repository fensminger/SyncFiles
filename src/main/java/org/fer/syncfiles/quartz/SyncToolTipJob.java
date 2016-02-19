package org.fer.syncfiles.quartz;

import javafx.application.Platform;

import org.apache.log4j.Logger;
import org.fer.syncfiles.App;
import org.fer.syncfiles.bus.SyncFilesDbService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.inject.Injector;

@DisallowConcurrentExecution
public class SyncToolTipJob implements Job {
	private static final Logger log = Logger.getLogger(SyncToolTipJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final Injector injector = App.getInjector();
		try {
			SyncFilesDbService syncFilesDbService = injector.getInstance(SyncFilesDbService.class);
			log.info("Vérification des erreurs de synchronisation.");
			if (!syncFilesDbService.isAllMsgErrRead()) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						final App app = injector.getInstance(App.class);
						app.startMissedSynchro();
					}
				});
			}
		} catch (Throwable e) {
			log.error("Echec de la vérification des jobs en erreur : " + e.getMessage(), e);
		}
	}

}
