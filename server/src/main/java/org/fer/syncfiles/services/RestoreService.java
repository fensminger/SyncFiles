package org.fer.syncfiles.services;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.fer.syncfiles.domain.InfoHubicObject;
import org.fer.syncfiles.domain.ParamSyncFiles;
import org.fer.syncfiles.services.hubic.HubicService;
import org.fer.syncfiles.services.hubic.SwiftAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by fensm on 15/02/2016.
 */
@Service
public class RestoreService {
    private static final Logger log = LoggerFactory.getLogger(RestoreService.class);

    @Autowired
    private StatusSynchroService statusSynchroService;

    @Autowired
    private ParamSyncFilesService paramSyncFilesService;

    @Autowired
    private HubicService hubicService;

    @Autowired
    @Qualifier("syncfilesSocketHandler")
    private SyncfilesSocketHandler syncfilesSocketHandler;

    int count = 0;
    Map<String, String> fileSended = null;
    private AtomicBoolean running = new AtomicBoolean(false);

    public void restoreWithRetry(ParamSyncFiles paramSyncFiles, String remotePath, String localPath) {

        synchronized (running) {
            if (running.get()) {
                throw new RuntimeException("A job is allready running...");
            } else {
                running.set(true);
            }
        }

        Random rand = new Random(System.currentTimeMillis());

        while (running.get()) {
            try {
                restore(paramSyncFiles, remotePath, localPath);
            } catch (Exception e) {
                int randValueHour = rand.nextInt(3) + 1;
                int randValues = rand.nextInt(1000) + 1;
                log.error(e.getMessage(), e);
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

    private void loadFileSended(String paramSyncFilesId) throws IOException {
        fileSended = new HashMap<>();
        File newFile = getSendedNewFile(paramSyncFilesId);
        if (newFile.exists()) {
            File file = getSendedFile(paramSyncFilesId);
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

    private File getSendedFile(String paramSyncFilesId) {
        return new File("./FileSended_"+paramSyncFilesId+".txt");
    }

    private File getSendedNewFile(String paramSyncFilesId) {
        return new File("./FileSendedNew_"+paramSyncFilesId+".txt");
    }

    @Async("threadPoolTaskExecutor")
    public void restore(ParamSyncFiles paramSyncFiles, String remoteHubicPath, String localPath) throws IOException, InterruptedException {
        String paramSyncFilesId = syncfilesSocketHandler.addNewSynchro("Restore", paramSyncFiles, null, false);
        try {
            syncfilesSocketHandler.addMessage(paramSyncFilesId, "Load files allready sended");
            loadFileSended(paramSyncFiles.getId());

            syncfilesSocketHandler.addMessage(paramSyncFilesId, "Authenticate to cloud");
            SwiftAccess swiftAccess = paramSyncFilesService.authenticate(paramSyncFiles.getId());

            syncfilesSocketHandler.addMessage(paramSyncFilesId, "Load files to restore");
            String remotePath = (remoteHubicPath==null) ? "" : "/" + remoteHubicPath;
            List<InfoHubicObject> infoHubicObjectList = listAllObjects(hubicService, paramSyncFiles.getSlaveDir() + remotePath);

            File file = getSendedNewFile(paramSyncFiles.getId());
            syncfilesSocketHandler.addMessage(paramSyncFilesId, "Start to restore files");
            try (BufferedWriter loadedFileWriter = new BufferedWriter(new FileWriter(file, true))) {
                for (InfoHubicObject infoHubicObject : infoHubicObjectList) {
                    String relativeFileName = infoHubicObject.getName().substring(paramSyncFiles.getSlaveDir().length());
                    log.info("Début écriture du fichier : " + relativeFileName);
                    try (CloseableHttpResponse res = hubicService.loadObject("default", infoHubicObject.getName(), false)) {
                        String name = localPath + "/" + relativeFileName;
                        syncfilesSocketHandler.addMessage(paramSyncFilesId, "Restore file : " + name);
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
                syncfilesSocketHandler.addError(paramSyncFilesId, "Unable to restore file", e);
                log.error("Unable to download object from restore : " + e.getMessage());
                throw e;
            }
        } finally {
            syncfilesSocketHandler.addMessage(paramSyncFilesId, "End of restore !!!");
            syncfilesSocketHandler.removeSynchro(paramSyncFilesId);
        }
        getSendedFile(paramSyncFilesId).delete();
        getSendedNewFile(paramSyncFilesId).delete();
        running.set(false);
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
