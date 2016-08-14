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


import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.OAuth2AccessTokenExtractor;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.utils.OAuthEncoder;
import com.github.scribejava.core.utils.Preconditions;

public class HubicApi extends DefaultApi20 {

    private static final String AUTHORIZE_URL = "https://api.hubic.com/oauth/auth/";
//    private static final String AUTHORIZE_URL = "https://api.hubic.com/oauth/auth/?client_id=%s&redirect_uri=%s&response_type=code&state=%s";
//	private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

	public static final String CREDENTIALS_URL = "https://api.hubic.com/1.0/account/credentials" ;

    public static HubicApi instance() {
        return HubicApi.InstanceHolder.INSTANCE;
    }

    @Override
	public String getAccessTokenEndpoint() {
		return "https://api.hubic.com/oauth/token";
	}

    @Override
    protected String getAuthorizationBaseUrl() {
        // return "https://api.hubic.com/oauth/auth";
        return AUTHORIZE_URL;
//        return String.format(AUTHORIZE_URL, config.getApiKey(),
//            OAuthEncoder.encode(config.getCallback()));
    }

    @Override
	public Verb getAccessTokenVerb() {
		return Verb.POST;
	}

//	@Override
//	public String getAuthorizationUrl(OAuthConfig config)
//	{
//		Preconditions.checkValidUrl(config.getCallback(), "Must provide a valid url as callback.") ;
//		if (config.hasScope())
//		{
//			return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(),
//					OAuthEncoder.encode(config.getCallback()),
//                OAuthEncoder.encode(config.getState()),
//                OAuthEncoder.encode(config.getScope())
//            );
//		}
//		else
//		{
//			return String.format(AUTHORIZE_URL, config.getApiKey(),
//					OAuthEncoder.encode(config.getCallback()));
//		}
//	}

	@Override
	public OAuth20Service createService(OAuthConfig config)
	{
		return new HubicOAuth20ServiceImpl(this, config);
	}

//	@Override
//	public AccessTokenExtractor getAccessTokenExtractor()
//	{
//		return new HubicTokenExtractorImpl();
//	}
//
    private static class InstanceHolder {
        private static final HubicApi INSTANCE = new HubicApi();

        private InstanceHolder() {
        }
    }

}
