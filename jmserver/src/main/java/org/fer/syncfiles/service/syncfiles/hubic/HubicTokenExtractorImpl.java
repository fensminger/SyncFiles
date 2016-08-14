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

import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.utils.Preconditions;
import com.google.gson.Gson;


public class HubicTokenExtractorImpl implements TokenExtractor<OAuth2AccessToken> {

	private static final String EMPTY_SECRET = "";

	private final Gson gson = new Gson();

	public OAuth2AccessToken extract(String response)
	{
		Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string");
		AccessToken at = gson.fromJson(response, AccessToken.class);
		return new OAuth2AccessToken(at.getAccessToken(), response);
	}

	public AccessToken getAccessToken (String response)
	{
		Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string");
		AccessToken at = gson.fromJson(response, AccessToken.class);
		return at ;
	}
}
