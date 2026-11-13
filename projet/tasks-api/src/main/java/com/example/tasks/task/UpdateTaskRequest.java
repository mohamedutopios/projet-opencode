package com.example.tasks.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @Size(max = 120) @Pattern(regexp = "(?s).*\\S.*", message = "title ne peut pas être vide") String title,
        @Size(max = 1000) String description,
        TaskStatus status) {

    @JsonIgnore
    @AssertTrue(message = "Au moins un champ doit être fourni")
    public boolean isAnyFieldPresent() {
        return title != null || description != null || status != null;
    }
}
