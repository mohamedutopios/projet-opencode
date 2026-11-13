package com.example.tasks.error;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(UUID id) {
        super("Tâche introuvable : " + id);
    }
}
