package org.fer.syncfiles.services;

import org.fer.syncfiles.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Hmac user details service
 * Created by Michael DESIGAUD on 15/02/2016.
 */
@Component
public class HmacUserDetailsService implements UserDetailsService{
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return null; // TODO implements method for authentication

//        UserDTO userDTO = MockUsers.findByUsername(username);
//        if (userDTO == null) {
//            throw new UsernameNotFoundException("User "+username+" not found");
//        }
//
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        if(!userDTO.getAuthorities().isEmpty()){
//            for(String authority : userDTO.getAuthorities()){
//                authorities.add(new SimpleGrantedAuthority(authority));
//            }
//        }
//
//        return new SecurityUser(userDTO.getId(),userDTO.getLogin(),userDTO.getPassword(),userDTO.getProfile(),authorities);
    }
}
