package org.fer.syncfiles.inject;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

import static org.junit.Assert.*;

public class DbTest {
	private final Provider<DbTestDao> dbTestDaoProvider;
	private final Provider<EntityManager> emProvider;

	@Inject
	public DbTest(Provider<DbTestDao> dbTestDaoProvider, Provider<EntityManager> emProvider) {
		super();
		this.dbTestDaoProvider = dbTestDaoProvider;
		this.emProvider = emProvider;
	}
	
	
	@Transactional
	public void testDbTransaction() {
		dbTestDaoProvider.get().testTransaction();
	}
	
	public void testDbWithoutTransaction() {
		dbTestDaoProvider.get().testWithoutTransaction();
	}
	
	public void testEmOpen() {
		DbTestDao dao = dbTestDaoProvider.get();
		
		assertTrue(dao.testEmOpen());
		EntityManager em = emProvider.get();
		em.close();
		assertFalse(dao.testEmOpen());
		dao = dbTestDaoProvider.get();
		assertTrue(dao.testEmOpen());
		em = emProvider.get();
		assertTrue(em.isOpen());
	}
}
