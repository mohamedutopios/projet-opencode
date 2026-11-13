package com.example.tasks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration applicative, préfixe {@code tasks}.
 *
 * @param apiToken Bearer token exigé sur POST/PATCH/DELETE ; vide = routes en écriture ouvertes
 */
@ConfigurationProperties(prefix = "tasks")
public record TasksProperties(String apiToken) {

    public boolean writeProtectionEnabled() {
        return apiToken != null && !apiToken.isBlank();
    }
}
