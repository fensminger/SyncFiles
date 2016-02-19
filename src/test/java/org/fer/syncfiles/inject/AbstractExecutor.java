package org.fer.syncfiles.inject;

import static org.junit.Assert.fail;

import org.apache.log4j.Logger;

import com.google.inject.Injector;

public abstract class AbstractExecutor implements Runnable, Executor {
	protected static final Logger log = Logger.getLogger(AbstractExecutor.class);

	protected final Injector injector;
	protected boolean error = false;

	public AbstractExecutor(Injector injector) {
		super();
		this.injector = injector;
	}

	@Override
	public boolean isError() {
		return error;
	}

	@Override
	public void run() {
		sleepWithoutException(100);
		execute();
	}
	
	protected void sleepWithoutException(long milliSec) {
		try {
			Thread.sleep(milliSec);
		} catch (InterruptedException e) {
			error = true;
			fail(e.getMessage());
		}
	}
}
