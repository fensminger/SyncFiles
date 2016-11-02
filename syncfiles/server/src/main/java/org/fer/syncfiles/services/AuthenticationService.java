package org.fer.syncfiles.services;

import org.fer.syncfiles.security.SecurityUser;
import org.fer.syncfiles.dto.LoginDTO;
import org.fer.syncfiles.dto.UserDTO;
import org.apache.commons.codec.binary.Base64;
import org.fer.syncfiles.security.XAuthTokenFilter;
import org.fer.syncfiles.security.hmac.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Authentication service
 * Created by Michael DESIGAUD on 15/02/2016.
 */
@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Authenticate a user in Spring Security
     * The following headers are set in the response:
     * - X-TokenAccess: JWT
     * - X-Secret: Generated secret in base64 using SHA-256 algorithm
     * - WWW-Authenticate: Used algorithm to encode secret
     * The authenticated user in set ine the Spring Security context
     * The generated secret is stored in a static list for every user
     * @param loginDTO credentials
     * @param response http response
     * @return UserDTO instance
     * @throws HmacException
     */
    public UserDTO authenticate(LoginDTO loginDTO, HttpServletResponse response) throws HmacException {
        //Retrieve user and authenticate
        SecurityUser securityUser = null; // TODO Authentication

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getLogin(),  Base64.encodeBase64String(loginDTO.getPassword().getBytes(Charset.defaultCharset())));
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        //Parse Granted authorities to a list of string authorities
        List<String> authorities = new ArrayList<>();
        for(GrantedAuthority authority : securityUser.getAuthorities()){
            authorities.add(authority.getAuthority());
        }

        //Get Hmac signed token
        Map<String,String> customClaims = new HashMap<>();
        customClaims.put(HmacSigner.ENCODING_CLAIM_PROPERTY, HmacUtils.HMAC_SHA_256);

        //Generate a random secret
        String secret = HmacSigner.generateSecret();

        HmacToken hmacToken = HmacSigner.getSignedToken(secret,String.valueOf(securityUser.getId()), HmacSecurityFilter.JWT_TTL,customClaims);

        // TODO load current user by login and save the secret

        //Set all tokens in http response headers
        response.setHeader(HmacUtils.X_TOKEN_ACCESS, hmacToken.getJwt());
        response.setHeader(HmacUtils.X_SECRET, hmacToken.getSecret());
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, HmacUtils.HMAC_SHA_256);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(securityUser.getId());
        userDTO.setLogin(securityUser.getUsername());
        userDTO.setAuthorities(authorities);
        userDTO.setProfile(securityUser.getProfile());
        return userDTO;
    }

    /**
     * Logout a user
     * - Clear the Spring Security context
     * - Remove the stored UserDTO secret
     */
    public void logout(){
        if(SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
        {
            SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // TODO load user by id
            //if(user != null){
                //user.setSecretKey(null);
                // TODO save user to delete the secret.;
            //}

        }
    }

    /**
     * Authentication for every request
     * - Triggered by every http request except the authentication
     * @see XAuthTokenFilter
     * Set the authenticated user in the Spring Security context
     * @param username username
     */
    public void tokenAuthentication(String username){
        UserDetails details = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    public void findUserById(Long id) {
        // TODO find user by id and return it
    }
}
