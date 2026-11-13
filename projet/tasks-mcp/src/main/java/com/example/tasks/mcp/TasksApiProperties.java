package com.example.tasks.mcp;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cible du MCP server, préfixe {@code tasks.api}.
 *
 * @param url   URL de base de tasks-api
 * @param token Bearer token envoyé sur les écritures ; vide = aucun header
 */
@ConfigurationProperties(prefix = "tasks.api")
public record TasksApiProperties(String url, String token) {

    public boolean hasToken() {
        return token != null && !token.isBlank();
    }
}
