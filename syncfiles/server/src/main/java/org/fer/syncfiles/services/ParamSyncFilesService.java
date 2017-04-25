package org.fer.syncfiles.services;

import com.mongodb.DBObject;
import org.fer.syncfiles.config.SchedulerConfig;
import org.fer.syncfiles.domain.*;
import org.fer.syncfiles.dto.ScheduleCalc;
import org.fer.syncfiles.repository.FileInfoRepository;
import org.fer.syncfiles.repository.ObjectInfoRepository;
import org.fer.syncfiles.repository.ParamSyncFilesRepository;
import org.fer.syncfiles.services.hubic.HubicService;
import org.fer.syncfiles.services.hubic.SwiftAccess;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
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

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private SchedulerConfig schedulerConfig;

    @Autowired
    private ApplicationContext appContext;

    private boolean isHubicConnected = false;

    @PostConstruct
    public void postConstruct() {
        isHubicConnected = false;
    }

    public ParamSyncFiles save(ParamSyncFiles paramSyncFiles) {
        // Calc cronExp if necessary...
        if (paramSyncFiles.getSchedule()!=null) {
            paramSyncFiles.setSchedule(calcSchedule(paramSyncFiles.getSchedule()).getSchedule());
        }

        final ParamSyncFiles paramSyncFilesSaved = paramSyncFilesRepository.save(paramSyncFiles);
        String jobId = paramSyncFilesSaved.getId();
        log.info("Info saved : " + jobId);
        JobKey jobKey = new JobKey(jobId);
        try {
            for(Trigger trigger : schedulerFactoryBean.getScheduler().getTriggersOfJob(jobKey)) {
//            for(TriggerKey trigger : schedulerFactoryBean.getScheduler().getTriggerKeys(null)) {
                boolean triggerDeleted = schedulerFactoryBean.getScheduler().unscheduleJob(trigger.getKey());
                log.info("Trigger deleted " + jobId + " : " + triggerDeleted);
            }
            boolean jobDeleted = schedulerFactoryBean.getScheduler().deleteJob(jobKey);
            log.info("Job deleted " + jobId + " : " + jobDeleted);

            Schedule schedule = paramSyncFilesSaved.getSchedule();
            if (schedule !=null && schedule.getCronExp()!=null && !Schedule.SCHEDULE_TYPE.MANUAL.equals(schedule.getType())) {
                JobDetail jobDetail = (JobDetail) appContext.getBean("syncFilesJobDetail", jobId);
                jobDetail.getJobDataMap().put("paramSyncFilesId", jobId);
                CronTrigger cronTrigger = (CronTrigger) appContext.getBean("cronJobTrigger", jobDetail, schedule.getCronExp());
                schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, cronTrigger);
            }
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return paramSyncFilesSaved;
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
        String paramSyncFilesId = syncfilesSocketHandler.addNewSynchro("Synchronize", paramSyncFiles, null);
        try {
            authenticate(paramSyncFilesId);
            updateFilesTree(paramSyncFiles);
            loadHubic(paramSyncFiles);
            simulate(paramSyncFiles);
            synchronizeToRemote(paramSyncFiles);
            loadHubicAfterSynchro(paramSyncFiles);
//            throw new RuntimeException("Ma belle erreur....");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            syncfilesSocketHandler.addError(paramSyncFilesId, "Unable to Synchronize", e);
        } finally {
            syncfilesSocketHandler.removeSynchro(paramSyncFilesId);
        }
    }

    public void listDirectory(String paramSyncFilesId, OriginFile originFile) {
        // db.fileInfo.distinct("parentPath", {originFile : "SOURCE", paramSyncFilesId : "58fa76cae339ca057ce020ca"});

        Criteria criteria = new Criteria();
        DBObject dbObject = criteria.where("originFile").is(originFile).where("paramSyncFilesId").is(paramSyncFilesId).getCriteriaObject();
        Query query = new Query(criteria);
        List<String> directoryList = mongoTemplate.getCollection("fileInfo").distinct("parentPath", dbObject);
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
                    fileInfoDir.setSyncState(SyncState.FINISHED);
                    fileInfoDir.setParamSyncFilesId(paramSyncFilesId);
                    fileInfoRepository.save(fileInfoDir);
                }
            }
        }
    }

    public void updateFilesTree(ParamSyncFiles paramSyncFiles) throws IOException {
        syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Start to analyse local files");

        fileInfoRepository.deleteByParamSyncFilesIdAndOriginFileAndFileInfoAction(paramSyncFiles.getId(), OriginFile.SOURCE, FileInfoAction.DELETE);

        mongoTemplate.updateMulti(
            query(where("paramSyncFilesId").is(paramSyncFiles.getId()))
                .addCriteria(where("originFile").is(OriginFile.SOURCE))
            , Update.update("fileInfoAction", FileInfoAction.DELETE)
            , FileInfo.class
        );

        Path prefix = Paths.get(paramSyncFiles.getMasterDir());
        SyncFileVisitor syncFileVisitor = new SyncFileVisitor(fileInfoRepository, fileUtils, paramSyncFiles
            , prefix, OriginFile.SOURCE, syncfilesSocketHandler);

        Files.walkFileTree(prefix, syncFileVisitor);
        for(FileInfo fileInfoDelete : fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndFileInfoAction(paramSyncFiles.getId(), OriginFile.SOURCE, FileInfoAction.DELETE)) {
            if (fileInfoDelete.isDirectory()) {
                fileInfoRepository.delete(fileInfoDelete);
            } else {
                syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Deleted local File : " + fileInfoDelete.getRelativePathString());
                syncfilesSocketHandler.getSyncfilesSynchroMsg(paramSyncFiles.getId()).getLocalResume().addDeletedFile();
            }
        }

        listDirectory(paramSyncFiles.getId(), OriginFile.SOURCE);

        syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Finish to analyse local files");
    }

    public SwiftAccess authenticate(String syncfilesInfoId) {
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
                    SwiftAccess swiftAccess = hubicService.authenticate(properties.getProperty("clientId")
                            , properties.getProperty("clientSecret")
                            , Integer.parseInt(properties.getProperty("port"))
                            , properties.getProperty("user")
                            , properties.getProperty("pwd")
                    );
                    isHubicConnected = true;
                    syncfilesSocketHandler.addMessage(syncfilesInfoId, "Connected to Hubic");
                    return swiftAccess;
                } catch (IOException | InterruptedException e) {
                    log.warn(e.getMessage(), e);
                    syncfilesSocketHandler.addError(syncfilesInfoId, "Unable to connect to Hubic", e);
                }
            }
        }
        return null;
    }

    public void loadHubic(ParamSyncFiles paramSyncFiles) throws IOException {
        syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Start to analyse hubic files");
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
                    syncfilesSocketHandler.getSyncfilesSynchroMsg(paramSyncFiles.getId()).getRemoteResume().addNewFile();
                    syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Hubic File that was added : " + fileInfo.getRelativePathString());
                } else {
                    if (objectInfo.getHash().equals(fileInfo.getHash())) {
                        fileInfo.setFileInfoAction(FileInfoAction.NOTHING);
                        syncfilesSocketHandler.getSyncfilesSynchroMsg(paramSyncFiles.getId()).getRemoteResume().addNothingFile();
                    } else {
                        fileInfo.setFileInfoAction(FileInfoAction.UPDATE);
                        syncfilesSocketHandler.getSyncfilesSynchroMsg(paramSyncFiles.getId()).getRemoteResume().addUpdatedFile();
                        syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Hubic File that was updated : " + fileInfo.getRelativePathString());
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
                    syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Deleted Remote File : " + fileInfoDelete.getRelativePathString());
                    syncfilesSocketHandler.getSyncfilesSynchroMsg(paramSyncFiles.getId()).getRemoteResume().addDeletedFile();
                }
            }

            syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Finish to analyse hubic files");
        } catch (IOException e) {
            syncfilesSocketHandler.addError(paramSyncFiles.getId(), "Unable to analyze Hubic files", e);
            throw e;
        }
    }

    public void loadHubicAfterSynchro(ParamSyncFiles paramSyncFiles) throws IOException {
        syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Load files from hubic after synchronisation");
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
                syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Load file from hubic : " + fileInfo.getRelativePathString());
            });

            listDirectory(paramSyncFiles.getId(), OriginFile.TARGET);

            syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Finish to load files from hubic after synchronisation");
        } catch (IOException e) {
            syncfilesSocketHandler.addError(paramSyncFiles.getId(), "Unable to load files from hubic after synchronisation", e);
            throw e;
        }
    }

    public void simulate(ParamSyncFiles paramSyncFiles) {
        syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Start analyse synchronisation to do");

        fileInfoRepository.deleteByParamSyncFilesIdAndOriginFile(paramSyncFiles.getId(), OriginFile.SYNCHRO);

        // Search local files to copy to remote

        for(FileInfo localFileInfo : fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndIsDirectory(paramSyncFiles.getId(), OriginFile.SOURCE, false)) {
            if (!localFileInfo.getFileInfoAction().equals(FileInfoAction.DELETE)) {
                Optional<FileInfo> remoteFileInfoOptional = fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndRelativePathString(paramSyncFiles.getId(), OriginFile.TARGET, localFileInfo.getRelativePathString());
                if (remoteFileInfoOptional.isPresent()) {
                    FileInfo remoteFileInfo = remoteFileInfoOptional.get();
                    if (localFileInfo.getHash().equals(remoteFileInfo.getHash())) {
                        FileInfo nothingTargetFileInfo = new FileInfo(paramSyncFiles.getId(), OriginFile.SYNCHRO, FileInfoAction.NOTHING, localFileInfo);
                        nothingTargetFileInfo.setSyncState(SyncState.FINISHED);
                        fileInfoRepository.save(nothingTargetFileInfo);
                        // syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getSynchroResume().addNothingFile();
                        syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "The file is not changed : " + nothingTargetFileInfo.getRelativePathString());
                    } else {
                        FileInfo updateTargetFileInfo = new FileInfo(paramSyncFiles.getId(), OriginFile.SYNCHRO, FileInfoAction.UPDATE, localFileInfo);
                        updateTargetFileInfo.setSyncState(SyncState.WAITING_FOR_UPDATE);
                        fileInfoRepository.save(updateTargetFileInfo);
                        syncfilesSocketHandler.getSyncfilesSynchroMsg(paramSyncFiles.getId()).getSynchroResume().addUpdatedFile();
                        syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "The file is to update : " + updateTargetFileInfo.getRelativePathString());
                    }
                } else {
                    FileInfo newTargetFileInfo = new FileInfo(paramSyncFiles.getId(), OriginFile.SYNCHRO, FileInfoAction.CREATE, localFileInfo);
                    newTargetFileInfo.setSyncState(SyncState.WAITING_FOR_UPDATE);
                    fileInfoRepository.save(newTargetFileInfo);
                    syncfilesSocketHandler.getSyncfilesSynchroMsg(paramSyncFiles.getId()).getSynchroResume().addNewFile();
                    syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "New file to copy to remote : " + newTargetFileInfo.getRelativePathString());
                }
            }
        }

        // Search remote (target) files to delete

        for(FileInfo remoteFileInfo : fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndIsDirectory(paramSyncFiles.getId(), OriginFile.TARGET, false)) {
            Optional<FileInfo> localFileInfoOptional = fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndRelativePathString(paramSyncFiles.getId(), OriginFile.SOURCE, remoteFileInfo.getRelativePathString());
            if (!localFileInfoOptional.isPresent() || localFileInfoOptional.get().getFileInfoAction().equals(FileInfoAction.DELETE)) {
                FileInfo deleteFileInfo = new FileInfo(paramSyncFiles.getId(), OriginFile.SYNCHRO, FileInfoAction.DELETE, remoteFileInfo);
                deleteFileInfo.setSyncState(SyncState.WAITING_FOR_UPDATE);
                fileInfoRepository.save(deleteFileInfo);
                syncfilesSocketHandler.getSyncfilesSynchroMsg(paramSyncFiles.getId()).getSynchroResume().addDeletedFile();
                syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Delete file to remote : " + deleteFileInfo.getRelativePathString());
            }
        }

        listDirectory(paramSyncFiles.getId(), OriginFile.SYNCHRO);

        syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Finish synchronisation simulated");
    }

    public void synchronizeToRemote(ParamSyncFiles paramSyncFiles) throws Exception {
        syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Start synchronisation.");

        for(FileInfo fileInfo : fileInfoRepository.findByParamSyncFilesIdAndOriginFile(paramSyncFiles.getId(), OriginFile.SYNCHRO)) {
            try {
                if (!fileInfo.isDirectory()) {
                    switch (fileInfo.getFileInfoAction()) {
                        case CREATE:
                            updateSyncState(fileInfo, SyncState.UPDATING);
                            File fileToUpload = new File(paramSyncFiles.getMasterDir() + "/" + fileInfo.getRelativePathString());
                            if (fileToUpload.isFile()) {
                                hubicService.uploadObject("default", paramSyncFiles.getSlaveDir() + "/" + fileInfo.getRelativePathString(), fileInfo.getHash(), fileToUpload);
                            }
                            syncfilesSocketHandler.getSyncfilesSynchroMsg(paramSyncFiles.getId()).getSynchroReal().addNewFile();
                            syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "File is created to remote : " + fileInfo.getRelativePathString());
                            updateSyncState(fileInfo, SyncState.FINISHED);
                            break;
                        case UPDATE:
                            updateSyncState(fileInfo, SyncState.UPDATING);
                            fileToUpload = new File(paramSyncFiles.getMasterDir() + "/" + fileInfo.getRelativePathString());
                            if (fileToUpload.isFile()) {
                                hubicService.uploadObject("default", paramSyncFiles.getSlaveDir() + "/" + fileInfo.getRelativePathString(), fileInfo.getHash(), fileToUpload);
                            }
                            syncfilesSocketHandler.getSyncfilesSynchroMsg(paramSyncFiles.getId()).getSynchroReal().addUpdatedFile();
                            syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "File is updated to remote : " + fileInfo.getRelativePathString());
                            updateSyncState(fileInfo, SyncState.FINISHED);
                            break;
                        case DELETE:
                            updateSyncState(fileInfo, SyncState.UPDATING);
                            hubicService.deleteObject("default", paramSyncFiles.getSlaveDir() + "/" + fileInfo.getRelativePathString());
                            syncfilesSocketHandler.getSyncfilesSynchroMsg(paramSyncFiles.getId()).getSynchroReal().addDeletedFile();
                            syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "File is deleted to remote : " + fileInfo.getRelativePathString());
                            updateSyncState(fileInfo, SyncState.FINISHED);
                            break;
                        case NOTHING:
                            // syncfilesSocketHandler.getSyncfilesSynchroMsg(syncfilesInfoId).getSynchroReal().addNothingFile();
                            syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "File is already to remote : " + fileInfo.getRelativePathString());
                        default:
                            break;
                    }

                }
            } catch (Exception e) {
                fileInfo.setSyncState(SyncState.ERROR);
                fileInfo.setErrorMessage(e.getMessage());
                fileInfoRepository.save(fileInfo);
                throw e;
            }
        }
        syncfilesSocketHandler.addMessage(paramSyncFiles.getId(), "Finish synchronisation.");
    }

    private void updateSyncState(FileInfo fileInfo, SyncState syncState) {
        fileInfo.setErrorMessage(null);
        fileInfo.setSyncState(syncState);
        fileInfoRepository.save(fileInfo);
    }

    public ScheduleCalc calcSchedule(final Schedule schedule) {
        final ScheduleCalc  scheduleCalc = schedule.calcCron();

        return scheduleCalc;
    }
}
