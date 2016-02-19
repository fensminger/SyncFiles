package org.fer.syncfiles.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

@Entity
public class ResultSync {
	@Id @GeneratedValue
	private Long id;
	@Version
	private long version;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="PARAM_ID")
	private Param param;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date executionDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date endExecutionDate;
	private String msgError;
	private boolean error;
	private boolean readed;
	private boolean toolTip;
	
	@XmlTransient
	@OneToMany(mappedBy="resultSync", cascade=CascadeType.ALL)
	@OrderBy("action ASC, path ASC")
	private List<ResultSyncDetail> actions;

	public List<String> getDisplayInfo() {
		List<String> res = new ArrayList<>();
		if (actions!=null) {
			for(ResultSyncDetail action : actions) {
				res.add(action.getDisplayInfo());
			}
		}
		return res;
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

	public Param getParam() {
		return param;
	}

	public void setParam(Param param) {
		this.param = param;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public List<ResultSyncDetail> getActions() {
		return actions;
	}

	public void setActions(List<ResultSyncDetail> actions) {
		this.actions = actions;
	}

	public Date getEndExecutionDate() {
		return endExecutionDate;
	}

	public void setEndExecutionDate(Date endExecutionDate) {
		this.endExecutionDate = endExecutionDate;
	}

	public String getMsgError() {
		return msgError;
	}

	public void setMsgError(String msgError) {
		this.msgError = msgError;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean isReaded() {
		return readed;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}

	public boolean isToolTip() {
		return toolTip;
	}

	public void setToolTip(boolean toolTip) {
		this.toolTip = toolTip;
	}

	@Override
	public String toString() {
		return param.getName();
	}
	
}
