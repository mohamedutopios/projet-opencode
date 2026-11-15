# tasks — règles du projet

Projet Maven multi-module, Java 21, Spring Boot 4.1, Spring AI 2.0.

- `tasks-api` : API REST de gestion de tâches (Spring MVC, Bean Validation, ProblemDetail, Actuator). Stockage en mémoire derrière l'interface `TaskRepository`.
- `tasks-mcp` : MCP server stdio (Spring AI) qui expose l'API à OpenCode sous forme d'outils `tasks-api_*`.

## Commandes

Toujours utiliser le wrapper `./mvnw`, jamais `mvn`.

- Build complet et tests (à lancer avant de déclarer une tâche terminée) : `./mvnw -B verify`
- Tests d'un module : `./mvnw -pl tasks-api test`
- Un test précis : `./mvnw -pl tasks-api test -Dtest=TaskControllerTest#update -Dsurefire.failIfNoSpecifiedTests=false`
- Lancer l'API (port 8080, lit `.env` s'il existe) : `./mvnw -pl tasks-api spring-boot:run`. **Ne pas lancer soi-même** : c'est une commande bloquante, demander à l'utilisateur.
- Le MCP server est lancé par OpenCode depuis `tasks-mcp/target/tasks-mcp.jar` : après une modification de `tasks-mcp`, relancer `./mvnw -B verify` puis redémarrer OpenCode.

## Structure

```
tasks-api/src/main/java/com/example/tasks/
  TasksApiApplication.java      point d'entrée
  config/TasksProperties.java   @ConfigurationProperties("tasks")
  config/WebConfig.java         intercepteur, Clock
  security/ApiTokenInterceptor  Bearer token sur POST/PATCH/DELETE
  task/                         Task (record), TaskStatus, *Request (records validés), TaskRepository,
                                InMemoryTaskRepository, TaskService, TaskController
  error/                        TaskNotFoundException, ApiExceptionHandler (ProblemDetail)
tasks-api/src/main/resources/application.yml
tasks-mcp/src/main/java/com/example/tasks/mcp/
  TasksApiClient.java           client RestClient de l'API
  TaskTools.java                outils @McpTool
  TasksApiProperties.java       @ConfigurationProperties("tasks.api")
```

## Règles impératives

- Entrées HTTP : records Java annotés Bean Validation (`@NotBlank`, `@Size`…) et `@Valid` dans le controller.
- Erreurs : lever une exception métier, traduite en `ProblemDetail` par `ApiExceptionHandler`. Jamais de `ResponseEntity.status(4xx)` ad hoc dans un controller.
- Format JSON : succès = la ressource directement ; erreur = `application/problem+json` (`type`, `title`, `status`, `detail`, `instance`, `errors?`).
- Statuts JSON en minuscules (`todo`, `in_progress`, `done`) via `@JsonProperty` sur l'enum.
- Injection par constructeur uniquement (pas de `@Autowired` sur un champ en code de production).
- Configuration : `@ConfigurationProperties` (record) + placeholder `${VARIABLE:défaut}` dans `application.yml`. Pas de `@Value` dispersés.
- Toute nouvelle route ou règle métier est accompagnée de tests.
- Ne jamais lire ni modifier `.env` ; documenter toute nouvelle variable dans `.env.example`.
- Ne pas ajouter de dépendance sans le justifier dans la réponse.
- Dans `tasks-mcp`, rien ne doit écrire sur stdout (pas de `System.out`, pas de log console) : stdout est réservé au protocole MCP.

## Définition de « terminé »

1. `./mvnw -B verify` passe sans erreur.
2. Les nouveaux comportements sont couverts par des tests.
3. `README.md` est à jour si l'API publique change.
