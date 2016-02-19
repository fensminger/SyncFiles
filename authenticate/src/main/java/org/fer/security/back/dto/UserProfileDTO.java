package org.fer.security.back.dto;


import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

public class UserProfileDTO extends DefaultDTO {


    private Long id;
    private String email;
    private String password;
    private String username;
    private Date creation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
