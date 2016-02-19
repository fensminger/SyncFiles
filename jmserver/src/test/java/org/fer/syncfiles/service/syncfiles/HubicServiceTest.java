package org.fer.syncfiles.service.syncfiles;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import org.fer.syncfiles.service.syncfiles.hubic.AuthBatch;
import org.fer.syncfiles.service.syncfiles.hubic.AuthHttpServer;
import org.fer.syncfiles.service.syncfiles.hubic.HubicApi;
import org.fer.syncfiles.service.syncfiles.hubic.SwiftAccess;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by fensm on 15/02/2016.
 */
public class HubicServiceTest extends HubicTestPassword {

    @Test
    public void hubicConnexionTest() throws IOException, InterruptedException {
        final String clientId = "api_hubic_hB3LO1RcO0Rz2xhqiYZBvYyFv0OQ5mmM";
        final String clientSecret = "h0USSa0WU81zauQNI77om1gWNuE7KSJX7YmJsGU2FXVJISNpjy7MQxu7NV8Dg8F7";
        final String secretState = "RandomString_" + new Random().nextInt(999_999);
        final int port = 9000;
        HubicApi hubicApiInstance = HubicApi.instance();
        final OAuth20Service service = new ServiceBuilder()
            .apiKey(clientId)
            .apiSecret(clientSecret)
//            .scope("usage.r,account.r,getAllLinks.r,credentials.r,sponsorCode.r,activate.w,sponsored.r,links.drw")
            .scope("credentials.r") // replace with desired scope
            .state(secretState)
//            .callback("https://api.hubic.com/sandbox/")
            .callback("http://localhost:"+port+"/")
            .build(hubicApiInstance);
        final Scanner in = new Scanner(System.in, "UTF-8");

        System.out.println("=== Hubics's OAuth Workflow ===");
        System.out.println();

        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        final String authorizationUrl = service.getAuthorizationUrl();
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
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


        String code = "Test";
        final Verifier verifier = new Verifier(params.get("code"));
        System.out.println();

        System.out.println("And paste the state from server here. We have set 'secretState'='" + secretState + "'.");
        System.out.print(">>");
        final String value = secretState;
        if (secretState.equals(value)) {
            System.out.println("State value does match!");
        } else {
            System.out.println("Ooops, state value does not match!");
            System.out.println("Expected = " + secretState);
            System.out.println("Got      = " + value);
            System.out.println();
        }

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        final Token accessToken = service.getAccessToken(verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();


        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.hubic.com/1.0/account/credentials", service);
        request.setConnectionKeepAlive(false);
        service.signRequest(accessToken, request);
        final Response response = request.send();

        SwiftAccess swiftAccess = new Gson().fromJson(response.getBody(), SwiftAccess.class) ;
        swiftAccess.setAccessToken(accessToken);

        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());

        System.out.println();
        System.out.println(swiftAccess);

    }
}
