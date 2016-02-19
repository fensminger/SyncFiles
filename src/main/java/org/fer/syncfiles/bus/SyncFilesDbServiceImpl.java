package org.fer.syncfiles.bus;

import java.util.Date;
import java.util.List;

import org.fer.syncfiles.bus.dao.ParamDao;
import org.fer.syncfiles.bus.dao.ResultSyncDao;
import org.fer.syncfiles.inject.Transactional;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.model.ParamList;
import org.fer.syncfiles.model.ResultSync;
import org.fer.syncfiles.model.ResultSyncDetail;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SyncFilesDbServiceImpl implements SyncFilesDbService {

	private final Provider<ParamDao> paramDaoProvider;
	private final Provider<ResultSyncDao> resultSyncDaoProvider;
	
	@Inject
	public SyncFilesDbServiceImpl(Provider<ParamDao> paramDaoProvider, Provider<ResultSyncDao> resultSyncDaoProvider) {
		super();
		this.paramDaoProvider = paramDaoProvider;
		this.resultSyncDaoProvider = resultSyncDaoProvider;
	}

	@Override
	@Transactional
	public void save(ResultSync resultSync) {
		ResultSyncDao dao = resultSyncDaoProvider.get();
		dao.save(resultSync);
	}

	@Override
	@Transactional
	public void saveOnlyState(ResultSync resultSync) {
		ResultSyncDao dao = resultSyncDaoProvider.get();
		dao.saveOnlyState(resultSync);
	}

	@Override
	@Transactional
	public void deleteResultSync(ResultSync resultSync) {
		ResultSyncDao dao = resultSyncDaoProvider.get();
		dao.deleteResultSync(resultSync);
	}

	@Override
	@Transactional
	public List<ResultSyncDetail> loadResultSyncDetail(ResultSync resultSync) {
		ResultSyncDao dao = resultSyncDaoProvider.get();
		return dao.loadResultSyncDetail(resultSync);
	}

	@Override
	@Transactional
	public void purgeResultSync(Date olderThan) {
		ResultSyncDao dao = resultSyncDaoProvider.get();
		dao.purgeResultSync(olderThan);		
	}

	@Override
	@Transactional
	public void removeAllMsgErr() {
		ResultSyncDao dao = resultSyncDaoProvider.get();
		dao.removeAllMsgErr();
	}

	@Override
	@Transactional
	public boolean isAllMsgErrRead() {
		ResultSyncDao dao = resultSyncDaoProvider.get();
		return dao.isAllMsgErrRead();
	}

	@Override
	@Transactional
	public List<ResultSync> loadAllResultSync() {
		ResultSyncDao dao = resultSyncDaoProvider.get();
		return dao.loadAllResultSync();
	}

	@Override
	@Transactional
	public Param save(Param param) {
		ParamDao dao = paramDaoProvider.get();
		return dao.save(param);
	}

	@Override
	@Transactional
	public void delete(Param paramToDelete) {
		ParamDao dao = paramDaoProvider.get();
		dao.delete(paramToDelete);
	}

	@Override
	@Transactional
	public ParamList findAll() {
		ParamDao dao = paramDaoProvider.get();
		return dao.findAll();
	}

	@Override
	@Transactional
	public Param load(Param param) {
		ParamDao dao = paramDaoProvider.get();
		return dao.load(param);
	}
	
}
