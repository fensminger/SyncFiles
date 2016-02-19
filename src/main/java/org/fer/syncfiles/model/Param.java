package org.fer.syncfiles.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

@XmlType
@Entity
public class Param implements Serializable {
	private static final Logger log = Logger.getLogger(Param.class);
	
	private static final long serialVersionUID = -1636369614390375815L;
	
	@Id @GeneratedValue
	private Long id;
	@Version
	private long version;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	private String name;
	private String cronExp;
	
	private String masterDir;
	private String slaveDir;
	private boolean includeDir;
	
	@ElementCollection(fetch=FetchType.EAGER)
	private List<String> includeExcludePatterns;
	
	@XmlTransient @Transient
	private String regExpIncludeExcludePattern = null;

	@OneToMany(mappedBy="param", cascade=CascadeType.ALL, fetch=FetchType.LAZY)  
	@XmlTransient
	private List<ResultSync> resultSync;
	
	public Param() {
		super();
	}

	public Param(Param p) {
		this.id = p.id;
		this.version = p.version;
		this.creationDate = new Date(p.creationDate.getTime());
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

	public String getRegExpIncludeExcludePattern() {
		if (regExpIncludeExcludePattern==null && includeExcludePatterns!=null) {
			String regExp = null;
			for(String str : includeExcludePatterns) {
				if (regExp==null) {
					regExp = "";
				} else {
					regExp += "|";
				}
				regExp += "(" + str.replaceAll("\\.", "\\.").replace("\\", "\\\\").replace("*", ".*") + ")";
			}
			log.info("regExp : " + regExp);
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
		return "Param [name=" + name + ", cronExp=" + cronExp + ", masterDir=" + masterDir + ", slaveDir=" + slaveDir + ", includeDir=" + includeDir
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
		Param other = (Param) obj;
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getJobName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return name + "_" + sdf.format(creationDate);
	}

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

	public List<ResultSync> getResultSync() {
		return resultSync;
	}

	public void setResultSync(List<ResultSync> resultSync) {
		this.resultSync = resultSync;
	}

}
