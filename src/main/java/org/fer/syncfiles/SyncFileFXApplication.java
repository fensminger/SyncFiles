/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fer.syncfiles;

import java.util.ArrayList;
import java.util.Collection;

import org.fer.syncfiles.inject.SyncFilesModule;

import com.cathive.fx.guice.GuiceApplication;
import com.google.inject.Inject;
import com.google.inject.Module;

import javafx.application.Platform;
import javafx.stage.Stage;

/**
 *
 * @author Frederic
 */
public class SyncFileFXApplication extends GuiceApplication {
	
	@Inject
	private App app;

    @Override
    public void start(Stage stage) throws Exception {
    	App.setInjector(getInjector());
    	
    	Platform.setImplicitExit(false);
    	app.start(stage);
    	
   }
  
    public static void main(String[] args) throws Exception {
        launch(args);
    }

	@Override
	public Collection<Module> initModules() {
		Module module = new SyncFilesModule();
		ArrayList<Module> moduleList = new ArrayList<Module>();
		moduleList.add(module);
		return moduleList;
	}
    
}
