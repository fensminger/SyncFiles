package org.fer.syncfiles.service;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.fer.syncfiles.dto.SynchroInfoDto;
import org.fer.syncfiles.model.*;
import org.javaswift.joss.exception.CommandException;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.DirectoryOrObject;
import org.javaswift.joss.model.StoredObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.swiftexplorer.config.Configuration;
import org.swiftexplorer.config.localization.HasLocalizationSettings;
import org.swiftexplorer.swift.SwiftAccess;
import org.swiftexplorer.swift.client.factory.AccountConfigFactory;
import org.swiftexplorer.swift.operations.CallBackInfo;
import org.swiftexplorer.swift.operations.SwiftOperations;
import org.swiftexplorer.swift.operations.SwiftOperationsImpl;
import org.swiftexplorer.swift.util.HubicBatchSwift;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by fer on 01/10/2014.
 */
@Service("HubicSynchro")
@Scope("singleton")
public class HubicSynchroImpl implements HubicSynchro {
    private static final Logger log = Logger.getLogger(HubicSynchroImpl.class);

    Container mainContainer ;

    @Autowired
    FeedbackEvent feedbackEvent;

    @Override
    public Container getMainContainer() {
        if (mainContainer==null) {
            initHubic();
        }
        return mainContainer;
    }

