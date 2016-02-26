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

import com.github.scribejava.core.model.Token;

public class SwiftAccess {

	private final String token ;
	private final String endpoint ;
	private final String expires ;

	private Token accessToken ;

	public SwiftAccess(String token, String endpoint, String expires, Token accessToken) {
		super();
		this.token = token;
		this.endpoint = endpoint;
		this.expires = expires;
		this.accessToken = accessToken ;
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

    @Override
    public String toString() {
        return "SwiftAccess{" +
            "token='" + token + '\'' +
            ", endpoint='" + endpoint + '\'' +
            ", expires='" + expires + '\'' +
            ", accessToken=" + accessToken.getToken() + ", secret=" + accessToken.getSecret() + ", rawResponse=" + accessToken.getRawResponse() +
            '}';
    }
}
