package org.fer.syncfiles.dto;

/**
 * Created by fensm on 07/04/2017.
 */
public class RestoreInfo {
    String idParamSyncFiles;
    String remoteHubicPath;
    String localPath;

    public RestoreInfo() {
        super();
    }

    public RestoreInfo(String idParamSyncFiles, String remoteHubicPath, String localPath) {
        this();
        this.idParamSyncFiles = idParamSyncFiles;
        this.remoteHubicPath = remoteHubicPath;
        this.localPath = localPath;
    }

    public String getIdParamSyncFiles() {
        return idParamSyncFiles;
    }

    public void setIdParamSyncFiles(String idParamSyncFiles) {
        this.idParamSyncFiles = idParamSyncFiles;
    }

    public String getRemoteHubicPath() {
        return remoteHubicPath;
    }

    public void setRemoteHubicPath(String remoteHubicPath) {
        this.remoteHubicPath = remoteHubicPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
