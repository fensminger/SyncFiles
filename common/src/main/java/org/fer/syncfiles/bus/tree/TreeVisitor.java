package org.fer.syncfiles.bus.tree;

public interface TreeVisitor<T extends Object> {

	public void visit(T value);
}
