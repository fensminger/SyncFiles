package org.fer.lombok;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ToStringExampleTest {
	Logger log = Logger.getLogger(ToStringExampleTest.class);

	@Test
	public void test() {
		ToStringExample toStringVal = new ToStringExample();
		
		log.info("toString : " + toStringVal.toString());
		Assert.assertEquals("ToStringExample(name=null, shape=ToStringExample.Square(super=ToStringExample.Shape(), width=50, height=20), tags=null)", toStringVal.toString());
	}
}
