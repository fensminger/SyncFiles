package org.fer.syncfiles.controller;

import org.fer.syncfiles.config.ScreensConfiguration;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.service.HubicSynchro;
import org.fer.syncfiles.service.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by fer on 26/09/2014.
 */
@Service
public class AppBus {

    @Autowired
    ScreensConfiguration screensConfiguration;

    @Autowired
    RestTemplateService restTemplateService;


    public String showInputDialog(String title, String text) throws IOException {
        FXMLDialog dialog = screensConfiguration.loadTextInputDialog();
        TextInputDialogController ctrl = dialog.getController(TextInputDialogController.class);
        ctrl.setTitle(title);
        ctrl.setValue(text);
        ctrl.showAndWait();
        String result = ctrl.getText();
        return "".equals(result)?null:result;
    }

    public void startInfoTreeDialog(Param param, int index) throws IOException {
        FXMLDialog dialog = screensConfiguration.loadSynchroInfoTreeDialog();
        SyncInfoTreeController ctrl = dialog.getController(SyncInfoTreeController.class);
        ctrl.setParam(index, param);
        dialog.showAndWait();
        dialog.close();
    }

    public void startSynchroProgress(SyncAllTemplateController syncAllTemplateController, Param param, int selectedIndex, boolean isSimulation) {
        if (selectedIndex>=0) {
            FXMLDialog syncProgressDialog = screensConfiguration.loadSynchroProgressDialog();
            syncProgressDialog.setTitle("Synchronisation en cours...");
            SynchroProgressController ctrl = syncProgressDialog.getController(SynchroProgressController.class);
            ctrl.start(param, isSimulation);
            syncProgressDialog.showAndWait();
            syncProgressDialog.close();

            FXMLDialog resultDialog = screensConfiguration.loadSynchroResultDialog();
            SynchroResultController ctrlResult = resultDialog.getController(SynchroResultController.class);
            ctrlResult.start(ctrl.getTask());
            resultDialog.showAndWait();
            resultDialog.close();
        }
    }

    public void startHubicSynchro(SyncAllTemplateController syncAllTemplateController, Param param, int selectedIndex, boolean isSimulation) throws IOException {
        if (selectedIndex>=0) {
//            hubicSynchro.start(param, isSimulation);
            restTemplateService.startHubicSynchro(param, selectedIndex, isSimulation);
        }
    }

    public void startPrepUploadHubicSynchro(SyncAllTemplateController syncAllTemplateController, Param param, int selectedIndex, boolean isSimulation) {
        if (selectedIndex>=0) {
//            hubicSynchro.startUpload(param, isSimulation);
            restTemplateService.startPrepUploadHubicSynchro(param, selectedIndex, isSimulation);
        }

    }

    public void startUploadHubicSynchro(SyncAllTemplateController syncAllTemplateController, Param param, int selectedIndex, boolean isSimulation) {
        if (selectedIndex>=0) {
//            hubicSynchro.startUpload(param, isSimulation);
            restTemplateService.startUploadHubicSynchro(param, selectedIndex, isSimulation);
        }

    }

    public void startLoadHubicFiles() {
        restTemplateService.startLoadHubicFiles();
    }

    public void startSynchronizeHubic(Param param, boolean checkHubic, boolean checkSimulation) {
        if (param!=null) {
            restTemplateService.startSynchronizeHubic(param, checkHubic, checkSimulation);
        }
    }

    public void stopServer() {
        restTemplateService.quitServer();
    }
}
