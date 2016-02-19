package org.fer.syncfiles.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.fer.syncfiles.service.SyncFilesTask;
import org.fer.syncfiles.model.Param;
import org.springframework.beans.factory.annotation.Autowired;


public class SynchroProgressController implements DialogController {

    @Autowired
    private AppBus appBus;

    @FXML //  fx:id="btCancel"
    private Button btCancel; // Value injected by FXMLLoader

    @FXML //  fx:id="lbEtape"
    private Label lbEtape; // Value injected by FXMLLoader

    @FXML //  fx:id="progressBar"
    private ProgressBar progressBar; // Value injected by FXMLLoader

    @Autowired
    private SyncFilesTask task;

    private FXMLDialog dialog;

    @FXML
    public void onBtCancel() {
    	btCancel.setDisable(true);
    	task.cancel();
        dialog.hide();
    }
    
    
    public void start(Param param, boolean isSimulation) {
        assert btCancel != null : "fx:id=\"btCancel\" was not injected: check your FXML file 'SynchroProgress.fxml'.";
        assert lbEtape != null : "fx:id=\"lbEtape\" was not injected: check your FXML file 'SynchroProgress.fxml'.";
        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'SynchroProgress.fxml'.";

        task.init(dialog, param, isSimulation);
        progressBar.progressProperty().bind(task.progressProperty());
        lbEtape.textProperty().bind(task.messageProperty());
//        new Thread(task).start();
    }

    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }

    public SyncFilesTask getTask() {
        return task;
    }
}
