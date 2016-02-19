/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fer.syncfiles.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import org.apache.log4j.Logger;
import org.fer.syncfiles.model.ParamEntry;
import org.fer.syncfiles.config.ScreensConfiguration;
import org.fer.syncfiles.model.Param;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fer.syncfiles.service.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

/**
 * FXML Controller class
 *
 * @author Frederic
 */
public class SyncAllTemplateController implements DialogController {
	private static final Logger log = Logger.getLogger(SyncAllTemplateController.class);

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private AppBus appBus;

	private FXMLDialog dialog;

    private ScreensConfiguration screensConfiguration;

    @FXML
    private TextArea serverInfo;

    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }

    private List<Param> paramList = null;

    @Override
    public void postConstruct() {
        loadData();
    }

    private void loadData() {

        name.setCellValueFactory(
                new PropertyValueFactory<ParamEntry, String>("name")
        );
        cronExp.setCellValueFactory(
                new PropertyValueFactory<ParamEntry,String>("cronExp")
        );
        data = FXCollections.observableArrayList(); // create the data

        Task loadTask = new Task() {
            @Override
            protected Object call() throws Exception {
                boolean paramLoaded = false;

                while (!paramLoaded) {
                    try {
                        paramList = restTemplateService.findAll().getParams();
                        for(Param param : paramList) {
                            data.add(getParamEntry(param));
                            log.info("JobName : " + param.getJobName());
                        }
                        Platform.runLater(() -> {
                            tableView.setItems(data); // assign the data to the table
                            serverInfo.setText("");
                        });
                        paramLoaded = true;
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            serverInfo.setText("Vérifier le bon fonctionnement du serveur : " + e.getMessage());
                        });
                        log.error(e);
                        paramLoaded = false;
                        try {
                            Thread.currentThread().sleep(5000);
                        } catch (InterruptedException e1) {
                            log.warn(e1);
                        }
                    }
                }
                return null;
            }
        };

        serverInfo.setText("Chargement des informations du serveur...");
        new Thread(loadTask).start();

    }

    public SyncAllTemplateController(ScreensConfiguration screensConfiguration) {
        this.screensConfiguration = screensConfiguration;
    }


	@FXML
	private TableView<ParamEntry> tableView = new TableView<ParamEntry>();
	private ObservableList<ParamEntry> data;
	
	@FXML
	private TableColumn<ParamEntry, String> name;
	
	@FXML
	private TableColumn<ParamEntry,String> cronExp;

    @FXML
    private CheckBox checkHubicChange;
    @FXML
    private CheckBox checkSimulation;
    @FXML
    private Button btStopServer;

	@FXML
	protected void syncStart() throws Exception {
        log.info("Start real sync...");
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();

        if (selectedIndex>=0) {
            appBus.startSynchronizeHubic(paramList.get(selectedIndex), checkHubicChange.isSelected(), checkSimulation.isSelected());
        }
    }

	@FXML
	protected void syncAddSynchro() throws Exception {
        final Param paramRef = new Param();
        screensConfiguration.showParamDialog(this, paramRef, -1);
    }

	@FXML
	protected void syncModifySynchro() throws Exception {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();

        if (selectedIndex>=0) {
            screensConfiguration.showParamDialog(this, paramList.get(selectedIndex), selectedIndex);
        }
    }

    @FXML
    protected void syncDupliquerSynchro() throws Exception {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();

        if (selectedIndex>=0) {
            final Param paramOrig = paramList.get(selectedIndex);
            Param paramNew = new Param(paramOrig);
            paramNew.setName("Copie de " + paramNew.getName());
            paramNew.setKey(null);
            paramNew = restTemplateService.save(paramNew);

            paramList.add(paramNew);
            setParam(-1, paramNew);
        }
    }

    @FXML
    protected void stopServer() {
        appBus.stopServer();
    }

	@FXML
	protected void syncDelSynchro() throws Exception {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
//		app.removeParamAtIndex(selectedIndex);
		data.remove(selectedIndex);
    }

    @FXML
    protected  void loadHubicFiles() throws Exception {
        appBus.startLoadHubicFiles();
    }

    @FXML
    protected  void syncHubicPrepSynchro() throws Exception {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex>=0) {
            final boolean isSimulation = true;
            appBus.startHubicSynchro(this, paramList.get(selectedIndex), selectedIndex, isSimulation);
        }
    }

    @FXML
    protected  void syncHubicSynchro() throws Exception {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex>=0) {
            final boolean isSimulation = true;
            appBus.startPrepUploadHubicSynchro(this, paramList.get(selectedIndex), selectedIndex, isSimulation);
        }
    }

    @FXML
	protected void syncPreviewSynchro() throws Exception {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketHandler myHandler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                log.info("Un message ws : " + message);
            }
        };
        sockJsClient.doHandshake(myHandler, "ws://localhost:9999/websocket");

		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex>=0) {
            final boolean isSimulation = true;
//            appBus.startSynchroProgress(this, paramList.get(selectedIndex), selectedIndex, isSimulation);
            appBus.startInfoTreeDialog(paramList.get(selectedIndex), selectedIndex);
        }
    }
	
	@FXML
	protected void syncExecuteSynchro() throws Exception {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex>=0) {
            final boolean isSimulation = false;
//            appBus.startSynchroProgress(this, paramList.get(selectedIndex), selectedIndex, isSimulation);
            appBus.startUploadHubicSynchro(this, paramList.get(selectedIndex), selectedIndex, isSimulation);
        }
    }

    private ParamEntry getParamEntry(Param param) {
    	ParamEntry res = new ParamEntry();
    	res.name.set(param.getName());
    	res.cronExp.set(param.getCronExp());
    	return res;
    }

    public void setParam(int index, Param param) {
        if (index>=0) {
            paramList.set(index, param);
            data.set(index, getParamEntry(param));
        } else {
            paramList.add(param);
            data.add(getParamEntry(param));
        }
    }
}
