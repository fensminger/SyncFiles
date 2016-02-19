package org.fer.syncfiles.model;

import java.util.List;

/**
 * Created by fer on 22/10/2014.
 */
public class MergedFileInfoList {
    private List<MergedFileInfoJson> mergedFileInfoList;


    public MergedFileInfoList() {
        super();
    }

    public MergedFileInfoList(List<MergedFileInfoJson> mergedFileInfoList) {
        this.mergedFileInfoList = mergedFileInfoList;
    }

    public List<MergedFileInfoJson> getMergedFileInfoList() {
        return mergedFileInfoList;
    }

    public void setMergedFileInfoList(List<MergedFileInfoJson> mergedFileInfoList) {
        this.mergedFileInfoList = mergedFileInfoList;
    }
}
