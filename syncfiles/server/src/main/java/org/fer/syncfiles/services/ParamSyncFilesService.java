package org.fer.syncfiles.services;

import org.fer.syncfiles.domain.*;
import org.fer.syncfiles.repository.FileInfoRepository;
import org.fer.syncfiles.repository.ObjectInfoRepository;
import org.fer.syncfiles.repository.ParamSyncFilesRepository;
import org.fer.syncfiles.services.hubic.HubicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by fensm on 04/02/2016.
 */
@Service
public class ParamSyncFilesService {
    private static final Logger log = LoggerFactory.getLogger(ParamSyncFilesService.class);

    @Autowired
    private ParamSyncFilesRepository paramSyncFilesRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;
    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ObjectInfoRepository objectInfoRepository;

    @Autowired
    private HubicService hubicService;

    @Autowired
    @Qualifier("syncfilesSocketHandler")
    private SyncfilesSocketHandler syncfilesSocketHandler;

    private boolean isHubicConnected = false;

    @PostConstruct
    public void postConstruct() {
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

    @Async("threadPoolTaskExecutor")
    public void synchronize(ParamSyncFiles paramSyncFiles) {
        Long syncfilesInfoId = syncfilesSocketHandler.addNewSynchro("Synchronize", paramSyncFiles.getJobName(), null);
        try {
            authenticate(syncfilesInfoId);
            updateFilesTree(paramSyncFiles, syncfilesInfoId);
            loadHubic(paramSyncFiles, syncfilesInfoId);
            simulate(paramSyncFiles, syncfilesInfoId);
            synchronizeToRemote(paramSyncFiles, syncfilesInfoId);
            loadHubicAfterSynchro(paramSyncFiles, syncfilesInfoId);
//            throw new RuntimeException("Ma belle erreur....");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            syncfilesSocketHandler.addError(syncfilesInfoId, "Unable to Synchronize", e);
        } finally {
            syncfilesSocketHandler.removeSynchro(syncfilesInfoId);
        }
    }

    public void listDirectory(String paramSyncFilesId, OriginFile originFile) {
        // db.fileInfo.distinct("parentPath", { "originFile": "TARGET" })

        Criteria criteria = new Criteria();
        criteria.where("originFile").is(originFile);
        Query query = new Query(criteria);
        List<String> directoryList = mongoTemplate.getCollection("fileInfo").distinct("parentPath", query.getQueryObject());
        for(String directory : directoryList) {
            if (!directory.equals("/")) {
                String relativePathString = directory.substring(1, directory.length()-1);
                if (!fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndRelativePathString(paramSyncFilesId, originFile, relativePathString).isPresent()) {
                    FileInfo fileInfoDir = new FileInfo();
                    fileInfoDir.setContentType("application/directory");
                    fileInfoDir.setHash(relativePathString);
                    fileInfoDir.setRelativePathString(relativePathString);
                    fileInfoDir.setDirectory(true);
                    fileInfoDir.setOriginFile(originFile);
                    fileInfoDir.setFileInfoAction(FileInfoAction.NOTHING);
                    fileInfoDir.setParamSyncFilesId(paramSyncFilesId);
                    fileInfoRepository.save(fileInfoDir);
                }
            }
        }
    }

    public void updateFilesTree(ParamSyncFiles paramSyncFiles, Long syncfilesInfoId) throws IOException {
        syncfilesSocketHandler.addMessage(syncfilesInfoId, "Start to analyse local files");

        fileInfoRepository.deleteByParamSyncFilesIdAndOriginFileAndFileInfoAction(paramSyncFiles.getId(), OriginFile.SOURCE, FileInfoAction.DELETE);

        mongoTemplate.updateMulti(
            query(where("paramSyncFilesId").is(paramSyncFiles.getId()))
                .addCriteria(where("originFile").is(OriginFile.SOURCE))
            , Update.update("fileInfoAction", FileInfoAction.DELETE)
            , FileInfo.class
        );

        Path prefix = Paths.get(paramSyncFiles.getMasterDir());
        SyncFileVisitor syncFileVisitor = new SyncFileVisitor(fileInfoRepository, fileUtils, paramSyncFiles
            , prefix, OriginFile.SOURCE, syncfilesInfoId, syncfilesSocketHandler);

        Files.walkFileTree(prefix, syncFileVisitor);
        for(FileInfo fileInfoDelete : fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndFileInfoAction(paramSyncFiles.getId(), OriginFile.SOURCE, FileInfoAction.DELETE)) {
            if (fileInfoDelete.isDirectory()) {
                fileInfoRepository.delete(fileInfoDelete);
            } else {
                syncfilesSocketHandler.addMessage(syncfilesInfoId, "Deleted local File : " + fileInfoDelete.getRelativePathString());
                syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getLocalResume().addDeletedFile();
            }
        }

        listDirectory(paramSyncFiles.getId(), OriginFile.SOURCE);

        syncfilesSocketHandler.addMessage(syncfilesInfoId, "Finish to analyse local files");
    }

    public void authenticate(Long syncfilesInfoId) {
        if (!isHubicConnected) {
            Properties properties = new Properties();
            try {
                properties.load(this.getClass().getResourceAsStream("/hubic.properties"));
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
                    syncfilesSocketHandler.addMessage(syncfilesInfoId, "Connected to Hubic");
                } catch (IOException | InterruptedException e) {
                    log.warn(e.getMessage(), e);
                    syncfilesSocketHandler.addError(syncfilesInfoId, "Unable to connect to Hubic", e);
                }
            }
        }
    }

