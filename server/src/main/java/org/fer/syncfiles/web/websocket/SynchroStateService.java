package org.fer.syncfiles.web.websocket;

import org.fer.syncfiles.dto.SynchroInfoDto;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.inject.Inject;
import java.security.Principal;
import java.util.Date;

@Controller
public class SynchroStateService {

    private static final Logger log = LoggerFactory.getLogger(SynchroStateService.class);

    private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @Inject
    SimpMessageSendingOperations messagingTemplate;

    @SendTo("/topic/synchro")
    public SynchroInfoDto sendActivity(@Payload SynchroInfoDto synchroInfoDto, StompHeaderAccessor stompHeaderAccessor, Principal principal) {
        log.debug("Sending user tracking data {}", synchroInfoDto);
        return synchroInfoDto;
    }

    @Scheduled(fixedDelay = 60000)
    public void schedSynchroInfo() {
        SynchroInfoDto synchroInfoDto = new SynchroInfoDto();
        synchroInfoDto.setCode("Test");
        synchroInfoDto.setDescription(new Date().toString());
        messagingTemplate.convertAndSend("/topic/synchro", synchroInfoDto);
    }
}
