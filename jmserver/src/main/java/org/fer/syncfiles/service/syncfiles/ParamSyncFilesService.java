package org.fer.syncfiles.service.syncfiles;

import org.fer.syncfiles.domain.syncfiles.*;
import org.fer.syncfiles.repository.syncfiles.FileInfoRepository;
import org.fer.syncfiles.repository.syncfiles.ObjectInfoRepository;
import org.fer.syncfiles.repository.syncfiles.ParamSyncFilesRepository;
import org.fer.syncfiles.service.syncfiles.hubic.HubicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import static org.springframework.data.mongodb.core.query.Query.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * Created by fensm on 04/02/2016.
 */
@Service
public class ParamSyncFilesService {
    private static final Logger log = LoggerFactory.getLogger(ParamSyncFilesService.class);

    @Inject
    private ParamSyncFilesRepository paramSyncFilesRepository;

    @Inject
    private FileInfoRepository fileInfoRepository;
    @Inject
    private FileUtils fileUtils;

    @Inject
    private MongoTemplate mongoTemplate;

    @Inject
    private ObjectInfoRepository objectInfoRepository;

    private HubicService hubicService;
    private boolean isHubicConnected = false;

    @PostConstruct
    public void postConstruct() {
        hubicService = new HubicService();
        isHubicConnected = false;
    }

    public ParamSyncFiles save(ParamSyncFiles paramSyncFiles) {
        return paramSyncFilesRepository.save(paramSyncFiles);
    }

    public void deleteParamSyncFiles(ParamSyncFiles paramSyncFiles) {
        paramSyncFilesRepository.delete(paramSyncFiles);
    }

    public List<ParamSyncFiles> findParamSyncFilesAll() {
        return paramSyncFilesRepository.findAll();
    }

    public ParamSyncFiles findParamSyncFilesByName(String name) {
        return paramSyncFilesRepository.findByName(name);
    }

    public ParamSyncFiles findParamSyncFilesById(String id) {
        return paramSyncFilesRepository.findOne(id);
    }

    public void deleteParamSyncFiles(String id) {
        paramSyncFilesRepository.delete(id);
    }

    public List<FileInfo> findFileInfoByParamSyncFilesIdAndOriginFile(String paramSyncFilesId, OriginFile originFile) {
        //return fileInfoRepository.findAll();
        return fileInfoRepository.findByParamSyncFilesIdAndOriginFile(paramSyncFilesId, originFile);
    }


    public void updateFilesTree(ParamSyncFiles paramSyncFiles) throws IOException {
        mongoTemplate.updateMulti(
            query(where("paramSyncFilesId").is(paramSyncFiles.getId()))
                .addCriteria(where("originFile").is(OriginFile.SOURCE))
            , Update.update("fileInfoAction", FileInfoAction.DELETE)
            , FileInfo.class
        );

        Path prefix = Paths.get(paramSyncFiles.getMasterDir());
        SyncFileVisitor syncFileVisitor = new SyncFileVisitor(fileInfoRepository, fileUtils, paramSyncFiles
            , prefix, OriginFile.SOURCE);

        Files.walkFileTree(prefix, syncFileVisitor);
    }

    public void authenticate() {
        if (!isHubicConnected) {
            Properties properties = new Properties();
            try {
                properties.load(this.getClass().getResourceAsStream("/config/hubicAuthenticate.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            int nbRetry = 0;
            while (!isHubicConnected) {
                try {
                    Thread.sleep(nbRetry*60*1000);
                } catch (InterruptedException e) {
                    log.warn(e.getMessage(), e);
                }
                nbRetry++;
                try {
                    hubicService.authenticate(properties.getProperty("clientId")
                        , properties.getProperty("clientSecret")
                        , Integer.parseInt(properties.getProperty("port"))
                        , properties.getProperty("user")
                        , properties.getProperty("pwd")
                    );
                    isHubicConnected = true;
                } catch (IOException | InterruptedException e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }

    public void loadHubic(ParamSyncFiles paramSyncFiles) throws IOException {
        try {

            mongoTemplate.updateMulti(
                query(where("paramSyncFilesId").is(paramSyncFiles.getId()))
                    .addCriteria(where("originFile").is(OriginFile.TARGET))
                , Update.update("fileInfoAction", FileInfoAction.DELETE)
                , FileInfo.class
            );

            hubicService.consumeObjects("default", paramSyncFiles.getSlaveDir(), objectInfo -> {
                FileInfo fileInfo = fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndRelativePathString(
                    paramSyncFiles.getId(), OriginFile.TARGET, objectInfo.getName()).orElseGet(() -> new FileInfo());

                if (fileInfo.getId()==null) {
                    fileInfo.setFileInfoAction(FileInfoAction.CREATE);
                } else {
                    if (objectInfo.getHash().equals(fileInfo.getHash())) {
                        fileInfo.setFileInfoAction(FileInfoAction.NOTHING);
                    } else {
                        fileInfo.setFileInfoAction(FileInfoAction.UPDATE);
                    }
                }
                fileInfo.setParamSyncFilesId(paramSyncFiles.getId());
                fileInfo.setContentType(objectInfo.getContentType());
                fileInfo.setHash(objectInfo.getHash());
                fileInfo.setDirectory(false);
                //fileInfo.setLastModifiedTime(objectInfo.getLastModified());
                fileInfo.setLastModifiefTimeStr(objectInfo.getLastModified());
                fileInfo.setRelativePathString(objectInfo.getName());
                fileInfo.setOriginFile(OriginFile.TARGET);

                objectInfo.setParamSyncFilesId(paramSyncFiles.getId());
                objectInfoRepository.save(objectInfo);
            });
        } catch (IOException e) {
//            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
