package org.fer.syncfiles.rest;

import org.fer.syncfiles.domain.ParamSyncFiles;
import org.fer.syncfiles.domain.SyncfilesSynchroMsg;
import org.fer.syncfiles.security.AuthoritiesConstants;
import org.fer.syncfiles.services.ParamSyncFilesService;
import org.fer.syncfiles.services.SyncfilesSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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


}
