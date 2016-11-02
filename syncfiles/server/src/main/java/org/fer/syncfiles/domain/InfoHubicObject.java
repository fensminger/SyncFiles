package org.fer.syncfiles.domain;

/**
 * Created by fensm on 27/02/2016.
 */
public class InfoHubicObject {
    private String name;
    private Long size;

    public InfoHubicObject() {
        super();
    }

    public InfoHubicObject(String name, Long size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "InfoHubicObject{" +
            "name='" + name + '\'' +
            ", size=" + size +
            '}';
    }
}
