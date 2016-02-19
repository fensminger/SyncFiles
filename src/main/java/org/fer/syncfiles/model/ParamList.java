package org.fer.syncfiles.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ParamList {

	private List<Param> params =  new ArrayList<Param>();

	public ParamList() {
		super();
	}

	public List<Param> getParams() {
		return params;
	}

	public void setParams(List<Param> params) {
		this.params = params;
	}

	public void addOrUpdate(Param param, int index) {
		if (index>=0) {
			params.set(index, param);
		} else {
			params.add(param);
		}
	}

	public Param removeParamAtIndex(int index) {
		Param res = params.get(index);
		params.remove(index);
		return res;
	}

	public Param getCurrentParam(int indexParam) {
		if (indexParam<0) {
			return null;
		} else {
			return params.get(indexParam);
		}
	}

	public void updateIdToNull() {
		for(Param param : params) {
			param.setId(null);
			param.setVersion(0);
		}
	}
}
