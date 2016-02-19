package org.fer.security.back.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

/**
 * User: wilfried Date: 16/12/13 Time: 08:27
 */
public class SecurityService {

    @Autowired
    private ShaPasswordEncoder passwordEncoder;

    //TODO: set in config files
    private String saltNumber = "cookieNumber1";

    public String buildToken1(String username, String ip) {
        return passwordEncoder.encodePassword("username:"+ username + ",ip:" + ip , saltNumber);
    }

    // TODO: for token2, take into account last Logout time ... this shall enable a global logout functionality ...

    public String buildToken2(String username, String password) {
        return passwordEncoder.encodePassword("username:"+ username + ",pwd:" + password , saltNumber);
    }

    public String buildToken3(String username, String ip, String password, Long validityTimeStamp, Long passwordChangeTimeStamp) {
        return passwordEncoder.encodePassword("username:"+ username + ", pwd:" + password + ", ip:" + ip + ", validityTimeStamp:" + validityTimeStamp + ", passwordChangeTimeStamp:" + passwordChangeTimeStamp, saltNumber);
    }


    public String buildLostPasswordToken (String username, String ip, String password, Long validityTimeStamp, Long passwordChangeTimeStamp) {
        String token1 = buildToken1(username, ip);
        String token3 = buildToken3(username, ip, password,validityTimeStamp, passwordChangeTimeStamp);
        String result = username + ":" + validityTimeStamp + ":" + token1 + ":" + token3;
        return result;
    }

    public String encodePassword(String rawPassword, String salt) {
        return passwordEncoder.encodePassword(rawPassword, salt) ;
    }

}
