package org.fer.syncfiles.bus.dao;

import java.util.Date;
import java.util.List;

import org.fer.syncfiles.model.ResultSync;
import org.fer.syncfiles.model.ResultSyncDetail;

import com.google.inject.ImplementedBy;

@ImplementedBy(ResultSyncDaoImpl.class)
public interface ResultSyncDao {

	public abstract void save(ResultSync resultSync);

	public abstract void saveOnlyState(ResultSync resultSync);

	public abstract void deleteResultSync(ResultSync resultSync);

	public abstract List<ResultSyncDetail> loadResultSyncDetail(ResultSync resultSync);

	public abstract void purgeResultSync(Date olderThan);

	public abstract void removeAllMsgErr();

	public abstract boolean isAllMsgErrRead();

	public abstract List<ResultSync> loadAllResultSync();

}