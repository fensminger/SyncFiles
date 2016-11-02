package org.fer.syncfiles.service.syncfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fer.syncfiles.domain.syncfiles.MsgDownloadUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fensm on 05/09/2016.
 */
@Component("fredTest")
public class DownloadSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(DownloadSocketHandler.class);

    protected Map<String, WebSocketSession> sessionMap = new HashMap<>();
    protected MsgDownloadUpload msgDownloadUpload = new MsgDownloadUpload();

    protected void sendMessage() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String msg = objectMapper.writeValueAsString(msgDownloadUpload);
            for(WebSocketSession session : sessionMap.values()) {
                    session.sendMessage(new TextMessage(msg));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void addNewSynchro(long nbFiles) {
        synchronized (msgDownloadUpload) {
            if (msgDownloadUpload.getCountFiles().equals(msgDownloadUpload.getTotalNbOfFiles())) {
                msgDownloadUpload.setTotalNbOfFiles(msgDownloadUpload.getTotalNbOfFiles());
                msgDownloadUpload.setCountFiles(0L);
            } else {
                msgDownloadUpload.setTotalNbOfFiles(msgDownloadUpload.getTotalNbOfFiles()+nbFiles);
            }
            msgDownloadUpload.setLastStateDate(new Date());
        }
        sendMessage();
    }

    public void addFile(String msg) {
        synchronized (msgDownloadUpload) {
            msgDownloadUpload.getLastMsgList().add(msg);
            msgDownloadUpload.setCountFiles(msgDownloadUpload.getCountFiles()+1);
        }
        sendMessage();
    }

    public void addError(String msgErr, String msgErrStackTrace) {
        synchronized (msgDownloadUpload) {
            msgDownloadUpload.setLastStateDate(new Date());
            msgDownloadUpload.setMsgError(msgErr);
            msgDownloadUpload.setMsgErrorStackTrace(msgErrStackTrace);
            msgDownloadUpload.setRunning(false);
        }
        sendMessage();
    }

    public void removeError() {
        synchronized (msgDownloadUpload) {
            msgDownloadUpload.setLastStateDate(new Date());
            msgDownloadUpload.setMsgError(null);
            msgDownloadUpload.setMsgErrorStackTrace(null);
            msgDownloadUpload.setRunning(true);
        }
        sendMessage();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connection established : " + session.getId());
        this.sessionMap.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
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

}