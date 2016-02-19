package org.fer.syncfiles.repository.syncfiles;

import org.fer.syncfiles.domain.syncfiles.ParamSyncFiles;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by fensm on 02/02/2016.
 */
public interface ParamSyncFilesRepository extends MongoRepository<ParamSyncFiles, String> {
    public ParamSyncFiles findByName(String name);
}
