package org.fer.syncfiles.domain;

/**
 * Created by fensm on 15/10/2016.
 */
public class SyncfilesResumeMsg {
    private String title;
    private long numberOfFiles = 0;
    private long numberOfNewFiles = 0;
    private long numberOfUpdatedFiles = 0;
    private long numberOfDeletedFiles = 0;

    public SyncfilesResumeMsg(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addNothingFile() {
        numberOfFiles++;
    }

    public void addNewFile() {
        numberOfNewFiles++;
        numberOfFiles++;
    }

    public void addUpdatedFile() {
        numberOfUpdatedFiles++;
        numberOfFiles++;
    }

    public void addDeletedFile() {
        numberOfDeletedFiles++;
        numberOfFiles++;
    }

    public long getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(long numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public long getNumberOfNewFiles() {
        return numberOfNewFiles;
    }

    public void setNumberOfNewFiles(long numberOfNewFiles) {
        this.numberOfNewFiles = numberOfNewFiles;
    }

    public long getNumberOfUpdatedFiles() {
        return numberOfUpdatedFiles;
    }

    public void setNumberOfUpdatedFiles(long numberOfUpdatedFiles) {
        this.numberOfUpdatedFiles = numberOfUpdatedFiles;
    }

    public long getNumberOfDeletedFiles() {
        return numberOfDeletedFiles;
    }

    public void setNumberOfDeletedFiles(long numberOfDeletedFiles) {
        this.numberOfDeletedFiles = numberOfDeletedFiles;
    }
}
