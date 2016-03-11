package org.fer.syncfiles.domain.syncfiles;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by fensm on 25/02/2016.
 */
@Document(collection = "hubicDetailObject")
@CompoundIndexes({
    @CompoundIndex(name = "search_indexes", def = "{'paramSyncFilesId': 1}")
})
public class ObjectDetailInfo {

    private String id;
    private String paramSyncFilesId;

    private String contentType;
    private long contentLength;
    private Date lastModified;
    private String hashMd5;
    private Date deleteAt;
    private Boolean manifest;
    private String manifestPrefix;

    public ObjectDetailInfo() {
        super();
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setHashMd5(String hashMd5) {
        this.hashMd5 = hashMd5;
    }

    public String getHashMd5() {
        return hashMd5;
    }

    public void setDeleteAt(Date deleteAt) {
        this.deleteAt = deleteAt;
    }

    public Date getDeleteAt() {
        return deleteAt;
    }

    public void setManifest(Boolean manifest) {
        this.manifest = manifest;
    }

    public Boolean getManifest() {
        return manifest;
    }

    public void setManifestPrefix(String manifestPrefix) {
        this.manifestPrefix = manifestPrefix;
    }

    public String getManifestPrefix() {
        return manifestPrefix;
    }

    @Override
    public String toString() {
        return "ObjectDetailInfo{" +
            "contentType='" + contentType + '\'' +
            ", contentLength=" + contentLength +
            ", lastModified=" + lastModified +
            ", hashMd5='" + hashMd5 + '\'' +
            ", deleteAt=" + deleteAt +
            ", manifest=" + manifest +
            ", manifestPrefix='" + manifestPrefix + '\'' +
            '}';
    }
}
