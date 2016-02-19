package org.fer.syncfiles.web.rest;

import org.fer.syncfiles.dto.SynchroInfoDto;
import org.fer.syncfiles.model.*;
import org.fer.syncfiles.service.AsyncSynchroService;
import org.fer.syncfiles.service.FeedbackEvent;
import org.fer.syncfiles.service.ParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
public class SyncFilesServerController {

    @Autowired
    ParamService paramService;

    @Autowired
    FeedbackEvent feedbackEvent;

//    @Autowired
//    WebSocketConfig webSocketConfig;
//    private ServletServerContainerFactoryBean container;

    @Autowired
    AsyncSynchroService asyncSynchroService;

    @RequestMapping(value = "/rest/syncfiles/prepSynchro/{paramName}/{isSimulation}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public boolean startHubicSynchro(@PathVariable("paramName") String paramName, @PathVariable("isSimulation") boolean isSimulation) throws IOException {
        Param param = paramService.findOne(paramName);
        return asyncSynchroService.startPrepareSynchro(param, isSimulation);
    }

    @RequestMapping(value = "/rest/syncfiles/doPrepSynchro/{paramName}/{isSimulation}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Boolean startPrepUploadHubicSynchro(@PathVariable("paramName") String paramName, @PathVariable("isSimulation") boolean isSimulation) {
        Param param = paramService.findOne(paramName);
        return asyncSynchroService.startPrepUploadHubicSynchro(param, isSimulation);
    }

    // Lancement de la synchronisation
    @RequestMapping(value = "/rest/syncfiles/doSynchro/{paramName}/{isSimulation}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Boolean startUploadHubicSynchro(@PathVariable("paramName") String paramName, @PathVariable("isSimulation") boolean isSimulation) {
        Param param = paramService.findOne(paramName);
        return asyncSynchroService.startUploadHubicSynchro(param, isSimulation);
    }

    // Préparation de la synchronisation
    @RequestMapping(value = "/rest/syncfiles/startSynchronizeHubic/{paramName}/{hubicCheck}/{isSimulation}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public boolean startSynchronizeHubic(@PathVariable("paramName") String paramName, @PathVariable("hubicCheck") boolean hubicCheck, @PathVariable("isSimulation") boolean isSimulation) {
        Param param = paramService.findOne(paramName);
        return asyncSynchroService.startSynchronizeHubic(param, hubicCheck, isSimulation);
    }

    @RequestMapping(value = "/rest/syncfiles/quitServer", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Boolean quitServer() {
        paramService.quitApp();
        return true;
    }

    @RequestMapping(value = "/rest/syncfiles/loadHubicFiles", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Boolean loadHubicFiles() {
        return asyncSynchroService.startPrepareHubicFiles();
    }

    @RequestMapping(value = "/rest/syncfiles/saveParam", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Param saveParam(@RequestBody Param param) {
        return paramService.save(param);
    }

//    @RequestMapping(value = "/rest/syncfiles/deleteParam/{paramName}", method = RequestMethod.DELETE, produces = "application/json")
//    @ResponseBody
//    public void deleteParam(@PathVariable("paramName") String paramName) {
//        final Param paramToDelete = new Param();
//        paramToDelete.setName(paramName);
//        paramService.delete(paramToDelete);
//    }

    @RequestMapping(value = "/rest/syncfiles/loadAllParam", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ParamList findAllParam() {
        return paramService.findAll();
    }


    @RequestMapping(value = "/rest/syncfiles/loadParam/{key}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Param findOneParam(@PathVariable("key") String key) {
        return paramService.findOne(key);
    }

    @RequestMapping(value = "/rest/syncfiles/newParam", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public void newParam() {
        Param param = new Param();
        param.setKey(null);
        param.setName("Nouveau paramétrage à spécialiser");
        paramService.save(param);
    }

    @RequestMapping(value = "/rest/syncfiles/copyParam/{key}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public void copyParam(@PathVariable("key") String key) {
        Param param = paramService.findOne(key);
        param.setKey(null);
        param.setName("Copie de " + param.getName());
        paramService.save(param);
    }

    @RequestMapping(value = "/rest/syncfiles/deleteParam/{key}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public void deleteParam(@PathVariable("key") String key) {
        paramService.delete(key);
    }

    @RequestMapping(value = "/rest/syncfiles/loadMergedFileFromParam/{key}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public MergedFileInfoList loadMergedFileFromParam(@PathVariable("key") String key) {
        try {
            paramService.printHubicCache("./hubicPrevisualise.txt", key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new MergedFileInfoList(paramService.loadMergedFileFromParam(key));
    }


    @RequestMapping(value = "/rest/syncfiles/loadSearchMergedFileFromParam/{key}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public MergedFileInfoList loadSearchMergedFileFromParam(@PathVariable("key") String key
            , @RequestParam("pathNode") String pathNode
            , @RequestParam("nameFilter") String nameFilter
            , @RequestParam("fileToSync") boolean fileToSync) {
        return new MergedFileInfoList(paramService.loadMergedFileFromParam(key, pathNode, nameFilter, fileToSync));
    }


    @RequestMapping(value = "/rest/syncfiles/loadMergedFileViewFromParam/{key}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public MapTreeResult loadMergedFileViewFromParam(@PathVariable("key") String key, @RequestParam("path") String path) {
//        try {
//            paramService.printHubicCache("./hubicPrevisualise.txt", key);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        return paramService.loadMergedFileViewFromParam(key, path);
    }

    @RequestMapping(value = "/rest/syncfiles/loadFlatMergedFileFromParam/{key}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<MergedFileInfoJson> loadFlatMergedFileFromParam(@PathVariable("key") String key, @RequestParam("path") String path) {
        if (path==null) {
            path = "/";
        }
        return paramService.loadFlatMergedFileFromParam(key, path);
    }

    @RequestMapping(value = "/rest/syncfiles/loadCurrentEvents", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Collection<SynchroInfoDto> loadCurrentEvents() {
        return feedbackEvent.getCurrentEvents();
    }
}
