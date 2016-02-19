package org.fer.security.back.dto;

/**
 * User: wilfried Date: 18/12/13 Time: 18:25
 */
public class UserRoleDTO {

    private String username;
    private String role;

    public UserRoleDTO() {

    }

    public UserRoleDTO(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
