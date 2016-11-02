package org.fer.syncfiles.repository;

import org.fer.syncfiles.domain.CurrentSynchro;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by fensm on 02/02/2016.
 */
public interface CurrentSynchroRepository extends MongoRepository<CurrentSynchro, String> {
    List<CurrentSynchro> findByType(String type);
}
