package org.fer.syncfiles;

import javafx.beans.property.SimpleStringProperty;

public class ParamEntry {
    public SimpleStringProperty itemName = new SimpleStringProperty(); 
    public SimpleStringProperty itemCronExp = new SimpleStringProperty();
    
    
    public String getItemName() {
        return itemName.get();
    }

    public String getItemCronExp() {
        return itemCronExp.get();
    }

}
