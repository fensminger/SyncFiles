package org.fer.syncfiles.bus;

import javax.persistence.EntityManager;

public class AbstractDao {

	protected final EntityManager em;
	
	public AbstractDao(EntityManager em) {
		super();
		this.em = em;
	}

}
