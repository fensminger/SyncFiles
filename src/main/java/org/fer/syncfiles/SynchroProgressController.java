package org.fer.syncfiles;

import java.net.URL;
import java.util.ResourceBundle;

import com.cathive.fx.guice.FXMLController;
import com.google.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;


@FXMLController(controllerId="SynchroProgressController")
public class SynchroProgressController
    implements Initializable {

	@Inject
	private App app;
	
    @FXML //  fx:id="btCancel"
    private Button btCancel; // Value injected by FXMLLoader

    @FXML //  fx:id="lbEtape"
    private Label lbEtape; // Value injected by FXMLLoader

    @FXML //  fx:id="progressBar"
    private ProgressBar progressBar; // Value injected by FXMLLoader

    private SyncFilesTask task;
    
    @FXML
    public void onBtCancel() {
    	btCancel.setDisable(true);
    	task.cancel();
    }
    
    
    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert btCancel != null : "fx:id=\"btCancel\" was not injected: check your FXML file 'SynchroProgress.fxml'.";
        assert lbEtape != null : "fx:id=\"lbEtape\" was not injected: check your FXML file 'SynchroProgress.fxml'.";
        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'SynchroProgress.fxml'.";

        // initialize your logic here: all @FXML variables will have been injected
        task = app.createSyncStask();
        progressBar.progressProperty().bind(task.progressProperty());
        lbEtape.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }

}
