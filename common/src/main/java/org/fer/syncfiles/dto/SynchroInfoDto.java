package org.fer.syncfiles.dto;

/**
 * Created by fer on 22/04/2015.
 */
public class SynchroInfoDto {
    private String code;
    private String description;


    public SynchroInfoDto() {
        super();
    }

    public SynchroInfoDto(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public SynchroInfoDto(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynchroInfoDto)) return false;

        SynchroInfoDto that = (SynchroInfoDto) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        return !(description != null ? !description.equals(that.description) : that.description != null);

    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SynchroInfoDto{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
