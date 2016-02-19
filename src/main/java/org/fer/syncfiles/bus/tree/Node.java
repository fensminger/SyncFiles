package org.fer.syncfiles.bus.tree;

import java.util.Set;
import java.util.TreeSet;

public class Node<T extends Comparable<T>> implements Comparable<Node<T>>{
	private T value;
	
	private Set<Node<T>> children = new TreeSet<>();
	private Node<T> parent;

	public Node() {
		super();
		this.value = null;
	}
	
	public Node(T value) {
		super();
		this.value = value;
	}

	public void addChild(Node<T> childNode) {
		children.add(childNode);
		childNode.parent = this;
	}
	
	public void setParent(Node<T> parent) {
		parent.addChild(this); 
	}
	
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Set<Node<T>> getChildren() {
		return children;
	}

	public Node<T> getParent() {
		return parent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Node other = (Node) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public int compareTo(Node<T> o) {
		if (this==o) {
			return 0;
		}
		if (o == null) {
			return -1;
		}
		T v1 = getValue();
		T v2 = o.getValue();
		if (v1==v2) {
			return 0;
		}
		if (v2 == null) {
			return -1;
		}
		return v1.compareTo(v2);
	}

	public void walkTree(TreeVisitor<T> visitor) {
		visitor.visit(getValue());
		for(Node<T> child : getChildren()) {
			child.walkTree(visitor);
		}
	}
	
	public void walkTreeLeafFirst(TreeVisitor<T> visitor) {
		for(Node<T> child : getChildren()) {
			child.walkTreeLeafFirst(visitor);
		}
		visitor.visit(getValue());
	}
	
	public int getLevel() {
		if (parent==null) {
			return 1;
		} else {
			return 1 + parent.getLevel();
		}
	}
	
	public String getLevelStr() {
		if (parent==null) {
			return ">";
		} else {
			return "-" + parent.getLevelStr();
		}
	}
	
	public String getLevelValue() {
		return getLevelStr() + toString();
	}
	
	public String getTreeStr() {
		StringBuffer res = new StringBuffer();
		
		res.append(getLevelValue());
		res.append("\n");
		for(Node<T> child : getChildren()) {
			res.append(child.getTreeStr());
		}
		
		return res.toString();
	}
}
