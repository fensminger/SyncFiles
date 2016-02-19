package org.fer.syncfiles;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.management.RuntimeErrorException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.fer.syncfiles.bus.SyncFilesDbService;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.model.ParamList;
import org.hsqldb.util.DatabaseManagerSwing;
import org.quartz.SchedulerException;
import org.quartz.utils.DBConnectionManager;

import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class App {
	private static final Logger log = Logger.getLogger(App.class);
	
	private static final String SYNC_ALL_TEMPLATE_FXML = "SyncAllTemplate.fxml";
	private static final String SYNC_ALL_RESULT_FXML = "SyncAllResult.fxml";
	
	private static final String SYNC_PARAM_FXML = "SyncParam.fxml";
    private Stage stage;
	
	private AnchorPane root = null;
	
	private ParamList paramList;
	private int indexParam;

    private SyncFilesTask task = null;
    
	
    private static Injector injector = null;

    @Inject
    private SchedulerMgr schedulerMgr;
    
    @Inject
    private Provider<GuiceFXMLLoader> fxmlLoaderProvider = null;
    
    @Inject
    private Provider<SyncFilesTask> taskProvider = null;
    
    @Inject
    private SyncFilesDbService syncFilesDbService;

    @Inject
	Provider<EntityManager> emProvider;
    
	public App() {
		super();
	}
	
	private BorderPane getMainBorderPane() {
		return (BorderPane) getRoot().getChildren().get(0);
	}

	private AnchorPane getRoot() {
		if (root==null) {
		    try {
				URL resource = App.class.getResource("Main.fxml");
				log.info(""+ resource);
				root = fxmlLoaderProvider.get().load(resource).getRoot();
			} catch (IOException e) {
				throw new RuntimeErrorException(new Error(e));
			}
		}
		return root;
	}

	public void changeToParamController() throws Exception {
		replaceSceneContent(SYNC_PARAM_FXML);
	}

	public void changeToAllTemplateController() throws Exception {
		replaceSceneContent(SYNC_ALL_TEMPLATE_FXML);
	}
	
	public void changeToAllResultController() throws Exception {
		replaceSceneContent(SYNC_ALL_RESULT_FXML);
	}
	
    private Parent replaceSceneContent(String fxml) throws Exception {
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(getRoot(), 700, 550);
            scene.getStylesheets().add(App.class.getResource("sync.css").toExternalForm());
            stage.setTitle("SyncFiles");
            stage.setScene(scene);
        }
        
        Parent page = (Parent) fxmlLoaderProvider.get().load(App.class.getResource(fxml)).getRoot();
    	BorderPane borderPane = getMainBorderPane();
    	borderPane.setCenter(page);
        //stage.getScene().setRoot(page);
        stage.sizeToScene();
        return page;
    }

	public void start(Stage stage) throws Exception {
		this.stage = stage;
		
		ObservableList<Image> icons = stage.getIcons();
		icons.add(new Image("org/fer/syncfiles/another_folder_icon_16.png"));
		icons.add(new Image("org/fer/syncfiles/another_folder_icon_32.png"));
		
		initInitialContext();
		
		paramList = syncFilesDbService.findAll();
		
		try {
			replaceSceneContent(SYNC_ALL_TEMPLATE_FXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		initSystemTray();
		//initDataBase();
		initQuartz();
		//testDataBase();
		
        stage.show();
	}
	
	private void initInitialContext() throws NamingException {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"org.fer.syncfiles.jndi.InitialContextFactory");
		System.setProperty(Context.PROVIDER_URL, "file://jndiProvider");
//		InitialContext ic = new InitialContext();

		// Construct BasicDataSource reference
//		Reference ref = new Reference("javax.sql.DataSource",
//				"com.jolbox.bonecp.BoneCPDataSource", null);
//		ref.add(new StringRefAddr("driverClassName",
//				"org.hsqldb.jdbcDriver"));
//		ref.add(new StringRefAddr("jdbcUrl", "jdbc:hsqldb:file:dbSyncFiles"));
//		ref.add(new StringRefAddr("username", "sa"));
//		ref.add(new StringRefAddr("password", ""));
//		ic.rebind("jdbc/syncfiles", ref);
	}

	@SuppressWarnings("unused")
	private void testDataBase() {
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			//Connection connexion = DriverManager.getConnection("jdbc:hsqldb:file:dbSyncFiles", "sa",  "");
			Connection connexion = DBConnectionManager.getInstance().getConnection("syncFilesDS");
			
			log.info("InitDataBASE --------------------");
			Statement statement = connexion.createStatement();
//			statement.execute("SET AUTOCOMMIT TRUE");
			//statement.execute("DROP TABLE test");
			//statement.execute("CREATE TABLE test (" + 
			//	"colonne1 INT , colonne2 INT)");
//			statement.execute("insert into test values (1,2)");
//			ResultSet rs = statement.executeQuery("select colonne1 from test");
//			while (rs.next()) {
//				log.info(rs.getInt("colonne1"));
//			}
//			rs.close();
			
			ResultSet rs = statement.executeQuery("SELECT * FROM QRTZ_JOB_DETAILS");
			while (rs.next()) {
				log.info("ResultSet : "+rs.getObject(1));
			}
			
			//statement.execute("SHUTDOWN");
			statement.close();
			//connexion.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void initQuartz() {
		schedulerMgr.start();
	}
	
	public void stopAppli() {
		schedulerMgr.stop();
        System.exit(0);
	}

	@SuppressWarnings("unused")
	private void initDataBase() {
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			Connection connexion = DriverManager.getConnection("jdbc:hsqldb:file:dbSyncFiles", "sa",  "");
			
			
			Statement statement = connexion.createStatement();
			statement.execute("SET AUTOCOMMIT TRUE");
			//statement.execute("DROP TABLE test");
			//statement.execute("CREATE TABLE test (" + 
			//	"colonne1 INT , colonne2 INT)");
//			statement.execute("insert into test values (1,2)");
//			ResultSet rs = statement.executeQuery("select colonne1 from test");
//			while (rs.next()) {
//				log.info(rs.getInt("colonne1"));
//			}
//			rs.close();
			//statement.execute("SHUTDOWN");
			statement.close();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}

	private TrayIcon trayIcon = null;

	private void initSystemTray() {
		if (SystemTray.isSupported()) {

		    SystemTray tray = SystemTray.getSystemTray();
		    URL url = App.class.getResource("another_folder_icon_32.png");
			java.awt.Image image =  Toolkit.getDefaultToolkit().getImage(url);

		    MouseListener mouseListener = new MouseListener() {
		                
		        public void mouseClicked(MouseEvent e) {
		        	log.debug("Tray Icon - Mouse clicked!");                 
		        }

		        public void mouseEntered(MouseEvent e) {
		        	log.debug("Tray Icon - Mouse entered!");                 
		        }

		        public void mouseExited(MouseEvent e) {
		        	log.debug("Tray Icon - Mouse exited!");                 
		        }

		        public void mousePressed(MouseEvent e) {
		        	log.debug("Tray Icon - Mouse pressed!");                 
		        }

		        public void mouseReleased(MouseEvent e) {
		        	log.debug("Tray Icon - Mouse released!");                 
		        }
		    };

		    ActionListener exitListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	log.info("Exiting...");
		            stopAppli();
		        }
		    };
		            
		    ActionListener startListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							log.info("Démarre l'application...");
							stage.show();
							stage.toFront();
						}
					});
		        }
		    };
		            
		    PopupMenu popup = new PopupMenu();
		    MenuItem startItem = new MenuItem("Démarrer");
		    startItem.addActionListener(startListener);
		    popup.add(startItem);
		    
		    MenuItem deleteAllScheduleItem = new MenuItem("Supprimer tous les scheduling");
		    deleteAllScheduleItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						schedulerMgr.deleteAll();
					} catch (SchedulerException e1) {
						e1.printStackTrace();
					}
				}
			});
		    popup.add(deleteAllScheduleItem);

		    MenuItem baseAccessItem = new MenuItem("Accéder à la base de données");
		    baseAccessItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String[] args = new String[] {};
					DatabaseManagerSwing.main(args );
				}
			});
		    popup.add(baseAccessItem);

		    
		    MenuItem exitItem = new MenuItem("Quitter");
		    exitItem.addActionListener(exitListener);
		    popup.add(exitItem);

		    trayIcon = new TrayIcon(image, "Synchronisation des fichiers", popup);

