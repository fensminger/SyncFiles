package org.fer.syncfiles.repository;

import org.fer.syncfiles.domain.ObjectInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by fensm on 02/02/2016.
 */
public interface ObjectInfoRepository extends MongoRepository<ObjectInfo, String> {

}
