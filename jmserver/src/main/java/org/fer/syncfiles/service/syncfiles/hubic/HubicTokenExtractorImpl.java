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

import com.github.scribejava.core.extractors.AccessTokenExtractor;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.utils.Preconditions;
import com.google.gson.Gson;


public class HubicTokenExtractorImpl implements AccessTokenExtractor {

	private static final String EMPTY_SECRET = "";

	private final Gson gson = new Gson();

	public Token extract(String response)
	{
		Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string");
		AccessToken at = gson.fromJson(response, AccessToken.class);
		return new Token(at.getAccessToken(), EMPTY_SECRET, response);
	}

	public AccessToken getAccessToken (String response)
	{
		Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string");
		AccessToken at = gson.fromJson(response, AccessToken.class);
		return at ;
	}
}
