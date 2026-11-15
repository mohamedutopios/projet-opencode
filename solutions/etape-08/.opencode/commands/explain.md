---
description: Explique un fichier du projet (usage /explain tasks-api/src/main/java/com/example/tasks/task/TaskService.java)
agent: plan
---

Explique le fichier @$1 à un développeur qui découvre le projet :
- rôle du fichier dans l'architecture (voir `AGENTS.md`) ;
- beans Spring impliqués, ce qui les injecte et ce qu'ils injectent ;
- flux d'exécution principal (requête HTTP → controller → service → repository, ou appel d'outil MCP → client HTTP) ;
- points d'extension et pièges éventuels.

Reste factuel, cite les numéros de ligne, ne propose pas de modification.