//		    ActionListener actionListener = new ActionListener() {
//		        public void actionPerformed(ActionEvent e) {
//		            trayIcon.displayMessage("Action Event", 
//		                "An Action Event Has Been Performed!",
//		                TrayIcon.MessageType.INFO);
//		        }
//		    };
		            
		    trayIcon.setImageAutoSize(true);
		    //trayIcon.addActionListener(actionListener);
		    trayIcon.addMouseListener(mouseListener);

		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		        System.err.println("TrayIcon could not be added.");
		    }

		}		
	}
	
	private Stage dialogStage;
	
	public void closeDialog() {
		dialogStage.close();
		getRoot().setDisable(false);
	}
	
	public String showInputDialog(String title, String text) throws IOException {
		dialogStage = new Stage();
		getRoot().setDisable(true);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Parent page = (Parent) fxmlLoaderProvider.get().load(App.class.getResource("TextInputDialog.fxml")).getRoot();
		dialogStage.setScene(new Scene(page));
		dialogStage.setTitle(title);
		TextField textField = (TextField) page.getChildrenUnmodifiable().get(0);
		if (text!=null) {
			textField.setText(text);
		}
		dialogStage.showAndWait();
		dialogStage.close();
		String result = textField.getText();
		return "".equals(result)?null:result;
	}

	private Stage syncStage;
	private boolean simulation = true;
	
	public boolean isSimulation() {
		return simulation;
	}
	
	public void displayResultSync() throws Exception {
		syncStage.close();
		syncStage = null;
		getRoot().setDisable(false);
		replaceSceneContent("SyncResult.fxml");
	}
	
	public void startSync(boolean isSimulation) throws IOException {
		if (indexParam>=0) {
			getRoot().setDisable(true);
			simulation = isSimulation;
			syncStage = new Stage();
			syncStage.initModality(Modality.WINDOW_MODAL);
			syncStage.setResizable(true);
			Parent page = (Parent) fxmlLoaderProvider.get().load(App.class.getResource("SynchroProgress.fxml")).getRoot();
			syncStage.setScene(new Scene(page));
			syncStage.setTitle("Synchronisation en cours...");
			syncStage.show();
		}
	}

	public void hideMainWindow() {
		stage.hide();
	}
	
	public Param addOrUpdateParam(Param param) throws JAXBException, SchedulerException {
		if (param.getCreationDate()==null) {
			param.setCreationDate(new Date());
		}
		
		schedulerMgr.insertOrUpdateJob(param);
		
		paramList.addOrUpdate(param, getIndexParam());
		
		return syncFilesDbService.save(param);
	}

	public ParamList getParamList() {
		return paramList;
	}

	public void removeParamAtIndex(int index) throws JAXBException {
		Param paramToDelete = paramList.removeParamAtIndex(index);
		syncFilesDbService.delete(paramToDelete);

	}

	public int getIndexParam() {
		return indexParam;
	}

	public void setIndexParam(int indexParam) {
		this.indexParam = indexParam;
	}

	public Param getCurrentParam() {
		return paramList.getCurrentParam(getIndexParam());
	}

	public SyncFilesTask createSyncStask() {
		task = taskProvider.get();
		task.init(getCurrentParam(), isSimulation());
		return task;
	}

	public SyncFilesTask getTask() {
		return task;
	}

	private Stage missedStage;
	private boolean isToolTip = false;
	
	public synchronized void closeMissedSynchro() throws Exception {
		missedStage.close();
		missedStage = null;
		isToolTip = false;
	}
	
	public void showToAllResultController() throws Exception {
		stage.show();
		stage.toFront();
		changeToAllResultController();
	}
	
	public synchronized void startMissedSynchro() {
		try {
			if (!isToolTip) {
				isToolTip = true;
				missedStage = new Stage();
				missedStage.initModality(Modality.WINDOW_MODAL);
				missedStage.setResizable(true);
				Parent page = (Parent) fxmlLoaderProvider.get().load(
						App.class.getResource("SyncMsgSyncError.fxml")).getRoot();
				missedStage.setScene(new Scene(page));
				missedStage.setTitle("Synchronisation en cours...");
				missedStage.show();
				missedStage.toFront();
			}
		} catch (IOException e) {
			log.error("Impossible de charger le fichier de ressource Java FX SynMsgSyncError.fxml");
		}
	}
	
	public static Injector getInjector() {
		return injector;
	}
	public static void setInjector(Injector injector) {
		App.injector = injector;
	}
}
