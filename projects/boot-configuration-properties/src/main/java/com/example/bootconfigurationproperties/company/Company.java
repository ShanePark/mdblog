package com.example.bootconfigurationproperties.company;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "company")
public record Company(
        String name,
        Location location,
        List<Employee> employees
) {
}
