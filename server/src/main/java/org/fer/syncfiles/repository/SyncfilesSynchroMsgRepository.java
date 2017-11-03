package org.fer.syncfiles.repository;

import org.fer.syncfiles.domain.SyncfilesSynchroMsg;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by fensm on 03/11/2016.
 */
public interface SyncfilesSynchroMsgRepository extends MongoRepository<SyncfilesSynchroMsg, String> {
    public SyncfilesSynchroMsg findOneByParamSyncFilesId(String paramSyncFilesId);
    public List<SyncfilesSynchroMsg> findByRunning(boolean running);
}
