package org.fer.syncfiles.bus.tree;

import org.fer.syncfiles.service.ParamService;
import org.infinispan.tree.Fqn;

/**
 * Created by fer on 02/11/2014.
 */
public class RemoveActionTreeCacheVisitor implements TreeCacheVisitor {
//    private static final Logger log = Logger.getLogger(RemoveActionTreeCacheVisitor.class);

    private final ParamService paramService;


    public RemoveActionTreeCacheVisitor(ParamService paramService) {
        this.paramService = paramService;
    }

    @Override
    public void visit(Fqn fqn, Object key, Object o) {
        paramService.removeTreeCache(fqn, key);
    }
}
