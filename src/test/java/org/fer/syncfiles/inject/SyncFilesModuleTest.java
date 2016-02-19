package org.fer.syncfiles.inject;

import javax.naming.Context;

import org.apache.log4j.Logger;
import org.fer.syncfiles.bus.SyncFilesDbService;
import org.fer.syncfiles.bus.dao.ParamDao;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static org.junit.Assert.*;

public class SyncFilesModuleTest {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SyncFilesModuleTest.class);

	private Injector injector;
	private DbTest dbService;
	@SuppressWarnings("unused")
	private SyncFilesDbService syncFilesDbService;

	@Before
	public void setup() {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"org.fer.syncfiles.jndi.InitialContextFactory");
		System.setProperty(Context.PROVIDER_URL, "file://jndiProvider");
		injector = Guice.createInjector(new SyncFilesModule());
		dbService = injector.getInstance(DbTest.class);
		syncFilesDbService = injector.getInstance(SyncFilesDbService.class);
	}
	
	@Test
	public void test_get_instance() {
		ParamDao val = injector.getInstance(ParamDao.class);
		assertNotNull(val);
	}
	
	@Test
	public void test_thread_transaction() throws InterruptedException {
		TransactionExecutor transExe = new TransactionExecutor(injector);
		WithoutTransactionExecutor withoutTransExe = new WithoutTransactionExecutor(injector);
		Thread serviceWithTransaction = new Thread(transExe);
		serviceWithTransaction.setName("serviceWithTransaction");
		Thread serviceWithoutTransaction = new Thread(withoutTransExe);
		serviceWithTransaction.setName("serviceWithoutTransaction");
		serviceWithoutTransaction.start();
		serviceWithTransaction.start();
		
		serviceWithTransaction.join();
		serviceWithoutTransaction.join();
		
		assertFalse("TransactionExecutor", transExe.isError());
		assertFalse("WithoutTransactionExecutor", withoutTransExe.isError());
	}
	
	@Test
	public void test_entity_manager_open() {
		dbService.testEmOpen();
	}
	
	@Test
	public void test_Transaction_lock() throws InterruptedException {
		ConcurrentExecutionExecutor loadAllParamExe = new ConcurrentExecutionExecutor(injector, true);
		ConcurrentExecutionExecutor isToolTipExe = new ConcurrentExecutionExecutor(injector, false);
		Thread t1 = new Thread(loadAllParamExe);
		Thread t2 = new Thread(isToolTipExe);
		
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();
		
		assertFalse("loadAllParamExe", loadAllParamExe.isError());
		assertFalse("isToolTipExe", isToolTipExe.isError());
	}
}
