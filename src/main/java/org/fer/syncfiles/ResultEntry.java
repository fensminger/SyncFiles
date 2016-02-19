package org.fer.syncfiles;

import org.fer.syncfiles.model.ResultSync;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class ResultEntry {
	public SimpleObjectProperty<ResultSync> name = new SimpleObjectProperty<>();
	public SimpleObjectProperty<ResultSyncMsgWrapper> msg = new SimpleObjectProperty<>();
	public SimpleStringProperty executionDate = new SimpleStringProperty();
	public SimpleStringProperty duration = new SimpleStringProperty();
	
	public ResultSync getName() {
		return name.get();
	}
	public void setName(ResultSync ref) {
		name.set(ref);
	}
	
	public ResultSyncMsgWrapper getMsg() {
		return msg.get();
	}
	public String getExecutionDate() {
		return executionDate.get();
	}
	public String getDuration() {
		return duration.get();
	}
}
