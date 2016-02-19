package org.fer.syncfiles.dto;

import org.fer.syncfiles.model.FileInfo;
import org.fer.syncfiles.model.Param;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fer on 02/10/2014.
 */
public class SynchroContext {
    private Param param;

    private Map<String, FileInfo> sourceMap;
    private Map<String, FileInfo> destMap;

    public SynchroContext() {
        super();
    }

    public Map<String, FileInfo> getSourceMap() {
        return sourceMap;
    }

    public void setSourceMap(Map<String, FileInfo> sourceMap) {
        this.sourceMap = sourceMap;
    }

    public Map<String, FileInfo> getDestMap() {
        return destMap;
    }

    public void setDestMap(Map<String, FileInfo> destMap) {
        this.destMap = destMap;
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }
}
