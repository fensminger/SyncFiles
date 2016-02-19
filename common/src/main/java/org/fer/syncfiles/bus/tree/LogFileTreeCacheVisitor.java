package org.fer.syncfiles.bus.tree;

import org.apache.log4j.Logger;
import org.infinispan.tree.Fqn;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * Created by fer on 02/11/2014.
 */
public class LogFileTreeCacheVisitor implements TreeCacheVisitor {
    private static final Logger log = Logger.getLogger(UpdateActionTreeCacheVisitor.class);
    private final PrintWriter writer;

    public LogFileTreeCacheVisitor(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void visit(Fqn fqn, Object key, Object o) {
        writer.println(fqn + " : " + key + " -> " + o.toString());
    }
}
