# Étape 07 — Un agent de développement en TDD, avec point d'arrêt

**Durée indicative** : 35 min

## Contexte

`build` n'impose aucune méthode. L'équipe veut un agent qui travaille en TDD strict et **s'arrête après le premier test rouge**, pour qu'un humain valide le test avant le code de production.

## Fonctionnalité de l'étape

| | |
|---|---|
| Demande | `Une tâche terminée (done) ne doit pas pouvoir revenir au statut todo.` |
| Branches | `feat/07-build` (avec `build`) et `feat/07-tdd` (avec `tdd`), toutes deux à partir de `main` |
| Ce qu'on en fait | `feat/07-tdd` fusionnée ; `feat/07-build` abandonnée |

## Ce que vous devez obtenir

Un agent principal `tdd`, sur Sonnet 5.5, **agent par défaut**, qui pour toute demande qui modifie du code :

1. liste les comportements à couvrir, avec le code HTTP attendu ;
2. écrit un seul test rouge, le lance, explique l'échec ;
3. **s'arrête** et attend l'accord ;
4. écrit ensuite le code minimal, puis les comportements suivants un par un ;
5. lance le build complet et cite les lignes `Tests run:` ;
6. fait contrôler la couverture et, si l'API a changé, le contrat, **par délégation** ;
7. termine par un compte rendu et un message de commit proposé.

Il ne lance pas l'API, ne commite pas, ne lit pas `.env`. Son prompt **ne nomme pas** les subagents. Il peut déléguer à `test-gaps` et `api-contract`, pas à `changelog`.

## Marche à suivre

1. Créez `.opencode/agents/tdd.md` (même structure qu'`architecte`), avec une clé `task` qui n'autorise que les deux subagents.
2. Empêchez-le de modifier `CHANGELOG.md` et faites-le renvoyer vers la commande de l'équipe (étape 08).
3. Passez `default_agent` à `tdd`. Commitez sur `main`.
4. Faites la fonctionnalité avec `build` sur `feat/07-build`, puis avec `tdd` sur `feat/07-tdd`. Commitez chaque branche après relecture.
5. Fusionnez `feat/07-tdd` dans `main`.

## Comment vérifier

| Vérification | Attendu |
|---|---|
| `opencode debug config --standalone` | agent par défaut `tdd` |
| `resume_agents.py tdd subagent` | `* deny`, puis `test-gaps` et `api-contract` |
| `test-gaps` dans le corps du prompt de `tdd` | absent |
| À `tdd` : `Mets à jour le journal des changements.` | pas de délégation à `changelog`, renvoi vers la commande |
| Au point d'arrêt, `git status` | seul un fichier de test modifié |
| Avec `tdd` | liste, rouge avant le code, arrêt, 409 en `ProblemDetail`, build vert, sessions enfants visibles |
| Avec `build` | comparez sur les mêmes critères |
| `./mvnw -B verify` sur `main` après fusion | `BUILD SUCCESS` |

## Questions à se poser

- Un prompt impose une méthode : la garantit-il ? Comment rendre les écarts visibles ?
- En mode non interactif, que doit faire l'agent au point d'arrêt ?

## Documentation

- OpenCode — Agents : https://opencode.ai/docs/agents/
- OpenCode — Config (agent par défaut) : https://opencode.ai/docs/config/
