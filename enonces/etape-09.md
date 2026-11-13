# Étape 09 — Les skills du projet

**Durée indicative** : 45 min

## Contexte

À l'étape 08, l'agent a sans doute commis des erreurs : imports Spring Boot 3, `@Validated` sur le controller, statut en minuscules refusé. Ces savoirs sont trop détaillés pour les règles : ce sont des **skills**, chargés seulement quand ils servent.

## Fonctionnalité de l'étape

| | |
|---|---|
| Demande à `tdd` | le filtre `?status=` de l'étape 08, cette fois avec mise à jour du contrat |
| Branche | `feat/09-filtre-statut`, à partir de `main` |
| Ce qu'on en fait | fusionnée |

## Ce que vous devez obtenir

| Skill | Ce qu'il apprend à l'agent |
|---|---|
| `rest-tasks` | faire évoluer une route : record validé, paramètres bornés, enum en minuscules dans la query, service, repository, controller sans logique, protection par le token, contrat dans `README.md` |
| `sb4-tests` | tests Spring Boot 4 : packages exacts, propriété du token dans les tests, matrice de cas par endpoint, service testé sans Spring, tests de `tasks-mcp` |
| `problem-detail` | erreurs HTTP : exceptions métier, handler, `type` en URN, choix du code (400, 401, 404, 409, 422, 500), ce qu'on ne renvoie ni ne journalise jamais |

`tdd` charge les skills utiles, `test-gaps` charge `sb4-tests`, `AGENTS.md` liste les skills.

## Marche à suivre

1. Créez `.opencode/skills/<nom>/SKILL.md`, avec un en-tête `name` (identique au dossier) et `description` (**quoi** et **quand**).
2. Partez du code existant : imports de `TaskControllerTest`, `ApiExceptionHandler`, `WebConfig`.
3. Vérifiez que chaque exemple compile en le copiant dans un fichier de test temporaire.
4. Branchez les skills dans les prompts de `tdd` et `test-gaps` et dans `AGENTS.md`. Commitez.
5. Faites la fonctionnalité sur `feat/09-filtre-statut`, comparez avec `feat/08-filtre-statut`, fusionnez.

## Contraintes

- 100 lignes au plus par skill, avec au moins un exemple qui compile.
- Contenu vérifié dans la documentation de Spring Boot 4 et Spring Framework 7, pas dans la mémoire du modèle.

## Comment vérifier

| Vérification | Attendu |
|---|---|
| Sur Haiku : `Sans charger aucun skill ni utiliser d'outil : liste les skills que ton outil skill propose.` | vos 3 skills et les skills intégrés |
| Fonctionnalité | `rest-tasks` et `sb4-tests` chargés ; build vert du premier coup |
| `GET /tasks?status=done` | 200, tâches terminées seulement |
| `GET /tasks?status=archived` | 400 en `ProblemDetail` |
| `TaskController` | sans `@Validated` ; contrat à jour dans le README |
| Sur une branche jetable : un paramètre borné et son test de 400, puis `@Validated` ajouté sur le controller | le test échoue : quel code obtenez-vous ? |

## Questions à se poser

- Qu'est-ce qui fait qu'un skill est chargé ou non ? Comment garantir le chargement ?
- Qu'est-ce qui distingue une règle d'un skill ?

## Documentation

- OpenCode — Agent Skills : https://opencode.ai/docs/skills/
- Agent Skills — Spécification : https://agentskills.io/specification
- Spring Boot 4.0 Migration Guide : https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
- Spring Framework — Validation : https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html
- Spring Framework — Error Responses : https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html
