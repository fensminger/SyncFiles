package org.fer.syncfiles.domain;

import java.util.Date;

/**
 * Created by fensm on 14/10/2016.
 */
public class SyncfilesDetailSynchroMsg {
    private Date date;
    private String msg;

    public SyncfilesDetailSynchroMsg(String msg) {
        this.date = new Date();
        this.msg = msg;
    }

    public Date getDate() {
        return date;
    }

    public String getMsg() {
        return msg;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
