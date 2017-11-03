package org.fer.syncfiles.repository;

import org.fer.syncfiles.domain.HubicInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by fensm on 02/02/2016.
 */
public interface HubicInfoRepository extends MongoRepository<HubicInfo, String> {
    Stream<HubicInfo> findByName(String name);
    HubicInfo findByContainerAndName(String container, String name);
    Stream<HubicInfo> findByContainer(String container);
}
