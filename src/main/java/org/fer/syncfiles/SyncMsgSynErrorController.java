/**
 * Sample Skeleton for "SyncMsgSyncError.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/

package org.fer.syncfiles;

import java.net.URL;
import java.util.ResourceBundle;

import org.fer.syncfiles.bus.SyncFilesDbService;

import com.cathive.fx.guice.FXMLController;
import com.google.inject.Inject;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;


@FXMLController(controllerId="SyncMsgSynErrorController")
public class SyncMsgSynErrorController
    implements Initializable {

	@Inject
	private App app;

    @Inject
    private SyncFilesDbService syncFilesDbService;
	
    // Handler for Button[Button[id=null, styleClass=button]] onAction
    public void btClose(ActionEvent event) throws Exception {
    	app.closeMissedSynchro();
    }

    // Handler for Button[Button[id=null, styleClass=button]] onAction
    public void btDisplayError(ActionEvent event) throws Exception {
		app.closeMissedSynchro();
    	app.showToAllResultController();
    	ignoreAllMissed();
    }

    // Handler for Button[Button[id=null, styleClass=button]] onAction
    public void btIgnoreAll(ActionEvent event) throws Exception {
    	ignoreAllMissed();
    	app.closeMissedSynchro();
    }
    
    private void ignoreAllMissed() {
    	syncFilesDbService.removeAllMsgErr();
    }

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        // initialize your logic here: all @FXML variables will have been injected

    }

}
