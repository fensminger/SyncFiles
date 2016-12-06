package org.fer.syncfiles.rest;

import org.fer.syncfiles.domain.*;
import org.fer.syncfiles.dto.FileInfoPage;
import org.fer.syncfiles.security.AuthoritiesConstants;
import org.fer.syncfiles.services.InfosFilesService;
import org.fer.syncfiles.services.ParamSyncFilesService;
import org.fer.syncfiles.services.SyncfilesSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fensm on 16/03/2016.
 */
@RestController
@RequestMapping("/api/sync-files")
public class ParamSyncFilesController {

    @Autowired
    private ParamSyncFilesService paramSyncFilesService;

    @Autowired
    @Qualifier("syncfilesSocketHandler")
    private SyncfilesSocketHandler syncfilesSocketHandler;

    @Autowired
    private InfosFilesService infosFilesService;

    @RequestMapping(value = "/param/save",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public @ResponseBody
    ParamSyncFiles saveParamSyncFiles(@RequestBody ParamSyncFiles paramSyncFiles) throws IOException {
        return paramSyncFilesService.save(paramSyncFiles);
    }

    @RequestMapping(value = "/param/load-all",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public @ResponseBody
    List<ParamSyncFiles> findParamSyncFilesAll() throws IOException {
        return paramSyncFilesService.findParamSyncFilesAll();
    }

    @RequestMapping(value = "/param/load-one/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public @ResponseBody
    ParamSyncFiles findParamSyncFilesById(@PathVariable String id) throws IOException {
        return paramSyncFilesService.findParamSyncFilesById(id);
    }

    @RequestMapping(value = "/param/remove/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public void deleteParamSyncFiles(@PathVariable String id) throws IOException {
        paramSyncFilesService.deleteParamSyncFiles(id);
    }

    @RequestMapping(value = "/synchronize/{idParamSyncFiles}",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public void synchronize(@PathVariable String idParamSyncFiles) throws IOException {
        ParamSyncFiles paramSyncFiles = paramSyncFilesService.findParamSyncFilesById(idParamSyncFiles);
        paramSyncFilesService.synchronize(paramSyncFiles);
    }


    @RequestMapping(value = "/param/load-one-msg/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public @ResponseBody SyncfilesSynchroMsg findMsgParamSyncFilesById(@PathVariable String id) throws IOException {
        return syncfilesSocketHandler.findMsgByParamSyncFilesById(id);
    }

    @RequestMapping(value = "/view/list/{id}/{originFile}/{pageNumber}/{pageSize}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public @ResponseBody FileInfoPage viewList(
            @PathVariable String id
            , @PathVariable OriginFile originFile
            , @PathVariable int pageNumber
            , @PathVariable int pageSize

            , @RequestParam(required = false) String filterName
            , @RequestParam(required = false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) Date startDate
            , @RequestParam(required = false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) Date endDate
            , @RequestParam(required = false) String fileInfoActionsStr
            , @RequestParam(required = false) String syncStateStr
        ) throws IOException {

        if (fileInfoActionsStr==null || syncStateStr==null) {
            return new FileInfoPage(1, pageSize, 0, 0, null);
        }

        final List<FileInfoAction> fileInfoActionList = getFileInfoActions(fileInfoActionsStr);

        final List<SyncState> syncStateList = getSyncStates(syncStateStr);

        return infosFilesService.loadFileInfo(id, originFile, pageNumber, pageSize,
                filterName, startDate, endDate, fileInfoActionList, syncStateList);
    }

    private List<FileInfoAction> getFileInfoActions(String fileInfoActionsStr) {
        if (fileInfoActionsStr==null) {
            return null;
        }

        final List<FileInfoAction> fileInfoActionList = new ArrayList<>();
        String[] fileInfoActionStrList = fileInfoActionsStr.split(",");
        for(String fileInfoActionStr : fileInfoActionStrList) {
            fileInfoActionList.add(FileInfoAction.valueOf(fileInfoActionStr));
        }
        return fileInfoActionList;
    }

    private List<SyncState> getSyncStates(String syncStateStr) {
        if (syncStateStr==null) {
            return null;
        }

        final List<SyncState> syncStateList = new ArrayList<>();
        String[] syncStateStrList = syncStateStr.split(",");
        for(String syncState : syncStateStrList) {
            syncStateList.add(SyncState.valueOf(syncState));
        }
        return syncStateList;
    }

}
