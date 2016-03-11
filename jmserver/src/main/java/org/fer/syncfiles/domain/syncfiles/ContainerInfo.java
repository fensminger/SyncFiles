package org.fer.syncfiles.domain.syncfiles;

/**
 * Created by fensm on 23/02/2016.
 */
public class ContainerInfo {
    private String nameValue;
    private Long countValue;
    private Long sizeValue;

    public ContainerInfo(String nameValue, Long countValue, Long sizeValue) {
        this.nameValue = nameValue;
        this.countValue = countValue;
        this.sizeValue = sizeValue;
    }

    public String getNameValue() {
        return nameValue;
    }

    public Long getCountValue() {
        return countValue;
    }

    public Long getSizeValue() {
        return sizeValue;
    }

    @Override
    public String toString() {
        return "ContainerInfo{" +
            "nameValue='" + nameValue + '\'' +
            ", countValue=" + countValue +
            ", sizeValue=" + sizeValue +
            '}';
    }
}
