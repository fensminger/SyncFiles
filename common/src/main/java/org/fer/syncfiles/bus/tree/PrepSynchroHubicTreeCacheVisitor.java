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

import static org.fer.syncfiles.model.FileInfoAction.CREATE;
import static org.fer.syncfiles.model.FileInfoAction.DELETE;
import static org.fer.syncfiles.model.FileInfoAction.UPDATE;

/**
 * Created by fer on 02/11/2014.
 */
public class PrepSynchroHubicTreeCacheVisitor implements TreeCacheVisitor {
    private static final Logger log = Logger.getLogger(PrepSynchroHubicTreeCacheVisitor.class);
    private final ParamService paramService;
    private final FeedbackEvent feedBackEvent;

    private Param param;
    private final Fqn fqnParam;

    private long nbSourceFile=0;
    private long nbTargetFile=0;
    private long nbNewFile=0;
    private long nbUpdateFile=0;
    private long nbRemoveFile=0;


    public PrepSynchroHubicTreeCacheVisitor(Param param, ParamService paramService, FeedbackEvent feedbackEvent) {
        this.param = param;
        this.paramService = paramService;
        this.fqnParam = Fqn.fromString(param.getKey());
        this.feedBackEvent = feedbackEvent;
    }

    @Override
    public void visit(Fqn fqn, Object key, Object o) {
        FileInfo fileInfo = (FileInfo) o;
        fileInfo.setFileInfoAction(FileInfoAction.NOTHING);
        if (!fileInfo.isDirectory() && !"".equals(fileInfo.getRelativePathString())) {
            if (fileInfo.getOriginFile().equals(OriginFile.SOURCE)) {
                nbSourceFile++;
                FileInfo fileInfoTarget = paramService.getTreeCacheByOrigin(fqn, OriginFile.TARGET);
                if (fileInfoTarget == null || !fileInfoTarget.getHash().equals(fileInfo.getHash())) {
                    if (fileInfoTarget == null) {
                        fileInfo.setFileInfoAction(CREATE);
                        log.info("File new to upload : " + fileInfo.getRelativePathString());
                        nbNewFile++;
                    } else {
                        if (!fileInfo.getHash().equals(fileInfoTarget.getHash())) {
                            log.info("File updated to uploaded : " + fileInfo.getRelativePathString() + " , " + fileInfo.getHash() + " : " + fileInfoTarget.getHash());
                            fileInfo.setFileInfoAction(UPDATE);
                            nbUpdateFile++;
                        }
                    }
                }
            } else {
                nbTargetFile++;
                FileInfo fileInfoSource = paramService.getTreeCacheByOrigin(fqn, OriginFile.SOURCE);
                if (fileInfoSource == null) {
                    fileInfo.setFileInfoAction(DELETE);
                    log.info("File removed to Hubic : " + fileInfo.getRelativePathString());
                    nbRemoveFile++;
                }
            }
            paramService.putTreeCache(param.getKey(), fileInfo);
        }
    }

    public void logStat() {
        final String message = "Stat maj nbSourceFile=" + nbSourceFile
                + ", nbTargetFile=" + nbTargetFile
                + ", nbNewFile=" + nbNewFile
                + ", nbUpdateFile=" + nbUpdateFile
                + ", nbRemoveFile=" + nbRemoveFile;
        log.info(message);
        feedBackEvent.sendEvent(new SynchroInfoDto(message));
    }
}
