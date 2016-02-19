package org.fer.security.back.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

public class CookieAuthenticationFilter extends GenericFilterBean {

  private final UserDetailsService userDetailService;

  private final CookieService cookieService;
  private final SecurityService securityService;

  public CookieAuthenticationFilter(UserDetailsService userDetailService,
      CookieService cookieService, SecurityService securityService) {
    this.userDetailService = userDetailService;
    this.cookieService = cookieService;
      this.securityService = securityService;
  }

  static final String FILTER_APPLIED = "__spring_security_scpf_applied";

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
    ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    if (request.getAttribute(FILTER_APPLIED) != null) {
      // ensure that filter is only applied once per request
      chain.doFilter(request, response);
      return;
    }

    request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

    SecurityContext contextBeforeChainExecution = loadSecurityContext(request);

    try {
      SecurityContextHolder.setContext(contextBeforeChainExecution);
      chain.doFilter(request, response);
    }
    finally {
     // Clear the context and free the thread local
      SecurityContextHolder.clearContext();
      request.removeAttribute(FILTER_APPLIED);
    }
  }

  /**
   * Loads information such as roles etc about the user if the user cookie were present.
   * @param request HttpServletRequest
   * @return A SecurityContext
   */
  private SecurityContext loadSecurityContext(HttpServletRequest request) {
    final String[] info = cookieService.extractInfo(request.getCookies());



    if (null != info) {
        String ip = request.getRemoteAddr();
        String username = info[0];
        String token1 = securityService.buildToken1(username, ip);

        // check first token without db access (just for perfs)
        if (token1.equals(info[1])) {

            UserDetails ud = userDetailService.loadUserByUsername(username);
            if (null != ud) {
                String token2 = securityService.buildToken2(username, ud.getPassword());

                if (token2.equals(info[2])) {
                    return new CookieSecurityContext(ud);
                }

                else {
                    return SecurityContextHolder.createEmptyContext();
                }
            }
            else {
                return SecurityContextHolder.createEmptyContext();
            }




        }
        else {
            return SecurityContextHolder.createEmptyContext();
        }
    }
      else {
        return SecurityContextHolder.createEmptyContext();
    }

  }
}
