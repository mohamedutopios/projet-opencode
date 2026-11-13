package com.example.tasks.mcp;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpTool.McpAnnotations;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

/**
 * Outils MCP exposés à l'agent. Spring AI les découvre via @McpTool et génère le JSON Schema
 * des paramètres. OpenCode les préfixe par le nom du serveur : tasks-api_list_tasks, etc.
 * Les valeurs par défaut de @McpAnnotations sont pessimistes (destructiveHint = true) :
 * chaque outil déclare donc ses annotations explicitement.
 */
@Component
public class TaskTools {

    private static final Set<String> STATUSES = Set.of("todo", "in_progress", "done");

    private final TasksApiClient client;

    public TaskTools(TasksApiClient client) {
        this.client = client;
    }

    @McpTool(name = "health", description = "Vérifie que tasks-api répond (GET /actuator/health).",
            annotations = @McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public Map<String, Object> health() {
        return client.health();
    }

    @McpTool(name = "list_tasks",
            description = "Liste les tâches de tasks-api, avec filtre optionnel sur le statut.",
            annotations = @McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public List<TaskDto> listTasks(
            @McpToolParam(description = "Filtre : todo, in_progress ou done", required = false) String status) {
        validateStatus(status);
        List<TaskDto> tasks = client.listTasks();
        return status == null ? tasks : tasks.stream().filter(t -> status.equals(t.status())).toList();
    }

    @McpTool(name = "get_task", description = "Retourne une tâche par son id (UUID).",
            annotations = @McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public TaskDto getTask(@McpToolParam(description = "Identifiant de la tâche (UUID)") String id) {
        return client.getTask(id);
    }

    @McpTool(name = "create_task",
            description = "Crée une tâche (POST /tasks). Le token éventuel est géré par le serveur MCP.",
            annotations = @McpAnnotations(readOnlyHint = false, destructiveHint = false, idempotentHint = false,
                    openWorldHint = false))
    public TaskDto createTask(
            @McpToolParam(description = "Titre, 120 caractères maximum") String title,
            @McpToolParam(description = "Description, 1000 caractères maximum", required = false) String description,
            @McpToolParam(description = "Statut initial : todo (défaut), in_progress ou done", required = false)
            String status) {
        validateStatus(status);
        return client.createTask(title, description, status);
    }

    @McpTool(name = "update_task",
            description = "Met à jour le titre, la description ou le statut d'une tâche (PATCH /tasks/{id}).",
            annotations = @McpAnnotations(readOnlyHint = false, destructiveHint = false, idempotentHint = true,
                    openWorldHint = false))
    public TaskDto updateTask(
            @McpToolParam(description = "Identifiant de la tâche (UUID)") String id,
            @McpToolParam(description = "Nouveau titre", required = false) String title,
            @McpToolParam(description = "Nouvelle description", required = false) String description,
            @McpToolParam(description = "Nouveau statut : todo, in_progress ou done", required = false) String status) {
        validateStatus(status);
        return client.updateTask(id, title, description, status);
    }

    @McpTool(name = "delete_task", description = "Supprime définitivement une tâche (DELETE /tasks/{id}).",
            annotations = @McpAnnotations(readOnlyHint = false, destructiveHint = true, idempotentHint = true,
                    openWorldHint = false))
    public Map<String, String> deleteTask(@McpToolParam(description = "Identifiant de la tâche (UUID)") String id) {
        client.deleteTask(id);
        return Map.of("deleted", id);
    }

    private static void validateStatus(String status) {
        if (status != null && !STATUSES.contains(status)) {
            throw new IllegalArgumentException("Statut invalide : " + status + " (attendu : todo, in_progress, done)");
        }
    }
}
