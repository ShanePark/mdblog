package io.github.shanepark.simplewebsocketsample.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ProcessNotifier {

    ConcurrentHashMap<WebSocketSession, Integer> sessionMap = new ConcurrentHashMap<>();

    @Async
    public void run() throws IOException {
        while (true) {
            for (int i = 0; i < 10; i++) {
                for (Map.Entry<WebSocketSession, Integer> e : sessionMap.entrySet()) {
                    int id = e.getValue();
                    if (id != i) {
                        continue;
                    }
                    WebSocketSession session = e.getKey();
                    session.sendMessage(new TextMessage(i + "is now processing"));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void addSession(WebSocketSession session, int id) {
        log.info("{} is Now on receiving message for {} list", session, id);
        sessionMap.put(session, id);
    }

    public void removeSession(WebSocketSession session) {
        sessionMap.remove(session);
        log.info("Connection closed: {}", session.getId());
    }
}
