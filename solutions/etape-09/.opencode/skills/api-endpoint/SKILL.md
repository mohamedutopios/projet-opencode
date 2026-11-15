---
name: api-endpoint
description: Procédure pour ajouter ou modifier un endpoint REST dans tasks-api (record de requête validé, service, controller Spring MVC, paramètres de query, ProblemDetail, tests MockMvc). À charger avant toute modification d'un controller ou d'une route HTTP.
---

# Ajouter ou modifier un endpoint dans tasks-api

## 1. Entrée : record validé (`task/<Action>TaskRequest.java`)

```java
public record CreateTaskRequest(
        @NotBlank(message = "title est obligatoire") @Size(max = 120) String title,
        @Size(max = 1000) String description,
        TaskStatus status) {
}
```

- Une contrainte Bean Validation par règle ; message en français si le message par défaut n'est pas clair.
- Règle portant sur plusieurs champs : méthode `@AssertTrue public boolean isXxx()` annotée `@JsonIgnore`.
- Le controller annote le paramètre `@Valid @RequestBody` : une violation déclenche `MethodArgumentNotValidException`, traduite en 400 avec la liste `errors` par `ApiExceptionHandler`.

## 2. Paramètres de query et de path

Validation sur paramètres simples : annoter directement les paramètres. Spring MVC 7 les valide nativement.

```java
@GetMapping
public List<Task> list(
        @RequestParam(required = false) TaskStatus status,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
        @RequestParam(defaultValue = "0") @Min(0) int offset) { ... }
```

- Une violation lève `HandlerMethodValidationException`, déjà traduite en 400 `ProblemDetail` par `ResponseEntityExceptionHandler`.
- **Ne pas ajouter `@Validated` sur la classe du controller** : cela active la validation par proxy AOP, qui lève `ConstraintViolationException` à la place, interceptée par le handler générique → 500.
- **Enum en minuscules dans la query** (`?status=done`) : Spring convertit par défaut sur le **nom Java** (`DONE`). Ajouter un converter dans `WebConfig` :

```java
@Override
public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(String.class, TaskStatus.class,
            value -> TaskStatus.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT)));
}
```

  Une valeur inconnue lève `MethodArgumentTypeMismatchException`, traduite en 400 par `ApiExceptionHandler`.
- Path variable `UUID` : un id mal formé donne aussi `MethodArgumentTypeMismatchException` → 400.

## 3. Service (`TaskService`)

- Toute la logique métier : normalisation (`strip()`), valeurs par défaut, règles de gestion.
- Ressource absente : `throw new TaskNotFoundException(id)` → 404.
- Nouvelle erreur métier : créer une exception dans `error/` et son `@ExceptionHandler` dans `ApiExceptionHandler`, qui retourne `ProblemDetail.forStatusAndDetail(...)`.

## 4. Repository

- Ajouter la méthode à l'interface `TaskRepository` **et** à `InMemoryTaskRepository`.
- Le repository ne connaît ni HTTP ni les exceptions métier : il retourne `Optional`, `List` ou `boolean`.

## 5. Controller (`TaskController`)

- Retourner la ressource directement (`Task`, `List<Task>`), `ResponseEntity.created(uri).body(...)` pour un 201, `ResponseEntity.noContent().build()` pour un 204.
- Aucune logique métier ni `try/catch` dans le controller.
- Les méthodes POST/PATCH/PUT/DELETE sous `/tasks` sont automatiquement protégées par `ApiTokenInterceptor`. Un nouveau préfixe de route doit être ajouté dans `WebConfig.addInterceptors`.

## 6. Réponse paginée (si besoin)

```java
public record Page<T>(List<T> items, Meta meta) {
    public record Meta(long total, int limit, int offset) { }
}
```

## 7. Tests

Charger le skill `write-tests`. Au minimum : nominal, 400 (body/query invalide), 404 (id inexistant), 401 (écriture sans token).

## 8. Vérification

`./mvnw -B verify`, puis mettre à jour la section API de `README.md`.
