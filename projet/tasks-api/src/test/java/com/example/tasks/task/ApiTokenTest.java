package com.example.tasks.task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "tasks.api-token=secret-test")
@AutoConfigureMockMvc
@DisplayName("protection par token")
class ApiTokenTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("autorise la lecture sans token")
    void readWithoutToken() throws Exception {
        mvc.perform(get("/tasks")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("refuse l'écriture sans token")
    void writeWithoutToken() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"A\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Token manquant ou invalide"));
    }

    @Test
    @DisplayName("refuse l'écriture avec un mauvais token")
    void writeWithWrongToken() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer mauvais")
                        .content("{\"title\":\"A\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("autorise l'écriture avec le bon token")
    void writeWithToken() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer secret-test")
                        .content("{\"title\":\"A\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("laisse le health check public")
    void healthIsPublic() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
