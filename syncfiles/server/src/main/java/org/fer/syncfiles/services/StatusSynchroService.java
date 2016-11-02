package org.fer.syncfiles.services;

import org.fer.syncfiles.domain.CurrentSynchro;
import org.fer.syncfiles.domain.CurrentSynchroDetail;
import org.fer.syncfiles.repository.CurrentSynchroDetailRepository;
import org.fer.syncfiles.repository.CurrentSynchroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by fensm on 23/09/2016.
 */
@Service
public class StatusSynchroService {
    private CurrentSynchroRepository currentSynchroRepository;
    private CurrentSynchroDetailRepository currentSynchroDetailRepository;

    @Autowired
    public StatusSynchroService(
            CurrentSynchroRepository currentSynchroRepository
            , CurrentSynchroDetailRepository currentSynchroDetailRepository) {
        this.currentSynchroRepository = currentSynchroRepository;
        this.currentSynchroDetailRepository = currentSynchroDetailRepository;
    }

    public CurrentSynchro addSynchro(CurrentSynchro currentSynchro) {
        return currentSynchroRepository.save(currentSynchro);
    }

    public void addDetailSynchro(String synchroId, String relativePath) {
        CurrentSynchroDetail currentSynchroDetail = new CurrentSynchroDetail();
        currentSynchroDetail.setCurrentSynchroId(synchroId);
        currentSynchroDetail.setRelativePath(relativePath);
        currentSynchroDetailRepository.save(currentSynchroDetail);
    }

    public Map<String, String> loadCurrentInfoSynchro(String synchroId) {
        return currentSynchroDetailRepository.findByCurrentSynchroId(synchroId).stream()
                .collect(Collectors.toMap(CurrentSynchroDetail::getRelativePath, CurrentSynchroDetail::getRelativePath));
    }

    public void removeSynchro(String synchroId) {
        CurrentSynchro curSynchro = currentSynchroRepository.findOne(synchroId);

        currentSynchroDetailRepository.deleteByCurrentSynchroId(curSynchro.getId());
        currentSynchroRepository.delete(curSynchro);
    }
}
