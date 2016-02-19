package org.fer.syncfiles.model;

import java.util.List;

/**
 * Created by fer on 05/04/2015.
 */
public class DirFileList {
    String dir;
    List<MergedFileInfoJson> mergedFileInfoJsonList;

    public DirFileList() {
        super();
    }

    public DirFileList(String dir, List<MergedFileInfoJson> mergedFileInfoJsonList) {
        this.dir = dir;
        this.mergedFileInfoJsonList = mergedFileInfoJsonList;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public List<MergedFileInfoJson> getMergedFileInfoJsonList() {
        return mergedFileInfoJsonList;
    }

    public void setMergedFileInfoJsonList(List<MergedFileInfoJson> mergedFileInfoJsonList) {
        this.mergedFileInfoJsonList = mergedFileInfoJsonList;
    }
}
