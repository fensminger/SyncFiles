package org.fer.syncfiles.web.rest.syncfiles;

import org.fer.syncfiles.domain.syncfiles.ParamSyncFiles;
import org.fer.syncfiles.security.AuthoritiesConstants;
import org.fer.syncfiles.service.syncfiles.ParamSyncFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
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

    @RequestMapping(value = "/param/save",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public @ResponseBody ParamSyncFiles saveParamSyncFiles(@RequestBody ParamSyncFiles paramSyncFiles) throws IOException {
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
    public @ResponseBody ParamSyncFiles findParamSyncFilesById(@PathVariable String id) throws IOException {
        return paramSyncFilesService.findParamSyncFilesById(id);
    }

    @RequestMapping(value = "/param/remove/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public void deleteParamSyncFiles(@PathVariable String id) throws IOException {
        paramSyncFilesService.deleteParamSyncFiles(id);
    }

    @RequestMapping(value = "/files/load/{idParamSyncFiles}",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public void loadFiles(@PathVariable String idParamSyncFiles) throws IOException {
        ParamSyncFiles paramSyncFiles = paramSyncFilesService.findParamSyncFilesById(idParamSyncFiles);
        paramSyncFilesService.updateFilesTree(paramSyncFiles);
    }

    @RequestMapping(value = "/hubic/load/{idParamSyncFiles}",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public void loadHubic(@PathVariable String idParamSyncFiles) throws IOException {
        ParamSyncFiles paramSyncFiles = paramSyncFilesService.findParamSyncFilesById(idParamSyncFiles);
        paramSyncFilesService.authenticate();
        paramSyncFilesService.loadHubic(paramSyncFiles);
    }
}
