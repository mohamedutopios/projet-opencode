package com.example.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TasksApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasksApiApplication.class, args);
    }
}
