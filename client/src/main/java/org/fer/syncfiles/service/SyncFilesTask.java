package org.fer.syncfiles.service;

import javafx.application.Platform;
import javafx.concurrent.Task;

import org.fer.syncfiles.controller.FXMLDialog;
import org.fer.syncfiles.UpdateTaskListener;
import org.fer.syncfiles.bus.SyncFileMgr;
import org.fer.syncfiles.controller.AppBus;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.model.ResultSync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SyncFilesTask extends Task<Void> implements UpdateTaskListener {

    @Autowired
	private SyncFileMgr syncFileMgr = null;

    @Autowired
    private AppBus appBus;

	private Param param;
	private boolean isSimulation;
    private FXMLDialog fxmlDialog;
	
	public SyncFilesTask() {
		super();
	}
	
	
	public ResultSync getResultList() {
		assert syncFileMgr!=null : "syncFileMgr doit être initialisée.";
		
		return syncFileMgr.getResultSync();
	}

	public void init(FXMLDialog fxmlDialog, Param param, boolean isSimulation) {
		this.param = param;
		this.isSimulation = isSimulation;
        this.fxmlDialog = fxmlDialog;
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
                    fxmlDialog.hide();
//					Injector injector = App.getInjector();
//					App app = injector.getInstance(App.class);
//					app.displayResultSync();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
