package org.fer.syncfiles.domain;

/**
 * Created by fensm on 19/12/2016.
 */
public class IncludeExcludeInfo {
    private IncludeExcludeInfoType type;
    private String value;

    public IncludeExcludeInfo(IncludeExcludeInfoType type, String value) {
        super();
        this.type = type;
        this.value = value;
    }

    public IncludeExcludeInfo() {
        super();
    }

    public IncludeExcludeInfoType getType() {
        return type;
    }

    public void setType(IncludeExcludeInfoType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
