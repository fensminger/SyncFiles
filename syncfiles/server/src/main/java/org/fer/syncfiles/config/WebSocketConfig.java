package org.fer.syncfiles.config;

import org.fer.syncfiles.services.SyncfilesSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created by fen on 30/08/2016.
 */
@Configuration
@EnableWebSocket
@EnableScheduling
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
    @Qualifier("syncfilesSocketHandler")
    SyncfilesSocketHandler syncfilesSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(syncfilesSocketHandler, "/websocket/syncfilesInfo").setAllowedOrigins("http://localhost:4100", "http://localhost:4200");
	}

}
