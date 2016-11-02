package org.fer.syncfiles.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "paramSyncFiles")
public class ParamSyncFiles implements Serializable {
//	private static final Logger log = Logger.getLogger(ParamSyncFiles.class);

	private static final long serialVersionUID = -1636369614390375815L;

    @Id
	private String id;

	@Version
	private long version;

	private ZonedDateTime creationDate;

	private String name;
	private String cronExp;

	private String masterDir;
	private String slaveDir;
	private boolean includeDir;

    private SyncState syncState;

	private List<String> includeExcludePatterns;

	private String regExpIncludeExcludePattern = null;

	public ParamSyncFiles() {
		super();
	}

	public ParamSyncFiles(ParamSyncFiles p) {
		this.id = p.id;
		this.version = p.version;
		this.creationDate = ZonedDateTime.of(p.creationDate.toLocalDate(), p.creationDate.toLocalTime(), p.creationDate.getZone());
		this.name = p.name;
		this.cronExp = p.cronExp;
		this.masterDir = p.masterDir;
		this.slaveDir = p.slaveDir;
		this.includeDir = p.includeDir;
		if (p.includeExcludePatterns==null) {
			this.includeExcludePatterns = null;
		} else {
			this.includeExcludePatterns = new ArrayList<>();
			for (String pattern : p.includeExcludePatterns) {
				this.includeExcludePatterns.add(pattern);
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCronExp() {
		return cronExp;
	}

	public void setCronExp(String cronExp) {
		this.cronExp = cronExp;
	}

	public String getMasterDir() {
		return masterDir;
	}

	public void setMasterDir(String masterDir) {
		this.masterDir = masterDir;
	}

	public String getSlaveDir() {
		return slaveDir;
	}

	public void setSlaveDir(String slaveDir) {
		this.slaveDir = slaveDir;
	}

	public boolean isIncludeDir() {
		return includeDir;
	}

	public void setIncludeDir(boolean includeDir) {
		this.includeDir = includeDir;
	}

	public List<String> getIncludeExcludePatterns() {
		return includeExcludePatterns;
	}

    public String getId() {
        return id;
    }

    public String getRegExpIncludeExcludePattern() {
		if (regExpIncludeExcludePattern==null && includeExcludePatterns!=null) {
			String regExp = null;
			for(String str : includeExcludePatterns) {
				if (regExp==null) {
					regExp = "";
				} else {
					regExp += "|";
				}
				regExp += "(" + str + ")";
			}
//			log.info("regExp : " + regExp);
			regExpIncludeExcludePattern = regExp;
		}
		return regExpIncludeExcludePattern;
	}

	public void setIncludeExcludePatterns(List<String> includeExcludePatterns) {
		this.includeExcludePatterns = includeExcludePatterns;
		regExpIncludeExcludePattern = null;
	}

	@Override
	public String toString() {
		return "ParamSyncFiles [name=" + name + ", cronExp=" + cronExp + ", masterDir=" + masterDir + ", slaveDir=" + slaveDir + ", includeDir=" + includeDir
				+ ", includeExcludePatterns=" + includeExcludePatterns + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cronExp == null) ? 0 : cronExp.hashCode());
		result = prime * result + (includeDir ? 1231 : 1237);
		result = prime * result + ((includeExcludePatterns == null) ? 0 : includeExcludePatterns.hashCode());
		result = prime * result + ((masterDir == null) ? 0 : masterDir.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((slaveDir == null) ? 0 : slaveDir.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParamSyncFiles other = (ParamSyncFiles) obj;
		if (cronExp == null) {
			if (other.cronExp != null)
				return false;
		} else if (!cronExp.equals(other.cronExp))
			return false;
		if (includeDir != other.includeDir)
			return false;
		if (includeExcludePatterns == null) {
			if (other.includeExcludePatterns != null)
				return false;
		} else if (!includeExcludePatterns.equals(other.includeExcludePatterns))
			return false;
		if (masterDir == null) {
			if (other.masterDir != null)
				return false;
		} else if (!masterDir.equals(other.masterDir))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (slaveDir == null) {
			if (other.slaveDir != null)
				return false;
		} else if (!slaveDir.equals(other.slaveDir))
			return false;
		return true;
	}

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getJobName() {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return name; // + "_" + ((creationDate==null)?"":sdf.format(creationDate));
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

    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(SyncState syncState) {
        this.syncState = syncState;
    }

}
