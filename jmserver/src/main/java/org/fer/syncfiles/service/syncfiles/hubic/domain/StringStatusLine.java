package org.fer.syncfiles.service.syncfiles.hubic.domain;

import org.apache.http.StatusLine;

/**
 * Created by fensm on 27/02/2016.
 */
public class StringStatusLine {
    private String content;
    private StatusLine statusLine;

    public StringStatusLine(String content, StatusLine statusLine) {
        this.content = content;
        this.statusLine = statusLine;
    }

    public String getContent() {
        return content;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }
}
