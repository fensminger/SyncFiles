package org.fer.syncfiles.bus;

import org.fer.syncfiles.bus.dao.ParamDao;
import org.fer.syncfiles.bus.dao.ResultSyncDao;

import com.google.inject.ImplementedBy;

@ImplementedBy(SyncFilesDbServiceImpl.class)
public interface SyncFilesDbService extends ParamDao, ResultSyncDao {

}
