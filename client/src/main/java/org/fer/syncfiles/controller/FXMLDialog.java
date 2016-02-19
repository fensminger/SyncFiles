package org.fer.syncfiles.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import org.fer.syncfiles.controller.DialogController;

import java.io.IOException;
import java.net.URL;

/**
 * Created by fer on 20/09/2014.
 */
public class FXMLDialog extends Stage {

    private DialogController controller;

    public FXMLDialog(DialogController controller, URL fxml, Window owner) {
        this(controller, fxml, owner, StageStyle.DECORATED);
    }

    public FXMLDialog(final DialogController controller, URL fxml, Window owner, StageStyle style) {
        super(style);
        this.controller = controller;
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);
        FXMLLoader loader = new FXMLLoader(fxml);
        try {
            loader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> aClass) {
                    return controller;
                }
            });
            controller.setDialog(this);
            setScene(new Scene((Parent) loader.load()));
            controller.postConstruct();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DialogController getController() {
        return controller;
    }

    public<T extends DialogController> T getController(Class<T> unusedTypeObj) {
        return (T) controller;
    }
}
