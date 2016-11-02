package org.fer.syncfiles.service.syncfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fer.syncfiles.domain.syncfiles.MsgDownloadUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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
// @Component("uploadSocketHandler")
public class UploadSocketHandler extends DownloadSocketHandler {
}
