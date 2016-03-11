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

import com.google.common.io.CharStreams;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AuthBatch {

	final private Logger logger = LoggerFactory.getLogger(AuthBatch.class);

	private final Object lock = new Object () ;

    public AuthBatch() {
        super();
    }

	public boolean startHubic(final String url, String login, String password, AuthHttpServer httpServer)
	{
        return tryOneHubicConnection(url, login, password, httpServer);
	}

    private boolean tryOneHubicConnection(String url, String login, String password, AuthHttpServer httpServer) {
        try {
            ConnectionKeepAliveStrategy myStrategy = (response, context) -> {
                // Honor 'keep-alive' header
                HeaderElementIterator it = new BasicHeaderElementIterator(
                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        try {
                            return Long.parseLong(value) * 1000;
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }
                return 30 * 1000;
            };


            HttpClientContext httpClientContext = HttpClientContext.create();

            CookieStore cookieStore = new BasicCookieStore();
            httpClientContext.setCookieStore(cookieStore);
            BasicClientCookie cookie = new BasicClientCookie("_pk_id.21.7fb5", "b2b408d941f96548.1386016715.2.1395093565.1386016715.");
            cookie.setDomain("localhost");
            cookieStore.addCookie(cookie);

//            CloseableHttpClient httpClient = HttpClients.custom()
//                    //.setKeepAliveStrategy(myStrategy)
//                    .build();

//            SSLContext context = SSLContext.getInstance("TLSv1");
            SSLContext sslcontext = SSLContexts.custom()
//                    .loadKeyMaterial(keyStore, "password".toCharArray())
                            //.loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
                    .build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[] { "TLSv1" },
                    null,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            CloseableHttpClient httpClient = HttpClientBuilder
                    .create()
                    .setSslcontext(SSLContexts.custom().useProtocol("TLSv1").build())
                    .setSSLSocketFactory(sslsf)
                    .build();

            HttpGet httpGet = new HttpGet(url);
            Header[] getHeaders = new Header[]{
                    new BasicHeader("Host", "api.hubic.com"),
                    new BasicHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:31.0) Gecko/20100101 Firefox/31.0"),
                    new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
                    new BasicHeader("Accept-Language", "fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3"),
                    new BasicHeader("Accept-Encoding", "gzip, deflate"),
                    new BasicHeader("Cache-control", "max-age=0"),
                    new BasicHeader("Connection", "keep-alive")
            };
            httpGet.setHeaders(getHeaders);

            HttpResponse response = httpClient.execute(httpGet, httpClientContext);

            final HttpEntity entity = response.getEntity();
            String body = "";
            try (InputStreamReader content = new InputStreamReader(entity.getContent())) {
                body = CharStreams.toString(content);
            }
            Header[] headers = response.getAllHeaders();
            for(Header header : headers) {
                logger.info("Header : " + header.toString());
            }

            for(Cookie onaCookie : cookieStore.getCookies()) {
                logger.info("Cookie : " + onaCookie.toString());
            }

            Document doc = Jsoup.parse(body);
            Map<String, String> params = anaParamsForFirstGet(doc, login, password);
            String action = loadAction(doc);

            logger.info("Accès à l'URL : " + url);

            //logger.info(body);

            logger.info("===================================================================================================================");
            String urlPost = "https://api.hubic.com/oauth/auth/"; //"https://api.hubic.com/" ;//+ action;
            Header[] postHeaders = new Header[]{
                    new BasicHeader("Host", "api.hubic.com"),
                    new BasicHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:31.0) Gecko/20100101 Firefox/31.0"),
                    new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
                    new BasicHeader("Accept-Language", "fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3"),
                    new BasicHeader("Accept-Encoding", "gzip, deflate"),
                    new BasicHeader("Referer", url),
                    new BasicHeader("Connection", "keep-alive")
            };
            for(Header header : postHeaders) {
                logger.info("Post Header : " + header.toString());
            } // "https://api.hubic.com/oauth/auth/?client_id=api_hubic_hB3LO1RcO0Rz2xhqiYZBvYyFv0OQ5mmM&redirect_uri=http%3A%2F%2Flocalhost%3A9000%2F&response_type=code&state=RandomString&scope=credentials.r"

            CookieStore cookiePostStore = new BasicCookieStore();
            //httpClientContext.setCookieStore(cookiePostStore);
            cookiePostStore.addCookie(cookie);

            HttpPost httpPost = new HttpPost(urlPost);
            httpPost.setHeaders(postHeaders);
            List<NameValuePair> paramList = new ArrayList<NameValuePair>();
            for(String key : params.keySet()) {
                paramList.add(new BasicNameValuePair(key, params.get(key)));
                if (!"user_pwd".equals(key)) {
                    logger.info("Paramètres : " + key + " : " + params.get(key));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(paramList));
            httpPost.setHeader("ContentType", "application/x-www-form-urlencoded");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(),e);
            }

            HttpResponse responsePost = httpClient.execute(httpPost, httpClientContext);
            StatusLine statusLine = responsePost.getStatusLine();
            logger.info("statusLine: " + statusLine);
            String bodyPost = "";
            try (InputStreamReader content = new InputStreamReader(responsePost.getEntity().getContent())) {
                bodyPost = CharStreams.toString(content);
            }

//                logger.info("Accès à l'URL de post: " + urlPost);
//                for(Cookie cookie : cookieStore.getCookies()) {
//                    logger.info("Cookie : " + cookie.toString());
//                }

            String urlRedirectLocal = null;
            for(Header header : responsePost.getAllHeaders()) {
                logger.info("Response Post Header : " + header.toString());
                if ("Location".equals(header.getName())) {
                    urlRedirectLocal = header.getValue();
                }
            }

            if (urlRedirectLocal!=null) {
                logger.info("Url de redirection : " + urlRedirectLocal);
                HttpGet httpGetRedirect = new HttpGet(urlRedirectLocal);
                HttpResponse responseRedirect = httpClient.execute(httpGetRedirect);
                logger.info("statusLine redirect: " + responseRedirect.getStatusLine());
                logger.info("statusLine status code: " + responseRedirect.getStatusLine().getStatusCode());
                String bodyRedirectResponse = "";
                try (InputStreamReader content = new InputStreamReader(responseRedirect.getEntity().getContent())) {
                    bodyRedirectResponse = CharStreams.toString(content);
                }
                logger.info("Response body redirect : " + bodyRedirectResponse);
                if (!bodyRedirectResponse.contains("Host = [")) {
                    return false;
                }
            }

            // Bonne réponse :
//                2014-11-09 17:45:06,792 INFO  org.swiftexplorer.auth.webbrowser.AuthBatch lambda$start$1- Url de redirection : http://localhost:9998/?code=14155551068lKKFWaBkvEbDaTHK9sw2g1m9B4bxukogu0gbqpmPVK4xbk8CnwLTd1NlDiHpj0a&scope=account.r,credentials.r&state=RandomString
//                2014-11-09 17:45:06,819 INFO  org.swiftexplorer.auth.webbrowser.AuthBatch lambda$start$1- statusLine redirect: HTTP/1.1 200 OK
//                2014-11-09 17:45:06,821 INFO  org.swiftexplorer.auth.webbrowser.AuthBatch lambda$start$1- Response body redirect : Accept-encoding = [gzip,deflate]
//                Connection = [Keep-Alive]
//                Host = [localhost:9998]
//                User-agent = [Apache-HttpClient/4.3.5 (java 1.5)]
//
//                2014-11-09 17:45:11,517 INFO  org.swiftexplorer.swift.util.HubicBatchSwift getSwiftAccess- Swift access token expiry date: 2014-11-09T23:25:56+01:00
//                2


        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("Impossible de se connecter à hubic. " + e.getMessage(), e);
            if (httpServer!=null) {
                try {
                    httpServer.stopWaiting();
                } catch (InterruptedException e1) {
                    logger.error("Unable to stop serveur : " + e1.getMessage(), e1);
                }
            }
            return false;
        }

        return true;
    }

    protected Map<String, String> anaParamsForFirstGet(Document doc, String user, String password) {
        Elements links = doc.select("input");
        HashMap<String, String> res = new HashMap<>();
        for(Element elt : links) {
            String type = elt.attr("type");
            if (!"button".equals(type) && !"submit".equals(type)) {
                String name = elt.attr("name");
                String value = elt.attr("value");
                if (value==null || "".equals(value.trim())) {
                    if (name.contains("pwd") || name.contains("password")) {
                        value = password;
                    }
                    if (name.contains("login")) {
                        value = user;
                    }
                }
                res.put(name, value);
            }
        }
        return res;
    }

    protected String loadAction(Document doc) {
        return doc.select("form").attr("action");
    }

}
