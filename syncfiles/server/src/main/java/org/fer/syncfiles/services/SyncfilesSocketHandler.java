package org.fer.syncfiles.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fer.syncfiles.domain.SyncfilesSynchroMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fensm on 05/09/2016.
 */
@Component("syncfilesSocketHandler")
public class SyncfilesSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(SyncfilesSocketHandler.class);

    protected Map<String, WebSocketSession> sessionMap = new HashMap<>();
    protected Map<Long, SyncfilesSynchroMsg> syncfilesSynchroMsgMap = new ConcurrentHashMap<>();
    protected AtomicLong currentId = new AtomicLong(1);

    protected AtomicLong nbOfMessageToSend = new AtomicLong(0);

    @Scheduled(fixedRate = 1000)
    protected void sendMessage() {
        try {
            if (nbOfMessageToSend.getAndSet(0L)==0) {
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String msg = objectMapper.writeValueAsString(syncfilesSynchroMsgMap.values());
            for(WebSocketSession session : sessionMap.values()) {
                    session.sendMessage(new TextMessage(msg));
            }
        } catch (IOException e) {
            log.error("Ignore socket error sending message : " + e.getMessage(), e);
        }
    }

    public long addNewSynchro(String type, String title, String message) {
        SyncfilesSynchroMsg syncfilesSynchroMsg = null;
        synchronized (syncfilesSynchroMsgMap) {
            long id = currentId.getAndAdd(1);
            syncfilesSynchroMsg = new SyncfilesSynchroMsg(id, type, title);
            syncfilesSynchroMsg.setLastStateDate(new Date());
            syncfilesSynchroMsg.setRunning(true);
            if (message!=null) {
                syncfilesSynchroMsg.addMessage(message);
            }
        }
        syncfilesSynchroMsgMap.put(syncfilesSynchroMsg.getId(), syncfilesSynchroMsg);
        nbOfMessageToSend.incrementAndGet();
        sendMessage();
        return syncfilesSynchroMsg.getId();
    }

    public SyncfilesSynchroMsg getSyncfilesSynchroMsg(long id) {
        return syncfilesSynchroMsgMap.get(id);
    }

    public void addMessage(long id, String msg) {
        if (id==-1L) {
            return;
        }
        SyncfilesSynchroMsg syncfilesSynchroMsg = syncfilesSynchroMsgMap.get(id);
        syncfilesSynchroMsg.addMessage(msg);
        nbOfMessageToSend.incrementAndGet();
        // sendMessage();
    }

    public void addError(long id, String msgErr, Exception e) {
        if (id==-1L) {
            return;
        }
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter errWriter = new PrintWriter(stringWriter)) {
            e.printStackTrace(errWriter);
            errWriter.flush();
            String msgErrStackTrace = stringWriter.getBuffer().toString();
//            msgErrStackTrace.replaceAll("\n","<br/>").replaceAll("\r","");
            SyncfilesSynchroMsg syncfilesSynchroMsg = syncfilesSynchroMsgMap.get(id);
            syncfilesSynchroMsg.setLastStateDate(new Date());
            syncfilesSynchroMsg.setMsgError(msgErr);
            syncfilesSynchroMsg.setMsgErrorStackTrace(msgErrStackTrace);
            syncfilesSynchroMsg.setRunning(false);
            nbOfMessageToSend.incrementAndGet();
            // sendMessage();
        }
    }

    public void removeError(long id) {
        if (id==-1L) {
            return;
        }
        SyncfilesSynchroMsg syncfilesSynchroMsg = syncfilesSynchroMsgMap.get(id);
        syncfilesSynchroMsg.setLastStateDate(new Date());
        syncfilesSynchroMsg.setMsgError(null);
        syncfilesSynchroMsg.setMsgErrorStackTrace(null);
        syncfilesSynchroMsg.setRunning(true);
        nbOfMessageToSend.incrementAndGet();
        // sendMessage();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connection established : " + session.getId());
        this.sessionMap.put(session.getId(), session);
        nbOfMessageToSend.incrementAndGet();
        // sendMessage();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connection removed : " + session.getId());
        sessionMap.remove(session.getId());
    }



    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
        throws Exception {
        log.info("Websocket message : " + message.getPayload());
//        if ("CLOSE".equalsIgnoreCase(message.getPayload())) {
//            session.close();
//        } else {
//            System.out.println("Received:" + message.getPayload());
//        }
    }

    public void removeSynchro(Long syncfilesInfoId) {
        if (syncfilesInfoId==-1L) {
            return;
        }

        new Thread(() -> {
            try {
                Thread.sleep(1000*60*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            syncfilesSynchroMsgMap.remove(syncfilesInfoId);
            nbOfMessageToSend.incrementAndGet();
            sendMessage();
        }).start();

    }
}
