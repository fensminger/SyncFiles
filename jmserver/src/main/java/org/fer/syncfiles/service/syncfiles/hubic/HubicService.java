package org.fer.syncfiles.service.syncfiles.hubic;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.fer.syncfiles.service.syncfiles.hubic.Consumer.ContainerConsumer;
import org.fer.syncfiles.service.syncfiles.hubic.Consumer.ObjectConsumer;
import org.fer.syncfiles.domain.syncfiles.ContainerInfo;
import org.fer.syncfiles.domain.syncfiles.ObjectDetailInfo;
import org.fer.syncfiles.domain.syncfiles.ObjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by fensm on 23/02/2016.
 */
public class HubicService {
    private final Logger log = LoggerFactory.getLogger(HubicService.class);

    private OAuth20Service service = null;
    private SwiftRequest swiftRequest;
    private String clientId;
    private String clientSecret;

    public HubicService() {
        this(new SwiftRequest());
    }

    public HubicService(SwiftRequest swiftRequest) {
        this.swiftRequest = swiftRequest;
    }

    public void consumeContainers(Consumer<ContainerInfo> containerConsumer) throws IOException {
        refreshTokenIfExpired();
        swiftRequest.listContainers(new ContainerConsumer(containerConsumer));
    }

    public void consumeObjects(String container, String prefix, Consumer<ObjectInfo> objectConsumer) throws IOException {
        refreshTokenIfExpired();
        ObjectConsumer objectConsumerObj = new ObjectConsumer(objectConsumer);
        String marker = null;
        String prevMarker = null;
        StatusLine statusLine = null;
        int nbLecture = 0;
        while ((statusLine = swiftRequest.listObjects(container, 10000, marker, prefix, objectConsumerObj)).getStatusCode() == 200) {
            marker = objectConsumerObj.getMarker().getName();
            log.info("Lot number (10000) : " + (++nbLecture) + ", marker : " + marker);
            if (prevMarker == marker) {
                break;
            } else {
                prevMarker = marker;
            }
        }
        if (statusLine == null || statusLine.getStatusCode() != 200) {
            throw new IOException("Problem to download list of objects : " + statusLine.getStatusCode() + " -> " + statusLine.getReasonPhrase());
        }
    }

    public ObjectDetailInfo loadObjectMetaData(String container, String fileName) throws IOException {
        refreshTokenIfExpired();
        return swiftRequest.loadObjectMetaData(container, fileName);
    }

    public CloseableHttpResponse loadObject(String container, String fileName, boolean loadManifest) throws IOException {
        refreshTokenIfExpired();
        CloseableHttpResponse response = swiftRequest.loadObject(container, fileName, loadManifest);
//        for (Header header : response.getAllHeaders()) {
//            log.info("Header of " + fileName + " : " + header.getName() + "="+ header.getValue());
//        }
        return response;
    }

    public void uploadObject(String container, String fileName, String md5, File fileToUpload) throws IOException {
        refreshTokenIfExpired();
        swiftRequest.uploadObject(container, fileName, md5, fileToUpload);
    }

    private void refreshTokenIfExpired() {
        if (swiftRequest.getSwiftAccess().isExpire()) {
            refreshToken();
        }
    }

    public void deleteObject(String container, String fileName) throws IOException {
        refreshTokenIfExpired();
        swiftRequest.deleteObject(container, fileName);
    }

