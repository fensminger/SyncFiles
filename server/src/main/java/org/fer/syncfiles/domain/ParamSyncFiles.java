package org.fer.syncfiles.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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
	private Schedule schedule;

	private String masterDir;
	private String slaveDir;
	private boolean includeDir;

    private SyncState syncState;

	private List<IncludeExcludeInfo> includeExcludePatterns;

	@Transient
	private String regExpIncludeExcludePattern = null;

	public ParamSyncFiles() {
		super();
	}

	public ParamSyncFiles(ParamSyncFiles p) {
		this.id = p.id;
		this.version = p.version;
		this.creationDate = ZonedDateTime.of(p.creationDate.toLocalDate(), p.creationDate.toLocalTime(), p.creationDate.getZone());
		this.name = p.name;
		this.schedule = p.schedule;
		this.masterDir = p.masterDir;
		this.slaveDir = p.slaveDir;
		this.includeDir = p.includeDir;
		if (p.includeExcludePatterns==null) {
			this.includeExcludePatterns = null;
		} else {
			this.includeExcludePatterns = new ArrayList<>();
			for (IncludeExcludeInfo pattern : p.includeExcludePatterns) {
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

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
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

	public List<IncludeExcludeInfo> getIncludeExcludePatterns() {
		return includeExcludePatterns;
	}

    public String getId() {
        return id;
    }

	public void setIncludeExcludePatterns(List<IncludeExcludeInfo> includeExcludePatterns) {
		this.includeExcludePatterns = includeExcludePatterns;
		regExpIncludeExcludePattern = null;
	}

	@Override
	public String toString() {
		return "ParamSyncFiles{" +
				"id='" + id + '\'' +
				", version=" + version +
				", creationDate=" + creationDate +
				", name='" + name + '\'' +
				", schedule=" + schedule +
				", masterDir='" + masterDir + '\'' +
				", slaveDir='" + slaveDir + '\'' +
				", includeDir=" + includeDir +
				", syncState=" + syncState +
				", includeExcludePatterns=" + includeExcludePatterns +
				", regExpIncludeExcludePattern='" + regExpIncludeExcludePattern + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ParamSyncFiles that = (ParamSyncFiles) o;

		if (version != that.version) return false;
		if (includeDir != that.includeDir) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (creationDate != null ? !creationDate.equals(that.creationDate) : that.creationDate != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (schedule != null ? !schedule.equals(that.schedule) : that.schedule != null) return false;
		if (masterDir != null ? !masterDir.equals(that.masterDir) : that.masterDir != null) return false;
		if (slaveDir != null ? !slaveDir.equals(that.slaveDir) : that.slaveDir != null) return false;
		if (syncState != that.syncState) return false;
		if (includeExcludePatterns != null ? !includeExcludePatterns.equals(that.includeExcludePatterns) : that.includeExcludePatterns != null)
			return false;
		return regExpIncludeExcludePattern != null ? regExpIncludeExcludePattern.equals(that.regExpIncludeExcludePattern) : that.regExpIncludeExcludePattern == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (int) (version ^ (version >>> 32));
		result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (schedule != null ? schedule.hashCode() : 0);
		result = 31 * result + (masterDir != null ? masterDir.hashCode() : 0);
		result = 31 * result + (slaveDir != null ? slaveDir.hashCode() : 0);
		result = 31 * result + (includeDir ? 1 : 0);
		result = 31 * result + (syncState != null ? syncState.hashCode() : 0);
		result = 31 * result + (includeExcludePatterns != null ? includeExcludePatterns.hashCode() : 0);
		result = 31 * result + (regExpIncludeExcludePattern != null ? regExpIncludeExcludePattern.hashCode() : 0);
		return result;
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
