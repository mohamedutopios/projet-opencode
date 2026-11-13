package com.example.tasks.task;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "tasks.api-token=")
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    TaskRepository repository;

    @BeforeEach
    void reset() {
        repository.deleteAll();
    }

    private String createTask(String json) throws Exception {
        String body = mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    @Test
    @DisplayName("GET /actuator/health répond UP")
    void health() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("crée puis lit une tâche")
    void createThenGet() throws Exception {
        String body = mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"  Écrire le guide  \"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Écrire le guide"))
                .andExpect(jsonPath("$.status").value("todo"))
                .andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(body, "$.id");

        mvc.perform(get("/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @DisplayName("renvoie le header Location à la création")
    void locationHeader() throws Exception {
        var response = mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"A\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        String id = JsonPath.read(response.getContentAsString(), "$.id");
        org.assertj.core.api.Assertions.assertThat(response.getHeader("Location")).isEqualTo("/tasks/" + id);
    }

    @Test
    @DisplayName("liste les tâches")
    void list() throws Exception {
        createTask("{\"title\":\"A\"}");
        createTask("{\"title\":\"B\",\"status\":\"done\"}");
        mvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("refuse une tâche sans titre")
    void rejectBlankTitle() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Requête invalide"))
                .andExpect(jsonPath("$.errors[0].field").value("title"));
    }

    @Test
    @DisplayName("refuse un statut inconnu")
    void rejectUnknownStatus() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"X\",\"status\":\"archived\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("met à jour une tâche")
    void update() throws Exception {
        String id = createTask("{\"title\":\"A\"}");
        mvc.perform(patch("/tasks/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"in_progress\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("in_progress"))
                .andExpect(jsonPath("$.title").value("A"));
    }

    @Test
    @DisplayName("refuse un PATCH vide")
    void rejectEmptyPatch() throws Exception {
        String id = createTask("{\"title\":\"A\"}");
        mvc.perform(patch("/tasks/" + id).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("supprime une tâche")
    void deleteTask() throws Exception {
        String id = createTask("{\"title\":\"A\"}");
        mvc.perform(delete("/tasks/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/tasks/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("renvoie 404 sur un id inexistant")
    void notFound() throws Exception {
        mvc.perform(get("/tasks/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("renvoie 400 sur un id mal formé")
    void malformedId() throws Exception {
        mvc.perform(get("/tasks/pas-un-uuid")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("renvoie 400 sur un JSON invalide")
    void invalidJson() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content("{ invalide"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("JSON invalide"));
    }
}
