package org.fer.lombok;

import org.apache.log4j.Logger;
import org.junit.Test;

import junit.framework.Assert;

public class GetterSetterExampleTest {
	Logger log = Logger.getLogger(GetterSetterExampleTest.class);
	
	@Test
	public void sample() {
		GetterSetterExample moi = new GetterSetterExample();
		
		moi.setAge(42);
		moi.setName("Fred");
		
		log.info(moi);
		
		Assert.assertEquals(42, moi.getAge());
		Assert.assertEquals("Fred (age: 42)", moi.toString());
	}
}
