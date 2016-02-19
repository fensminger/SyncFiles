package org.fer.syncfiles.service;

import org.fer.syncfiles.dto.SynchroInfoDto;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by fer on 30/04/2015.
 */

@Service
@Scope("singleton")
public class FeedbackEventImpl implements FeedbackEvent {
    @Inject
    SimpMessageSendingOperations messagingTemplate;

    ArrayDeque<SynchroInfoDto> synchroInfoDtoList = new ArrayDeque<>();


    @Override
    public synchronized void sendEvent(SynchroInfoDto synchroInfoDto) {
        synchroInfoDtoList.offer(synchroInfoDto);
        if (synchroInfoDtoList.size()>10000) {
            synchroInfoDtoList.poll();
        }
        messagingTemplate.convertAndSend("/topic/synchro", synchroInfoDto);
    }

    @Override
    public Collection<SynchroInfoDto> getCurrentEvents() {
        return Collections.unmodifiableCollection(synchroInfoDtoList);
    }
}
