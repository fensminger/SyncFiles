package org.fer.security.back.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * User: wilfried Date: 17/11/13 Time: 01:01
 */
@Entity
@Table(	name = "USERGROUP")
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic
    private String application;

    @Basic
    private String type;

    @Basic
    private String role;

    @ManyToOne()
    @JoinColumn(nullable=true)
    protected UserProfile owner;

    @Basic
    @JoinColumn(nullable=true)
    private String object;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="USER_GROUPS")
    private List<UserProfile> users;

    @Basic
    private Date creationTime;
    @Basic
    private Date modificationTime;

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this);
//    }

    public static Builder getBuilder(String application, String type, String role, String object, UserProfile owner) {
        return new Builder(application, type, role, object, owner);
    }

    public static class Builder {
        UserGroup built;

        Builder(String application, String type, String role, String object, UserProfile owner) {
            built = new UserGroup();
            built.application = application;
            built.type = type;
            built.role = role;
            built.object = object;
            built.owner = owner;
        }

        public UserGroup build() {
            return built;
        }
    }

    public void update(String username, String email, String password) {
        this.application = application;
        this.type = type;
        this.object = object;
        this.owner = owner;
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
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserProfile getOwner() {
        return owner;
    }

    public void setOwner(UserProfile owner) {
        this.owner = owner;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<UserProfile> getUsers() {
        return users;
    }

    public void setUsers(List<UserProfile> users) {
        this.users = users;
    }
}
