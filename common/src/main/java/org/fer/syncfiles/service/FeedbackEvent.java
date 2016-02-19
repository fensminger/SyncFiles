package org.fer.syncfiles.service;

import org.fer.syncfiles.dto.SynchroInfoDto;

import java.util.Collection;

/**
 * Created by fer on 30/04/2015.
 */
public interface FeedbackEvent {
    public void sendEvent(SynchroInfoDto synchroInfoDto);

    Collection<SynchroInfoDto> getCurrentEvents();
}
