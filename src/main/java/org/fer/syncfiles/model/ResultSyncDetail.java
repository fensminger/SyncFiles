package org.fer.syncfiles.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class ResultSyncDetail {
	@Id @GeneratedValue
	private Long id;
	@Version
	private long version;

	@ManyToOne
	@JoinColumn(name="RESULTSYNC_ID")
	private ResultSync resultSync;
	
	@Enumerated
	private ResultSyncAction action;
	
	private String path;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public ResultSync getResultSync() {
		return resultSync;
	}

	public void setResultSync(ResultSync resultSync) {
		this.resultSync = resultSync;
	}

	public ResultSyncAction getAction() {
		return action;
	}

	public void setAction(ResultSyncAction action) {
		this.action = action;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getDisplayInfo() {
		switch (action) {
		case NEW_DIRECTORY:
			return "Création du répertoire cible : " + getPath();
		case NEW_FILE:
			return "Copie du nouveau fichier sur la cible : " + getPath();
		case DELETE_DIRECTORY:
			return "Suppression du répertoire cible : " + getPath();
		case DELETE_FILE:
			return "Suppression du fichier cible : " + getPath();
		case COPY_FILE:
			return "Copie du fichier existant sur la cible : " + getPath();

		default:
			return null;
		}
	}
	
	public String toString() {
		return getDisplayInfo();
	}
}
