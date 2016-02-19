package org.fer.lombok;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Delegate;

public class DelegationExample {
	private interface SimpleCollection {
		boolean add(String item);

		boolean remove(Object item);
	}

	@Delegate(types = SimpleCollection.class)
	private final Collection<String> collection = new ArrayList<String>();
}

