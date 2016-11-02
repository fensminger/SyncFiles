package org.fer.syncfiles.config;

import com.mongodb.Mongo;
import org.fer.syncfiles.service.UserService;
import org.fer.syncfiles.service.syncfiles.DownloadSocketHandler;
import org.fer.syncfiles.service.syncfiles.ParamSyncFilesService;
import org.fer.syncfiles.service.syncfiles.TestSocketHandler;
import org.fer.syncfiles.service.syncfiles.UploadSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.inject.Inject;

/**
 * Created by fensm on 08/09/2016.
 */
@Configuration
@EnableWebSocket
@EnableScheduling
public class WebsocketUploadDownloadConfig implements WebSocketConfigurer {

//    @Autowired
//    @Qualifier("fredTest")
//    private DownloadSocketHandler downloadSocketHandler;
//
//    @Inject
//    private Environment env;
//
//    @Inject
//    private UserService userService;

//    @Autowired
//    @Qualifier("uploadSocketHandler")
//    private UploadSocketHandler uploadSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(uploadSocketHandler, "/websocket/upload").setAllowedOrigins("http://localhost:4200", "http://localhost:9000");
//        registry.addHandler(downloadSocketHandler, "/websocket/download").setAllowedOrigins("http://localhost:4200", "http://localhost:9000");

    }
}
