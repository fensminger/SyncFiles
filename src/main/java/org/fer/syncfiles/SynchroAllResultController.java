package org.fer.syncfiles;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.fer.syncfiles.bus.SyncFilesDbService;
import org.fer.syncfiles.model.ResultSync;
import org.fer.syncfiles.model.ResultSyncDetail;

import com.cathive.fx.guice.FXMLController;
import com.google.inject.Inject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

@FXMLController(controllerId="SynchroAllResultController")
public class SynchroAllResultController implements Initializable {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SynchroAllResultController.class);

    @Inject
    private SyncFilesDbService syncFilesDbService;
	
	@FXML
	// fx:id="durationCol"
	private TableColumn<ResultEntry, String> durationCol; // Value injected by
															// FXMLLoader

	@FXML
	// fx:id="executionDateCol"
	private TableColumn<ResultEntry, String> executionDateCol; // Value injected
																// by FXMLLoader

	@FXML
	// fx:id="lbResSynchro"
	private Label lbResSynchro; // Value injected by FXMLLoader

	@FXML
	// fx:id="msgCol"
	private TableColumn<ResultEntry, ResultSyncMsgWrapper> msgCol; // Value injected by
														// FXMLLoader

	@FXML
	// fx:id="nameCol"
	private TableColumn<ResultEntry, ResultSync> nameCol; // Value injected by
															// FXMLLoader

	@FXML
	// fx:id="resDetailListView"
	private ListView<ResultSyncDetail> resDetailListView; // Value injected by FXMLLoader

	@FXML
	// fx:id="resultListTable"
	private TableView<ResultEntry> resultListTable = new TableView<ResultEntry>(); // Value
																					// injected
																					// by
																					// FXMLLoader
    @FXML //  fx:id="resDetailLabel"
    private Label resDetailLabel; // Value injected by FXMLLoader

    private ObservableList<ResultEntry> obsList;
	private ObservableList<ResultSyncDetail> obsResDetailList = FXCollections.observableArrayList();

    // Handler for TableView[fx:id="resultListTable"] onKeyTyped
    public void onKeyTyped(KeyEvent event) {
    	if (event.getCharacter().equals(" ")
    			|| event.getCharacter().equals("\r")) {
    		@SuppressWarnings("unchecked")
    		TableView<ResultEntry> obj = (TableView<ResultEntry>) event.getSource();
    		changeSelection(obj);
    	}
    	
    }

	// Handler for TableView[fx:id="resultListTable"] onMouseClicked
	public void onMouseClicked(MouseEvent event) {
		@SuppressWarnings("unchecked")
		TableView<ResultEntry> obj = (TableView<ResultEntry>) event.getSource();
		changeSelection(obj);
	}

	private void changeSelection(TableView<ResultEntry> obj) {
		int selectedIndex = obj.getSelectionModel().getSelectedIndex();
		if (selectedIndex > -1) {
			ResultEntry resultEntry = obsList.get(selectedIndex);
			ResultSync name = resultEntry.getName();
			name.setReaded(true);
			syncFilesDbService.saveOnlyState(name);
			obsList.set(selectedIndex, getResultEntry(name));
			
			obsResDetailList.clear();
			for(ResultSyncDetail detail : syncFilesDbService.loadResultSyncDetail(name)) {
				obsResDetailList.add(detail);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			resDetailLabel.setText(name.getParam().getName() + ", " + sdf.format(name.getExecutionDate()) + (name.isError()?", erreur de synchronisation":""));
		}
	}

	@Override
	// This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		assert durationCol != null : "fx:id=\"durationCol\" was not injected: check your FXML file 'SyncAllResult.fxml'.";
		assert executionDateCol != null : "fx:id=\"executionDateCol\" was not injected: check your FXML file 'SyncAllResult.fxml'.";
		assert lbResSynchro != null : "fx:id=\"lbResSynchro\" was not injected: check your FXML file 'SyncAllResult.fxml'.";
		assert msgCol != null : "fx:id=\"msgCol\" was not injected: check your FXML file 'SyncAllResult.fxml'.";
		assert nameCol != null : "fx:id=\"nameCol\" was not injected: check your FXML file 'SyncAllResult.fxml'.";
		assert resDetailListView != null : "fx:id=\"resDetailListView\" was not injected: check your FXML file 'SyncAllResult.fxml'.";
		assert resultListTable != null : "fx:id=\"resultListTable\" was not injected: check your FXML file 'SyncAllResult.fxml'.";

		nameCol.setCellFactory(new Callback<TableColumn<ResultEntry, ResultSync>, TableCell<ResultEntry, ResultSync>>() {

			@Override
			public TableCell<ResultEntry, ResultSync> call(TableColumn<ResultEntry, ResultSync> col) {
				TableCell<ResultEntry, ResultSync> cell = new TableCell<ResultEntry, ResultSync>() {

					@Override
					protected void updateItem(ResultSync resultSync, boolean empty) {
						if (empty) {
							super.updateItem(resultSync, empty);
						} else {
							Label name = new Label(resultSync.getParam().getName());
							if (!resultSync.isReaded()) {
								name.setStyle("-fx-font-weight: bold;");
							}
							setGraphic(name);
						}
					}

				};
				return cell;
			}
		});

		nameCol.setCellValueFactory(new PropertyValueFactory<ResultEntry, ResultSync>("name"));

		msgCol.setCellFactory(new Callback<TableColumn<ResultEntry, ResultSyncMsgWrapper>, TableCell<ResultEntry, ResultSyncMsgWrapper>>() {

			@Override
			public TableCell<ResultEntry, ResultSyncMsgWrapper> call(TableColumn<ResultEntry, ResultSyncMsgWrapper> col) {
				TableCell<ResultEntry, ResultSyncMsgWrapper> cell = new TableCell<ResultEntry, ResultSyncMsgWrapper>() {

					@Override
					protected void updateItem(ResultSyncMsgWrapper resultSyncMsg, boolean empty) {
						if (empty) {
							super.updateItem(resultSyncMsg, empty);
						} else {
							Label name = new Label(resultSyncMsg.toString());
							if (resultSyncMsg.isError()) {
								name.setStyle("-fx-font-weight: bold;");
							}
							setGraphic(name);
						}
					}

				};
				return cell;
			}
		});

		msgCol.setCellValueFactory(new PropertyValueFactory<ResultEntry, ResultSyncMsgWrapper>("msg"));
		executionDateCol.setCellValueFactory(new PropertyValueFactory<ResultEntry, String>("executionDate"));
		durationCol.setCellValueFactory(new PropertyValueFactory<ResultEntry, String>("duration"));

		List<ResultSync> resultList = syncFilesDbService.loadAllResultSync();

		obsList = FXCollections.observableArrayList();
		for (ResultSync resultSync : resultList) {
			obsList.add(getResultEntry(resultSync));
		}

		resultListTable.setItems(obsList);
		resDetailListView.setItems(obsResDetailList);
	}

	private ResultEntry getResultEntry(ResultSync resultSync) {
		ResultEntry res = new ResultEntry();

		res.name.set(resultSync);
		res.msg.set(new ResultSyncMsgWrapper(resultSync));
		Date executionDate = resultSync.getExecutionDate();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		res.executionDate.set(sdf.format(executionDate));
		final long duration;
		if (resultSync.getEndExecutionDate()==null) {
			duration = 0;
		} else {
			duration = (resultSync.getEndExecutionDate().getTime() - executionDate.getTime()) / 1000;
		}
		res.duration.set("" + duration);

		return res;
	}

}