    @Override
    public void loadAllHubicFilesIntoCache(String prefix, ParamService paramService) throws IOException {
        final int pageSize = 9999;
        int recordsToGo = getMainContainer().getCount();
        String marker = null;
        int nbWithSlash = 0;
        feedbackEvent.sendEvent(new SynchroInfoDto("Début de lecture de l'arborescence Hubic"));
        try (LocalFileJsonWriter localFileJsonWriter = new LocalFileJsonWriter(ParamService.HUBIC_KEY_TREE)) {
            while (recordsToGo > 0) {
                log.info("Début de page : " + marker);
                Collection<DirectoryOrObject> res = mainContainer.listDirectory(null, null, marker, pageSize); // getAllContainedStoredObject(mainContainer, null);
                for (DirectoryOrObject storedObject : res) {
                    //log.info("Is Object :   " + storedObject.isObject() + " -> " + storedObject.getName());
                    if (storedObject.isObject()) {
                        final StoredObject asObject = storedObject.getAsObject();
                        try {
                            String relativePathString = asObject.getName();
                            if (relativePathString.startsWith("/")) {
                                nbWithSlash++;
                                log.warn("Un fichier Hubic commence par / : " + relativePathString + " : " + asObject.getEtag());
                                asObject.delete();
                            } else {
                                final FileInfo hubicFileInfo = new FileInfo();
                                hubicFileInfo.setHash(asObject.getEtag());
                                hubicFileInfo.setLastModifiedTime(asObject.getLastModifiedAsDate());
                                hubicFileInfo.setSize(asObject.getContentLength());
                                hubicFileInfo.setRelativePathString(relativePathString);
                                hubicFileInfo.setRegularFile(asObject.isObject());
                                hubicFileInfo.setDirectory(asObject.isDirectory());
                                hubicFileInfo.setOther(false);
                                hubicFileInfo.setOriginFile(OriginFile.TARGET);
                                hubicFileInfo.setFileInfoAction(FileInfoAction.CREATE);
                                hubicFileInfo.setParamKey(prefix);
                                if (hubicFileInfo.getSize()==0) {
                                    // Je ne sais pas pour l'instant différencier un fichier de taille 0 et
                                    // un répertoire de taille 0
                                    // Je privilégie le répertoire car plus fréquent.
                                    hubicFileInfo.setDirectory(true);
                                    hubicFileInfo.setRegularFile(false);
                                }
                                localFileJsonWriter.writeObject(hubicFileInfo);
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            log.error("Load to hubic : " + storedObject.getName() + " <--> " + asObject.getName(), e);
                        }
                    }
                    marker = storedObject.getName();
                }
                recordsToGo -= res.size() == 0 ? recordsToGo : (res.size() < pageSize ? res.size() : pageSize);
                feedbackEvent.sendEvent(new SynchroInfoDto("Nombre de fichiers restant à lire dans l'arborescence hubic " + recordsToGo));
            }
        }
        feedbackEvent.sendEvent(new SynchroInfoDto("Fin de lecture de l'arborescence Hubic"));
        final String message = "Nb nbWithSlash : " + nbWithSlash;
        feedbackEvent.sendEvent(new SynchroInfoDto(message));
        log.error(message);
    }

    private Container initHubic() {
        String settingsFile = "swiftexplorer-settings.xml" ;
        if (!new File(settingsFile).exists()) {
            settingsFile = null ;
            log.warn("Le fichier swiftexplorer-settings.xml n'a malheureusement pas ete trouve.");
        } else {
            final String message = "Le fichier swiftexplorer-settings.xml a ete trouve....";
            log.info(message);
            feedbackEvent.sendEvent(new SynchroInfoDto(message));
        }
        try
        {
            Configuration.INSTANCE.load(settingsFile);
        }
        catch (ConfigurationException e)
        {
            log.error("Error occurred while initializing the configuration", e);
            feedbackEvent.sendEvent(new SynchroInfoDto("Error occurred while initializing the configuration : " + e.getMessage()));
        }


        HasLocalizationSettings localizationSettings = Configuration.INSTANCE.getLocalizationSettings() ;
        if (localizationSettings != null)
        {
            Locale.Builder builder = new Locale.Builder () ;
            HasLocalizationSettings.LanguageCode lang = localizationSettings.getLanguage() ;
            HasLocalizationSettings.RegionCode reg = localizationSettings.getRegion() ;
            builder.setLanguage(lang.toString()) ;
            if (reg != null)
                builder.setRegion(reg.toString()) ;
            else
                builder.setRegion("") ;
        }

        SwiftAccess access = HubicBatchSwift.getSwiftAccess();

        final SwiftOperations ops = new SwiftOperationsImpl() ;
        final CallBackInfo callBackInfo = new CallBackInfo();

        SwiftOperations.SwiftCallback swiftCallBack = new SwiftOperations.SwiftCallback() {
            @Override
            public void onStart() {
                log.info("onStart");
            }

            @Override
            public void onDone() {
                log.info("onDone");
            }

            @Override
            public void onError(CommandException ex) {
                log.info("onError");
            }

            @Override
            public void onUpdateContainers(Collection<Container> containers) {
                log.info("onUpdateContainers");
                callBackInfo.containers = containers;
            }

            @Override
            public void onNewStoredObjects() {
                log.info("onNewStoredObjects");
            }

            @Override
            public void onAppendStoredObjects(Container container, int page, Collection<StoredObject> storedObjects) {
                log.info("onAppendStoredObjects");
            }

            @Override
            public void onLoginSuccess() {
                log.info("onLoginSuccess -> yes");
            }

            @Override
            public void onLogoutSuccess() {
                log.info("onLogoutSuccess");
            }

            @Override
            public void onContainerUpdate(Container container) {
                log.info("onContainerUpdate");
            }

            @Override
            public void onStoredObjectUpdate(StoredObject obj) {
                log.info("onStoredObjectUpdate");
            }

            @Override
            public void onNumberOfCalls(int nrOfCalls) {
                log.info("onNumberOfCalls : " + nrOfCalls);
            }

            @Override
            public void onStoredObjectDeleted(Container container, StoredObject storedObject) {
                log.info("onStoredObjectDeleted");
            }

            @Override
            public void onStoredObjectDeleted(Container container, Collection<StoredObject> storedObjects) {
                log.info("onStoredObjectDeleted");
            }

            @Override
            public void onProgress(double totalProgress, String totalMsg, double currentProgress, String currentMsg) {
                log.info("onProgress");
            }

            @Override
            public void onStopped() {
                log.info("onStopped");
            }
        };

        ops.login(AccountConfigFactory.getHubicAccountConfig(access), Configuration.INSTANCE.getHttpProxySettings(), swiftCallBack);

        ops.refreshContainers(swiftCallBack);

        for (Container container : callBackInfo.containers) {
            log.info("Container name : " + container.getName());
            if ("default".equals(container.getName())) {
                mainContainer = container;
            }
        }
        feedbackEvent.sendEvent(new SynchroInfoDto("La connexion à Hubic est effectuée."));
        return mainContainer;
    }

    @Override
    public void updateFileToHubic(Param param, FileInfo fileInfo, String msg) {
        boolean uploaded = false;

        while (!uploaded) {
            try {
                if (fileInfo.isRegularFile()) {
                    final String targetFileName = param.getSlaveDir().substring(1) + "/" + fileInfo.getRelativePathString();
                    final String sourceFileName = param.getMasterDir() + "/" + fileInfo.getRelativePathString();
                    StoredObject storedObjectToUpload = getMainContainer().getObject(targetFileName);
                    feedbackEvent.sendEvent(new SynchroInfoDto("Début du téléversement du fichier " + fileInfo.getRelativePathString() + " sous hubic"));
                    final File fileToUpload = new File(sourceFileName);
                    if (fileToUpload.exists()) {
                        storedObjectToUpload.uploadObject(fileToUpload);
                        log.info(msg + targetFileName + " from " + sourceFileName);
                    } else {
                        log.warn("File are removed from local system. Skip this file : " + sourceFileName);
                    }
                    feedbackEvent.sendEvent(new SynchroInfoDto("Fin du téléversement du fichier " + fileInfo.getRelativePathString() + " sous hubic"));
                }
                uploaded = true;
            } catch (CommandException e) {
                mainContainer = null;
                log.error("Retry to upload", e);
            }
        }
    }

    @Override
    public void deleteHubicObject(String targetFileName) {
        boolean uploaded = false;

        while (!uploaded) {
            try {
                StoredObject storedObjectToDelete = getMainContainer().getObject(targetFileName);
                storedObjectToDelete.delete();
                uploaded = true;
                log.info("Fichier supprimé : " + targetFileName);
                feedbackEvent.sendEvent(new SynchroInfoDto("Fin de suppression du fichier " + targetFileName + " sous hubic"));
            } catch (CommandException e) {
                mainContainer = null;
                log.error("Retry to upload", e);
            }
        }
    }


}
