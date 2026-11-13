package com.example.tasks.task;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Statut d'une tâche. Sérialisé en JSON en minuscules : todo, in_progress, done. */
public enum TaskStatus {
    @JsonProperty("todo")
    TODO,
    @JsonProperty("in_progress")
    IN_PROGRESS,
    @JsonProperty("done")
    DONE
}