    public SwiftAccess authenticate(String clientId, String clientSecret, int port, String user, String pwd) throws IOException, InterruptedException {
        HubicApi hubicApiInstance = HubicApi.instance();
        final String secretState = "RandomString_" + new Random().nextInt(999_999);

        this.clientId = clientId;
        this.clientSecret = clientSecret;

        service = new ServiceBuilder()
            .apiKey(clientId)
            .apiSecret(clientSecret)
            .scope("usage.r,account.r,getAllLinks.r,credentials.r,sponsorCode.r,activate.w,sponsored.r,links.drw")
//            .scope("credentials.r") // replace with desired scope
            .state(secretState)
//            .callback("https://api.hubic.com/sandbox/")
            .callback("http://localhost:" + port + "/")
            .build(hubicApiInstance);

        log.info("=== Hubics's OAuth Workflow ===");
        log.info("");

        // Obtain the Authorization URL
        log.info("Fetching the Authorization URL...");
        final String authorizationUrl = service.getAuthorizationUrl();
        log.info("Got the Authorization URL!");
        log.info("Now go and authorize ScribeJava here:");
        log.info(authorizationUrl);
        log.info("And paste the authorization code here");
        System.out.print(">>");

        final AuthHttpServer httpServer = new AuthHttpServer(port);
        new Thread(() -> {
            AuthBatch authBatch = new AuthBatch();
            try {
                // On attend que le serveur http soit correctement démarrer.
                // Les NAS sont plutôt lents...
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.warn(e.getMessage());
            }
            authBatch.startHubic(authorizationUrl, user, pwd, httpServer);
        }).start();

        Map<String, String> params = null;
        // the server is being started
        params = httpServer.startAndWaitForData();
        httpServer.stopServer();

        if (params.size()==0) {
            throw new IOException("Unable to authenticate.");
        }

        String code = params.get("code");
        log.info("===> code : " + code);

        log.info("");

        log.info("And paste the state from server here. We have set 'secretState'='" + secretState + "'.");
        System.out.print(">>");
        final String value = secretState;
        if (secretState.equals(value)) {
            log.info("State value does match!");
        } else {
            log.info("Ooops, state value does not match!");
            log.info("Expected = " + secretState);
            log.info("Got      = " + value);
            log.info("");
        }

        SwiftAccess swiftAccess = retrieveToken(service, code);
        swiftRequest.setSwiftAccess(swiftAccess);
        return swiftAccess;

//        KeystoneToken keystoneToken = new KeystoneToken();
//        keystoneToken.applyContext("https://api.hubic.com/1.0/account/credentials", (TokenAuth) null);
//        Access access = AccessWrapper.wrap(keystoneToken);
//        OSClient os = OSFactory.clientFromAccess(access);
//        os.objectStorage().containers().list();

//        AppInfoRepository appInfoRepo = new AppInfoFileRepository(new File("hubicInfoRepo.properties"));
//        UserCredentialsRepository userCredentialsRepo = new OAuth2Credentials( accessToken, swiftAccess.getExpires(), null, swiftAccess.getToken()) ;
//        IStorageProvider storage = StorageFacade.forProvider("hubic")
//            .setAppInfoRepository(appInfoRepo, "mytestapp")
//            .setUserCredentialsRepository(userCredentialsRepo, null)
//            //.setHttpClient(customClient) // Optional: redefine the HttpClient
//            .build();
//
//        CFolderContent res = storage.listRootFolder();
//        for(CFile cfile : res.getFiles()) {
//            log.info(cfile.getPath().toString());
//        }

    }

    public void refreshToken() {

        final OAuthRequest request = new OAuthRequest(service.getApi().getAccessTokenVerb(), service.getApi().getAccessTokenEndpoint(), service);

        AccessToken at = new Gson().fromJson(swiftRequest.getSwiftAccess().getAccessToken().getRawResponse(), AccessToken.class);

        String authenticationCode = clientId + ":" + clientSecret ;
        byte[] bytesEncodedAuthenticationCode = Base64.encodeBase64(authenticationCode.getBytes());
        request.addHeader ("Authorization", "Basic " + new String(bytesEncodedAuthenticationCode)) ;

        request.addBodyParameter("refresh_token", at.getRefreshToken());
        request.addBodyParameter("grant_type", "refresh_token");
        Response response = request.send();
        String body = response.getBody();
        if (response.getCode()!=200) {
            throw new RuntimeException("Unable to refresh token " + response.getCode() + " : " + body);
        }
        Token newToken = service.getApi().getAccessTokenExtractor().extract(response.getBody());
// {"expires_in":21600,"access_token":"06lWcO3bkjcjtDLrSNaRpdAUKdmZxqTD9dI6sG0Dhbxj0I3q6JF33WI562OBEZgI","token_type":"Bearer"}
        SwiftAccess swiftAccess = signRequest(service, newToken);
        swiftRequest.setSwiftAccess(swiftAccess);
    }

    private SwiftAccess retrieveToken(OAuth20Service service, String code) {
        final Verifier verifier = new Verifier(code);
        // Trade the Request Token and Verfier for the Access Token
        log.info("Trading the Request Token for an Access Token...");
        final Token accessToken = service.getAccessToken(verifier);
        log.info("Got the Access Token!");
        log.info("(if your curious it looks like this: " + accessToken + " )");
        log.info("");


        return signRequest(service, accessToken);
    }

    private SwiftAccess signRequest(OAuth20Service service, Token accessToken) {
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.hubic.com/1.0/account/credentials", service);
        request.setConnectionKeepAlive(false);
        service.signRequest(accessToken, request);
        final Response response = request.send();

        SwiftAccess swiftAccess = new Gson().fromJson(response.getBody(), SwiftAccess.class);
        swiftAccess.setAccessToken(accessToken);

        log.info("");
        log.info("" + response.getCode());
        log.info(response.getBody());

        log.info("");
        log.info("" + swiftAccess);

        return swiftAccess;
    }

}
