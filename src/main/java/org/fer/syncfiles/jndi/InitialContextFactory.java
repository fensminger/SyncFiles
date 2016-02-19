package org.fer.syncfiles.jndi;

import java.util.Hashtable;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

public class InitialContextFactory implements javax.naming.spi.InitialContextFactory {
	private static final Logger log = Logger.getLogger(InitialContextFactory.class);

	private static volatile SimpleContext context = null;
	
	@Override
	public SimpleContext getInitialContext(Hashtable<?, ?> env)
			throws NamingException {
		log.debug("Params : "+env);
		if (context == null) {
			context = new SimpleContext();
		}
		return context;
	}

}
