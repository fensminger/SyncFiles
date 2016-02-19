package org.fer.security.back.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * User: wilfried Date: 17/11/13 Time: 01:01
 */


@Entity
@Table(	name = "USER")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Basic
    @Column(unique=true)
    private String email;
    @Basic
    @Column(unique=true)
    private String username;
    @Basic
    private String password;

    // TODO: define fetch strategy according to some use cases only .-)


    @ManyToMany(mappedBy="users", fetch = FetchType.EAGER)
    private List<UserGroup> groups;


    @Basic
    private Date creationTime;

    @Basic
    private Date modificationTime;

    @Basic
    private Date credentialChangeTime;   // This will be used

    @Basic
    private Date blockingTime;   // This will be used

    @Basic
    private Integer authenticationRetriesNumber;

    @Basic
    private String status;


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static class Builder {
        UserProfile built;

        Builder(String username, String email, String password) {
            built = new UserProfile();
            built.username = username;
            built.email = email;
            built.password = password;
            //built.groups = groups;
        }

        public UserProfile build() {
            return built;
        }
    }

    public void update(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @PreUpdate
    public void preUpdate() {
        modificationTime = new Date();
    }

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        creationTime = now;
        modificationTime = now;
        credentialChangeTime = now;
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



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static Builder getBuilder(String username, String email, String password) {
        return new Builder(username, email, password);
    }

    public List<UserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;
    }

    public Date getCredentialChangeTime() {
        return credentialChangeTime;
    }

    public void setCredentialChangeTime(Date credentialChangeTime) {
        this.credentialChangeTime = credentialChangeTime;
    }

    public Date getBlockingTime() {
        return blockingTime;
    }

    public void setBlockingTime(Date blockingTime) {
        this.blockingTime = blockingTime;
    }

    public Integer getAuthenticationRetriesNumber() {
        return authenticationRetriesNumber;
    }

    public void setAuthenticationRetriesNumber(Integer authenticationRetriesNumber) {
        this.authenticationRetriesNumber = authenticationRetriesNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
