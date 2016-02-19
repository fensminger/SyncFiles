package org.fer.syncfiles.inject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import static com.google.inject.matcher.Matchers.*;

public class SyncFilesModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(EntityManager.class).toProvider(EntityManagerProvider.class);
		binder.bindInterceptor(any(), annotatedWith(Transactional.class), 
				new TransactionalInterceptor(binder.getProvider(EntityManager.class)) );
	}

	@Provides @Singleton
	public EntityManagerFactory provideEntityManagerFactory() {
		return Persistence.createEntityManagerFactory("syncFilesUnit");
	}
	
}
