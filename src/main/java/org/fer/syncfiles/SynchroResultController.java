package org.fer.syncfiles;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;

import org.fer.syncfiles.model.ResultSync;

import com.cathive.fx.guice.FXMLController;
import com.google.inject.Inject;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

@FXMLController(controllerId="SynchroResultController")
public class SynchroResultController implements Initializable {

	@Inject
	private App app;
	
	@FXML
	// fx:id="lbResSynchro"
	private Label lbResSynchro; // Value injected by FXMLLoader

	@FXML
	// fx:id="resListView"
	private ListView<String> resListView; // Value injected by FXMLLoader

	@FXML
	// fx:id="resMsgError"
	private TextArea resMsgError; // Value injected by FXMLLoader

	// Handler for Button[Button[id=null, styleClass=button]] onAction
	public void btClose(ActionEvent event) throws Exception {
		app.changeToAllTemplateController();
	}

	@Override
	// This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		assert lbResSynchro != null : "fx:id=\"lbResSynchro\" was not injected: check your FXML file 'SyncResult.fxml'.";
		assert resListView != null : "fx:id=\"resListView\" was not injected: check your FXML file 'SyncResult.fxml'.";
		assert resMsgError != null : "fx:id=\"resMsgError\" was not injected: check your FXML file 'SyncResult.fxml'.";

		// initialize your logic here: all @FXML variables will have been
		// injected

		SyncFilesTask task = app.getTask();
		ResultSync res = task.getResultList();
		resMsgError.setText(res.getMsgError());
		resListView.setItems(FXCollections.observableArrayList(res.getDisplayInfo()));
	}

	public String stackTraceToString(Throwable e) {
		String retValue = null;
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			retValue = sw.toString();
		} finally {
			try {
				if (pw != null)
					pw.close();
				if (sw != null)
					sw.close();
			} catch (IOException ignore) {
			}
		}
		return retValue;
	}

}
