package org.fer.syncfiles.config;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;
import org.fer.syncfiles.controller.*;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.service.SchedulerMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

/**
 * Created by fer on 20/09/2014.
 */
@Configuration
@Lazy
public class ScreensConfiguration {
    private static final Logger log = Logger.getLogger(ScreensConfiguration.class);

    private Stage primaryStage;
    private FXMLDialog addParamDialog = null;
    private FXMLDialog allTemplateDialog = null;
    private FXMLDialog textDialog = null;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showScreen(Parent screen) {
        primaryStage.setScene(new Scene(screen));
        primaryStage.show();
    }

    @Bean
    @Scope("prototype")
    public FXMLDialog loadSyncAllTemplate() {
        allTemplateDialog = new FXMLDialog(syncAllTemplateController(), getClass().getResource("../SyncAllTemplate.fxml"), primaryStage, StageStyle.DECORATED);
        return allTemplateDialog;
    }

    @Bean
    @Scope("prototype")
    public FXMLDialog loadNewParamDialog() {
        return new FXMLDialog(syncParamController(), getClass().getResource("../SyncParam.fxml"), primaryStage, StageStyle.DECORATED);
    }

    @Bean
    @Scope("prototype")
    public FXMLDialog loadSynchroProgressDialog() {
        return new FXMLDialog(synchroProgressController(), getClass().getResource("../SynchroProgress.fxml"), primaryStage, StageStyle.DECORATED);
    }

    @Bean
    @Scope("prototype")
    public FXMLDialog loadSynchroResultDialog() {
        return new FXMLDialog(synchroResultController(), getClass().getResource("../SyncResult.fxml"), primaryStage, StageStyle.DECORATED);
    }

    @Bean
    @Scope("prototype")
    public FXMLDialog loadSynchroInfoTreeDialog() {
        return new FXMLDialog(synchroInfoTreeController(), getClass().getResource("../SyncInfoTree.fxml"), primaryStage, StageStyle.DECORATED);
    }

    @Bean
    @Scope("prototype")
    public SynchroResultController synchroResultController() {
        return new SynchroResultController();
    }

    @Bean
    @Scope("prototype")
    public FXMLDialog loadTextInputDialog() {
        if (textDialog!=null) {
            textDialog.toFront();
        } else {
            textDialog = new FXMLDialog(syncTextInputDialogController(), getClass().getResource("../TextInputDialog.fxml"), primaryStage, StageStyle.DECORATED);
        }
        return textDialog;
    }
    @Bean
    @Scope("prototype")
    TextInputDialogController syncTextInputDialogController() {
        return new TextInputDialogController(this);
    }

    public void closeDialog() {
        if (textDialog!=null) {
            textDialog.close();
            textDialog = null;
        }
    }


    @Bean
    @Scope("singleton")
    public SchedulerMgr loadSchedulerMgr() {
        return new SchedulerMgr();
    }

    @Bean
    @Scope("prototype")
    SyncAllTemplateController syncAllTemplateController() {
        return new SyncAllTemplateController(this);
    }

    @Bean
    @Scope("prototype")
    SyncParamController syncParamController() {
        return new SyncParamController(this);
    }

    @Bean
    @Scope("prototype")
    SynchroProgressController synchroProgressController() {
        return new SynchroProgressController();
    }

    @Bean
    @Scope("prototype")
    SyncInfoTreeController synchroInfoTreeController() {
        return new SyncInfoTreeController();
    }

    @Bean
    @Scope("singleton")
    public RestTemplate loadRestTemplate() {
        return new RestTemplate();
    }


    public void showParamDialog(SyncAllTemplateController syncAllTemplateController, Param paramRef, int index) {
        log.info("syncAddSynchro");
        if (addParamDialog==null) {
            addParamDialog = loadNewParamDialog();
            SyncParamController syncParamController = addParamDialog.getController(SyncParamController.class);
            syncParamController.postConstruct(paramRef, index);
            addParamDialog.show();
        } else {
            addParamDialog.toFront();
        }
    }

    public void closeParamController(Param param, int index) {
        if (param!=null) {
            SyncAllTemplateController ctrl = allTemplateDialog.getController(SyncAllTemplateController.class);
            ctrl.setParam(index, param);
        }
        addParamDialog.close();
        addParamDialog = null;
    }
}
