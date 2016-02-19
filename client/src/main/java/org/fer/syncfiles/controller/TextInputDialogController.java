/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fer.syncfiles.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.fer.syncfiles.config.ScreensConfiguration;

/**
 * FXML Controller class
 *
 * @author Frederic
 */
public class TextInputDialogController implements DialogController {

    ScreensConfiguration screensConfiguration;
    private FXMLDialog dialog;

    public TextInputDialogController(ScreensConfiguration screensConfiguration) {
        this.screensConfiguration = screensConfiguration;
    }

	@FXML
	private TextField value;

	@FXML private void btOk() {
        screensConfiguration.closeDialog();
	}
	
	@SuppressWarnings("unused")
	@FXML private void btCancel() {
		value.setText("");
        screensConfiguration.closeDialog();
	}
	
    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }

    public void setTitle(String title) {
        dialog.setTitle(title);
    }
    public void setValue(String value) {
        if (value==null) {
            return;
        }
        this.value.setText(value);
    }

    public String getText() {
        return value.getText();
    }

    public void showAndWait() {
        if (!dialog.isShowing()) {
            dialog.showAndWait();
            dialog.close();
        }
    }
}
