package org.fer.syncfiles.service;

import org.fer.syncfiles.dto.SynchroContext;
import org.fer.syncfiles.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by fer on 10/10/2014.
 */
@Service
public class RestTemplateService {
    @Autowired
    private Environment environment;

    @Autowired
    private RestTemplate restTemplate;

    public Boolean startHubicSynchro(Param param, int selectedIndex, boolean isSimulation) throws IOException {
        if (selectedIndex>=0) {
            return restTemplate.getForObject(getRootUrl()+"prepSynchro/"+param.getKey()+"/"+isSimulation, Boolean.class);
        } else {
            return true;
        }
    }

    public Boolean startUploadHubicSynchro(Param param, int selectedIndex, boolean isSimulation) {
        if (selectedIndex>=0) {
            return restTemplate.getForObject(getRootUrl() + "doSynchro/" + param.getKey() + "/" + isSimulation, Boolean.class);
        } else {
            return true;
        }

    }

    public Boolean startPrepUploadHubicSynchro(Param param, int selectedIndex, boolean isSimulation) {
        if (selectedIndex>=0) {
            return restTemplate.getForObject(getRootUrl() + "doPrepSynchro/" + param.getKey() + "/" + isSimulation, Boolean.class);
        } else {
            return true;
        }
    }

    public boolean startSynchronizeHubic(Param param, boolean hubicCheck, boolean isSimulation) {
        return restTemplate.getForObject(getRootUrl() + "startSynchronizeHubic/" + param.getKey() + "/" + hubicCheck + "/" + isSimulation, Boolean.class);
    }


    public boolean quitServer() {
        return restTemplate.getForObject(getRootUrl() + "quitServer", Boolean.class);
    }

    public Boolean startLoadHubicFiles() {
        return restTemplate.getForObject(getRootUrl() + "loadHubicFiles", Boolean.class);
    }

    public List<MergedFileInfoJson> loadMergedFileFromParam(String paramKey) {
        return restTemplate.getForObject(getRootUrl() + "loadMergedFileFromParam/" + paramKey, MergedFileInfoList.class).getMergedFileInfoList();
    }

    private String getRootUrl() {
        return "http://" + environment.getProperty("nas.server") + ":" + environment.getProperty("nas.port") + "/rest/syncfiles/";
    }

    public Param save(Param param) {
        return restTemplate.postForObject(getRootUrl() + "saveParam", param, Param.class);
    }

    public void delete(Param paramToDelete) {
        restTemplate.delete(getRootUrl()+"deleteParam/" + paramToDelete.getKey());
    }

    public ParamList findAll() {
//        try {
//            restTemplate.setErrorHandler(new ResponseErrorHandler() {
//                @Override
//                public boolean hasError(ClientHttpResponse response) throws IOException {
//                    return false;
//                }
//
//                @Override
//                public void handleError(ClientHttpResponse response) throws IOException {
//                    return;
//                }
//            });
//            String res = restTemplate.getForObject("http://192.168.1.60:9999", String.class);
            return restTemplate.getForObject(getRootUrl()+"loadAllParam", ParamList.class);
//        } catch (Throwable e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public Param load(Param param) {
        return findOne(param.getKey());
    }

    public Param findOne(String key) {
        return restTemplate.getForObject(getRootUrl()+"loadParam/" + key, Param.class);
    }

    public void loadAndUpdateFromDb(SynchroContext synchroContext) {
        throw new RuntimeException("Not Implemented");
    }

    public void updateFileMap(Param param, Map<String, FileInfo> localFilesMap) {
        throw new RuntimeException("Not Implemented");
    }

    public List<FileInfo> loadNewFileToUpload(Param param) {
        throw new RuntimeException("Not Implemented");
    }

    public List<FileInfo> loadUpdatedFileToUpload(Param param) {
        throw new RuntimeException("Not Implemented");
    }

    public List<FileInfo> loadRemovedFileToDelete(Param param) {
        throw new RuntimeException("Not Implemented");
    }

    public void updateFileInfo(FileInfo fileInfo, SyncState syncState, ResultSyncAction resultSyncAction) {
        throw new RuntimeException("Not Implemented");
    }

}
