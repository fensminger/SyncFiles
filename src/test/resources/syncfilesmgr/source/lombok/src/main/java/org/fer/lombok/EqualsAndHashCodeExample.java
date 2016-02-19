package org.fer.lombok;

import lombok.EqualsAndHashCode;

@SuppressWarnings("unused")
@EqualsAndHashCode(exclude = { "id", "shape" })
public class EqualsAndHashCodeExample {
	private transient int transientVar = 10;
	private String name;
	private double score;
	private Shape shape = new Square(5, 10);
	private String[] tags;
	private int id;

	public String getName() {
		return this.name;
	}

	public class Shape {

	}
	
	@EqualsAndHashCode(callSuper = true)
	public class Square extends Shape {
		private final int width, height;

		public Square(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}
}