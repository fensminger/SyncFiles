package org.fer.syncfiles.service.syncfiles;

import org.fer.syncfiles.domain.syncfiles.FileInfo;
import org.fer.syncfiles.domain.syncfiles.FileInfoAction;
import org.fer.syncfiles.domain.syncfiles.OriginFile;
import org.fer.syncfiles.domain.syncfiles.ParamSyncFiles;
import org.fer.syncfiles.repository.syncfiles.FileInfoRepository;
import org.fer.syncfiles.repository.syncfiles.ParamSyncFilesRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import static org.springframework.data.mongodb.core.query.Query.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by fensm on 04/02/2016.
 */
@Service
public class ParamSyncFilesService {
    @Inject
    private ParamSyncFilesRepository paramSyncFilesRepository;

    @Inject
    private FileInfoRepository fileInfoRepository;
    @Inject
    private FileUtils fileUtils;

    @Inject
    private MongoTemplate mongoTemplate;


    public ParamSyncFiles save(ParamSyncFiles paramSyncFiles) {
        return paramSyncFilesRepository.save(paramSyncFiles);
    }

    public void delete(ParamSyncFiles paramSyncFiles) {
        paramSyncFilesRepository.delete(paramSyncFiles);
    }

    public List<ParamSyncFiles> findAll() {
        return paramSyncFilesRepository.findAll();
    }

    public ParamSyncFiles findByName(String name) {
        return paramSyncFilesRepository.findByName(name);
    }

    public ParamSyncFiles findOne(String id) {
        return paramSyncFilesRepository.findOne(id);
    }

    public void delete(String id) {
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
}
