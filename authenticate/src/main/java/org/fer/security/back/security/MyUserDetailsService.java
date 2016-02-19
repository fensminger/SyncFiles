package org.fer.security.back.security;

import org.fer.security.back.model.UserProfile;
import org.fer.security.back.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * User: wilfried Date: 23/11/13 Time: 18:58
 */

public class MyUserDetailsService  implements UserDetailsService {

    @Resource
    //@Autowired
    private UserProfileService userProfileService;

    @Autowired
    private MyUserDetailsAuthorityMapper myUserDetailsAuthorityMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDetails ud = null;

        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        // TODO: plug a grnated autority mapper to link user groups with user role for this app (later)

        UserProfile up = userProfileService.findByUsername(username);
        if (null != up) {
//            // ROLE_USER is by default if exists ;-)
//            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//            if ("ROLE_ADMIN".equals(up.getRole())) {
//                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//            }

            authorities = myUserDetailsAuthorityMapper.mapAuthority(up.getGroups());

            ud =  new User(up.getUsername(), up.getPassword(), authorities);


        }

        return ud;
    }

    public void setMyUserDetailsAuthorityMapper(MyUserDetailsAuthorityMapper myUserDetailsAuthorityMapper) {
        this.myUserDetailsAuthorityMapper = myUserDetailsAuthorityMapper;
    }

    public void setUserProfileService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
}