package org.fer.syncfiles.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fer.syncfiles.domain.ParamSyncFiles;
import org.fer.syncfiles.domain.SyncfilesSynchroMsg;
import org.fer.syncfiles.repository.SyncfilesSynchroMsgRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
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
    protected Map<String, SyncfilesSynchroMsg> syncfilesSynchroMsgMap = new ConcurrentHashMap<>();

    protected AtomicLong nbOfMessageToSend = new AtomicLong(0);

    @Autowired
    private SyncfilesSynchroMsgRepository syncfilesSynchroMsgRepository;

    @PostConstruct
    public void postConstruct() {
        for(SyncfilesSynchroMsg m : syncfilesSynchroMsgRepository.findByRunning(true)) {
            m.setRunning(false);
            syncfilesSynchroMsgRepository.save(m);
        }
    }

    @Scheduled(fixedRate = 1000)
    protected void sendMessage() {
        if (nbOfMessageToSend.getAndSet(0L)==0) {
            return;
        }

        forceSendMessage();
    }

    private void forceSendMessage() {
        nbOfMessageToSend.set(0L);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String msg = objectMapper.writeValueAsString(syncfilesSynchroMsgMap.values());
            for(WebSocketSession session : sessionMap.values()) {
                    session.sendMessage(new TextMessage(msg));
            }
        } catch (IOException e) {
            log.error("Ignore socket error sending message : " + e.getMessage(), e);
        }
        syncfilesSynchroMsgMap.values().stream()
                .filter(SyncfilesSynchroMsg::isChanged)
                .forEach(syncfilesSynchroMsg -> {
                    syncfilesSynchroMsg.setChanged(false);
                    SyncfilesSynchroMsg res = syncfilesSynchroMsgRepository.save(syncfilesSynchroMsg);
                    syncfilesSynchroMsg.setVersion(res.getVersion());
        });
    }

    public String addNewSynchro(String type, ParamSyncFiles paramSyncFiles, String message) {
        SyncfilesSynchroMsg syncfilesSynchroMsg = null;
        synchronized (syncfilesSynchroMsgMap) {
            SyncfilesSynchroMsg syncfilesSynchroMsgRunning = syncfilesSynchroMsgMap.get(paramSyncFiles.getId());
            if (syncfilesSynchroMsgRunning !=null && syncfilesSynchroMsgRunning.isRunning()) {
                return null;
            }
            syncfilesSynchroMsg = new SyncfilesSynchroMsg(type, paramSyncFiles);
            Date date = new Date();
            syncfilesSynchroMsg.setLastStateDate(date);
            syncfilesSynchroMsg.setStartDate(date);
            syncfilesSynchroMsg.setRunning(true);
            syncfilesSynchroMsg.setChanged(false);
            if (message!=null) {
                syncfilesSynchroMsg.addMessage(message);
            }
            SyncfilesSynchroMsg syncfilesSynchroMsgExist = syncfilesSynchroMsgRepository.findOneByParamSyncFilesId(paramSyncFiles.getId());
            if (syncfilesSynchroMsgExist!=null) {
                syncfilesSynchroMsgRepository.delete(syncfilesSynchroMsgExist);
            }
            syncfilesSynchroMsg = syncfilesSynchroMsgRepository.save(syncfilesSynchroMsg);
        }
        syncfilesSynchroMsgMap.put(paramSyncFiles.getId(), syncfilesSynchroMsg);
        nbOfMessageToSend.incrementAndGet();
        //sendMessage();
        return paramSyncFiles.getId();
    }

    public SyncfilesSynchroMsg getSyncfilesSynchroMsg(String id) {
        return syncfilesSynchroMsgMap.get(id);
    }

    public void addMessage(String id, String msg) {
        if (id==null) {
            return;
        }
        SyncfilesSynchroMsg syncfilesSynchroMsg = syncfilesSynchroMsgMap.get(id);
        syncfilesSynchroMsg.setChanged(true);
        syncfilesSynchroMsg.addMessage(msg);
        syncfilesSynchroMsg.setLastStateDate(new Date());
        nbOfMessageToSend.incrementAndGet();
        // sendMessage();
    }

    public void addError(String id, String msgErr, Exception e) {
        if (id==null) {
            return;
        }
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter errWriter = new PrintWriter(stringWriter)) {
            e.printStackTrace(errWriter);
            errWriter.flush();
            String msgErrStackTrace = stringWriter.getBuffer().toString();
//            msgErrStackTrace.replaceAll("\n","<br/>").replaceAll("\r","");
            SyncfilesSynchroMsg syncfilesSynchroMsg = syncfilesSynchroMsgMap.get(id);
            syncfilesSynchroMsg.setChanged(true);
            syncfilesSynchroMsg.setLastStateDate(new Date());
            syncfilesSynchroMsg.setMsgError(msgErr);
            syncfilesSynchroMsg.setMsgErrorStackTrace(msgErrStackTrace);
            syncfilesSynchroMsg.setRunning(false);
            syncfilesSynchroMsg.setLastStateDate(new Date());
            nbOfMessageToSend.incrementAndGet();
            // sendMessage();
        }
    }

    public void removeError(long id) {
        if (id==-1L) {
            return;
        }
        SyncfilesSynchroMsg syncfilesSynchroMsg = syncfilesSynchroMsgMap.get(id);
        syncfilesSynchroMsg.setChanged(true);
        syncfilesSynchroMsg.setLastStateDate(new Date());
        syncfilesSynchroMsg.setMsgError(null);
        syncfilesSynchroMsg.setMsgErrorStackTrace(null);
        syncfilesSynchroMsg.setRunning(true);
        syncfilesSynchroMsg.setLastStateDate(new Date());
        nbOfMessageToSend.incrementAndGet();
        // sendMessage();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connection established : " + session.getId());
        this.sessionMap.put(session.getId(), session);
        nbOfMessageToSend.incrementAndGet();
        forceSendMessage();
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

    public void removeSynchro(String syncfilesInfoId) {
        SyncfilesSynchroMsg syncfilesSynchroMsg = syncfilesSynchroMsgMap.get(syncfilesInfoId);
        if (syncfilesSynchroMsg!=null) {
            syncfilesSynchroMsg.setChanged(true);
            syncfilesSynchroMsg.setRunning(false);
            syncfilesSynchroMsg.setLastStateDate(new Date());
        } else {
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
//            sendMessage();
        }).start();

    }

    public SyncfilesSynchroMsg findMsgByParamSyncFilesById(String id) {
        return syncfilesSynchroMsgRepository.findOneByParamSyncFilesId(id);
    }
}
