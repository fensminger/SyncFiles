package org.fer.syncfiles.repository;

import org.fer.syncfiles.domain.CurrentSynchro;
import org.fer.syncfiles.domain.CurrentSynchroDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by fensm on 02/02/2016.
 */
public interface CurrentSynchroDetailRepository extends MongoRepository<CurrentSynchroDetail, String> {
    List<CurrentSynchroDetail> findByCurrentSynchroId(String currentSynchroId);
    void deleteByCurrentSynchroId(String currentSynchroId);
}
