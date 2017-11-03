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

package org.fer.syncfiles.services.hubic;

//import com.github.scribejava.core.model.OAuthRequest;
//import com.github.scribejava.core.model.Response;
//import com.github.scribejava.core.model.Token;
//import com.github.scribejava.core.model.Verb;
//import com.google.gson.Gson;
//import org.javaswift.joss.client.factory.AuthenticationMethod;
//import org.javaswift.joss.command.shared.identity.access.AccessBasic;
//import org.javaswift.joss.model.Access;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class HubicAccessProvider { // implements AuthenticationMethod.AccessProvider{
//
//	final private Logger logger = LoggerFactory.getLogger(org.swiftexplorer.swift.client.impl.HubicAccessProvider.class);
//
//	private final SwiftAccess swiftAccess ;
//    private final HubicOAuth20ServiceImpl hubicOAuth20Service;
//    private boolean hasAlreadyAuthenticated = false ;
//
//
//	public HubicAccessProvider(SwiftAccess swiftAccess, HubicOAuth20ServiceImpl hubicOAuth20Service) {
//		super();
//		if (swiftAccess == null)
//			throw new NullPointerException () ;
//		this.swiftAccess = swiftAccess;
//        this.hubicOAuth20Service = hubicOAuth20Service;
//	}
//
//
//	@Override
//	public Access authenticate() {
//		AccessBasic access = new AccessBasic();
//		if (!hasAlreadyAuthenticated)
//		{
//    		access.setToken(swiftAccess.getToken());
//    		access.setUrl(swiftAccess.getEndpoint());
//    		hasAlreadyAuthenticated = true ;
//		}
//		else
//		{
//			logger.info("Refresh the access token.");
//
//    		SwiftAccess newSwiftAccess = getSwiftAccess(hubicOAuth20Service, hubicOAuth20Service.refreshAccessToken(this.swiftAccess.getAccessToken()));
//                // HubicSwift.refreshAccessToken(this.swiftAccess.getAccessToken())
//    		access.setToken(newSwiftAccess.getToken());
//    		access.setUrl(newSwiftAccess.getEndpoint());
//		}
//		return access ;
//	}
//
//    private SwiftAccess getSwiftAccess (HubicOAuth20ServiceImpl service, Token accessToken)
//    {
//        String urlCredential = HubicApi.CREDENTIALS_URL;
//
//        OAuthRequest request = new OAuthRequest(Verb.GET, urlCredential, service);
//        request.setConnectionKeepAlive(false);
//        service.signRequest(accessToken, request);
//        Response responseReq = request.send();
//
//        SwiftAccess ret = new Gson().fromJson(responseReq.getBody(), SwiftAccess.class) ;
//        ret.setAccessToken(accessToken);
//
//        logger.info("Swift access token expiry date: " + ret.getExpires());
//
//        return ret ;
//    }
//
}
