package org.fer.syncfiles.service;

import org.apache.log4j.Logger;
import org.fer.syncfiles.model.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by fer on 12/10/2014.
 */
@Service
@Scope("singleton")
public class AsyncSynchroService {
    private static final Logger log = Logger.getLogger(AsyncSynchroService.class);

    private int nbThread = 1;
    private Semaphore semaphore;
    private Map<String, Thread> threadRunningMap = new HashMap<>();

    @Autowired
    ParamService paramService;

    public AsyncSynchroService() {
        semaphore = new Semaphore(nbThread, true);
    }

    public boolean startPrepUploadHubicSynchro(Param param, boolean isSimulation) {
        boolean acquired = semaphore.tryAcquire();

        if (acquired) {
            PrepUploadHubicThread prepUploadHubicThread = new PrepUploadHubicThread(param, isSimulation);
            prepUploadHubicThread.start();
            return true;
        } else {
            return false;
        }
    }

    public boolean startUploadHubicSynchro(Param param, boolean isSimulation) {
        boolean acquired = semaphore.tryAcquire();

        if (acquired) {
            UploadHubicThread uploadHubicThread = new UploadHubicThread(param, isSimulation);
            uploadHubicThread.start();
            return true;
        } else {
            return false;
        }
    }

    public boolean startPrepareSynchro(Param param, boolean isSimulation) {
        boolean acquired = semaphore.tryAcquire();

        if (acquired) {
            PrepareSynchroThread prepareSynchroThread = new PrepareSynchroThread(param, isSimulation);
            prepareSynchroThread.start();
            return true;
        } else {
            return false;
        }
    }

    public boolean startPrepareHubicFiles() {
        boolean acquired = semaphore.tryAcquire();

        if (acquired) {
            PrepareHubicFileThread prepareHubicFiles = new PrepareHubicFileThread();
            prepareHubicFiles.start();
            return true;
        } else {
            return false;
        }
    }

    public boolean startSynchronizeHubic(Param param, boolean hubicCheck, boolean isSimulation) {
        boolean acquired = semaphore.tryAcquire();

        if (acquired) {
            SynchronizeHubicThread synchronizeHubicThread = new SynchronizeHubicThread(param, hubicCheck, isSimulation);
            synchronizeHubicThread.start();
            log.info("Traitement startSynchronizeHubic lancé.");
            return true;
        } else {
            log.info("Un traitement batch est déjà en cours.");
            return false;
        }
    }

    private class SynchronizeHubicThread extends Thread {
        private final boolean isSimulation;
        private final boolean hubicCheck;
        private Param param;
        private boolean isAskStop = false;

        public SynchronizeHubicThread(Param param, boolean hubicCheck, boolean isSimulation) {
            setName(param.getKey() + " : " + param.getName());
            this.param = param;
            this.isSimulation = isSimulation;
            this.hubicCheck = hubicCheck;
            threadRunningMap.put(param.getKey(), this);
        }

        @Override
        public void run() {
            try {
                log.info("End upload file to hubic, now start to load hubic Files.");
                if (hubicCheck) {
                    paramService.initCacheTreeForAllHubicFiles();
                    paramService.printHubicCache("./hubicLoaded.txt", param.getKey());
                }

                //HubicPrep
                paramService.prepFilesToSynchronize(param);
                paramService.printHubicCache("./hubicPrep.txt", param.getKey());
                //HubicSync
                paramService.prepSynchroSourceTargetHubic(param);
                paramService.printHubicCache("./hubicSync.txt", param.getKey());

                // Bouton synchronize
                if (!isSimulation) {
                    paramService.synchroHubic(param);
                    paramService.printHubicCache("./hubicUpload.txt", param.getKey());
                } else {
                    paramService.writeTreeToJson(param.getKey());
                }

                log.info("End synchronize hubic");
            } catch (IOException e) {
                log.error("Problème : " + e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (ParseException e) {
                log.error("Problème : " + e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (Throwable e) {
                log.error("Problème d'upload : " + e.getMessage(), e);
                throw e;
            } finally {
                threadRunningMap.remove(param.getKey());
                semaphore.release();
            }
        }

        public void stopUpload() {
            isAskStop = true;
        }
    }

    private class PrepUploadHubicThread extends Thread {
        private final boolean isSimulation;
        private Param param;
        private boolean isAskStop = false;

        public PrepUploadHubicThread(Param param, boolean isSimulation) {
            setName(param.getKey() + " : " + param.getName());
            this.param = param;
            this.isSimulation = isSimulation;
            threadRunningMap.put(param.getKey(), this);
        }

        @Override
        public void run() {
            try {
                paramService.prepSynchroSourceTargetHubic(param);
                log.info("End Prep upload file to hubic");
            } catch (Throwable e) {
                log.error("Problème d'upload : " + e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                threadRunningMap.remove(param.getKey());
                semaphore.release();
            }
        }

        public void stopUpload() {
            isAskStop = true;
        }
    }

    private class UploadHubicThread extends Thread {
        private final boolean isSimulation;
        private Param param;
        private boolean isAskStop = false;

        public UploadHubicThread(Param param, boolean isSimulation) {
            setName(param.getKey() + " : " + param.getName());
            this.param = param;
            this.isSimulation = isSimulation;
            threadRunningMap.put(param.getKey(), this);
        }

        @Override
        public void run() {
            try {
                paramService.synchroHubic(param);
                log.info("End upload file to hubic, now start to load hubic Files.");
            } catch (Throwable e) {
                log.error("Problème d'upload : " + e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                threadRunningMap.remove(param.getKey());
                semaphore.release();
            }
        }

        public void stopUpload() {
            isAskStop = true;
        }
    }

    private class PrepareSynchroThread extends Thread {
        private final boolean isSimulation;
        private Param param;
        private boolean isAskStop = false;

        public PrepareSynchroThread(Param param, boolean isSimulation) {
            setName(param.getKey() + " : " + param.getName());
            this.param = param;
            this.isSimulation = isSimulation;
            threadRunningMap.put(param.getKey(), this);
        }

        @Override
        public void run() {
            try {
                paramService.prepFilesToSynchronize(param);
                log.info("End prepare synchro");
            } catch (IOException e) {
                log.error("Problème de préparation : " + e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (ParseException e) {
                log.error("Problème de préparation : " + e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (Throwable e) {
                log.error("Problème de préparation : " + e.getMessage(), e);
                throw e;
            } finally {
                threadRunningMap.remove(param.getKey());
                semaphore.release();
            }
        }

        public void stopPrepareSynchro() {
            isAskStop = true;
        }
    }

    private class PrepareHubicFileThread extends Thread {
        private boolean isAskStop = false;
        final String key = "LOAD Hubic Files";

        public PrepareHubicFileThread() {
            setName(key);
            threadRunningMap.put(key, this);
        }

        @Override
        public void run() {
            try {
                paramService.initCacheTreeForAllHubicFiles();
                log.info("End prepare synchro");
            } catch (Throwable e) {
                log.error("Problème de préparation : " + e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                threadRunningMap.remove(key);
                semaphore.release();
            }
        }

        public void stopPrepareSynchro() {
            isAskStop = true;
        }
    }

}
