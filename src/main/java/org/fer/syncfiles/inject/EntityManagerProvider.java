package org.fer.syncfiles.inject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class EntityManagerProvider implements Provider<EntityManager> {

	private EntityManagerFactory emf;
	
	private static ThreadLocal<EntityManager> entityManagerLocal = new ThreadLocal<>();
	
	@Inject
	public EntityManagerProvider(EntityManagerFactory emf) {
		super();
		this.emf = emf;
	}

	@Override
	public EntityManager get() {
		EntityManager entityManager = entityManagerLocal.get();
		if (entityManager==null || !entityManager.isOpen()) {
			entityManager = emf.createEntityManager();
			entityManagerLocal.set(entityManager);
		}
		return entityManager;
	}

}
