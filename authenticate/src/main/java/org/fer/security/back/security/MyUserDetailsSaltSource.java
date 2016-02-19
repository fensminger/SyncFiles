package org.fer.security.back.security;

import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User: wilfried Date: 01/12/13 Time: 13:18
 */
public class MyUserDetailsSaltSource implements SaltSource{

    @Override
    public Object getSalt(UserDetails user) {
        return user.getUsername();
    }
}
