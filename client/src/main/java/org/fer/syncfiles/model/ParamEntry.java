package org.fer.syncfiles.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ParamEntry {
    public StringProperty name = new SimpleStringProperty();
    public StringProperty cronExp = new SimpleStringProperty();
    
    
    public String getName() {
        return name.get();
    }

    public String getCronExp() {
        return cronExp.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setCronExp(String cronExp) {
        this.cronExp.set(cronExp);
    }
}
