# Étape 04 — Les permissions

**Durée indicative** : 35 min

## Contexte

Les règles de l'étape 02 sont des **consignes** : un modèle peut les ignorer. L'équipe veut des **contraintes techniques**.

## Ce que vous devez obtenir

Des permissions globales dans `opencode.json` :

| Action de l'agent | Comportement attendu |
|---|---|
| Lire un fichier du projet | sans demander |
| Lire `.env` ou une variante (`.env.local`…) | refusé ; `.env.example` reste lisible |
| Modifier un fichier du projet | sans demander |
| Modifier `.env`, `target/`, le wrapper Maven (`mvnw`, `mvnw.cmd`, `.mvn/`) | refusé |
| `git status`, `git diff`, `git log`, `git show` | sans demander |
| Compiler, tester, build complet (avec ou sans `-pl <module>`) | sans demander |
| Lancer l'API, `clean` | demande une validation |
| Déployer ou publier un artefact Maven | refusé |
| `git commit` | demande une validation |
| `git push`, `git reset --hard`, `rm -rf`, `sudo` | refusé |
| Toute autre commande shell | demande une validation |
| Toute commande shell qui mentionne `.env` | refusé |
| Accès web, dossier hors du projet | demande une validation |

## Marche à suivre

1. Ajoutez une clé `permission` avec les actions `read`, `edit`, `bash`, `webfetch`, `external_directory`. Chaque action prend un effet (`allow`, `ask`, `deny`) ou un objet `motif → effet` :

   ```json
   "permission": {
     "read": { "*": "allow", "*.env": "deny", "*.env.*": "deny", "*.env.example": "allow" },
     "edit": { … },
     "bash": { "*": "ask", … , "*.env*": "deny" },
     …
   }
   ```

2. La dernière règle qui correspond l'emporte : règle générale d'abord, exceptions ensuite, `*.env*` en dernier.
3. Autorisez les utilitaires de lecture des pipes (`grep`, `tail`, `head`…).
4. Contrôlez avec `resume_agents.py build shell` et `build edit`, puis testez avec l'agent. Commitez.

## Comment vérifier

`resume_agents.py build shell` : `* ask` en premier, `*.env* deny` en dernier.

Avec `build`, notez **qui** refuse : le modèle ou la permission.

| Prompt | Attendu |
|---|---|
| `Lance les tests du module tasks-api` | exécuté sans demande |
| `Ajoute la ligne « # wrapper » après la première ligne de mvnw` | refus par la permission |
| `Lance ./mvnw dependency:analyze` | demande de validation |
| `Affiche le contenu de .env` / `Exécute cat .env` | refus |
| `Fais un commit de l'état actuel` | demande de validation |
| `Pousse sur origin` | refus |
| `Lance ./mvnw clean verify` | demande de validation (pourquoi ?) |

## Questions à se poser

- Comment est évaluée une commande chaînée (`cd x && commande | tail`) ?
- `grep -r API_TOKEN .` lit-il `.env` ? Quelle limite cela montre-t-il ?
- Répondre « toujours » à une demande de validation : pour combien de temps ?

## Documentation

- OpenCode — Permissions : https://opencode.ai/docs/permissions/
