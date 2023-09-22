package io.github.shanepark.simplewebsocketsample.handler;

import io.github.shanepark.simplewebsocketsample.service.ProcessNotifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class CustomWebsocketHandler extends TextWebSocketHandler {

    private final ProcessNotifier processNotifier;

    public CustomWebsocketHandler(ProcessNotifier processNotifier) throws IOException {
        this.processNotifier = processNotifier;
        processNotifier.run();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var clientMessage = message.getPayload();
        int id = Integer.parseInt(clientMessage);
        processNotifier.addSession(session, id);

        TextMessage textMessage = new TextMessage(String.format("Hello. your sessionId is %s and you will receive message for [%s] list", session.getId(), id));
        session.sendMessage(textMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        processNotifier.removeSession(session);
    }

}
