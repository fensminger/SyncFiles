/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fer.syncfiles.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.fer.syncfiles.service.RestTemplateService;
import org.fer.syncfiles.service.SchedulerMgr;
import org.fer.syncfiles.config.ScreensConfiguration;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.service.ParamService;
import org.quartz.SchedulerException;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javafx.stage.DirectoryChooserBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FXML Controller class
 *
 * @author Frederic
 */
public class SyncParamController implements DialogController {
	private static final Logger log = Logger.getLogger(SyncParamController.class);
	
	private ScreensConfiguration screensConfiguration;

    @Autowired
    private AppBus appBus;

    @Autowired
    private RestTemplateService restTemplateService;

	private Param param;
    private int index;

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

    public SyncParamController(ScreensConfiguration screensConfiguration) {
        this.screensConfiguration = screensConfiguration;
    }

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
        Param newParam = null;
		try {
            if (param.getCreationDate()==null) {
                param.setCreationDate(new Date());
            }

//            schedulerMgr.insertOrUpdateJob(param);

            newParam = restTemplateService.save(param);
//		} catch (SchedulerException e) {
//			error = true;
//			e.printStackTrace();
//			showErrorMsg("Erreur de scheduling du job : " + e.getMessage());
		} catch (RuntimeException e) {
			error = true;
			e.printStackTrace();
			showErrorMsg("Erreur de scheduling du job : " + e.getMessage());
		}
		
		if (!error) {
            screensConfiguration.closeParamController(newParam, index);
		}
    }
	
	@FXML
	protected void btCancel() throws Exception {
        screensConfiguration.closeParamController(null, index);
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
		String pattern = appBus.showInputDialog("Veuillez entrer un pattern", null);
		if (pattern!=null) {
			ObservableList<String> list = includeExcludeList.getItems();
			list.add(pattern);
		}
    }
	
	@FXML
	protected void btRemoveDir() {
		int index = includeExcludeList.getSelectionModel().getSelectedIndex();
        if (index>=0) {
            ObservableList<String> list = includeExcludeList.getItems();
            list.remove(index);
        }
    }

	@FXML
    public void btModifyDir(ActionEvent event) throws IOException {
		int selectedIndex = includeExcludeList.getSelectionModel().getSelectedIndex();
		if (selectedIndex>=0) {
			String pattern = appBus.showInputDialog("Veuillez entrer un pattern", includeExcludeList.getSelectionModel().getSelectedItem());
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
    public void postConstruct(Param paramRef, int index) {
    	errorMsg.setVisible(false);
        if (index==-1) {
            param = new Param();
            this.index = index;
        } else {
            this.param = restTemplateService.load(paramRef);
            this.index = index;
            if (param!=null) {
                name.setText(param.getName());
                cronExp.setText(param.getCronExp());
                masterDir.setText(param.getMasterDir());
                slaveDir.setText(param.getSlaveDir());
                ObservableList<String> list = includeExcludeList.getItems();
                if (param.getIncludeExcludePatterns()!=null) {
                    list.addAll(param.getIncludeExcludePatterns().stream().collect(Collectors.toList()));
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
                this.index = index;
            }
        }
    }

    private FXMLDialog dialog;

    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }
}
