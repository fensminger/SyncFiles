package org.fer.lombok;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class GetterSetterExample {

	@Getter	@Setter
	private int age = 10;
	@Setter(AccessLevel.PROTECTED)
	private String name;

	@Override
	public String toString() {
		return String.format("%s (age: %d)", name, age);
	}
}
