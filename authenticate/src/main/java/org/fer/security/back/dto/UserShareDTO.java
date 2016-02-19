package org.fer.security.back.dto;

import java.util.List;
import java.util.Set;

/**
 * User: wilfried Date: 19/12/13 Time: 07:54
 */
public class UserShareDTO {

    private String type;
    private String ownerName;
    private String name;

    private Set<String> roles;
    private String currentUserRole;
    private List<UserRoleDTO> userRoles;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(String currentUserRole) {
        this.currentUserRole = currentUserRole;
    }

    public List<UserRoleDTO> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRoleDTO> userRoles) {
        this.userRoles = userRoles;
    }
}
