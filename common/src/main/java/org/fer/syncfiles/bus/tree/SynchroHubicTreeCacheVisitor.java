package org.fer.syncfiles.bus.tree;

import org.apache.log4j.Logger;
import org.fer.syncfiles.dto.SynchroInfoDto;
import org.fer.syncfiles.model.FileInfo;
import org.fer.syncfiles.model.FileInfoAction;
import org.fer.syncfiles.model.OriginFile;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.service.FeedbackEvent;
import org.fer.syncfiles.service.ParamService;
import org.infinispan.tree.Fqn;
import org.javaswift.joss.exception.CommandException;

/**
 * Created by fer on 02/11/2014.
 */
public class SynchroHubicTreeCacheVisitor implements TreeCacheVisitor {
    private static final Logger log = Logger.getLogger(SynchroHubicTreeCacheVisitor.class);
    private final ParamService paramService;
    private final FeedbackEvent feedBackEvent;

    private Param param;
    private final Fqn fqnParam;

    private long nbSourceFile=0;
    private long nbTargetFile=0;
    private long nbSourceTargetFile=0;
    private long nbNewFile=0;
    private long nbUpdateFile=0;
    private long nbRemoveFile=0;


    public SynchroHubicTreeCacheVisitor(Param param, ParamService paramService, FeedbackEvent feedbackEvent) {
        this.param = param;
        this.fqnParam = Fqn.fromString(param.getKey());
        this.paramService = paramService;
        this.feedBackEvent = feedbackEvent;
    }

    @Override
    public void visit(Fqn fqn, Object key, Object o) {
        FileInfo fileInfo = (FileInfo) o;
        if (fileInfo.getOriginFile().equals(OriginFile.SOURCE)) {
            nbSourceFile++;
        } else if (fileInfo.getOriginFile().equals(OriginFile.TARGET)) {
            nbTargetFile++;
        } else {
            nbSourceTargetFile++;
        }

        switch (fileInfo.getFileInfoAction()) {
            case CREATE:
                FileInfo fileInfoCreate = new FileInfo();
                fileInfoCreate.setRelativePathString(fileInfo.getRelativePathString());
                fileInfoCreate.setOriginFile(OriginFile.TARGET);
                fileInfoCreate.setHash(fileInfo.getHash());
                fileInfoCreate.setLastAccessTime(fileInfo.getLastAccessTime());
                fileInfoCreate.setLastModifiedTime(fileInfo.getLastModifiedTime());
                fileInfoCreate.setSize(fileInfo.getSize());
                fileInfoCreate.setParamKey(fileInfo.getParamKey());
                fileInfoCreate.setDirectory(false);
                fileInfoCreate.setRegularFile(true);
                nbNewFile++;
//                log.info("SynchroHubicTreeCacheVisitor: Fichier crée : " + fileInfo);
                paramService.updateFileToHubic(param, fileInfo, "File new uploaded to Hubic : ");
                paramService.putTreeCache(param.getKey(), fileInfoCreate);
                break;
            case UPDATE:
                FileInfo fileInfoUpdate = paramService.getTreeCacheByOrigin(fqn, OriginFile.TARGET);
                paramService.initPreviousData(fileInfoUpdate);
                fileInfoUpdate.setHash(fileInfo.getHash());
                fileInfoUpdate.setLastAccessTime(fileInfo.getLastAccessTime());
                fileInfoUpdate.setLastModifiedTime(fileInfo.getLastModifiedTime());
                fileInfoUpdate.setSize(fileInfo.getSize());
//                log.info("SynchroHubicTreeCacheVisitor: Fichier mis à jour : " + fileInfo);
                nbUpdateFile++;
                paramService.updateFileToHubic(param, fileInfo, "File update to Hubic : ");
                paramService.putTreeCache(param.getKey(), fileInfoUpdate);
                break;
            case DELETE:
                final String targetFileName = param.getSlaveDir().substring(1) + "/" + fileInfo.getRelativePathString();
                try {
                    paramService.deleteHubicObject(targetFileName);
                    nbRemoveFile++;
//                log.info("SynchroHubicTreeCacheVisitor: Fichier supprimé : " + targetFileName);
                    paramService.removeTreeCache(param.getKey(), fileInfo);
                } catch (CommandException e) {
                    log.info("SynchroHubicTreeCacheVisitor: Impossible de supprimer le fichier : " + targetFileName);
                    log.error(e);
                }
                break;
            default:
        }
        fileInfo.setFileInfoAction(FileInfoAction.NOTHING);
        paramService.putTreeCache(param.getKey(), fileInfo);
    }

    public void logStat() {
        final String message = "Stat maj nbSourceFile=" + nbSourceFile
                + ", nbTargetFile=" + nbTargetFile
                + ", nbSourceTargetFile=" + nbSourceTargetFile
                + ", nbNewFile=" + nbNewFile
                + ", nbUpdateFile=" + nbUpdateFile
                + ", nbRemoveFile=" + nbRemoveFile;
        log.info(message);
        feedBackEvent.sendEvent(new SynchroInfoDto(message));
    }
}
