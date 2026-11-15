---
name: write-tests
description: Patterns de tests JUnit 5 pour ce projet Spring Boot 4 (MockMvc + jsonPath, @SpringBootTest, propriétés de test, token, MockRestServiceServer pour le module tasks-mcp). À charger avant d'écrire ou de compléter des tests dans src/test.
---

# Écrire des tests — Spring Boot 4

## Test d'endpoint (tasks-api)

```java
@SpringBootTest(properties = "tasks.api-token=")   // token vide : écritures ouvertes
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired MockMvc mvc;
    @Autowired TaskRepository repository;

    @BeforeEach
    void reset() {
        repository.deleteAll();                     // aucun état partagé entre tests
    }

    @Test
    @DisplayName("refuse une tâche sans titre")
    void rejectBlankTitle() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("title"));
    }
}
```

Imports Spring Boot 4 (les packages de test ont changé depuis Boot 3) :

| Classe | Package |
|---|---|
| `@AutoConfigureMockMvc`, `@WebMvcTest` | `org.springframework.boot.webmvc.test.autoconfigure` |
| `@SpringBootTest` | `org.springframework.boot.test.context` |
| `MockMvc`, `post`, `get`, `status`, `jsonPath` | `org.springframework.test.web.servlet…` |
| `JsonPath.read` (extraire un id) | `com.jayway.jsonpath` |

- Toujours fixer `tasks.api-token` dans `@SpringBootTest(properties = …)` : sinon un `API_TOKEN` présent dans l'environnement change le comportement.
- Tests d'auth : classe dédiée avec `properties = "tasks.api-token=secret-test"` et `.header(HttpHeaders.AUTHORIZATION, "Bearer secret-test")`.
- Extraire un id créé : `String id = JsonPath.read(body, "$.id");`.

## Matrice minimale par endpoint

| Cas | Assertion |
|---|---|
| Nominal | code 2xx + champs clés via `jsonPath` |
| Body / query invalide | 400 + `content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)` |
| UUID inexistant | 404 |
| UUID mal formé | 400 |
| Écriture sans token | 401 (classe de test avec token) |

## Test du service seul

Pas de contexte Spring : `new TaskService(new InMemoryTaskRepository(), Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC))`. Rapide et déterministe sur les dates.

## Test du module tasks-mcp

`MockRestServiceServer` lié au `RestClient.Builder`, sans démarrer l'API :

```java
RestClient.Builder builder = RestClient.builder();
MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
TaskTools tools = new TaskTools(new TasksApiClient(builder, new TasksApiProperties("http://tasks.test", "secret")));

server.expect(requestTo("http://tasks.test/tasks")).andExpect(method(HttpMethod.POST))
      .andExpect(header("Authorization", "Bearer secret"))
      .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(json));
```

Terminer chaque test par `server.verify()`.

## Conventions

- `@DisplayName` en français, à l'indicatif ; nom de méthode court en anglais.
- AssertJ (`assertThat`, `assertThatThrownBy`) plutôt que les assertions JUnit.
- Pas de `Thread.sleep`, pas de dépendance à l'ordre des tests, pas d'assertion sur un UUID ou une date générés.
- Vérifier qu'un nouveau test échoue avant d'implémenter.

## Exécution

```bash
./mvnw -pl tasks-api test
./mvnw -pl tasks-api test -Dtest=TaskControllerTest#update -Dsurefire.failIfNoSpecifiedTests=false
./mvnw -B verify
```

Rapports détaillés : `<module>/target/surefire-reports/`.
