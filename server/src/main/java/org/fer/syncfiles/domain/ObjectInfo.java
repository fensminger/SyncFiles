package org.fer.syncfiles.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;

/**
 * Created by fensm on 24/02/2016.
 */
@Document(collection = "objectInfo")
@CompoundIndexes({
    @CompoundIndex(name = "search_indexes", def = "{'paramSyncFilesId': 1}")
})
public class ObjectInfo {

    private String id;
    private String paramSyncFilesId;
    private long size;

    private String contentType;
    private boolean directory;
    private String hash;
    private String lastModified;
    private String name;

    public ObjectInfo() {
        super();
    }



    //        "bytes": 3231182,
    //        "content_type": "application/octet-stream",
    //        "hash": "35c38a8ead88fbd5a0e756642716af21",
    //        "last_modified": "2014-01-02T18:24:58.532960",
    //        "name": "ff3afa83-f159-4cef-8cc9-6c13b82eecbb/1388686123/108088782/00000001"
    public ObjectInfo(long size, String contentType, String hash, String lastModified, String name) {
        this.size = size;
        this.contentType = contentType;
        this.hash = hash;
        this.lastModified = lastModified;
        this.name = name;
    }

    public ObjectInfo(HubicInfo hubicInfo, String prefix) {
        this.size = hubicInfo.getSize();
        this.contentType = hubicInfo.getContentType();
        this.hash = hubicInfo.getHashMd5();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.lastModified = sdf.format(hubicInfo.getLastModified());
        if (prefix==null) {
            this.name = hubicInfo.getName();
        } else {
            this.name = hubicInfo.getName().substring(prefix.length() + 1);
        }
        this.directory = hubicInfo.isDirectory();
    }

    public ObjectInfo(long size, String contentType, boolean isDirectory, String hash, String lastModified, String name) {
        this.size = size;
        this.contentType = contentType;
        this.directory = isDirectory;
        this.hash = hash;
        this.lastModified = lastModified;
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public String getContentType() {
        return contentType;
    }

    public String getHash() {
        return hash;
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParamSyncFilesId() {
        return paramSyncFilesId;
    }

    public void setParamSyncFilesId(String paramSyncFilesId) {
        this.paramSyncFilesId = paramSyncFilesId;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return "ObjectInfo{" +
            "size=" + size +
            ", contentType='" + contentType + '\'' +
            ", hash='" + hash + '\'' +
            ", lastModified='" + lastModified + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
