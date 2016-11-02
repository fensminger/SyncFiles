package org.fer.syncfiles.repository;

import org.fer.syncfiles.domain.FileInfo;
import org.fer.syncfiles.domain.FileInfoAction;
import org.fer.syncfiles.domain.OriginFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by fensm on 02/02/2016.
 */
public interface FileInfoRepository extends MongoRepository<FileInfo, String> {
    public Optional<FileInfo> findByParamSyncFilesIdAndOriginFileAndRelativePathString(
            String paramSyncFilesId, OriginFile originFile, String relativePathString);

    public List<FileInfo> findByParamSyncFilesIdAndOriginFile(String paramSyncFilesId, OriginFile originFile);
    public List<FileInfo> findByParamSyncFilesIdAndOriginFileAndIsDirectory(String paramSyncFilesId, OriginFile originFile, boolean isDirectory);

    public List<FileInfo> findByParamSyncFilesIdAndOriginFileAndFileInfoAction(String paramSyncFilesId, OriginFile originFile, FileInfoAction fileInfoAction);
    public List<FileInfo> findByParamSyncFilesIdAndOriginFileAndFileInfoActionAndIsDirectory(String paramSyncFilesId, OriginFile originFile, FileInfoAction fileInfoAction, boolean isDirectory);

    public void deleteByParamSyncFilesIdAndOriginFile(String paramSyncFilesId, OriginFile originFile);

    public void deleteByParamSyncFilesIdAndOriginFileAndFileInfoAction(String paramSyncFilesId, OriginFile originFile, FileInfoAction fileInfoAction);

}
