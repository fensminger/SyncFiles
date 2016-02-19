/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fer.syncfiles;

import java.net.URL;
import java.util.ResourceBundle;

import org.hsqldb.util.DatabaseManagerSwing;

import com.cathive.fx.guice.FXMLController;
import com.google.inject.Inject;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Frederic
 */
@FXMLController(controllerId="MainController")
public class MainController implements Initializable {

	@Inject
	private App app;
	
    // Handler for Button[Button[id=null, styleClass=button]] onAction
    public void btClose(ActionEvent event) {
    	app.hideMainWindow();
    }

    // Handler for Button[Button[id=null, styleClass=button]] onAction
    public void btDb(ActionEvent event) {
		String[] args = new String[] {};
		DatabaseManagerSwing.main(args );
    }

    // Handler for Button[Button[id=null, styleClass=button]] onAction
    public void btLastExecution(ActionEvent event) throws Exception {
    	app.changeToAllResultController();
    }

    // Handler for Button[Button[id=null, styleClass=button]] onAction
    public void btParam(ActionEvent event) throws Exception {
    	app.changeToAllTemplateController();
    }

	
	
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
}
