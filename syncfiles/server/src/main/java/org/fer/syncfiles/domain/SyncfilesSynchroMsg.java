package org.fer.syncfiles.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by fensm on 07/09/2016.
 */
public class SyncfilesSynchroMsg {
    private Long id;

    private String type;
    private String title;

    private ArrayBlockingQueue<SyncfilesDetailSynchroMsg> lastMsgList;

    private boolean running;
    private Date lastStateDate;
    private String msgError;
    private String msgErrorStackTrace;

    private SyncfilesResumeMsg localResume = new SyncfilesResumeMsg("Local files");
    private SyncfilesResumeMsg remoteResume = new SyncfilesResumeMsg("Remote files");
    private SyncfilesResumeMsg synchroResume = new SyncfilesResumeMsg("Files to Synchronize");
    private SyncfilesResumeMsg synchroReal = new SyncfilesResumeMsg("Synchronized files");
    private List<SyncfilesResumeMsg> resumeNumber = new ArrayList<>();

    public SyncfilesSynchroMsg(long id, String type, String title) {
        super();
        this.lastMsgList = new ArrayBlockingQueue<>(100);
        this.type = type;
        this.title = title;
        this.id = id;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
