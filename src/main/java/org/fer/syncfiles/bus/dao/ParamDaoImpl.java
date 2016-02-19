package org.fer.syncfiles.bus.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.fer.syncfiles.bus.AbstractDao;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.model.ParamList;
import org.fer.syncfiles.model.ResultSync;
import org.fer.syncfiles.model.ResultSyncDetail;

import com.google.inject.Inject;

public class ParamDaoImpl extends AbstractDao implements ParamDao {
	//private static final Logger log = Logger.getLogger(ParamDao.class);

	@Inject
	public ParamDaoImpl(EntityManager em) {
		super(em);
	}

	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.ParamDaoInterface#save(org.fer.syncfiles.model.Param)
	 */
	@Override
	public Param save(Param param) {
		Param res = null;
		if (param.getId()==null) {
			em.persist(param);
			res = em.find(Param.class, param.getId());
		} else {
			res = em.merge(param);
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.ParamDaoInterface#delete(org.fer.syncfiles.model.Param)
	 */
	@Override
	public void delete(Param paramToDelete) {
		Param param = em.find(Param.class, paramToDelete.getId());
		em.remove(param);
	}
	
	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.ParamDaoInterface#findAll()
	 */
	@Override
	public ParamList findAll() {
		List<Param> paramList = em.createQuery("SELECT p FROM Param p", Param.class).getResultList();
		ParamList res = new ParamList();
		res.setParams(paramList);
		return res;
	}

	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.ParamDaoInterface#load(org.fer.syncfiles.model.Param)
	 */
	@Override
	public Param load(Param param) {
		if (param==null) {
			return new Param();
		}
		
		Param res = em.find(Param.class, param.getId());
		
		return res;
	}
	
}
