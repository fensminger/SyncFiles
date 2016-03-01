package org.fer.syncfiles.service.syncfiles.hubic;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.fer.syncfiles.service.syncfiles.hubic.Consumer.ContainerConsumer;
import org.fer.syncfiles.service.syncfiles.hubic.Consumer.ObjectConsumer;
import org.fer.syncfiles.service.syncfiles.hubic.domain.ContainerInfo;
import org.fer.syncfiles.service.syncfiles.hubic.domain.ObjectDetailInfo;
import org.fer.syncfiles.service.syncfiles.hubic.domain.ObjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by fensm on 23/02/2016.
 */
public class HubicService {
    private final Logger log = LoggerFactory.getLogger(HubicService.class);

    private SwiftRequest swiftRequest;

    public HubicService() {
    }

    public HubicService(SwiftRequest swiftRequest) {
        this.swiftRequest = swiftRequest;
    }

    public void consumeContainers(Consumer<ContainerInfo> containerConsumer) throws IOException {
        swiftRequest.listContainers(new ContainerConsumer(containerConsumer));
    }

    public void consumeObjects(String container, String prefix, Consumer<ObjectInfo> objectConsumer) throws IOException {
        ObjectConsumer objectConsumerObj = new ObjectConsumer(objectConsumer);
        String marker = null;
        String prevMarker = null;
        StatusLine statusLine = null;
        int nbLecture = 0;
        while((statusLine=swiftRequest.listObjects(container, 10000, marker, prefix, objectConsumerObj)).getStatusCode()==200) {
            marker = objectConsumerObj.getMarker().getName();
            log.info("Lot number (10000) : " + (++nbLecture) + ", marker : " + marker);
            if (prevMarker==marker) {
                break;
            } else {
                prevMarker = marker;
            }
        }
        if (statusLine==null || statusLine.getStatusCode()!=200) {
            throw new IOException("Problem to download list of objects : " + statusLine.getStatusCode() + " -> " + statusLine.getReasonPhrase());
        }
    }

    public ObjectDetailInfo loadObjectMetaData(String container, String fileName) throws IOException {
        return swiftRequest.loadObjectMetaData(container, fileName);
    }

    public CloseableHttpResponse loadObject(String container, String fileName, boolean loadManifest) throws IOException {
        CloseableHttpResponse response = swiftRequest.loadObject(container, fileName, loadManifest);
//        for (Header header : response.getAllHeaders()) {
//            log.info("Header of " + fileName + " : " + header.getName() + "="+ header.getValue());
//        }
        return  response;
    }

    public void uploadObject(String container, String fileName, String md5, File fileToUpload) throws IOException {
        swiftRequest.uploadObject(container, fileName, md5, fileToUpload);
    }

    public void deleteObject(String container, String fileName) throws IOException {
        swiftRequest.deleteObject(container, fileName);
    }

    public SwiftAccess authenticate(String clientId, String clientSecret, int port, String user, String pwd) throws IOException, InterruptedException {
        HubicApi hubicApiInstance = HubicApi.instance();
        final String secretState = "RandomString_" + new Random().nextInt(999_999);

        final OAuth20Service service = new ServiceBuilder()
            .apiKey(clientId)
            .apiSecret(clientSecret)
            .scope("usage.r,account.r,getAllLinks.r,credentials.r,sponsorCode.r,activate.w,sponsored.r,links.drw")
//            .scope("credentials.r") // replace with desired scope
            .state(secretState)
//            .callback("https://api.hubic.com/sandbox/")
            .callback("http://localhost:"+port+"/")
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

        new Thread(() -> {
            AuthBatch authBatch = new AuthBatch();
            try {
                // On attend que le serveur http soit correctement démarrer.
                // Les NAS sont plutôt lents...
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            authBatch.startHubic(authorizationUrl, user, pwd);
        }).start();

        AuthHttpServer httpServer = new AuthHttpServer(port);
        Map<String, String> params = null ;
        // the server is being started
        params = httpServer.startAndWaitForData() ;
        httpServer.stopServer();


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

        SwiftAccess swiftAccess = new Gson().fromJson(response.getBody(), SwiftAccess.class) ;
        swiftAccess.setAccessToken(accessToken);

        log.info("");
        log.info(""+response.getCode());
        log.info(response.getBody());

        log.info("");
        log.info(""+swiftAccess);

        return swiftAccess;
    }

}
