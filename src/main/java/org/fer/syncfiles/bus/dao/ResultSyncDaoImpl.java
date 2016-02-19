package org.fer.syncfiles.bus.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.fer.syncfiles.bus.AbstractDao;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.model.ResultSync;
import org.fer.syncfiles.model.ResultSyncAction;
import org.fer.syncfiles.model.ResultSyncDetail;

import com.google.inject.Inject;

public class ResultSyncDaoImpl extends AbstractDao implements ResultSyncDao {

	@Inject
	public ResultSyncDaoImpl(EntityManager em) {
		super(em);
	}

	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.dao.ResultSyncDaoInterface#save(org.fer.syncfiles.model.ResultSync)
	 */
	@Override
	public void save(ResultSync resultSync) {
		Param param = em.find(Param.class, resultSync.getParam().getId());
		resultSync.setParam(param);
		em.persist(resultSync);
		for(ResultSyncDetail action : resultSync.getActions()) {
			em.persist(action);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.dao.ResultSyncDaoInterface#saveOnlyState(org.fer.syncfiles.model.ResultSync)
	 */
	@Override
	public void saveOnlyState(ResultSync resultSync) {
		ResultSync res = em.find(ResultSync.class, resultSync.getId());
		res.setReaded(resultSync.isReaded());
		res.setError(resultSync.isError());
		res.setToolTip(resultSync.isToolTip());
		em.persist(res);
		
	}
	
	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.dao.ResultSyncDaoInterface#deleteResultSync(org.fer.syncfiles.model.ResultSync)
	 */
	@Override
	public void deleteResultSync(ResultSync resultSync) {
		ResultSync resultSyncEntity = em.find(ResultSync.class, resultSync.getId());
		em.remove(resultSyncEntity);
		
	}
	
	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.dao.ResultSyncDaoInterface#loadResultSyncDetail(org.fer.syncfiles.model.ResultSync)
	 */
	@Override
	public List<ResultSyncDetail> loadResultSyncDetail(ResultSync resultSync) {
		ResultSync resultSyncEntity = em.find(ResultSync.class, resultSync.getId());
		List<ResultSyncDetail> res = resultSyncEntity.getActions();
		for(ResultSyncDetail val : res) {
			@SuppressWarnings("unused")
			ResultSyncAction action = val.getAction();
		}
		
		return res;
	}

	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.dao.ResultSyncDaoInterface#purgeResultSync(java.util.Date)
	 */
	@Override
	public void purgeResultSync(Date olderThan) {
		for(ResultSync resultSync : loadAllResultSync()) {
			if (resultSync.getExecutionDate().before(olderThan)) {
				deleteResultSync(resultSync);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.dao.ResultSyncDaoInterface#removeAllMsgErr()
	 */
	@Override
	public void removeAllMsgErr() {
		Query query = em.createQuery("update ResultSync rs set rs.toolTip = false  where rs.toolTip = true");
		query.executeUpdate();
	}

	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.dao.ResultSyncDaoInterface#isAllMsgErrRead()
	 */
	@Override
	public boolean isAllMsgErrRead() {
		boolean res = false;
		try {
			Query query = em.createQuery("select rs from ResultSync rs where rs.toolTip = true");
			query.getSingleResult();
			res = false;
		} catch (NoResultException e) {
			res = true;
		} catch (NonUniqueResultException e) {
			res = false;
		}
		
		return res;
	}

	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.dao.ResultSyncDaoInterface#loadAllResultSync()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ResultSync> loadAllResultSync() {
		return em.createQuery("select rs from ResultSync rs order by rs.executionDate DESC").getResultList();
	}
}
