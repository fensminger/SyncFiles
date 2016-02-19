package org.fer.lombok;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

public class GetterLazyExampleTest {
	Logger log = Logger.getLogger(GetterLazyExampleTest.class);

	@Test
	public void test() {
		GetterLazyExample getterLazyExample = new GetterLazyExample();
		
		long start = System.currentTimeMillis();
		double[] val1 = getterLazyExample.getCached();
		long step = System.currentTimeMillis();
		double[] val2 = getterLazyExample.getCached();
		long end = System.currentTimeMillis();
		
		Assert.assertEquals(val1, val2);
		
		long firstDuration = step-start;
		log.info("Duration time first getCached = " + firstDuration); 
		long secondDuration = end-step;
		log.info("Duration time second getCached = " + secondDuration); 
		
		Assert.assertTrue("First Access should be longer than second access", firstDuration>secondDuration);
	}
	
}
