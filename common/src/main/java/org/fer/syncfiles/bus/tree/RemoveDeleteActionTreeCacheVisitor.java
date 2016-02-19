package org.fer.syncfiles.bus.tree;

import org.fer.syncfiles.model.FileInfo;
import org.fer.syncfiles.model.FileInfoAction;
import org.fer.syncfiles.service.ParamService;
import org.infinispan.tree.Fqn;

/**
 * Created by fer on 02/11/2014.
 */
public class RemoveDeleteActionTreeCacheVisitor implements TreeCacheVisitor {
//    private static final Logger log = Logger.getLogger(RemoveDeleteActionTreeCacheVisitor.class);
    private final ParamService paramService;

    public RemoveDeleteActionTreeCacheVisitor(ParamService paramService) {
        this.paramService = paramService;
    }

    @Override
    public void visit(Fqn fqn, Object key, Object o) {
        FileInfo fileInfo = (FileInfo) o;
        if (FileInfoAction.DELETE.equals(fileInfo.getFileInfoAction())) {
            paramService.removeTreeCache(fqn, key);
        }
    }
}
