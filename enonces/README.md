# TP — Construire la configuration OpenCode d'un projet Spring Boot 4

Vous partez de `projet/` (API REST `tasks-api` + MCP server `tasks-mcp`), **sans aucune configuration OpenCode**, et vous construisez, étape par étape, l'outillage d'une équipe : règles, configuration, permissions, agents, commandes, skills, MCP.

## Comment lire un énoncé

- **Ce que vous devez obtenir** : le résultat attendu.
- **Marche à suivre** : les opérations, dans l'ordre. Les `…` des squelettes sont à compléter avec la documentation (liens en bas de chaque énoncé).
- **Comment vérifier** : une étape est terminée quand toutes les vérifications passent.
- **Questions à se poser** : à discuter avec le formateur, rien à rendre.

## Règles du TP

- Les commandes se lancent **depuis `projet/`** (sous Windows : Git Bash).
- Les étapes sont cumulatives. Chacune se termine par un commit sur `main` : `chore(ia): étape XX`.
- La configuration d'une étape est commitée sur `main` **avant** de créer une branche de fonctionnalité.
- Après toute modification de configuration : quittez le TUI, `opencode reload`, relancez `opencode`.
- Bloqué plus de 15 minutes : demandez au formateur l'état de référence de l'étape précédente.

## Le code du projet

Vous n'écrivez **ni le code de production ni les tests à la main** : vous les faites écrire par vos agents, puis vous les relisez et les commitez. Chaque fonctionnalité se fait sur une branche `feat/<étape>-<sujet>` créée à partir de `main`. Rien n'est fusionné sans build vert ni relecture du diff.

| Étape | Fonctionnalité demandée à l'agent | Branche | Ce qu'on en fait |
|---|---|---|---|
| 07 | une tâche terminée ne peut pas revenir à `todo` | `feat/07-build` et `feat/07-tdd` | `tdd` fusionnée, `build` abandonnée |
| 08 | filtre `GET /tasks?status=` | `feat/08-filtre-statut` | abandonnée : refaite à l'étape 09 |
| 09 | le même filtre, avec les skills | `feat/09-filtre-statut` | fusionnée |
| 10 | titre en doublon refusé | `feat/10-titre-unique` | fusionnée |
| 10 | extraction d'une méthode dans `ApiTokenInterceptor` | `feat/10-refactor-token` | fusionnée |
| 10 | pagination de `GET /tasks` | `feat/10-pagination` | conservée : terminée à l'étape 12 |
| 12 | adaptation de `tasks-mcp` à la pagination | `feat/10-pagination` (suite) | fusionnée |
| 14 | date d'échéance et tâches en retard | `feat/14-echeance` | fusionnée |

## Version et outils

- **OpenCode 2.x**. Vérifications sans appel au modèle : `opencode debug config --standalone`, `opencode debug agents --standalone`, `opencode mcp list`.
- `outils/resume_agents.py` résume les agents et leurs règles de permission, dans l'ordre d'évaluation (la **dernière** règle qui correspond l'emporte) :

```bash
opencode debug agents --standalone | python3 ../outils/resume_agents.py [agent] [action]
```

- Écrivez la configuration avec les clés de la documentation (`bash`, `task`…) : `debug agents` les affiche sous leur nom OpenCode 2 (`shell`, `subagent`).

## Les étapes

| Étape | Sujet | Durée indicative |
|---|---|---|
| [00](etape-00.md) | Poste prêt : OpenCode 2.x, provider, Java visible par OpenCode | 20 min |
| [01](etape-01.md) | Ligne de base : le projet, et OpenCode sans configuration | 20 min |
| [02](etape-02.md) | Les règles du projet | 25 min |
| [03](etape-03.md) | La configuration partagée de l'équipe | 20 min |
| [04](etape-04.md) | Les permissions | 35 min |
| [05](etape-05.md) | Un agent d'analyse en lecture seule | 25 min |
| [06](etape-06.md) | Trois subagents spécialisés, un modèle par rôle | 45 min |
| [07](etape-07.md) | Un agent de développement en TDD, avec point d'arrêt | 35 min |
| [08](etape-08.md) | Les commandes de l'équipe | 40 min |
| [09](etape-09.md) | Les skills du projet | 45 min |
| [10](etape-10.md) | Les skills de méthode | 40 min |
| [11](etape-11.md) | Sécurité, notes de version et permissions de skills | 40 min |
| [12](etape-12.md) | Le MCP server du projet | 45 min |
| [13](etape-13.md) | Un MCP server distant | 15 min |
| [14](etape-14.md) | Parcours complet d'une fonctionnalité | 45 min |
| [15](etape-15.md) | Mode non interactif | 20 min |
| [16](etape-16.md) | Réutiliser votre configuration | 20 min |

## Ce que vous rendez

Le dépôt `projet/` avec la configuration de l'étape 16, un commit `chore(ia): étape XX` par étape, et les branches de fonctionnalité dans l'état demandé.
