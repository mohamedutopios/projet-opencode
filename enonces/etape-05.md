# Étape 05 — Un agent d'analyse en lecture seule

**Durée indicative** : 25 min

## Contexte

Avant toute modification, l'équipe veut un avis d'architecte : options comparées, impacts, plan en étapes vérifiables. Cet agent raisonne finement mais ne modifie rien.

## Ce que vous devez obtenir

Un agent **principal** `architecte` qui :

1. utilise **Opus 5.5** ;
2. ne modifie aucun fichier, n'accède pas au web ;
3. n'exécute que des commandes de lecture (Git en lecture, utilitaires) ;
4. répond avec : reformulation, au plus deux options avec leur impact sur `README.md` (section « API ») et sur `tasks-mcp`, recommandation en étapes avec leur test, questions ouvertes ;
5. renvoie vers un agent de développement si on lui demande d'appliquer son plan.

`build` et `plan` restent inchangés.

## Marche à suivre

1. Créez `.opencode/agents/architecte.md` : un en-tête YAML, puis le prompt.

   ```markdown
   ---
   description: "…"
   mode: …
   model: …
   permission:
     edit: …
     webfetch: …
     bash:
       "*": deny
       …
   ---

   Tu es l'architecte de ce projet. …
   ```

2. Les règles de l'agent s'ajoutent après les règles globales : vérifiez que `.env` reste refusé.
3. Contrôlez avec `resume_agents.py`, testez avec `Tab` dans le TUI, commitez.

## Comment vérifier

| Vérification | Attendu |
|---|---|
| `resume_agents.py` | `architecte`, `primary`, Opus 5.5 |
| `resume_agents.py architecte edit` | dernière règle : `* deny` |
| `Propose un plan pour que GET /tasks accepte ?status=todo\|in_progress\|done.` | plan au format attendu, impacts sur le contrat et `tasks-mcp` |
| `Applique ce plan.` | refus ; `git status` inchangé |

## Questions à se poser

- Pourquoi un modèle coûteux convient-il ici, et pas pour un agent qui écrit beaucoup de code ?
- Que protège la lecture seule, et que ne protège-t-elle pas ?

## Documentation

- OpenCode — Agents : https://opencode.ai/docs/agents/
- OpenCode — Permissions : https://opencode.ai/docs/permissions/
