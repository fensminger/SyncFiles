package org.fer.syncfiles.jndi;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.jolbox.bonecp.BoneCPDataSource;

public class SimpleContext implements javax.naming.Context {
	private static final Logger log = Logger.getLogger(SimpleContext.class);

	public SimpleContext() {
		super();
	}

	@Override
	public Object addToEnvironment(String arg0, Object arg1)
			throws NamingException {
		log.debug(arg0 + ", " + arg1);
		return null;
	}

	@Override
	public void bind(Name arg0, Object arg1) throws NamingException {
		log.debug(arg0 + ", " + arg1);
		
	}

	@Override
	public void bind(String arg0, Object arg1) throws NamingException {
		log.debug(arg0 + ", " + arg1);
		
	}

	@Override
	public void close() throws NamingException {
		log.debug("Close");
	}

	@Override
	public Name composeName(Name arg0, Name arg1) throws NamingException {
		log.debug(arg0 + ", " + arg1);
		return null;
	}

	@Override
	public String composeName(String arg0, String arg1) throws NamingException {
		log.debug(arg0 + ", " + arg1);
		return null;
	}

	@Override
	public javax.naming.Context createSubcontext(Name arg0)
			throws NamingException {
		log.debug(arg0);
		return null;
	}

	@Override
	public javax.naming.Context createSubcontext(String arg0)
			throws NamingException {
		log.debug(arg0);
		return null;
	}

	@Override
	public void destroySubcontext(Name arg0) throws NamingException {
		log.debug(arg0);
		
	}

	@Override
	public void destroySubcontext(String arg0) throws NamingException {
		log.debug(arg0);
		
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		log.debug("return");
		return null;
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		log.debug("return");
		return null;
	}

	@Override
	public NameParser getNameParser(Name arg0) throws NamingException {
		log.debug(arg0);
		return null;
	}

	@Override
	public NameParser getNameParser(String arg0) throws NamingException {
		log.debug(arg0);
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name arg0)
			throws NamingException {
		log.debug(arg0);
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String arg0)
			throws NamingException {
		log.debug(arg0);
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name arg0)
			throws NamingException {
		log.debug(arg0);
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String arg0)
			throws NamingException {
		log.debug(arg0);
		return null;
	}

	@Override
	public Object lookup(Name name) throws NamingException {
		return lookup(name.toString());
	}

	private volatile BoneCPDataSource ds = null;
	
	@Override
	public Object lookup(String jndiName) throws NamingException {
		log.debug(jndiName);
		if ("jdbc/syncfiles".equals(jndiName)) {
			if (ds==null) {
				ds = new BoneCPDataSource();
				try {
					Class.forName("org.hsqldb.jdbcDriver");
				} catch (ClassNotFoundException e) {
					log.error(e);
				}
				ds.setJdbcUrl("jdbc:hsqldb:file:dbSyncFiles");
				ds.setUsername("sa");
				ds.setTransactionRecoveryEnabled(true);
			}
			log.info("get DataSource -> " + ds);
			return ds;
		}
		return null;
	}

	@Override
	public Object lookupLink(Name arg0) throws NamingException {
		log.debug(arg0);
		return null;
	}

	@Override
	public Object lookupLink(String arg0) throws NamingException {
		log.debug(arg0);
		return null;
	}

	@Override
	public void rebind(Name arg0, Object arg1) throws NamingException {
		log.debug(arg0 + ", " + arg1);
		
	}

	@Override
	public void rebind(String arg0, Object arg1) throws NamingException {
		log.debug(arg0 + ", " + arg1);
		
	}

	@Override
	public Object removeFromEnvironment(String arg0) throws NamingException {
		log.debug(arg0);
		return null;
	}

	@Override
	public void rename(Name arg0, Name arg1) throws NamingException {
		log.debug(arg0 + ", " + arg1);
		
	}

	@Override
	public void rename(String arg0, String arg1) throws NamingException {
		log.debug(arg0 + ", " + arg1);
		
	}

	@Override
	public void unbind(Name arg0) throws NamingException {
		log.debug(arg0);
		
	}

	@Override
	public void unbind(String arg0) throws NamingException {
		log.debug(arg0);
		
	}

}
