/*
 * Copyright 2014 Loic Merckel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.fer.syncfiles.service.syncfiles.hubic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.scribejava.core.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SwiftAccess {
    private static final Logger log = LoggerFactory.getLogger(SwiftAccess.class);

	private final String token ;
	private final String endpoint ;
	private final String expires ;

	private Token accessToken ;

    private Date expiresDate = null;

    public SwiftAccess(String token, String endpoint, String expires, Token accessToken) {
		super();
		this.token = token;
		this.endpoint = endpoint;
		this.expires = expires;
		this.accessToken = accessToken ;
        initExpiresDate(expires);
    }

    private void initExpiresDate(String expires) {
        if (expires!=null && expiresDate == null) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
            try {
                expiresDate = sdf.parse(expires);
            } catch (ParseException e) {
                expiresDate = null;
                log.warn("Unable to parse SwiftAccess expires date : " + expires + " -> " + e.getMessage());
            }
        }
    }

    public String getToken() {
		return token;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getExpires() {
		return expires;
	}

	public Token getAccessToken (){
		return accessToken ;
	}

	public void setAccessToken (Token token){
		accessToken = token ;
	}

    @JsonIgnore
    private Date getExpiresDate() {
        initExpiresDate(expires);
        return expiresDate;
    }

    @JsonIgnore
    public boolean isExpire() {
        if (expires==null) {
            return true;
        } else {
            log.debug("Expire token : " + expires + " -> " + getExpiresDate());
            return (getExpiresDate().getTime()-1000L*10L*1L) <= new Date().getTime();
        }
    }

    @Override
    public String toString() {
        return "SwiftAccess{" +
            "token='" + token + '\'' +
            ", endpoint='" + endpoint + '\'' +
            ", expires='" + expires + '\'' +
            ", accessToken=" + accessToken.getRawResponse() +
            '}';
    }
}
