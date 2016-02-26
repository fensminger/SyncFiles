package org.fer.syncfiles.service.syncfiles.hubic.domain;

import java.util.Date;

/**
 * Created by fensm on 24/02/2016.
 */
public class ObjectInfo {

    private long size;
    private String contentType;
    private String hash;
    private String lastModified;
    private String name;

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
