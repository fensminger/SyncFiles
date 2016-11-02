package org.fer.syncfiles.domain.syncfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fensm on 07/09/2016.
 */
public class MsgDownloadUpload {
    private List<String> lastMsgList;

    private boolean running;
    private Date lastStateDate;
    private String msgError;
    private String msgErrorStackTrace;

    private String currentFile;
    private Long countFiles;
    private Long totalNbOfFiles;

    public MsgDownloadUpload() {
        super();
        this.countFiles = 0L;
        this.totalNbOfFiles = 0L;
        this.lastMsgList = new ArrayList<>();
    }

    public List<String> getLastMsgList() {
        return lastMsgList;
    }

    public void setLastMsgList(List<String> lastMsgList) {
        this.lastMsgList = lastMsgList;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getMsgError() {
        return msgError;
    }

    public void setMsgError(String msgError) {
        this.msgError = msgError;
    }

    public String getMsgErrorStackTrace() {
        return msgErrorStackTrace;
    }

    public void setMsgErrorStackTrace(String msgErrorStackTrace) {
        this.msgErrorStackTrace = msgErrorStackTrace;
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
    }

    public Long getCountFiles() {
        return countFiles;
    }

    public void setCountFiles(Long countFiles) {
        this.countFiles = countFiles;
    }

    public Long getTotalNbOfFiles() {
        return totalNbOfFiles;
    }

    public void setTotalNbOfFiles(Long totalNbOfFiles) {
        this.totalNbOfFiles = totalNbOfFiles;
    }

    public Date getLastStateDate() {
        return lastStateDate;
    }

    public void setLastStateDate(Date lastStateDate) {
        this.lastStateDate = lastStateDate;
    }
}
