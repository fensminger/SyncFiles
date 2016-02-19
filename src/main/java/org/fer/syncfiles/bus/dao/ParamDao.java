package org.fer.syncfiles.bus.dao;

import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.model.ParamList;

import com.google.inject.ImplementedBy;

@ImplementedBy(ParamDaoImpl.class)
public interface ParamDao {

	public abstract Param save(Param param);

	public abstract void delete(Param paramToDelete);

	public abstract ParamList findAll();

	public abstract Param load(Param param);

}