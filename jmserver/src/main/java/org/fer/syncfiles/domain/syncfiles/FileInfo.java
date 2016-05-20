package org.fer.syncfiles.domain.syncfiles;

import org.apache.log4j.Logger;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "fileInfo")
@CompoundIndexes({
    @CompoundIndex(name = "search_indexes", def = "{'paramSyncFilesId': 1, 'originFile': 1, 'relativePathString':1}")
})
public class FileInfo implements Serializable {

    @Id
	private String id;
	@Version
	private Long version;

    private String paramSyncFilesId;
    private OriginFile originFile;
    private String relativePathString;

    private boolean isOther;
    private boolean isDirectory;
    private boolean isRegularFile;
    private boolean isSymbolicLink;

    private Date creationTime;
    private Date lastAccessTime;
    private Date lastModifiedTime;
    private long size;
    @Indexed
    private String hash;

    private Date previousLastAccessTime;
    private Date previousLastModifiedTime;
    private long previousSize;
    private String previousHash;

    private ResultSyncAction action;
    private SyncState syncState;

    private String paramKey;

    private String contentType;

    private FileInfoAction fileInfoAction;
    private String lastModifiefTimeStr;

    public FileInfo() {
		super();
        this.syncState = SyncState.FINISHED;
	}

//    public FileInfo(OriginFile originFile, FileInfo fileInfo) {
//        super();
//        this.originFile = originFile;
//        this.relativePathString = fileInfo.relativePathString;
//        this.isOther = fileInfo.isOther;
//        this.isDirectory = fileInfo.isDirectory;
//        this.isRegularFile = fileInfo.isRegularFile;
//        this.isSymbolicLink = fileInfo.isSymbolicLink;
//        this.creationTime = fileInfo.creationTime;
//        this.lastAccessTime = fileInfo.lastAccessTime;
//        this.lastModifiedTime = fileInfo.lastModifiedTime;
//        this.size = fileInfo.size;
//        this.hash = fileInfo.hash;
//    }
//
    public FileInfo(String id, OriginFile originFile, FileValue fileInfo) {
        super();
        this.paramSyncFilesId = id;
        this.originFile = originFile;
        this.relativePathString = fileInfo.getRelativePathString();
        this.isOther = fileInfo.isOther();
        this.isDirectory = fileInfo.isDirectory();
        this.isRegularFile = fileInfo.isRegularFile();
        this.isSymbolicLink = fileInfo.isSymbolicLink();
        this.creationTime = new Date(fileInfo.getCreationTime());
        this.lastAccessTime = new Date(fileInfo.getLastAccessTime());
        this.lastModifiedTime = new Date(fileInfo.getLastModifiedTime());
        this.size = fileInfo.getSize();
        this.hash = fileInfo.getHash();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getRelativePathString() {
        return relativePathString;
    }

    public void setRelativePathString(String relativePathString) {
        this.relativePathString = relativePathString;
    }

    public boolean isOther() {
        return isOther;
    }

    public void setOther(boolean isOther) {
        this.isOther = isOther;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public boolean isRegularFile() {
        return isRegularFile;
    }

    public void setRegularFile(boolean isRegularFile) {
        this.isRegularFile = isRegularFile;
    }

    public boolean isSymbolicLink() {
        return isSymbolicLink;
    }

    public void setSymbolicLink(boolean isSymbolicLink) {
        this.isSymbolicLink = isSymbolicLink;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public OriginFile getOriginFile() {
        return originFile;
    }

    public void setOriginFile(OriginFile originFile) {
        this.originFile = originFile;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public Date getPreviousLastAccessTime() {
        return previousLastAccessTime;
    }

    public void setPreviousLastAccessTime(Date previousLastAccessTime) {
        this.previousLastAccessTime = previousLastAccessTime;
    }

    public Date getPreviousLastModifiedTime() {
        return previousLastModifiedTime;
    }

    public void setPreviousLastModifiedTime(Date previousLastModifiedTime) {
        this.previousLastModifiedTime = previousLastModifiedTime;
    }

    public long getPreviousSize() {
        return previousSize;
    }

    public void setPreviousSize(long previousSize) {
        this.previousSize = previousSize;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public ResultSyncAction getAction() {
        return action;
    }

    public void setAction(ResultSyncAction action) {
        this.action = action;
    }

    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(SyncState syncState) {
        this.syncState = syncState;
    }

    public FileInfoAction getFileInfoAction() {
        return fileInfoAction;
    }

    public void setFileInfoAction(FileInfoAction fileInfoAction) {
        this.fileInfoAction = fileInfoAction;
    }

    public String getParamSyncFilesId() {
        return paramSyncFilesId;
    }

    public void setParamSyncFilesId(String paramSyncFilesId) {
        this.paramSyncFilesId = paramSyncFilesId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
            "id='" + id + '\'' +
            ", version=" + version +
            ", paramSyncFilesId='" + paramSyncFilesId + '\'' +
            ", originFile=" + originFile +
            ", relativePathString='" + relativePathString + '\'' +
            ", isOther=" + isOther +
            ", isDirectory=" + isDirectory +
            ", isRegularFile=" + isRegularFile +
            ", isSymbolicLink=" + isSymbolicLink +
            ", creationTime=" + creationTime +
            ", lastAccessTime=" + lastAccessTime +
            ", lastModifiedTime=" + lastModifiedTime +
            ", size=" + size +
            ", hash='" + hash + '\'' +
            ", previousLastAccessTime=" + previousLastAccessTime +
            ", previousLastModifiedTime=" + previousLastModifiedTime +
            ", previousSize=" + previousSize +
            ", previousHash='" + previousHash + '\'' +
            ", action=" + action +
            ", syncState=" + syncState +
            ", paramKey='" + paramKey + '\'' +
            ", fileInfoAction=" + fileInfoAction +
            '}';
    }

    public void updateInfo(FileInfo fileInfoNew) {
        this.previousHash = hash;
        this.previousLastAccessTime = lastAccessTime;
        this.previousLastModifiedTime = lastModifiedTime;

        this.isOther = fileInfoNew.isOther();
        this.isDirectory = fileInfoNew.isDirectory();
        this.isRegularFile = fileInfoNew.isRegularFile();
        this.isSymbolicLink = fileInfoNew.isSymbolicLink();
        this.creationTime = fileInfoNew.getCreationTime();
        this.lastAccessTime = fileInfoNew.getLastAccessTime();
        this.lastModifiedTime = fileInfoNew.getLastModifiedTime();
        this.size = fileInfoNew.getSize();
    }

    public void setLastModifiefTimeStr(String lastModifiefTimeStr) {
        this.lastModifiefTimeStr = lastModifiefTimeStr;
    }

    public String getLastModifiefTimeStr() {
        return lastModifiefTimeStr;
    }
}