    public void loadHubic(ParamSyncFiles paramSyncFiles, Long syncfilesInfoId) throws IOException {
        syncfilesSocketHandler.addMessage(syncfilesInfoId, "Start to analyse hubic files");
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
                    syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getRemoteResume().addNewFile();
                    syncfilesSocketHandler.addMessage(syncfilesInfoId, "Hubic File that was added : " + fileInfo.getRelativePathString());
                } else {
                    if (objectInfo.getHash().equals(fileInfo.getHash())) {
                        fileInfo.setFileInfoAction(FileInfoAction.NOTHING);
                        syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getRemoteResume().addNothingFile();
                    } else {
                        fileInfo.setFileInfoAction(FileInfoAction.UPDATE);
                        syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getRemoteResume().addUpdatedFile();
                        syncfilesSocketHandler.addMessage(syncfilesInfoId, "Hubic File that was updated : " + fileInfo.getRelativePathString());
                    }
                }
                fileInfo.setParamSyncFilesId(paramSyncFiles.getId());
                fileInfo.setContentType(objectInfo.getContentType());
                fileInfo.setHash(objectInfo.getHash());
                fileInfo.setDirectory(objectInfo.isDirectory());
                //fileInfo.setLastModifiedTime(objectInfo.getLastModified());
                fileInfo.setLastModifiefTimeStr(objectInfo.getLastModified());
                fileInfo.setRelativePathString(objectInfo.getName());
                fileInfo.setOriginFile(OriginFile.TARGET);

