package com.example.tasks.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Task(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        Instant createdAt,
        Instant updatedAt) {

    Task withChanges(String newTitle, String newDescription, TaskStatus newStatus, Instant now) {
        return new Task(
                id,
                newTitle != null ? newTitle : title,
                newDescription != null ? newDescription : description,
                newStatus != null ? newStatus : status,
                createdAt,
                now);
    }
}
