package org.fer.security.back.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.Cookie;

/**
 * This is a naive Cookie implementation. In a true enviorment, one would need 
 * to create a more secure cookie that cannot be spoofed. Use SHA, Salts what have you.
 * For the scope of this example, it will have to do though.
 */
public class CookieService {
  static final String COOKIE_NAME = "AUTHCOOKIE";

    @Autowired
    SecurityService securityService;


//    @Autowired
//    private ShaPasswordEncoder passwordEncoder;
//
//    private String saltNumber = "cookieNumber1";

//  public String extractUserName(Cookie[] cookies);
//  public Cookie getCookie(String cookieName, Cookie[] cookies);
//  public Cookie createCookie(String userDetails);
//
//  public static class Impl implements CookieService {
    
    //@Override
    public String[] extractInfo(Cookie[] cookies) {
      Cookie cookie = getCookie(COOKIE_NAME, cookies);

      String infos[] = null;
      if (null != cookie && null != cookie.getValue()) {
          String[] tokens = cookie.getValue().split(":");
          if (tokens.length == 3) {
              infos = tokens;
          }
      }
      return infos;
    }

//    public String extractUserName(Cookie[] cookies) {
//        Cookie cookie = getCookie(COOKIE_NAME, cookies);
//        return cookie == null ? null : parseCookieString(cookie.getValue()).getUsername();
//    }


    //@Override
    public Cookie createCookie(String username, String password, String ip) {
      Cookie c = new Cookie(COOKIE_NAME, buildCookieString(username, password, ip));
      c.setPath("/");
      return c;
    }

    private String buildCookieString(String username, String rawpassword, String ip) {

        String password = securityService.encodePassword(rawpassword, username);

        String token1 = securityService.buildToken1(username, ip);
        String token2 = securityService.buildToken2(username, password);
        String result = username + ":" + token1 + ":" + token2;

        return result;
    }

    private String buildCookieString(UserDetails userDetails, String ip) {
        String username = userDetails.getUsername();
        String password = userDetails.getPassword();

        String token1 = securityService.buildToken1(username, ip);
        String token2 = securityService.buildToken2(username, password);
        String result = username + ":" + token1 + ":" + token2;

        return result;
    }





//    private UserDetails parseCookieString (String value) {
//        UserDetails result = (value != null ? new User(value, "****", new HashSet<GrantedAuthority>()): null);
//        return result;
//    }

    public Cookie getCookie(String cookieName, Cookie[] cookies) {
      if (cookies == null) {
        return null;
      }

      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(cookieName)) {
          return cookie;
        }
      }

      return null;
//    }
  }
}
