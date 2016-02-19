package org.fer.syncfiles.bus.tree;

import org.apache.log4j.Logger;
import org.infinispan.tree.Fqn;

/**
 * Created by fer on 02/11/2014.
 */
public class LogTreeCacheVisitor implements TreeCacheVisitor {
    private static final Logger log = Logger.getLogger(UpdateActionTreeCacheVisitor.class);

    public LogTreeCacheVisitor() {
    }

    @Override
    public void visit(Fqn fqn, Object key, Object o) {
        log.info(" " + fqn + ":" + key + " -> " + o.toString());
    }
}
