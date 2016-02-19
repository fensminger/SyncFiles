/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fer.syncfiles;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.model.ParamList;

import com.cathive.fx.guice.FXMLController;
import com.google.inject.Inject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Frederic
 */
@FXMLController(controllerId="SyncAllTemplateController")
public class SyncAllTemplateController implements Initializable {
	private static final Logger log = Logger.getLogger(SyncAllTemplateController.class);

	@Inject
	private App app;
	
	@FXML
	private TableView<ParamEntry> tableView = new TableView<ParamEntry>();
	private ObservableList<ParamEntry> data;
	
	@FXML
	private TableColumn<ParamEntry, String> synchroName;
	
	@FXML
	private TableColumn<ParamEntry,String> cronExp;
	
	@FXML
	protected void syncStart() {
		log.info("Ok cela fonctionne...");
    }

	@FXML
	protected void syncAddSynchro() throws Exception {
		app.setIndexParam(-1);
		app.changeToParamController();
    }

	@FXML
	protected void syncModifySynchro() throws Exception {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
		app.setIndexParam(selectedIndex);
		app.changeToParamController();
    }

	@FXML
	protected void syncDelSynchro() throws Exception {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
		app.removeParamAtIndex(selectedIndex);
		data.remove(selectedIndex);
    }

	@FXML
	protected void syncPreviewSynchro() throws Exception {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
		app.setIndexParam(selectedIndex);
		app.startSync(true);
    }
	
	@FXML
	protected void syncExecuteSynchro() throws Exception {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
		app.setIndexParam(selectedIndex);
		app.startSync(false);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	synchroName.setCellValueFactory(
                new PropertyValueFactory<ParamEntry,String>("itemName")
        );
    	cronExp.setCellValueFactory(
                new PropertyValueFactory<ParamEntry,String>("itemCronExp")
        );
    	ParamList paramList = app.getParamList();
    	data = FXCollections.observableArrayList(); // create the data
    	for(Param param : paramList.getParams()) {
    		data.add(getParamEntry(param));
    	}
    	tableView.setItems(data); // assign the data to the table
    }    
    
    private ParamEntry getParamEntry(Param param) {
    	ParamEntry res = new ParamEntry();
    	res.itemName.set(param.getName());
    	res.itemCronExp.set(param.getCronExp());
    	return res;
    }
}
