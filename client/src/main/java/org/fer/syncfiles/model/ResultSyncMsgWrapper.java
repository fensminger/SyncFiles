package org.fer.syncfiles.model;

import org.fer.syncfiles.model.ResultSync;

public class ResultSyncMsgWrapper {

	private ResultSync resultSync;

	public ResultSyncMsgWrapper(ResultSync resultEntry) {
		super();
		this.resultSync = resultEntry;
	}
	
	public boolean isError() {
		if (resultSync==null) {
			return false;
		} else {
			return resultSync.isError();
		}
	}
	
	public String toString() {
		if (resultSync==null) {
			return "null";
		} else {
			return resultSync.getMsgError();
		}
	}
}
