package org.fer.syncfiles.bus.tree;

import org.fer.syncfiles.model.FileInfo;
import org.fer.syncfiles.model.FileInfoAction;
import org.fer.syncfiles.model.OriginFile;
import org.fer.syncfiles.service.ParamService;
import org.infinispan.tree.Fqn;

/**
 * Created by fer on 02/11/2014.
 */
public class UpdateActionTreeCacheVisitor implements TreeCacheVisitor {
//    private static final Logger log = Logger.getLogger(UpdateActionTreeCacheVisitor.class);
    private final OriginFile originFile;
    private final ParamService paramService;

    private FileInfoAction actionToApply;

    public UpdateActionTreeCacheVisitor(ParamService paramService, FileInfoAction actionToApply, OriginFile originFile) {
        this.paramService = paramService;
        this.actionToApply = actionToApply;
        this.originFile = originFile;
    }

    @Override
    public void visit(Fqn fqn, Object key, Object o) {
        FileInfo fileInfo = (FileInfo) o;
        if (originFile.equals(fileInfo.getOriginFile())) {
            fileInfo.setFileInfoAction(actionToApply);
            paramService.putTreeCache(fqn, fileInfo);
        }
    }
}
