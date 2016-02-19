package org.fer.lombok;

import lombok.ToString;

@SuppressWarnings("unused")
@ToString(exclude="id")
public class ToStringExample {
	private static final int STATIC_VAR = 10;
	private String name;
	private Shape shape = new Square(50, 20);
	private String[] tags;
	private int id;

	public String getName() {
		return this.name;
	}

	@ToString(callSuper = false)
	public class Shape {

	}
	
	@ToString(callSuper = true, includeFieldNames = true)
	public class Square extends Shape {
		private final int width, height;

		public Square(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}
}