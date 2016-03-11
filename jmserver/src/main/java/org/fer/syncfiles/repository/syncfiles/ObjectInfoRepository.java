package org.fer.syncfiles.repository.syncfiles;

import org.fer.syncfiles.domain.syncfiles.FileInfo;
import org.fer.syncfiles.domain.syncfiles.ObjectInfo;
import org.fer.syncfiles.domain.syncfiles.OriginFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by fensm on 02/02/2016.
 */
public interface ObjectInfoRepository extends MongoRepository<ObjectInfo, String> {

}
