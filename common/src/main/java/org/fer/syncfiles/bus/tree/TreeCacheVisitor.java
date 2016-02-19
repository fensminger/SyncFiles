package org.fer.syncfiles.bus.tree;

import org.infinispan.tree.*;
import org.infinispan.tree.Node;

import java.util.Map;

/**
 * Created by fer on 02/11/2014.
 */
public interface TreeCacheVisitor extends TreeVisitor<org.infinispan.tree.Node<Object, Object>> {

    @Override
    default void visit(Node<Object, Object> value) {
        Map<Object, Object> dataMap = value.getData();
        for(Object key : value.getKeys()) {
            visit(value.getFqn(), key, dataMap.get(key));
        }
    }

    void visit(Fqn fqn, Object key, Object o);
}
