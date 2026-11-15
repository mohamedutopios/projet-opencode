# Conventions de code

## Java et Spring

- Java 21 : `record` pour les DTO et les propriétés de configuration, `var` autorisé pour les variables locales dont le type est évident, pattern matching `instanceof`.
- Packages par fonctionnalité (`task`, `security`, `error`, `config`), pas par couche technique globale.
- Classes : `PascalCase` ; méthodes et champs : `camelCase` ; constantes : `UPPER_SNAKE_CASE`.
- Suffixes : `*Controller`, `*Service`, `*Repository`, `*Request` (entrée HTTP), `*Properties`, `*Exception`.
- Visibilité minimale : package-private pour les beans de configuration et les handlers internes.
- Injection par constructeur, champs `final`.

## HTTP

| Cas | Code |
|---|---|
| Lecture OK | 200 |
| Création | 201 + header `Location` |
| Suppression | 204 sans body |
| Validation échouée / JSON invalide / paramètre mal formé | 400 |
| Token absent ou invalide | 401 |
| Ressource absente | 404 |
| Erreur imprévue | 500 (message générique, stack trace en log uniquement) |

Routes : ressources au pluriel (`/tasks`), identifiant UUID en path variable (`/tasks/{id}`).

## Tests

- JUnit 5 + AssertJ + MockMvc (`jsonPath`) ; `MockRestServiceServer` pour les clients HTTP.
- `@DisplayName` en français, à l'indicatif : « refuse une tâche sans titre ».
- Un test = un comportement ; `@BeforeEach` remet l'état à zéro (`repository.deleteAll()`).
- Tester le cas nominal **et** au moins un cas d'erreur par endpoint.
- Nom de classe : `<ClasseTestée>Test`.

## Commits

Format Conventional Commits : `feat:`, `fix:`, `test:`, `refactor:`, `docs:`, `chore:`.
Message au présent, en français, ≤ 72 caractères sur la première ligne.
