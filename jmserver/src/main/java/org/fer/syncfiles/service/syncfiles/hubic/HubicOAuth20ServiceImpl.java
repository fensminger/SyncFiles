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
import java.util.HashMap;
import java.util.Map;

public class HubicOAuth20ServiceImpl extends OAuth20Service {

	final private Logger logger = LoggerFactory.getLogger(HubicOAuth20ServiceImpl.class);

	protected final HubicApi api;
	protected final OAuthConfig config;


	public HubicOAuth20ServiceImpl(HubicApi api, OAuthConfig config) {
        super(api, config);
		this.api = api;
		this.config = config;
	}

	@Override
    public void signRequest(OAuth2AccessToken accessToken, AbstractRequest request) {
        request.addHeader("Authorization", "Bearer " + accessToken.getAccessToken());
    }

}
