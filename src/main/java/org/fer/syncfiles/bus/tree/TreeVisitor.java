package org.fer.syncfiles.bus.tree;

public interface TreeVisitor<T extends Comparable<T>> {

	public void visit(T value);
}
