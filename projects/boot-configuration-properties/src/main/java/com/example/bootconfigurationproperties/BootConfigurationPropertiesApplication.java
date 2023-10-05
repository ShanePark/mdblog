package com.example.bootconfigurationproperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BootConfigurationPropertiesApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootConfigurationPropertiesApplication.class, args);
    }

}
