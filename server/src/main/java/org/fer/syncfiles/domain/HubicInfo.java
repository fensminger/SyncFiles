package org.fer.syncfiles.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fensm on 24/02/2016.
 */
@Document(collection = "hubicInfo")
//@CompoundIndexes({
//    @CompoundIndex(name = "search_indexes", def = "{'name': 1}")
//})
public class HubicInfo {
    private String id;

    private long size;
    private String contentType;
    private boolean directory;
    private String hashMd5;
    private Date lastModified;
    private String container;
    private String name;
    private long contentLength;

    public HubicInfo() {
        super();
    }

    public HubicInfo(long size, String contentType, String hash, Date lastModified, String name) {
        this.size = size;
        this.contentType = contentType;
        this.hashMd5 = hash;
        this.lastModified = lastModified;
        this.name = name;
    }

    public HubicInfo(ObjectInfo objectInfo) {
        this.size = objectInfo.getSize();
        this.contentType = objectInfo.getContentType();
        this.hashMd5 = objectInfo.getHash();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        if (objectInfo.getLastModified()!=null) {
            try {
                this.lastModified = sdf.parse(objectInfo.getLastModified());
            } catch (ParseException e) {
                Logger log = LoggerFactory.getLogger(HubicInfo.class);
                log.warn(e.getMessage(), e);
            }
        }
        this.name = objectInfo.getName();
        this.directory = objectInfo.isDirectory();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getHashMd5() {
        return hashMd5;
    }

    public void setHashMd5(String hashMd5) {
        this.hashMd5 = hashMd5;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }
}
