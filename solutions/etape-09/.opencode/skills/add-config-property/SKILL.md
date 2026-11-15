---
name: add-config-property
description: Procédure pour ajouter une propriété de configuration Spring Boot (record @ConfigurationProperties, application.yml avec variable d'environnement, .env.example, README, tests) sans jamais lire ni écrire .env. À charger dès qu'une fonctionnalité a besoin d'un paramètre configurable.
---

# Ajouter une propriété de configuration

## Règle absolue

Ne jamais lire, afficher ni modifier `.env` : il contient les secrets locaux du développeur. La source de vérité documentaire est `.env.example`.

## 1. Record de propriétés

Étendre le record existant (`TasksProperties`, préfixe `tasks`) ou en créer un par domaine :

```java
@ConfigurationProperties(prefix = "tasks")
@Validated
public record TasksProperties(
        String apiToken,
        @Min(1) @DefaultValue("1000") int maxTasks) {
}
```

- `@Validated` + contraintes Bean Validation : une valeur invalide fait échouer le démarrage (échec rapide, message explicite).
- `@DefaultValue` (package `org.springframework.boot.context.properties.bind`) pour une valeur par défaut côté Java.
- Les records sont détectés grâce à `@ConfigurationPropertiesScan` sur la classe principale : aucun `@EnableConfigurationProperties` à ajouter.
- Injection par constructeur là où la valeur est utilisée. Pas de `@Value`.

## 2. `application.yml`

Nom en kebab-case, valeur reliée à une variable d'environnement avec un défaut :

```yaml
tasks:
  api-token: ${API_TOKEN:}
  max-tasks: ${MAX_TASKS:1000}
```

Le fichier `.env` de la racine est importé par `spring.config.import: optional:file:.env[.properties]` : la variable y est lue automatiquement, sans code.

## 3. `.env.example`

Commentaire d'une ligne et valeur **factice** :

```bash
# Nombre maximum de tâches conservées en mémoire.
MAX_TASKS=1000
```

## 4. `README.md`

Ajouter une ligne au tableau « Configuration » : variable, propriété Spring, défaut, rôle.

## 5. Tests

- Valeur prise en compte : `@SpringBootTest(properties = "tasks.max-tasks=2")` et un test du comportement.
- Valeur invalide : `ApplicationContextRunner` (package `org.springframework.boot.test.context.runner`) qui vérifie `context.hasFailed()`, ou un test du record seul.

## 6. Vérification

`./mvnw -B verify`.
