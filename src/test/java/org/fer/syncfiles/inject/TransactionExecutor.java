package org.fer.syncfiles.inject;

import org.apache.log4j.Logger;

import com.google.inject.Injector;

public class TransactionExecutor extends AbstractExecutor {
	protected static final Logger log = Logger.getLogger(TransactionExecutor.class);

	public TransactionExecutor(Injector injector) {
		super(injector);
	}

	@Override
	public void execute() {
		DbTest dao = injector.getInstance(DbTest.class);
		dao.testDbTransaction();
		sleepWithoutException(200);
		dao.testDbTransaction();
	}

}
