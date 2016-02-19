package org.fer.security.back.dto;

/**
 * User: wilfried Date: 25/11/13 Time: 08:04
 */
public class UserLoginDTO {

    private String login;
    private String password;
    private Boolean stayConnected;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getStayConnected() {
        return stayConnected;
    }

    public void setStayConnected(Boolean stayConnected) {
        this.stayConnected = stayConnected;
    }
}
