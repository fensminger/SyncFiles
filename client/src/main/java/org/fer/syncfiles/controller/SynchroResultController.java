package org.fer.syncfiles.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.fer.syncfiles.service.SyncFilesTask;
import org.fer.syncfiles.model.ResultSync;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class SynchroResultController implements DialogController {

	@FXML
	// fx:id="lbResSynchro"
	private Label lbResSynchro; // Value injected by FXMLLoader

	@FXML
	// fx:id="resListView"
	private ListView<String> resListView; // Value injected by FXMLLoader

	@FXML
	// fx:id="resMsgError"
	private TextArea resMsgError; // Value injected by FXMLLoader
    private FXMLDialog dialog;

    // Handler for Button[Button[id=null, styleClass=button]] onAction
	public void btClose(ActionEvent event) throws Exception {
		dialog.hide();
        dialog.close();
	}

	public void start(SyncFilesTask task) {
		assert lbResSynchro != null : "fx:id=\"lbResSynchro\" was not injected: check your FXML file 'SyncResult.fxml'.";
		assert resListView != null : "fx:id=\"resListView\" was not injected: check your FXML file 'SyncResult.fxml'.";
		assert resMsgError != null : "fx:id=\"resMsgError\" was not injected: check your FXML file 'SyncResult.fxml'.";

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

    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }
}
