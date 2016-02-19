package org.fer.syncfiles;

import javafx.application.Platform;
import javafx.concurrent.Task;

import org.fer.syncfiles.bus.SyncFileMgr;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.model.ResultSync;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class SyncFilesTask extends Task<Void> implements UpdateTaskListener {
	
	private SyncFileMgr syncFileMgr = null;
	private Param param;
	private boolean isSimulation;
	
	@Inject
	public SyncFilesTask(SyncFileMgr syncFileMgr) {
		super();
		this.syncFileMgr = syncFileMgr;
	}
	
	
	public ResultSync getResultList() {
		assert syncFileMgr!=null : "syncFileMgr doit être initialisée.";
		
		return syncFileMgr.getResultSync();
	}

	public void init(Param param, boolean isSimulation) {
		this.param = param;
		this.isSimulation = isSimulation;
	}
	
	@Override
	protected Void call() throws Exception {
    	try {
    		syncFileMgr.launchSyncFiles(param, this, isSimulation);
    	} finally {
    		closeDialog();
    	}
//        final int max = 1000;
//        for (int i=1; i<=max; i++) {
//            if (isCancelled()) {
//               break;
//            }
//            try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//            updateMessage("Itération : " + i);
//            updateProgress(i, max);
//        }
        return null;
    }

	@Override
	public void updateMessage(String arg0) {
		super.updateMessage(arg0);
	}

	@Override
	public void updateProgress(double arg0, double arg1) {
		super.updateProgress(arg0, arg1);
	}

	@Override
	public void updateProgress(long arg0, long arg1) {
		super.updateProgress(arg0, arg1);
	}
	
	private void closeDialog() {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					Injector injector = App.getInjector();
					App app = injector.getInstance(App.class);
					app.displayResultSync();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
