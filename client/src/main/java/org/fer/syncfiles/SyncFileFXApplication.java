/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fer.syncfiles;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javafx.application.Application;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.fer.syncfiles.config.ScreensConfiguration;
import org.fer.syncfiles.service.SchedulerMgr;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Frederic
 */
public class SyncFileFXApplication extends Application {
    private static final Logger log = Logger.getLogger(SyncFileFXApplication.class);

    private Stage stage;

//    private SchedulerMgr schedulerMgr;

    private ScreensConfiguration screens;

    @Override
    public void start(Stage stage) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");

        Platform.setImplicitExit(false);
        screens = context.getBean(ScreensConfiguration.class);
//        schedulerMgr = screens.loadSchedulerMgr();
        screens.setPrimaryStage(stage);
        this.stage = stage;
        ObservableList<javafx.scene.image.Image> icons = stage.getIcons();
        icons.add(new javafx.scene.image.Image("org/fer/syncfiles/another_folder_icon_16.png"));
        icons.add(new javafx.scene.image.Image("org/fer/syncfiles/another_folder_icon_32.png"));

        initSystemTray();
        initQuartz();
        screens.loadSyncAllTemplate().show();

   }
  
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    private TrayIcon trayIcon = null;

    private void initSystemTray() {
        if (SystemTray.isSupported()) {

            SystemTray tray = SystemTray.getSystemTray();
            URL url = SyncFileFXApplication.class.getResource("another_folder_icon_32.png");
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
                            screens.loadSyncAllTemplate().show();
//                            stage.toFront();
                        }
                    });
                }
            };

            PopupMenu popup = new PopupMenu();
            MenuItem startItem = new MenuItem("Démarrer");
            startItem.addActionListener(startListener);
            popup.add(startItem);

//            MenuItem deleteAllScheduleItem = new MenuItem("Supprimer tous les scheduling");
//            deleteAllScheduleItem.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    try {
//                        schedulerMgr.deleteAll();
//                    } catch (SchedulerException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            });
//            popup.add(deleteAllScheduleItem);
//
//            MenuItem baseAccessItem = new MenuItem("Accéder à la base de données");
//            baseAccessItem.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    String[] args = new String[] {};
//                }
//            });
//            popup.add(baseAccessItem);


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

    public void stopAppli() {
//        schedulerMgr.stop();
        System.exit(0);
    }

    private void initQuartz() {
//        schedulerMgr.start();
    }


}
