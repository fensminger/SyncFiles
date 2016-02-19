package org.fer.security.back.security;

import org.fer.security.back.model.UserGroup;
import org.fer.security.back.service.ReqAppsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: wilfried Date: 11/12/13 Time: 18:36
 */
public class MyUserDetailsAuthorityMapper {

    public Set<GrantedAuthority> mapAuthority(List<UserGroup> groups) {

        Set<GrantedAuthority> result = new HashSet<GrantedAuthority>();

        for (UserGroup userGroup: groups) {

            if (ReqAppsService.AppName.equals(userGroup.getApplication())) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(convert(userGroup));
                result.add(authority);
            }
        }
        return result;

    }

    private String convert(UserGroup userGroup) {

       if ("Project".equals(userGroup.getType())) {
           return "ROLE_" + userGroup.getRole().toUpperCase() + "_PROJECT_" + userGroup.getObject();
       }
       else if ("Application".equals(userGroup.getType())) {
           return "ROLE_" + userGroup.getRole().toUpperCase();
       }
       else return "ROLE_UNKNOWN...";
    }


}
