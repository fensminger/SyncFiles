package org.fer.syncfiles.inject;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.eclipse.persistence.exceptions.JPQLException;

import com.google.inject.Provider;

public class TransactionalInterceptor implements MethodInterceptor {
	private static final Logger log = Logger.getLogger(TransactionalInterceptor.class);
	
	private final Provider<EntityManager> entityManagerProvider;
	private final int NB_CALL = 5;

	public TransactionalInterceptor(Provider<EntityManager> entityManagerProvider) {
		super();
		this.entityManagerProvider = entityManagerProvider;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String msg = "Concurrent Exception";
		Throwable t = null;
		for(int i = 0; i < NB_CALL; i++) {
			EntityManager em = entityManagerProvider.get();
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			try {
				return invocation.proceed();
			} catch (IllegalArgumentException e) {
				if (e.getCause()!=null && e.getCause() instanceof JPQLException) {
					t = e.getCause();
					if (t.getCause()!=null) {
						t = t.getCause();
					}
					StringWriter sw = new StringWriter();
					PrintWriter s = new PrintWriter(sw);
					t.printStackTrace(s);
					msg = sw.toString();
					log.warn("Tentative d'appel numéro : " + (i+1) +", " +msg);
					sleepWithoutException(100*i);
				} else {
					throw e;
				}
			} finally {
				if (!tx.getRollbackOnly()) {
					tx.commit();
				}
				em.close();
			}
		}
		log.error(msg);
		throw t;
	}

	protected void sleepWithoutException(long milliSec) {
		try {
			Thread.sleep(milliSec);
		} catch (InterruptedException e) {
			log.warn(e.getMessage());
		}
	}
}
