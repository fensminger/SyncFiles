package org.fer.syncfiles.repository.syncfiles;

import org.fer.syncfiles.domain.syncfiles.ObjectDetailInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by fensm on 02/02/2016.
 */
public interface ObjectDetailInfoRepository extends MongoRepository<ObjectDetailInfo, String> {

}
