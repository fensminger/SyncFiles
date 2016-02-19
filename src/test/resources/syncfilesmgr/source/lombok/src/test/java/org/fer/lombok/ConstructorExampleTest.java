package org.fer.lombok;

import org.junit.Assert;
import org.junit.Test;

public class ConstructorExampleTest {
	
	@Test
	public void testConstructorExample() {
		String description = "Descr";
		ConstructorExample<String> testConstructor = new ConstructorExample<String>(1,2,description);
		
		Assert.assertEquals(1, testConstructor.x);
		Assert.assertEquals(2, testConstructor.y);
		Assert.assertEquals(description, testConstructor.description);

		try {
			@SuppressWarnings("unused")
			ConstructorExample<String> testNullConstructor = new ConstructorExample<String>(1,2,null);
			Assert.fail("Null pointer exception expected");
		} catch (NullPointerException e) {
			Assert.assertTrue(true);
		}
	
		String aString = "string";
		ConstructorExample<String> ofTest = ConstructorExample.of(aString);
		Assert.assertEquals(0, ofTest.x);
		Assert.assertEquals(0, ofTest.y);
		Assert.assertEquals(aString, ofTest.description);
	}

}
