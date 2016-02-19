package org.fer.syncfiles;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fer on 03/02/15.
 */
public class TestServer {
    public static void main(String [] args) throws InterruptedException {
        List<Transport> transports = new ArrayList<Transport>(2);
        final StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        transports.add(new WebSocketTransport(webSocketClient));
//        transports.add(new RestTemplateXhrTransport());

        SockJsClient sockJsClient = new SockJsClient(transports);
//        SockJsMessageCodec messCodec = new Jackson2SockJsMessageCodec();
//        sockJsClient.setMessageCodec(messCodec);
        sockJsClient.doHandshake(new MyWebSocketHandler(), "ws://localhost:8080/add"); // topic/showResult

        while(true) {
            Thread.sleep(2000);
        }
    }

    private static class MyWebSocketHandler implements WebSocketHandler {
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            System.out.print("afterConnectionEstablished");
        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            System.out.print("handleMessage");
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            System.out.print("handleTransportError");
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            System.out.print("afterConnectionClosed");
        }

        @Override
        public boolean supportsPartialMessages() {
            System.out.print("supportsPartialMessages");
            return false;
        }
    }

}
