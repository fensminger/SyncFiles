package org.fer.syncfiles.inject;

import org.fer.syncfiles.bus.SyncFilesDbService;

import com.google.inject.Injector;

public class ConcurrentExecutionExecutor extends AbstractMultipleExecutor {

	private final SyncFilesDbService syncFilesDbService;
	private final boolean isFindAll;
	private int nbOk = 0;
	
	public ConcurrentExecutionExecutor(Injector injector, boolean isFindAll) {
		super(injector);
		syncFilesDbService = injector.getInstance(SyncFilesDbService.class);
		this.isFindAll = isFindAll;
	}

	@Override
	public void run() {
		error = true;
		super.run();
		error = CALL_NB!=nbOk;
	}
	
	@Override
	public void execute() {
		log.info("Execute on isFindAll " + isFindAll);
		try {
			if (isFindAll) {
				syncFilesDbService.findAll();
			} else {
				syncFilesDbService.isAllMsgErrRead();
			}
			nbOk++;
			log.info("Execute OK on isFindAll " + isFindAll + " : " + nbOk);
		} catch (Throwable e) {
			log.error("Throwable Exception on isFindAll " + isFindAll + ", msg Exception " + e.getMessage(), e);
			throw e;
		}
	}

}
