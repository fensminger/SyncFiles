/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fer.syncfiles;

import java.net.URL;
import java.util.ResourceBundle;

import com.cathive.fx.guice.FXMLController;
import com.google.inject.Inject;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Frederic
 */
@FXMLController(controllerId="TextInputDialogController")
public class TextInputDialogController implements Initializable {

	@Inject
	private App app;
	
	@FXML
	private TextField value;

	@FXML private void btOk() {
		app.closeDialog();
	}
	
	@SuppressWarnings("unused")
	@FXML private void btCancel() {
		value.setText("");
		btOk();
	}
	
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
}
