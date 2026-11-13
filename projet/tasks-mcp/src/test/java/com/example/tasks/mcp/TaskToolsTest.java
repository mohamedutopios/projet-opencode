package com.example.tasks.mcp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestClient;

@DisplayName("outils MCP tasks-api")
class TaskToolsTest {

    private static final String BASE = "http://tasks.test";
    private static final String TASK_JSON = """
            {"id":"11111111-1111-1111-1111-111111111111","title":"A","status":"todo",
             "createdAt":"2026-01-01T00:00:00Z","updatedAt":"2026-01-01T00:00:00Z"}""";

    private MockRestServiceServer server;
    private TaskTools tools;

    private void setUp(String token) {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        tools = new TaskTools(new TasksApiClient(builder, new TasksApiProperties(BASE, token)));
    }

    @BeforeEach
    void defaultSetUp() {
        setUp("secret");
    }

    @Test
    @DisplayName("list_tasks filtre par statut")
    void listFiltersByStatus() {
        server.expect(requestTo(BASE + "/tasks")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + TASK_JSON + "," + TASK_JSON.replace("todo", "done") + "]",
                        MediaType.APPLICATION_JSON));
        assertThat(tools.listTasks("done")).extracting(TaskDto::status).containsExactly("done");
        server.verify();
    }

    @Test
    @DisplayName("list_tasks refuse un statut inconnu sans appeler l'API")
    void listRejectsUnknownStatus() {
        assertThatThrownBy(() -> tools.listTasks("archived"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Statut invalide");
        server.verify();
    }

    @Test
    @DisplayName("create_task envoie le Bearer token et le payload")
    void createSendsToken() {
        server.expect(requestTo(BASE + "/tasks")).andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer secret"))
                .andExpect(content().json("{\"title\":\"A\"}"))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(TASK_JSON));
        assertThat(tools.createTask("A", null, null).title()).isEqualTo("A");
        server.verify();
    }

    @Test
    @DisplayName("create_task n'envoie pas de header sans token")
    void createWithoutToken() {
        setUp("");
        server.expect(requestTo(BASE + "/tasks"))
                .andExpect(MockRestRequestMatchers.headerDoesNotExist("Authorization"))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(TASK_JSON));
        tools.createTask("A", null, null);
        server.verify();
    }

    @Test
    @DisplayName("update_task envoie uniquement les champs fournis")
    void updateSendsOnlyProvidedFields() {
        server.expect(requestTo(BASE + "/tasks/11111111-1111-1111-1111-111111111111"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("{\"status\":\"done\"}", org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(TASK_JSON.replace("todo", "done"), MediaType.APPLICATION_JSON));
        assertThat(tools.updateTask("11111111-1111-1111-1111-111111111111", null, null, "done").status())
                .isEqualTo("done");
        server.verify();
    }

    @Test
    @DisplayName("delete_task confirme la suppression")
    void deleteConfirms() {
        server.expect(requestTo(BASE + "/tasks/abc")).andExpect(method(HttpMethod.DELETE))
                .andRespond(withNoContent());
        assertThat(tools.deleteTask("abc")).containsEntry("deleted", "abc");
        server.verify();
    }

    @Test
    @DisplayName("remonte le code HTTP d'une erreur de l'API")
    void propagatesApiError() {
        server.expect(requestTo(BASE + "/tasks/abc"))
                .andRespond(withResourceNotFound().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body("{\"status\":404,\"detail\":\"Tâche introuvable\"}"));
        assertThatThrownBy(() -> tools.getTask("abc"))
                .isInstanceOfSatisfying(TasksApiException.class, e -> assertThat(e.status()).isEqualTo(404))
                .hasMessageContaining("Tâche introuvable");
    }

    @Test
    @DisplayName("signale une API injoignable")
    void unreachableApi() {
        RestClient.Builder builder = RestClient.builder();
        TaskTools unreachable = new TaskTools(new TasksApiClient(builder, new TasksApiProperties("http://127.0.0.1:1", "")));
        assertThatThrownBy(unreachable::health)
                .isInstanceOfSatisfying(TasksApiException.class, e -> assertThat(e.status()).isZero())
                .hasMessageContaining("injoignable");
    }
}
