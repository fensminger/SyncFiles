package org.fer.syncfiles.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


@Document(collection = "currentSynchroDetail")
public class CurrentSynchroDetail implements Serializable {
//	private static final Logger log = Logger.getLogger(ParamSyncFiles.class);

    @Id
	private String id;

	@Version
	private long version;

	private String currentSynchroId;
	private String relativePath;

	public CurrentSynchroDetail() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getCurrentSynchroId() {
		return currentSynchroId;
	}

	public void setCurrentSynchroId(String currentSynchroId) {
		this.currentSynchroId = currentSynchroId;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
}
