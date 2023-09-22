package io.github.shanepark.simplewebsocketsample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SimpleWebsocketSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleWebsocketSampleApplication.class, args);
    }

}
