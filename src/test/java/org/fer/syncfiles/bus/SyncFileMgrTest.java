package org.fer.syncfiles.bus;


import java.io.File;
import java.nio.file.Path;

import org.fer.syncfiles.model.Param;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SyncFileMgrTest {
	private static final String SOURCE_DIR = "C:/Users/QUAD";
	//private static final String SOURCE_DIR = "src/test/resources/syncfilesmgr/source";
	private static final String DEST_DIR = "src/test/resources/syncfilesmgr/dest";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void test_Chargement_Arborescence() {
		File sourceDirFile = new File(SOURCE_DIR);
		File destDirFile = new File(DEST_DIR);
		
		SyncFileMgr syncFileMgr = new SyncFileMgr();
		Param param =  new Param();
		param.setMasterDir(sourceDirFile.getAbsolutePath());
		param.setSlaveDir(destDirFile.getAbsolutePath());
		syncFileMgr.launchSyncFiles(param , null, true);
		System.out.println("===================================================");
		final String masterTreeStr = syncFileMgr.getMasterTreeStr();
		System.out.println(masterTreeStr);
		System.out.println("==============FICHIERS EN ERREUR=====================================");
		for(Path path : syncFileMgr.getMasterErrorPath()) {
			System.out.println(path.toString());
		}
	}
}
