package org.fer.syncfiles.service.syncfiles;

import org.fer.syncfiles.service.syncfiles.hubic.*;
import org.fer.syncfiles.service.syncfiles.hubic.domain.ObjectDetailInfo;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by fensm on 15/02/2016.
 */
public class HubicServiceTest extends HubicTestPassword {
    final String clientId = "api_hubic_hB3LO1RcO0Rz2xhqiYZBvYyFv0OQ5mmM";
    final String clientSecret = "h0USSa0WU81zauQNI77om1gWNuE7KSJX7YmJsGU2FXVJISNpjy7MQxu7NV8Dg8F7";
    final int port = 9000;

    int count = 0;

    @Test
    public void hubicContainerTest() throws IOException, InterruptedException {
        HubicService hubicService = new HubicService(new SwiftRequest());

        SwiftAccess swiftAccess = hubicService.authenticate(clientId, clientSecret, port, user, pwd);


//        hubicService.consumeContainers(containerInfo -> {
//            System.out.println(containerInfo);
//        });

        Date dtDeb = new Date();
        hubicService.consumeObjects("default", containerObject -> {
            // System.out.println(containerObject);
            addCount();
        });
        System.out.println("Durée pour " + count + " : " + (new Date().getTime() - dtDeb.getTime()));

        ObjectDetailInfo objectDetailInfo = hubicService.loadObjectMetaData("default", "testBackup/local.properties");
        System.out.println(objectDetailInfo);
    }

    private void addCount() {
        count++;
    }

    @Test
    public void parseDateTest() throws ParseException {
        String date = "Fri, 08 May 2015 19:55:36 GMT";
        //String date = "Fri, 08 May 2015 19:55:36";

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);

        System.out.println(sdf.format(new Date()));
        System.out.println(sdf.parse(date.toLowerCase()));

    }
}