                fileInfoRepository.save(fileInfo);
            });

            for(FileInfo fileInfoDelete : fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndFileInfoAction(paramSyncFiles.getId(), OriginFile.TARGET, FileInfoAction.DELETE)) {
                if (fileInfoDelete.isDirectory()) {
                    fileInfoRepository.delete(fileInfoDelete);
                } else {
                    syncfilesSocketHandler.addMessage(syncfilesInfoId, "Deleted Remote File : " + fileInfoDelete.getRelativePathString());
                    syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getRemoteResume().addDeletedFile();
                }
            }

            syncfilesSocketHandler.addMessage(syncfilesInfoId, "Finish to analyse hubic files");
        } catch (IOException e) {
            syncfilesSocketHandler.addError(syncfilesInfoId, "Unable to analyze Hubic files", e);
            throw e;
        }
    }

    public void loadHubicAfterSynchro(ParamSyncFiles paramSyncFiles, Long syncfilesInfoId) throws IOException {
        syncfilesSocketHandler.addMessage(syncfilesInfoId, "Load files from hubic after synchronisation");
        try {

            fileInfoRepository.deleteByParamSyncFilesIdAndOriginFile(paramSyncFiles.getId(), OriginFile.TARGET);

            hubicService.consumeObjects("default", paramSyncFiles.getSlaveDir(), objectInfo -> {
                FileInfo fileInfo = new FileInfo();

                fileInfo.setParamSyncFilesId(paramSyncFiles.getId());
                fileInfo.setContentType(objectInfo.getContentType());
                fileInfo.setHash(objectInfo.getHash());
                fileInfo.setDirectory(objectInfo.isDirectory());
                //fileInfo.setLastModifiedTime(objectInfo.getLastModified());
                fileInfo.setLastModifiefTimeStr(objectInfo.getLastModified());
                fileInfo.setRelativePathString(objectInfo.getName());
                fileInfo.setOriginFile(OriginFile.TARGET);

                fileInfoRepository.save(fileInfo);
                syncfilesSocketHandler.addMessage(syncfilesInfoId, "Load file from hubic : " + fileInfo.getRelativePathString());
            });

            listDirectory(paramSyncFiles.getId(), OriginFile.TARGET);

            syncfilesSocketHandler.addMessage(syncfilesInfoId, "Finish to load files from hubic after synchronisation");
        } catch (IOException e) {
            syncfilesSocketHandler.addError(syncfilesInfoId, "Unable to load files from hubic after synchronisation", e);
            throw e;
        }
    }

    public void simulate(ParamSyncFiles paramSyncFiles, Long syncfilesInfoId) {
        syncfilesSocketHandler.addMessage(syncfilesInfoId, "Start analyse synchronisation to do");

        fileInfoRepository.deleteByParamSyncFilesIdAndOriginFile(paramSyncFiles.getId(), OriginFile.SYNCHRO);

        // Search local files to copy to remote

        for(FileInfo localFileInfo : fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndIsDirectory(paramSyncFiles.getId(), OriginFile.SOURCE, false)) {
            Optional<FileInfo> remoteFileInfoOptional = fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndRelativePathString(paramSyncFiles.getId(), OriginFile.TARGET, localFileInfo.getRelativePathString());
            if (remoteFileInfoOptional.isPresent()) {
                FileInfo remoteFileInfo = remoteFileInfoOptional.get();
                if (localFileInfo.getHash().equals(remoteFileInfo.getHash())) {
                    FileInfo nothingTargetFileInfo = new FileInfo(paramSyncFiles.getId(), OriginFile.SYNCHRO, FileInfoAction.NOTHING, localFileInfo);
                    fileInfoRepository.save(nothingTargetFileInfo);
                    // syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getSynchroResume().addNothingFile();
                    syncfilesSocketHandler.addMessage(syncfilesInfoId, "The file is not changed : " + nothingTargetFileInfo.getRelativePathString());
                } else {
                    FileInfo updateTargetFileInfo = new FileInfo(paramSyncFiles.getId(), OriginFile.SYNCHRO, FileInfoAction.UPDATE, localFileInfo);
                    fileInfoRepository.save(updateTargetFileInfo);
                    syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getSynchroResume().addUpdatedFile();
                    syncfilesSocketHandler.addMessage(syncfilesInfoId, "The file is to update : " + updateTargetFileInfo.getRelativePathString());
                }
            } else {
                FileInfo newTargetFileInfo = new FileInfo(paramSyncFiles.getId(), OriginFile.SYNCHRO, FileInfoAction.CREATE, localFileInfo);
                fileInfoRepository.save(newTargetFileInfo);
                syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getSynchroResume().addNewFile();
                syncfilesSocketHandler.addMessage(syncfilesInfoId, "New file to copy to remote : " + newTargetFileInfo.getRelativePathString());
            }
        }

        // Search remote (target) files to delete

        for(FileInfo remoteFileInfo : fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndIsDirectory(paramSyncFiles.getId(), OriginFile.TARGET, false)) {
            Optional<FileInfo> localFileInfoOptional = fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndRelativePathString(paramSyncFiles.getId(), OriginFile.SOURCE, remoteFileInfo.getRelativePathString());
            if (!localFileInfoOptional.isPresent() || localFileInfoOptional.get().getFileInfoAction().equals(FileInfoAction.DELETE)) {
                FileInfo deleteFileInfo = new FileInfo(paramSyncFiles.getId(), OriginFile.SYNCHRO, FileInfoAction.DELETE, remoteFileInfo);
                fileInfoRepository.save(deleteFileInfo);
                syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getSynchroResume().addDeletedFile();
                syncfilesSocketHandler.addMessage(syncfilesInfoId, "Delete file to remote : " + deleteFileInfo.getRelativePathString());
            }
        }

        listDirectory(paramSyncFiles.getId(), OriginFile.SYNCHRO);

        syncfilesSocketHandler.addMessage(syncfilesInfoId, "Finish analyse synchronisation to do");
    }

    public void synchronizeToRemote(ParamSyncFiles paramSyncFiles, Long syncfilesInfoId) throws Exception {
        syncfilesSocketHandler.addMessage(syncfilesInfoId, "Start synchronisation.");

        for(FileInfo fileInfo : fileInfoRepository.findByParamSyncFilesIdAndOriginFile(paramSyncFiles.getId(), OriginFile.SYNCHRO)) {
            try {
                if (!fileInfo.isDirectory()) {
                    switch (fileInfo.getFileInfoAction()) {
                        case CREATE:
                            File fileToUpload = new File(paramSyncFiles.getMasterDir() + "/" + fileInfo.getRelativePathString());
                            if (fileToUpload.isFile()) {
                                hubicService.uploadObject("default", paramSyncFiles.getSlaveDir() + "/" + fileInfo.getRelativePathString(), fileInfo.getHash(), fileToUpload);
                            }
                            syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getSynchroReal().addNewFile();
                            syncfilesSocketHandler.addMessage(syncfilesInfoId, "File is created to remote : " + fileInfo.getRelativePathString());
                            break;
                        case UPDATE:
                            fileToUpload = new File(paramSyncFiles.getMasterDir() + "/" + fileInfo.getRelativePathString());
                            if (fileToUpload.isFile()) {
                                hubicService.uploadObject("default", paramSyncFiles.getSlaveDir() + "/" + fileInfo.getRelativePathString(), fileInfo.getHash(), fileToUpload);
                            }
                            syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getSynchroReal().addUpdatedFile();
                            syncfilesSocketHandler.addMessage(syncfilesInfoId, "File is updated to remote : " + fileInfo.getRelativePathString());
                            break;
                        case DELETE:
                            hubicService.deleteObject("default", paramSyncFiles.getSlaveDir() + "/" + fileInfo.getRelativePathString());
                            syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getSynchroReal().addDeletedFile();
                            syncfilesSocketHandler.addMessage(syncfilesInfoId, "File is deleted to remote : " + fileInfo.getRelativePathString());
                            break;
                        case NOTHING:
                            // syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getSynchroReal().addNothingFile();
                            syncfilesSocketHandler.addMessage(syncfilesInfoId, "File is already to remote : " + fileInfo.getRelativePathString());
                        default:
                            break;
                    }

                    fileInfo.setErrorMessage(null);
                    fileInfo.setSyncState(SyncState.FINISHED);
                    fileInfoRepository.save(fileInfo);
                }
            } catch (Exception e) {
                fileInfo.setSyncState(SyncState.ERROR);
                fileInfo.setErrorMessage(e.getMessage());
                fileInfoRepository.save(fileInfo);
                throw e;
            }
        }
        syncfilesSocketHandler.addMessage(syncfilesInfoId, "Finish synchronisation.");
    }
}
