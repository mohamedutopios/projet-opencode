package com.example.tasks.mcp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/** Client HTTP de tasks-api. Le token n'est jamais journalisé ni renvoyé à l'appelant. */
@Component
public class TasksApiClient {

    private final RestClient restClient;
    private final TasksApiProperties properties;

    public TasksApiClient(RestClient.Builder builder, TasksApiProperties properties) {
        this.properties = properties;
        this.restClient = builder
                .baseUrl(properties.url())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultStatusHandler(status -> status.isError(), (request, response) -> {
                    String body = new String(response.getBody().readAllBytes());
                    int code = response.getStatusCode().value();
                    throw new TasksApiException(code, body.isBlank() ? "HTTP " + code : "HTTP " + code + " : " + body);
                })
                .build();
    }

    public Map<String, Object> health() {
        return call(() -> restClient.get().uri("/actuator/health")
                .retrieve().body(new ParameterizedTypeReference<Map<String, Object>>() { }));
    }

    public List<TaskDto> listTasks() {
        return call(() -> restClient.get().uri("/tasks")
                .retrieve().body(new ParameterizedTypeReference<List<TaskDto>>() { }));
    }

    public TaskDto getTask(String id) {
        return call(() -> restClient.get().uri("/tasks/{id}", id).retrieve().body(TaskDto.class));
    }

    public TaskDto createTask(String title, String description, String status) {
        Map<String, Object> payload = payload(title, description, status);
        return call(() -> restClient.post().uri("/tasks").headers(this::authorize)
                .contentType(MediaType.APPLICATION_JSON).body(payload)
                .retrieve().body(TaskDto.class));
    }

    public TaskDto updateTask(String id, String title, String description, String status) {
        Map<String, Object> payload = payload(title, description, status);
        return call(() -> restClient.patch().uri("/tasks/{id}", id).headers(this::authorize)
                .contentType(MediaType.APPLICATION_JSON).body(payload)
                .retrieve().body(TaskDto.class));
    }

    public void deleteTask(String id) {
        call(() -> restClient.delete().uri("/tasks/{id}", id).headers(this::authorize).retrieve().toBodilessEntity());
    }

    private void authorize(HttpHeaders headers) {
        if (properties.hasToken()) {
            headers.setBearerAuth(properties.token());
        }
    }

    private static Map<String, Object> payload(String title, String description, String status) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (title != null) payload.put("title", title);
        if (description != null) payload.put("description", description);
        if (status != null) payload.put("status", status);
        return payload;
    }

    private <T> T call(java.util.function.Supplier<T> action) {
        try {
            return action.get();
        } catch (ResourceAccessException e) {
            throw new TasksApiException(0, "tasks-api injoignable sur " + properties.url()
                    + " (" + e.getMostSpecificCause().getMessage() + "). Lancer « ./mvnw -pl tasks-api spring-boot:run ».");
        }
    }
}
