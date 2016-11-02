package org.fer.syncfiles.security;

import org.fer.syncfiles.dto.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;

/**
 * Security spring security user
 * Created by Michael DESIGAUD on 15/02/2016.
 */
public class SecurityUser extends User{

    private Integer id;

    private Profile profile;

    public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public SecurityUser(Integer id,String username, String password, Profile profile, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.profile = profile;
    }

    public Integer getId() {
        return id;
    }

    public Profile getProfile() {
        return profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityUser)) return false;
        if (!super.equals(o)) return false;
        SecurityUser that = (SecurityUser) o;
        return Objects.equals(id, that.id) &&
                profile == that.profile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, profile);
    }
}
