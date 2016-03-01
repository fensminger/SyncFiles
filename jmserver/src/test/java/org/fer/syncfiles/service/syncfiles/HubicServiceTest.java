package org.fer.syncfiles.service.syncfiles;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.boon.core.Sys;
import org.fer.syncfiles.service.syncfiles.hubic.*;
import org.fer.syncfiles.service.syncfiles.hubic.domain.ObjectDetailInfo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by fensm on 15/02/2016.
 */
public class HubicServiceTest extends HubicTestPassword {
    private final Logger log = LoggerFactory.getLogger(HubicServiceTest.class);


    final String clientId = "api_hubic_hB3LO1RcO0Rz2xhqiYZBvYyFv0OQ5mmM";
    final String clientSecret = "h0USSa0WU81zauQNI77om1gWNuE7KSJX7YmJsGU2FXVJISNpjy7MQxu7NV8Dg8F7";
    final int port = 9000;

    int count = 0;

    @Test
    public void hubicContainerTest() throws IOException, InterruptedException {
        final HubicService hubicService = new HubicService(new SwiftRequest());

        SwiftAccess swiftAccess = hubicService.authenticate(clientId, clientSecret, port, user, pwd);


//        hubicService.consumeContainers(containerInfo -> {
//            System.out.println(containerInfo);
//        });

//        listAllObjects(hubicService);

        String fileName = "testBackup/local.properties";
//        String fileName = "videosFamilles/iso/2003_02_Service 24 et retour à la maison.ISO"; // size : 4467234816
//        ObjectDetailInfo objectDetailInfo = hubicService.loadObjectMetaData("default", fileName);
//        System.out.println(fileName + " : " + objectDetailInfo);

//        try (CloseableHttpResponse res = hubicService.loadObject("default", fileName, true)) {
//
//            try (InputStream inputStream = res.getEntity().getContent()) {
//                int value;
//                int count = 0;
//                while ((value= inputStream.read())!=-1) {
//                    count++;
//                }
//                log.info("Taille réelle du fichier " + fileName + " : " + count);
//            }
//        }

        String fileToUpload = "testFileUpload.txt";
        File file = new File(this.getClass().getResource("/hubic/"+fileToUpload).getFile());
        hubicService.uploadObject("default", fileToUpload, getMd5(file), file);

        try (CloseableHttpResponse res = hubicService.loadObject("default", fileToUpload, false)) {
            log.info("download status : " + res.getStatusLine());
            log.info(EntityUtils.toString(res.getEntity()));
        }

        hubicService.deleteObject("default", fileToUpload);

    }

    private void listAllObjects(HubicService hubicService) throws IOException {
        Date dtDeb = new Date();
        String prefix = "BackupIsa/Isa";
        final InfoHubicObject infoHubicObject = new InfoHubicObject();
        infoHubicObject.setSize(-1L);
        hubicService.consumeObjects("default", null, containerObject -> {
            if (containerObject.getSize()>infoHubicObject.getSize()) {
                infoHubicObject.setSize(containerObject.getSize());
                infoHubicObject.setName(containerObject.getName());
            }
//            try {
//                if (containerObject.getSize()>50_000_000) {
//                    ObjectDetailInfo objectDetailInfo = hubicService.loadObjectMetaData("default", containerObject.getName());
//                    if (objectDetailInfo.getManifest()!=null && objectDetailInfo.getManifest()) {
//                        System.out.println(containerObject.getName() + " => " + objectDetailInfo);
//                    }
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e.getMessage(), e);
//            }
            // System.out.println(containerObject);
            addCount();
        });
        System.out.println("Durée pour " + count + " : " + (new Date().getTime() - dtDeb.getTime()));
        System.out.println("Gros fichier : " + infoHubicObject);
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

    private String getMd5(File file) {
        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                return org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
