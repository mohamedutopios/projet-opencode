# Étape 06 — Trois subagents spécialisés, un modèle par rôle

**Durée indicative** : 45 min

## Contexte

Trois tâches reviennent sans cesse : vérifier le contrat de l'API, repérer les comportements non testés, tenir le journal des changements. Chacune a son périmètre et son niveau de raisonnement : ce sont des **subagents**, qui travaillent dans une session séparée.

## Ce que vous devez obtenir

| Agent | Mission | Modèle | Peut modifier | Peut exécuter |
|---|---|---|---|---|
| `api-contract` | comparer la section « API » de `README.md` au code et à `tasks-mcp` ; tableau d'écarts et verdict | Haiku 5.5 | rien | Git et utilitaires de lecture |
| `test-gaps` | lister les comportements non testés, du plus risqué au moins risqué, avec un nom de test proposé, **sans écrire de test** | Sonnet 5.5 | rien | tests Maven, Git, utilitaires de lecture |
| `changelog` | mettre à jour `CHANGELOG.md` (Keep a Changelog) à partir de l'historique Git | Haiku 5.5 | `CHANGELOG.md` uniquement | Git et utilitaires de lecture |

Chaque agent a un format de réponse fixe.

- **Délégation automatique** : `build` leur confie de lui-même les demandes de leur rôle, grâce à leur seule description, sans que la demande les nomme ni que le prompt de `build` change.
- **Délégation restreinte** : `architecte` peut déléguer à `api-contract`, et à aucun autre.

## Marche à suivre

1. Créez un fichier par agent dans `.opencode/agents/`, avec `mode: subagent`.
2. Écrivez la description avec les **mots de l'utilisateur** (« contrat », « non testé », « journal des changements »…) : c'est elle qui déclenche la délégation.
3. Dans l'en-tête d'`architecte`, ajoutez une clé `task` qui refuse tout sauf `api-contract`.
4. Contrôlez avec `resume_agents.py`, commitez, puis testez. Si une délégation ne se déclenche pas, réécrivez la description et rejouez.

## Contraintes

- Aucun accès web. `.env` reste refusé, y compris par le shell (`*.env*: deny` en dernier).
- Les commandes avec pipe (`… | tail`) fonctionnent.

## Comment vérifier

| Vérification | Attendu |
|---|---|
| `resume_agents.py` | les trois agents, `subagent`, chacun sur son modèle |
| `resume_agents.py architecte subagent` | `* deny`, puis `api-contract allow` |
| `@api-contract vérifie la section API du README` | session enfant sur Haiku ; tableau d'écarts ; rien de modifié |
| `@test-gaps liste les comportements non testés de tasks-api/src/main/java/com/example/tasks/task` | tableau trié ; `Tests run:` cité ; aucun test écrit |
| `@test-gaps écris le premier test manquant` / `@changelog corrige aussi le README` | refus |

Avec `build`, sans nommer d'agent :

| Demande | Attendu |
|---|---|
| `La section API du README est-elle à jour par rapport au code ? Ne modifie rien.` | délégation à `api-contract` |
| `Quels comportements du package task ne sont pas testés ? Ne modifie rien.` | délégation à `test-gaps` |
| `Mets à jour le journal des changements.` | délégation à `changelog` ; seul `CHANGELOG.md` change |
| `Explique le rôle de TaskService en cinq lignes.` | aucune délégation |

Avec `architecte` : `Délègue à test-gaps` → il ne le trouve pas ; `Le contrat du README est-il à jour ?` → délégation à `api-contract`.

## Questions à se poser

- Un subagent voit-il l'historique de la session qui l'appelle ? Qu'est-ce que cela impose à la demande ?
- La délégation automatique est-elle garantie ? Quand vaut-il mieux l'imposer ?
- Un subagent interdit est-il refusé à l'appel, ou invisible ?
- Coût d'une session enfant sur Haiku, comparé à Opus (`opencode stats --cost`) ?

## Documentation

- OpenCode — Agents : https://opencode.ai/docs/agents/
- Keep a Changelog : https://keepachangelog.com/fr/1.1.0/
