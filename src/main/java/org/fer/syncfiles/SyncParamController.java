/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fer.syncfiles;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.fer.syncfiles.bus.SyncFilesDbService;
import org.fer.syncfiles.model.Param;
import org.quartz.SchedulerException;

import com.cathive.fx.guice.FXMLController;
import com.google.inject.Inject;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javafx.stage.DirectoryChooserBuilder;

/**
 * FXML Controller class
 *
 * @author Frederic
 */
@FXMLController(controllerId="SyncParamController")
public class SyncParamController implements Initializable {
	private static final Logger log = Logger.getLogger(SyncParamController.class);
	
	@Inject
	private App app;

    @Inject
    private SyncFilesDbService syncFilesDbService;
	
	private Param param;
	
	@FXML private TextField name;
	
	@FXML private TextField cronExp;

	@FXML private TextField masterDir;
	
	@FXML private TextField slaveDir;
	
	@FXML private ListView<String> includeExcludeList;
	
	@SuppressWarnings("unused")
	@FXML private ToggleGroup tgIncludeExclude;
	@FXML private RadioButton cbInclude;
	@FXML private RadioButton cbExclude;
	
	@FXML private Label errorMsg;
	
	@FXML
	protected void btOk() throws Exception {
		Param param = getParam();
		log.info(""+param);
		if (param.getName()==null || param.getName().trim().equals("")) {
			showErrorMsg("Le nom est obligatoire.");
			return;
		}
		if (param.getCronExp()==null || param.getCronExp().trim().equals("")) {
			showErrorMsg("La périodicité d'exécution est obligatoire.");
			return;
		}
		if (param.getMasterDir()==null || param.getMasterDir().trim().equals("")) {
			showErrorMsg("Le répertoire maître est obligatoire.");
			return;
		}
		if (param.getSlaveDir()==null || param.getSlaveDir().trim().equals("")) {
			showErrorMsg("Le répertoire cible est obligatoire.");
			return;
		}
		boolean error = false;
		try {
			@SuppressWarnings("unused")
			Param newParam = app.addOrUpdateParam(param);
		} catch (JAXBException e) {
			error = true;
			e.printStackTrace();
			showErrorMsg("Impossible de sauvegarder les paramètres : " + e.getMessage());
		} catch (SchedulerException e) {
			error = true;
			e.printStackTrace();
			showErrorMsg("Erreur de scheduling du job : " + e.getMessage());
		} catch (RuntimeException e) {
			error = true;
			e.printStackTrace();
			showErrorMsg("Erreur de scheduling du job : " + e.getMessage());
		}
		
		if (!error) {
			app.changeToAllTemplateController();
		}
    }
	
	@FXML
	protected void btCancel() throws Exception {
		app.changeToAllTemplateController();
    }
	
	@FXML
	protected void btMasterDir() {
		DirectoryChooserBuilder fcb = DirectoryChooserBuilder.create();
		DirectoryChooser fc = fcb.build();
		fc.setTitle("Sélection du répertoire maître");
		File file = fc.showDialog(null);
		if (file!=null) {
			masterDir.setText(file.getPath());
		}
    }
	
	@FXML
	protected void btSlaveDir() {
		DirectoryChooserBuilder fcb = DirectoryChooserBuilder.create();
		DirectoryChooser fc = fcb.build();
		fc.setTitle("Sélection du répertoire esclave");
		File file = fc.showDialog(null);
		if (file!=null) {
			slaveDir.setText(file.getPath());
		}
    }
	
	@FXML
	protected void btIncludeExcludeDir() {
		String path = masterDir.getText();
		if (path==null || path.trim().equals("")) {
			showErrorMsg("Sélectionner d'abord un répertoire maître.");
			return;
		}
		DirectoryChooserBuilder fcb = DirectoryChooserBuilder.create();
		fcb.initialDirectory(new File(path));
		DirectoryChooser fc = fcb.build();
		fc.setTitle("Sélection du répertoire esclave");
		File file = fc.showDialog(null);
		if (file!=null) {
			ObservableList<String> list = includeExcludeList.getItems();
			final String resPath = file.getPath();
			if (resPath.startsWith(path)) {
				String relatifPath = resPath.substring(path.length());
				if (relatifPath.startsWith("\\")) {
					relatifPath = relatifPath.substring(1);
				}
				list.add(relatifPath+"\\*");
			} else {
				showErrorMsg("Le répertoire n'est pas sous l'arborescence du répertoire maître.");
			}
		}
    }

	private void showErrorMsg(String msg) {
		errorMsg.setText(msg);
		errorMsg.setVisible(true);
		Task<Integer> task = new Task<Integer>() {

			@Override
			protected Integer call() throws Exception {
				try {
					Thread.sleep(10000);
				} finally {
					errorMsg.setVisible(false);
				}
				return null;
			}
		};
		new Thread(task).start();
	}
	
	@FXML
	protected void btIncludeExcludePattern() throws IOException {
		String pattern = app.showInputDialog("Veuillez entrer un pattern", null);
		if (pattern!=null) {
			ObservableList<String> list = includeExcludeList.getItems();
			list.add(pattern);
		}
    }
	
	@FXML
	protected void btRemoveDir() {
		int index = includeExcludeList.getSelectionModel().getSelectedIndex();
		ObservableList<String> list = includeExcludeList.getItems();
		list.remove(index);
    }

	@FXML
    public void btModifyDir(ActionEvent event) throws IOException {
		int selectedIndex = includeExcludeList.getSelectionModel().getSelectedIndex();
		if (selectedIndex>=0) {
			String pattern = app.showInputDialog("Veuillez entrer un pattern", includeExcludeList.getSelectionModel().getSelectedItem());
			if (pattern!=null) {
				ObservableList<String> list = includeExcludeList.getItems();
				list.set(selectedIndex, pattern);
			}
		}
    }

	
	protected Param getParam() {
		param.setName(name.getText());
		param.setCronExp(cronExp.getText());
		param.setIncludeDir(cbInclude.isSelected());
		param.setMasterDir(masterDir.getText());
		param.setSlaveDir(slaveDir.getText());
		param.setIncludeExcludePatterns(includeExcludeList.getItems());
		
		return param;
	}
	
	/**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	errorMsg.setVisible(false);
    	this.param = app.getCurrentParam();
    	this.param = syncFilesDbService.load(param);
    	if (param!=null) {
	    	name.setText(param.getName());
	    	cronExp.setText(param.getCronExp());
	    	masterDir.setText(param.getMasterDir());
	    	slaveDir.setText(param.getSlaveDir());
	    	ObservableList<String> list = includeExcludeList.getItems();
	    	if (param.getIncludeExcludePatterns()!=null) {
		    	for(String includeExcludeDir : param.getIncludeExcludePatterns()) {
		    		list.add(includeExcludeDir);
		    	}
	    	}
	    	if (param.isIncludeDir()) {
	    		cbInclude.setSelected(true);
	    		cbExclude.setSelected(false);
	    	} else {
	    		cbInclude.setSelected(false);
	    		cbExclude.setSelected(true);
	    	}
    	} else {
    		param = new Param();
    	}
    }    
}
