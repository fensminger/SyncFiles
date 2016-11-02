package org.fer.syncfiles;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.fer.syncfiles.service.syncfiles.hubic.HubicService;
import org.fer.syncfiles.service.syncfiles.hubic.SwiftAccess;
import org.fer.syncfiles.service.syncfiles.hubic.SwiftRequest;
import org.fer.syncfiles.service.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by fensm on 15/02/2016.
 */
public class RestaureLocal extends HubicTestPassword {
    private static final Logger log = LoggerFactory.getLogger(RestaureLocal.class);

    int count = 0;
    Map<String, String> fileSended = null;
    static boolean running = true;

    public static void main(String[] args) {

        String path = "Photos";
        if (args.length>0) {
            path = args[0];
        }

        Random rand = new Random(System.currentTimeMillis());

        while (running) {
            try {
                RestaureLocal restaureLocal = new RestaureLocal();
                restaureLocal.restaure(path);
            } catch (Exception e) {
                int randValueHour = rand.nextInt(3) + 1;
                int randValues = rand.nextInt(1000) + 1;
                e.printStackTrace();
                try {
                    log.info("Une erreur s'est produite. Début de l'attente : " + e.getMessage());
                    log.info("Nombres d'heures : " + randValueHour);
                    log.info("Nombres de secondes : " + randValues);
                    Thread.sleep((1000 * 60 * 60 * randValueHour) + (1000*randValues));
                    log.info("Une erreur s'est produite. Fin de l'attente : " + e.getMessage());
                } catch (InterruptedException ie) {
                    log.warn(ie.getMessage());
                }
            }
        }
    }

    private void loadFileSended() throws IOException {
        fileSended = new HashMap<>();
        File newFile = getSendedNewFile();
        if (newFile.exists()) {
            File file = getSendedFile();
            Files.copy(Paths.get(newFile.getAbsolutePath()), Paths.get(file.getAbsolutePath()), REPLACE_EXISTING);
            try (BufferedReader in = new BufferedReader(new FileReader(file))) {
                String fileName = in.readLine();
                while (fileName!=null) {
                    fileSended.put(fileName, fileName);
                    fileName = in.readLine();
                }
            }
        }
    }

    private File getSendedFile() {
        return new File("./FileSended.txt");
    }

    private File getSendedNewFile() {
        return new File("./FileSendedNew.txt");
    }

    public void restaure(String remoteHubicPath) throws IOException, InterruptedException {
        final HubicService hubicService = new HubicService(new SwiftRequest());
        loadFileSended();

        SwiftAccess swiftAccess = hubicService.authenticate(clientId, clientSecret, port, user, pwd);

        List<InfoHubicObject> infoHubicObjectList = listAllObjects(hubicService, remoteHubicPath);

        File file = getSendedNewFile();
        try (BufferedWriter loadedFileWriter = new BufferedWriter(new FileWriter(file, true))) {
            for (InfoHubicObject infoHubicObject : infoHubicObjectList) {
                log.info("Début écriture du fichier : " + infoHubicObject.getName());
                try (CloseableHttpResponse res = hubicService.loadObject("default", infoHubicObject.getName(), false)) {
                    String name = "./" + infoHubicObject.getName();
                    File fileName = new File(name);
                    new File(Paths.get(fileName.getAbsolutePath()).getParent().toString()).mkdirs();
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName))) {
                        res.getEntity().writeTo(out);
                        loadedFileWriter.write(infoHubicObject.getName());
                        loadedFileWriter.write("\n");
                        loadedFileWriter.flush();
                        log.info("Fin écriture du fichier : " + infoHubicObject.getName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Unable to download object from restaure : " + e.getMessage());
            throw e;
        }
        running = false;
    }

    private List<InfoHubicObject> listAllObjects(HubicService hubicService, String prefixe) throws IOException {
        Date dtDeb = new Date();
        final InfoHubicObject infoHubicObject = new InfoHubicObject();
        infoHubicObject.setSize(-1L);
        final List<InfoHubicObject> res = new ArrayList<>();
        hubicService.consumeObjects("default", null, containerObject -> {
            if (containerObject.getName().startsWith(prefixe)
                && !"application/directory".equals(containerObject.getContentType())
                && fileSended.get(containerObject.getName())==null
                && !containerObject.getName().contains("/.thumbnails.hubic/")) {
                if (containerObject.getSize()>infoHubicObject.getSize()) {
                    infoHubicObject.setSize(containerObject.getSize());
                    infoHubicObject.setName(containerObject.getName());
                }
//                log.info("Nom du fichier à récupérer : " + containerObject.getName());
                final InfoHubicObject infoHubicObjectToRestore = new InfoHubicObject();
                infoHubicObjectToRestore.setSize(containerObject.getSize());
                infoHubicObjectToRestore.setName(containerObject.getName());
                res.add(infoHubicObjectToRestore);
                addCount();
            }
        });
        log.info("Durée pour " + count + " : " + (new Date().getTime() - dtDeb.getTime()));
        log.info("Gros fichier : " + infoHubicObject);
        return res;
    }

    private void addCount() {
        count++;
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
