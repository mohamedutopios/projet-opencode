# tasks — API REST Spring Boot + MCP server

Projet de départ du TP OpenCode : les énoncés sont dans `../enonces/`.

| Module | Rôle |
|---|---|
| `tasks-api` | API REST de gestion de tâches (Spring Boot 4.1, Spring MVC, Bean Validation, ProblemDetail, Actuator) |
| `tasks-mcp` | MCP server stdio (Spring AI 2.0) qui expose l'API à un agent IA sous forme d'outils |

Prérequis : JDK 21. Maven n'est pas nécessaire : le wrapper `./mvnw` le télécharge.

## Démarrage

```bash
./mvnw -B verify
cp .env.example .env
./mvnw -pl tasks-api spring-boot:run
```

Ou, après le build :

```bash
java -jar tasks-api/target/tasks-api.jar
```

## Commandes Maven

| Commande | Rôle |
|---|---|
| `./mvnw -B verify` | Compilation, tests et jars des deux modules |
| `./mvnw -pl tasks-api test` | Tests du module API |
| `./mvnw -pl tasks-api test -Dtest=TaskControllerTest#update -Dsurefire.failIfNoSpecifiedTests=false` | Un test précis |
| `./mvnw -pl tasks-api spring-boot:run` | Lancer l'API (lit `.env` à la racine) |

## Configuration

| Variable (`.env` ou environnement) | Propriété Spring | Défaut | Rôle |
|---|---|---|---|
| `PORT` | `server.port` | `8080` | Port d'écoute de l'API |
| `API_TOKEN` | `tasks.api-token` | *(vide)* | Si défini, exige `Authorization: Bearer <token>` sur POST/PATCH/DELETE |
| `TASKS_API_URL` | `tasks.api.url` (tasks-mcp) | `http://localhost:8080` | API ciblée par le MCP server |
| `TASKS_API_TOKEN` | `tasks.api.token` (tasks-mcp) | valeur de `API_TOKEN` | Token envoyé par le MCP server sur les écritures |

## API

| Méthode | Route | Réponse |
|---|---|---|
| GET | `/actuator/health` | `200 { "status": "UP" }` |
| GET | `/tasks` | `200 Task[]` |
| GET | `/tasks/{id}` | `200 Task` · `400` (UUID mal formé) · `404` |
| POST | `/tasks` | `201 Task` + `Location` · `400` · `401` |
| PATCH | `/tasks/{id}` | `200 Task` · `400` · `401` · `404` |
| DELETE | `/tasks/{id}` | `204` · `401` · `404` |

`Task` : `{ id (UUID), title, description?, status: "todo" | "in_progress" | "done", createdAt, updatedAt }`

Erreurs au format `application/problem+json` (RFC 9457) :

```json
{ "type": "about:blank", "title": "Bad Request", "status": 400, "detail": "Requête invalide",
  "instance": "/tasks", "errors": [ { "field": "title", "message": "title est obligatoire" } ] }
```

```bash
curl -s localhost:8080/actuator/health
curl -s -X POST localhost:8080/tasks \
  -H 'content-type: application/json' \
  -H 'authorization: Bearer change-me-local-only' \
  -d '{"title":"Préparer la démo OpenCode"}'
curl -s localhost:8080/tasks
```

## MCP server

`tasks-mcp` expose six outils : `health`, `list_tasks`, `get_task`, `create_task`, `update_task`, `delete_task`. Le serveur se lance avec `java -jar tasks-mcp/target/tasks-mcp.jar` (transport stdio) et appelle l'API : elle doit tourner pour que les outils répondent.

Test manuel avec l'inspecteur MCP (Node.js requis) :

```bash
npx @modelcontextprotocol/inspector java -jar tasks-mcp/target/tasks-mcp.jar
```
