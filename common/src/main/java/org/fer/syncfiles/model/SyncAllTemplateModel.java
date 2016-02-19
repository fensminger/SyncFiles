package org.fer.syncfiles.model;

import org.fer.syncfiles.service.ParamService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by fer on 21/09/2014.
 */
public class SyncAllTemplateModel {
    @Autowired
    private ParamService paramService;

    private List<Param> param;


    public SyncAllTemplateModel() {
        super();
    }

    public List<Param> getParam() {
        return param;
    }

    public void setParam(List<Param> param) {
        this.param = param;
    }

    public void loadData() {
        param = paramService.findAll().getParams();
    }
}
