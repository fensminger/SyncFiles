package org.fer.syncfiles.controller;

/**
 * Created by fer on 20/09/2014.
 */
public interface DialogController {
    void setDialog(FXMLDialog dialog);

    default void postConstruct() {

    }
}
