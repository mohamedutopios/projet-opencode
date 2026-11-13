package com.example.tasks.mcp;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Représentation d'une tâche telle que renvoyée par tasks-api (statut en minuscules). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskDto(
        String id,
        String title,
        String description,
        String status,
        String createdAt,
        String updatedAt) {
}
