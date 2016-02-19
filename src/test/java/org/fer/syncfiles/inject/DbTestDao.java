package org.fer.syncfiles.inject;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.fer.syncfiles.bus.AbstractDao;

import com.google.inject.Inject;

import static org.junit.Assert.*;

public class DbTestDao extends AbstractDao {
	protected static final Logger log = Logger.getLogger(DbTestDao.class);

	@Inject
	public DbTestDao(EntityManager em) {
		super(em);
	}

	public void testTransaction() {
		boolean transActive = em.getTransaction().isActive();
		log.info("La transaction doit être active : " + transActive);
		assertTrue("La transaction doit être active.", transActive);
	}

	public void testWithoutTransaction() {
		boolean transActive = em.getTransaction().isActive();
		log.info("La transaction doit être inactive : " + transActive);
		assertFalse("La transaction doit être inactive.", transActive);
	}
	
	public boolean testEmOpen() {
		return em.isOpen();
	}

}
