package org.fer.security.back.dto;

/**
 * User: wilfried Date: 25/11/13 Time: 08:04
 */
public class UserChangePwdDTO extends UserLoginDTO {

    private String OldPassword;

    public String getOldPassword() {
        return OldPassword;
    }

    public void setOldPassword(String oldPassword) {
        OldPassword = oldPassword;
    }

}
