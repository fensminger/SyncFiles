package org.fer.syncfiles.dto;

/**
 * Created by fensm on 03/10/2016.
 */
public class InfoFileSynchro {
    private long nbFilesToCreate = 0;
    private long nbFilesToUpdate = 0;
    private long nbFilesOk = 0;

    public InfoFileSynchro() {
        super();
    }

    public long getTotalFiles() {
        return nbFilesOk+nbFilesToCreate+nbFilesToUpdate;
    }

    public void addNbFilesToCreate() {
        nbFilesToCreate++;
    }

    public void addNbFilesToUpdate() {
        nbFilesToUpdate++;
    }

    public void addNbFilesToOk() {
        nbFilesOk++;
    }

    public long getNbFilesToCreate() {
        return nbFilesToCreate;
    }

    public void setNbFilesToCreate(long nbFilesToCreate) {
        this.nbFilesToCreate = nbFilesToCreate;
    }

    public long getNbFilesToUpdate() {
        return nbFilesToUpdate;
    }

    public void setNbFilesToUpdate(long nbFilesToUpdate) {
        this.nbFilesToUpdate = nbFilesToUpdate;
    }

    public long getNbFilesOk() {
        return nbFilesOk;
    }

    public void setNbFilesOk(long nbFilesOk) {
        this.nbFilesOk = nbFilesOk;
    }
}
