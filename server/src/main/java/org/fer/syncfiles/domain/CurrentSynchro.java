package org.fer.syncfiles.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "currentSynchro")
public class CurrentSynchro implements Serializable {
//	private static final Logger log = Logger.getLogger(ParamSyncFiles.class);

    @Id
	private String id;

	@Version
	private long version;

	private String paramSyncFilesId;
	private String remotePath;
	private String localPath;
	private String type; // upload or download

	public CurrentSynchro() {
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

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParamSyncFilesId() {
		return paramSyncFilesId;
	}

	public void setParamSyncFilesId(String paramSyncFilesId) {
		this.paramSyncFilesId = paramSyncFilesId;
	}
}
