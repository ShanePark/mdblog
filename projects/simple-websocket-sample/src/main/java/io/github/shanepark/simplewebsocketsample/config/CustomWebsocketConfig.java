package io.github.shanepark.simplewebsocketsample.config;

import io.github.shanepark.simplewebsocketsample.handler.CustomWebsocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class CustomWebsocketConfig implements WebSocketConfigurer {

    private final CustomWebsocketHandler customWebsocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customWebsocketHandler, "/websocket");
    }

}
