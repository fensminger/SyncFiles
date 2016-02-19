package org.fer.security.back.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * User: wilfried Date: 25/11/13 Time: 08:08
 */
public class UserDetailsDTO extends User {

    String email = null;
    Long id = null;


    public UserDetailsDTO(String username, String password, String email, Long id, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        setEmail(email);
        setId(id);
    }

    public UserDetailsDTO(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // TODO: This is a pb: This DTO Should NOT inherit from Spring User
    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
