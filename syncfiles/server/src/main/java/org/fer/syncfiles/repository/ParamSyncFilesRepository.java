package org.fer.syncfiles.repository;

import org.fer.syncfiles.domain.ParamSyncFiles;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by fensm on 02/02/2016.
 */
public interface ParamSyncFilesRepository extends MongoRepository<ParamSyncFiles, String> {
    public ParamSyncFiles findByName(String name);
}
