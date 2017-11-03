package org.fer.syncfiles.repository;

import org.fer.syncfiles.domain.ObjectDetailInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by fensm on 02/02/2016.
 */
public interface ObjectDetailInfoRepository extends MongoRepository<ObjectDetailInfo, String> {

}
