package org.fer.syncfiles.domain;

/**
 * Created by fensm on 19/12/2016.
 */
public class IncludeExcludeInfo {
    public enum INCLUDE_EXCLUDE_INFO {START, END, CONTAIN, REGEXP};

    private INCLUDE_EXCLUDE_INFO type;
    private String value;

    public IncludeExcludeInfo(INCLUDE_EXCLUDE_INFO type, String value) {
        super();
        this.type = type;
        this.value = value;
    }

    public IncludeExcludeInfo() {
        super();
    }

    public INCLUDE_EXCLUDE_INFO getType() {
        return type;
    }

    public void setType(INCLUDE_EXCLUDE_INFO type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
