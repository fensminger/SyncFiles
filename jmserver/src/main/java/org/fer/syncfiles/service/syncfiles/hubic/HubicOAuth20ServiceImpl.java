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

import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth.OAuthService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HubicOAuth20ServiceImpl extends OAuth20Service {

	private static final String VERSION = "2.0";

	final private Logger logger = LoggerFactory.getLogger(HubicOAuth20ServiceImpl.class);

	protected final HubicApi api;
	protected final OAuthConfig config;


	public HubicOAuth20ServiceImpl(HubicApi api, OAuthConfig config) {
        super(api, config);
		this.api = api;
		this.config = config;
	}


	public Token refreshAccessToken (Token expiredToken)
	{
        logger.info("refreshAccessToken : " + expiredToken);
		if (expiredToken == null)
			return null ;

		OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint(), this);

		String authenticationCode = config.getApiKey() + ":" + config.getApiSecret() ;
		byte[] bytesEncodedAuthenticationCode = Base64.encodeBase64(authenticationCode.getBytes());
		request.addHeader ("Authorization", "Basic " + new String(bytesEncodedAuthenticationCode)) ;

		String charset = "UTF-8";

		request.setCharset(charset);
		request.setFollowRedirects(false);

		AccessToken at = new HubicTokenExtractorImpl().getAccessToken(expiredToken.getRawResponse()) ;

		try
		{
			request.addBodyParameter("refresh_token", at.getRefreshToken());
			//request.addBodyParameter("refresh_token", URLEncoder.encode(at.getRefreshToken(), charset));
			request.addBodyParameter("grant_type", "refresh_token");
			request.addBodyParameter(OAuthConstants.CLIENT_ID, URLEncoder.encode(config.getApiKey(), charset));
			request.addBodyParameter(OAuthConstants.CLIENT_SECRET, URLEncoder.encode(config.getApiSecret(), charset));
		}
		catch (UnsupportedEncodingException e)
		{
			logger.error("Error occurred while refreshing the access token", e);
		}

		Response response = request.send();
		Token newToken = api.getAccessTokenExtractor().extract(response.getBody());
		// We need to keep the initial RowResponse because it contains the refresh token
		return new Token (newToken.getToken(), newToken.getSecret(), expiredToken.getRawResponse()) ;
	}


	@Override
	public <T extends AbstractRequest> T createAccessTokenRequest(Verifier verifier, T request) {

		String authenticationCode = config.getApiKey() + ":" + config.getApiSecret() ;
		byte[] bytesEncodedAuthenticationCode = Base64.encodeBase64(authenticationCode.getBytes());
		request.addHeader ("Authorization", "Basic " + new String(bytesEncodedAuthenticationCode)) ;

		String charset = "UTF-8";

		request.setCharset(charset);
		request.setFollowRedirects(false);

		try
		{
			request.addBodyParameter(OAuthConstants.CODE, URLEncoder.encode(verifier.getValue(), charset));
			request.addBodyParameter(OAuthConstants.REDIRECT_URI, URLEncoder.encode(config.getCallback(), charset));
			request.addBodyParameter("grant_type", "authorization_code");
			request.addBodyParameter(OAuthConstants.CLIENT_ID, URLEncoder.encode(config.getApiKey(), charset));
			request.addBodyParameter(OAuthConstants.CLIENT_SECRET, URLEncoder.encode(config.getApiSecret(), charset));
		}
		catch (UnsupportedEncodingException e)
		{
			logger.error("Error occurred while getting the access token", e);
		}

        return request;

	}


    @Override
    public void signRequest(Token token, AbstractRequest abstractRequest) {
        abstractRequest.addHeader("Authorization",  "Bearer " + token.getToken());

    }

    @Override
	public String getVersion() {
		return VERSION;
	}


	public String getAuthorizationUrl(Token requestToken) {
		return api.getAuthorizationUrl(config);
	}
}
