package org.fer.syncfiles.services;

import org.fer.syncfiles.security.hmac.HmacRequester;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Hmac Requester service
 * Created by Michael DESIGAUD on 16/02/2016.
 */
@Service
public class DefaultHmacRequester implements HmacRequester{

    @Override
    public Boolean canVerify(HttpServletRequest request) {
        return false;
        // return request.getRequestURI().contains("/api") && !(request.getRequestURI().contains("/api/authenticate") || request.getRequestURI().contains("/api/config"));
    }

    @Override
    public String getSecret(String idUser) {
        // TODO retrieve user by ID and return the secret
        return null;
    }
}
