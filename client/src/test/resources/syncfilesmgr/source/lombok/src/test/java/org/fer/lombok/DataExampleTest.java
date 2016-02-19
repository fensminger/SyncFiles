package org.fer.lombok;

import static org.junit.Assert.*;

import org.junit.Test;

public class DataExampleTest {

	@Test
	public void testEqualsObject() {
		DataExample example = new DataExample("Test");
		
		assertFalse(example.equals("Different"));
		assertTrue(example.equals(example));
	}

}
