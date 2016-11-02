package org.fer.syncfiles.security;

import org.fer.syncfiles.security.hmac.HmacException;
import org.fer.syncfiles.security.hmac.HmacSigner;
import org.fer.syncfiles.security.hmac.HmacUtils;
import org.fer.syncfiles.services.AuthenticationService;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Auth token filter
 * Created by Michael DESIGAUD on 14/02/2016.
 */
public class XAuthTokenFilter extends GenericFilterBean{

    private AuthenticationService authenticationService;

    public XAuthTokenFilter(AuthenticationService authenticationService){
       this.authenticationService = authenticationService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (true || !request.getRequestURI().contains("/api") || request.getRequestURI().contains("/api/authenticate") || request.getRequestURI().contains("/api/config")){
            filterChain.doFilter(request, response);
        } else {

            try {
                String jwtHeader = request.getHeader(HmacUtils.AUTHENTICATION);
                String userId = HmacSigner.getJwtIss(jwtHeader);

                // TODO Authentication to complete
                //Retrieve user in cache
                authenticationService.findUserById(Long.valueOf(userId));
                //Assert.notNull(user,"No user found with id: "+userId);
                //this.authenticationService.tokenAuthentication(user.getUserName());
                filterChain.doFilter(request,response);
            } catch (HmacException e) {
                e.printStackTrace();
            }
        }

    }
}
