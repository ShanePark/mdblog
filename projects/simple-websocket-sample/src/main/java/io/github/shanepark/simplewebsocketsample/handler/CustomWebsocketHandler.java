package io.github.shanepark.simplewebsocketsample.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class CustomWebsocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var clientMessage = message.getPayload();
        session.sendMessage(new TextMessage("Hello, " + clientMessage));
    }

}
