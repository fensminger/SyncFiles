/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fer.syncfiles.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.log4j.Logger;
import org.fer.syncfiles.model.*;
import org.fer.syncfiles.service.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FXML Controller class
 *
 * @author Frederic
 */
public class SyncInfoTreeController implements DialogController {
	private static final Logger log = Logger.getLogger(SyncInfoTreeController.class);

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private AppBus appBus;

	private FXMLDialog dialog;

    private Param param;

    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }

    private List<Param> paramList = null;
    List<MergedFileInfo> allMergedFileInfoList;
    List<MergedFileInfo> filteredMergedFileInfoList;

    @Override
    public void postConstruct() {
        loadData();
    }


    private void loadData() {
    }

    public SyncInfoTreeController() {
    }


	@FXML
	protected void btClose() throws Exception {
        dialog.hide();
    }

    public void setParam(int index, Param param) {
        this.param = param;
        List<MergedFileInfoJson> allMergedFileInfoJsonList = restTemplateService.loadMergedFileFromParam(param.getKey());
        allMergedFileInfoList = allMergedFileInfoJsonList.stream().map(MergedFileInfo::new).collect(Collectors.toList());

        loadTree(allMergedFileInfoList);

        filteredMergedFileInfoList = new ArrayList<>();
        for(MergedFileInfo mergedFileInfo : allMergedFileInfoList) {
            FileInfo fileInfoSource = mergedFileInfo.getSourceFileInfo();
            FileInfo fileInfoTarget = mergedFileInfo.getTargetFileInfo();
            if ((fileInfoSource!=null && isFileActionModified(fileInfoSource))
                    || (fileInfoTarget!=null && isFileActionModified(fileInfoTarget))) {
                filteredMergedFileInfoList.add(mergedFileInfo);
            }
        }
    }

    private boolean isFileActionModified(FileInfo fileInfo) {
        return FileInfoAction.CREATE.equals(fileInfo.getFileInfoAction())
                || FileInfoAction.DELETE.equals(fileInfo.getFileInfoAction())
                || FileInfoAction.UPDATE.equals(fileInfo.getFileInfoAction());
    }

    private TreeItem<MergedFileInfo> root;

    @FXML
    private TreeTableView<MergedFileInfo> treeFileTable;

    private TreeTableView<MergedFileInfo> treeFileTableDisp = null;


    @FXML
    private TreeTableColumn<MergedFileInfo, String> relaPath;
    @FXML
    private TreeTableColumn<MergedFileInfo, String> actionSource;
    @FXML
    private TreeTableColumn<MergedFileInfo, String> actionTarget;
    @FXML
    private TreeTableColumn<MergedFileInfo, String> origin;
    @FXML
    private TreeTableColumn<MergedFileInfo, String> dtDerUpdateSource;
    @FXML
    private TreeTableColumn<MergedFileInfo, String> dtDerUpdateTarget;
    @FXML
    private TreeTableColumn<MergedFileInfo, String> sizeSource;
    @FXML
    private TreeTableColumn<MergedFileInfo, String> sizeTarget;

    @FXML
    private CheckBox cbShowAll;

    @FXML
    private Label infoSynchro;

    @FXML
    protected void actionShowAll() throws Exception{
        log.info("actionShowAll : " + cbShowAll.isSelected());
        if (cbShowAll.isSelected()) {
            loadTree(allMergedFileInfoList);
        } else {
            loadTree(filteredMergedFileInfoList);
        }
    }

    @FXML
    protected void clickTreeFileInfo(MouseEvent event) {
        TreeTableView.TreeTableViewSelectionModel<MergedFileInfo> model = treeFileTableDisp.getSelectionModel();
        if (model!=null) {
            TreeItem<MergedFileInfo> item = model.getSelectedItem();
            if (item !=null) {
                MergedFileInfo mergedFileInfo = item.getValue();
                if (mergedFileInfo!=null) {
                    FileInfo sourceFileInfo = mergedFileInfo.getSourceFileInfo();
                    FileInfo targetFileInfo = mergedFileInfo.getTargetFileInfo();
                    logFileInfo(sourceFileInfo);
                    logFileInfo(targetFileInfo);
                }
            }
        }
    }

    private void logFileInfo(FileInfo fileInfo) {
        if (fileInfo!=null) {
            log.info("Click on " + fileInfo.getOriginFile() + " : " + fileInfo.getKey() + " -> " + fileInfo);
        }
    }

    private void loadTree(List<MergedFileInfo> res) {
        final MergedFileInfo mergedFileInfoRoot = new MergedFileInfo();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setRelativePathString("");
        mergedFileInfoRoot.setSourceFileInfo(fileInfo);
        mergedFileInfoRoot.setTargetFileInfo(fileInfo);
        mergedFileInfoRoot.initSwing();
        root = new TreeItem<>(mergedFileInfoRoot);

        Map<String, TreeItem<MergedFileInfo>> treeItemMap = new HashMap<>();
        treeItemMap.put("", root);

        res.stream().forEach((MergedFileInfo mergedFileInfo) -> {
            mergedFileInfo.initSwing();
        });
        Collections.sort(res);


        final SynchroInfo synchroInfo = new SynchroInfo();
        res.stream().filter(mergedFileInfo -> !mergedFileInfo.isDirectory()).forEach(mergedFileInfo -> {
            if (mergedFileInfo.getPath().equals("01 - Janvier/2014-01-08")) {
                log.info("Yep -> " + mergedFileInfo);
            }

            addChildToHisParent(treeItemMap, mergedFileInfo);
            synchroInfo.nbFiles++;
            try {
                if (mergedFileInfo != null && (!mergedFileInfo.getInternalActionSource().equals(FileInfoAction.NOTHING)
                        || !mergedFileInfo.getInternalActionTarget().equals(FileInfoAction.NOTHING))) {
                    synchroInfo.nbFilesToSync++;
                }
            } catch (NullPointerException e) {
                log.error(e);
            }
        });

        log.info("Tree is created");
        relaPath.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<MergedFileInfo, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getName())
        );
        actionSource.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<MergedFileInfo, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getActionSource())
        );
        actionTarget.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<MergedFileInfo, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getActionTarget())
        );
        origin.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<MergedFileInfo, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getOrigin())
        );
        sizeSource.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<MergedFileInfo, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getSourceSize())
        );
        sizeTarget.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<MergedFileInfo, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getTargetSize())
        );
        dtDerUpdateSource.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<MergedFileInfo, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getSourceLastModifiedTime())
        );
        dtDerUpdateTarget.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<MergedFileInfo, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getTargetLastModifiedTime())
        );
        ObservableList<TreeItem<MergedFileInfo>> children = root.getChildren();
        if (children.size()==1) {
            root = children.get(0);
        }
        root.getValue().setName("Root");
        root.setExpanded(true);

        AnchorPane parent = (AnchorPane) treeFileTable.getParent();
        if (treeFileTableDisp!=null) {
            parent.getChildren().remove(treeFileTableDisp);
        }

        treeFileTableDisp = new TreeTableView<>();
        treeFileTableDisp.getColumns().addAll(treeFileTable.getColumns());
        treeFileTableDisp.setRoot(root);
        treeFileTableDisp.setTableMenuButtonVisible(true);
        treeFileTableDisp.setShowRoot(false);
        treeFileTableDisp.setLayoutX(treeFileTable.getLayoutX());
        treeFileTableDisp.setLayoutY(treeFileTable.getLayoutY());

        AnchorPane.setBottomAnchor(treeFileTableDisp, AnchorPane.getBottomAnchor(treeFileTable));
        AnchorPane.setLeftAnchor(treeFileTableDisp, AnchorPane.getLeftAnchor(treeFileTable));
        AnchorPane.setRightAnchor(treeFileTableDisp, AnchorPane.getRightAnchor(treeFileTable));
        AnchorPane.setTopAnchor(treeFileTableDisp, AnchorPane.getTopAnchor(treeFileTable));
        parent.getChildren().add(treeFileTableDisp);
        treeFileTableDisp.setVisible(true);
        treeFileTableDisp.setOnMouseClicked(event -> clickTreeFileInfo(event));
        treeFileTable.setVisible(false);

        infoSynchro.setText(synchroInfo.nbFilesToSync + "/" + synchroInfo.nbFiles);
    }

    private TreeItem<MergedFileInfo> addChildToHisParent(Map<String, TreeItem<MergedFileInfo>> treeItemMap, MergedFileInfo mergedFileInfo) {
        final String parentPath = mergedFileInfo.getParentPath();
        if (parentPath.equals("01 - Janvier/2014-01-08")) {
            log.info("Yep -> " + mergedFileInfo);
        }

        TreeItem<MergedFileInfo> parent = treeItemMap.get(parentPath);
        if (parent==null) {
            final MergedFileInfo mergedFileInfoParent = createParentMergedFile(parentPath);
            parent = addChildToHisParent(treeItemMap, mergedFileInfoParent);
            parent.setExpanded(true);
            treeItemMap.put(mergedFileInfoParent.getRelativePathString(), parent);
        }
        TreeItem<MergedFileInfo> child = new TreeItem<>(mergedFileInfo);
        parent.getChildren().add(child);
        return child;
    }

    private MergedFileInfo createParentMergedFile(String parentPath) {
        final MergedFileInfo mergedFileInfoParent = new MergedFileInfo();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setRelativePathString(parentPath);
        fileInfo.setDirectory(true);
        mergedFileInfoParent.setSourceFileInfo(fileInfo);
        mergedFileInfoParent.setTargetFileInfo(fileInfo);
        mergedFileInfoParent.initSwing();
        return mergedFileInfoParent;
    }
}
