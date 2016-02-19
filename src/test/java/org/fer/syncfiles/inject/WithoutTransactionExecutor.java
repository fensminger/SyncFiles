package org.fer.syncfiles.inject;

import com.google.inject.Injector;

public class WithoutTransactionExecutor extends AbstractExecutor {

	public WithoutTransactionExecutor(Injector injector) {
		super(injector);
	}

	@Override
	public void execute() {
		DbTest dao = injector.getInstance(DbTest.class);
		sleepWithoutException(100);
		dao.testDbWithoutTransaction();
		sleepWithoutException(200);
		dao.testDbWithoutTransaction();
	}

}
