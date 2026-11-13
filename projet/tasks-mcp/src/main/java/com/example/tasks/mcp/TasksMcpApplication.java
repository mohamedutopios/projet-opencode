package com.example.tasks.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TasksMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasksMcpApplication.class, args);
    }
}
