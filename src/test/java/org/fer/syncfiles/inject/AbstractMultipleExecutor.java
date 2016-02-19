package org.fer.syncfiles.inject;

import com.google.inject.Injector;

public abstract class AbstractMultipleExecutor extends AbstractExecutor {

	protected final int CALL_NB = 5;
	
	public AbstractMultipleExecutor(Injector injector) {
		super(injector);
	}

	@Override
	public void run() {
		for(int i = 0; i<CALL_NB; i++) {
			sleepWithoutException(200);
			execute();
		}
	}

}
