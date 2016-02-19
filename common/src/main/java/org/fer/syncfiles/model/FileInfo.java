package org.fer.syncfiles.model;

import org.apache.log4j.Logger;
import org.fer.syncfiles.bus.tree.FileValue;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Date;

@Entity
@XmlType
@Indexed
public class FileInfo implements Serializable {
	private static final Logger log = Logger.getLogger(FileInfo.class);

    @Field(analyze = Analyze.NO) @Id
	private String key;
	@Version
	private long version;

    @Field(analyze = Analyze.NO)
    private String relativePathString;

    private String treeKey;

    @Field
    private boolean isOther;
    @Field
    private boolean isDirectory;
    @Field
    private boolean isRegularFile;
    @Field
    private boolean isSymbolicLink;

    private Date creationTime;
    private Date lastAccessTime;
    @Field
    private Date lastModifiedTime;
    private long size;
    @Field(analyze = Analyze.NO)
    private String hash;

    private Date previousLastAccessTime;
    private Date previousLastModifiedTime;
    private long previousSize;
    private String previousHash;

    @Enumerated
    @Field(analyze = Analyze.NO)
    private ResultSyncAction action;
    @Enumerated
    @Field(analyze = Analyze.NO)
    private SyncState syncState;
    @Enumerated
    @Field(analyze = Analyze.NO)
    private OriginFile originFile;

    @Field(analyze = Analyze.NO)
    private String paramKey;

    @Transient
    private FileInfoAction fileInfoAction;

    public FileInfo() {
		super();
        this.syncState = SyncState.FINISHED;
	}

    public FileInfo(OriginFile originFile, FileInfo fileInfo) {
        super();
        this.originFile = originFile;
        this.relativePathString = fileInfo.relativePathString;
        this.isOther = fileInfo.isOther;
        this.isDirectory = fileInfo.isDirectory;
        this.isRegularFile = fileInfo.isRegularFile;
        this.isSymbolicLink = fileInfo.isSymbolicLink;
        this.creationTime = fileInfo.creationTime;
        this.lastAccessTime = fileInfo.lastAccessTime;
        this.lastModifiedTime = fileInfo.lastModifiedTime;
        this.size = fileInfo.size;
        this.hash = fileInfo.hash;
    }

    public FileInfo(OriginFile originFile, FileValue fileInfo) {
        super();
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

    public String getKey() {
        if (key==null) {
            initKey(getOriginFile(), getParamKey());
        }
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
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

    private void initKey(OriginFile originFile, String prefixDir) {
        key = getInternalKey(originFile, prefixDir);
    }

    private static String getInternalKey(OriginFile originFile, String prefixDir) {
        return originFile
                + ":" + prefixDir
                + ":" + KeyGenerator.generateUniqueId();
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "key='" + key + '\'' +
                ", relativePathString='" + relativePathString + '\'' +
                ", isDirectory=" + isDirectory +
                ", isRegularFile=" + isRegularFile +
                ", hash='" + hash + '\'' +
                ", paramKey='" + paramKey + '\'' +
                ", originFile=" + originFile +
                ", lastModifiedTime=" + lastModifiedTime +
                '}';
    }

    public String getTreeKey() {
        return treeKey;
    }

    public void setTreeKey(String treeKey) {
        this.treeKey = treeKey;
    }
}
