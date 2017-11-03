package org.fer.syncfiles.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by fensm on 07/09/2016.
 */
@Document(collection = "syncfilesSynchroMsg")
public class SyncfilesSynchroMsg {
    @Id
    private String id;
    @Version
    private Long version;

    private String type;
    private ParamSyncFiles paramSyncFiles;
    private String paramSyncFilesId;

    private MsgLinkedBlockingQueue<SyncfilesDetailSynchroMsg> lastMsgList;

    private boolean running;
    private boolean simulation;
    private Date startDate;
    private Date lastStateDate;
    private String msgError;
    private String msgErrorStackTrace;

    private SyncfilesResumeMsg localResume = new SyncfilesResumeMsg("Local files",0);
    private SyncfilesResumeMsg remoteResume = new SyncfilesResumeMsg("Remote files",1);
    private SyncfilesResumeMsg synchroResume = new SyncfilesResumeMsg("Files to Synchronize",2);
    private SyncfilesResumeMsg synchroReal = new SyncfilesResumeMsg("Synchronized files",3);
    private List<SyncfilesResumeMsg> resumeNumber = new ArrayList<>();
    private boolean changed;

    public SyncfilesSynchroMsg() {
        super();
    }

    public SyncfilesSynchroMsg(String type, ParamSyncFiles paramSyncFiles) {
        super();
        this.lastMsgList = new MsgLinkedBlockingQueue<>();
        this.type = type;
        this.paramSyncFiles = paramSyncFiles;
        this.paramSyncFilesId = paramSyncFiles.getId();
        this.resumeNumber.add(localResume);
        this.resumeNumber.add(remoteResume);
        this.resumeNumber.add(synchroResume);
        this.resumeNumber.add(synchroReal);
    }

    public Queue<SyncfilesDetailSynchroMsg> getLastMsgList() {
        return lastMsgList;
    }

    public void addMessage(String msg) {
        if (lastMsgList.remainingCapacity()==0) {
            lastMsgList.poll();
        }
        lastMsgList.add(new SyncfilesDetailSynchroMsg(msg));
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

    public Date getLastStateDate() {
        return lastStateDate;
    }

    public void setLastStateDate(Date lastStateDate) {
        this.lastStateDate = lastStateDate;
    }

    public String getId() {
        return paramSyncFiles.getId();
    }

    public String getTitle() {
        return paramSyncFiles.getJobName();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ParamSyncFiles getParamSyncFiles() {
        return paramSyncFiles;
    }

    public void setParamSyncFiles(ParamSyncFiles paramSyncFiles) {
        this.paramSyncFiles = paramSyncFiles;
    }

    public SyncfilesResumeMsg getLocalResume() {
        return localResume;
    }

    public void setLocalResume(SyncfilesResumeMsg localResume) {
        this.localResume = localResume;
    }

    public SyncfilesResumeMsg getRemoteResume() {
        return remoteResume;
    }

    public void setRemoteResume(SyncfilesResumeMsg remoteResume) {
        this.remoteResume = remoteResume;
    }

    public SyncfilesResumeMsg getSynchroResume() {
        return synchroResume;
    }

    public void setSynchroResume(SyncfilesResumeMsg synchroResume) {
        this.synchroResume = synchroResume;
    }

    public List<SyncfilesResumeMsg> getResumeNumber() {
        return resumeNumber;
    }

    public void setResumeNumber(List<SyncfilesResumeMsg> resumeNumber) {
        this.resumeNumber = resumeNumber;
    }

    public SyncfilesResumeMsg getSynchroReal() {
        return synchroReal;
    }

    public void setSynchroReal(SyncfilesResumeMsg synchroReal) {
        this.synchroReal = synchroReal;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return changed;
    }

    public String getParamSyncFilesId() {
        return paramSyncFilesId;
    }

    public void setParamSyncFilesId(String paramSyncFilesId) {
        this.paramSyncFilesId = paramSyncFilesId;
    }

    public boolean isSimulation() {
        return simulation;
    }

    public void setSimulation(boolean simulation) {
        this.simulation = simulation;
    }
}
