# Étape 01 — Ligne de base

**Durée indicative** : 20 min

## Contexte

Avant d'outiller un agent, on mesure ce qu'il fait **sans aide**. Les étapes suivantes reposeront les mêmes questions.

## Ce que vous devez obtenir

1. Le projet sous Git, un premier commit avec le code fourni seulement.
2. Le build complet vert.
3. L'API lancée à la main, et trois appels : santé, écriture sans token refusée, écriture avec token acceptée.
4. Deux sessions OpenCode **sans configuration de projet**, sur Sonnet 5.5, gardées pour comparaison.

## Marche à suivre

1. `git init`, vérifiez avec `git status` que ni `.env` ni `target/` n'apparaissent, puis premier commit.
2. `./mvnw -B verify`.
3. `cp .env.example .env`, puis dans un second terminal `./mvnw -pl tasks-api spring-boot:run`. Faites les trois appels avec `curl` (exemples dans `projet/README.md`, token `change-me-local-only`), puis arrêtez l'API.
4. Lancez `opencode`, choisissez Sonnet 5.5 avec `/models`, et posez chaque question dans sa propre session (`/new`) :
   - `Comment lance-t-on le build complet et les tests de ce projet, et quelle est la définition de « terminé » ?`
   - `Comment les erreurs HTTP doivent-elles être renvoyées dans ce projet ?`

   Comptez les appels d'outils avant chaque réponse.

## Contraintes

- `.env` n'est jamais commité.
- C'est vous qui lancez l'API, pas l'agent.

## Comment vérifier

| Vérification | Attendu |
|---|---|
| `git log --oneline` | un commit |
| `./mvnw -B verify` | `BUILD SUCCESS`, 17 tests dans `tasks-api`, 8 dans `tasks-mcp` |
| Les trois `curl` | `UP`, `401`, tâche créée |
| `opencode debug config --standalone` | aucune source propre au projet |
| En-tête des réponses | Sonnet 5.5 |
| `opencode session list` | les deux sessions de référence |

## Questions à se poser

- Sans configuration, quel modèle OpenCode a-t-il choisi ? Pourquoi est-ce un problème pour une équipe ?
- La réponse était-elle juste ? Combien de lectures de fichiers a-t-elle coûté ?

## Documentation

- OpenCode — TUI : https://opencode.ai/docs/tui/
- `projet/README.md`
