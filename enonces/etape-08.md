# Étape 08 — Les commandes de l'équipe

**Durée indicative** : 40 min

## Contexte

Certaines demandes reviennent chaque jour. L'équipe veut les lancer en une commande, exécutées par le bon agent, avec les données utiles **injectées avant l'envoi au modèle**.

## Fonctionnalité de l'étape

| | |
|---|---|
| Demande à `tdd` | `GET /tasks accepte un paramètre facultatif ?status=todo\|in_progress\|done pour filtrer les tâches. Ne mets pas à jour le README.` |
| Branche | `feat/08-filtre-statut`, à partir de `main` |
| Ce qu'on en fait | abandonnée : refaite à l'étape 09 avec les skills |

## Ce que vous devez obtenir

| Commande | Exécutée par | Ce qu'elle fait |
|---|---|---|
| `/verify` | `tdd` | injecte le résultat du build complet (lignes utiles seulement) et demande une synthèse sans rien corriger |
| `/contract [point d'attention]` | `api-contract`, session séparée | injecte les routes du code et les changements en cours, demande la comparaison avec le contrat |
| `/gaps [périmètre]` | `test-gaps`, session séparée | périmètre en argument (par défaut `tasks-api/src/main`), liste des tests injectée |
| `/release-notes [contexte]` | `changelog`, session séparée | injecte les derniers commits, demande la mise à jour de `CHANGELOG.md` |

## Marche à suivre

1. Créez un fichier par commande dans `.opencode/commands/` :

   ```markdown
   ---
   description: …
   agent: …
   subtask: …
   ---

   Prompt. Sortie injectée : !`commande shell`
   Argument de l'utilisateur : $ARGUMENTS
   ```

2. Testez `/verify` et `/gaps` sur `main`, commitez.
3. Sur `feat/08-filtre-statut`, faites la fonctionnalité, commitez, puis lancez `/contract` et `/release-notes`.
4. Avant de revenir sur `main`, regardez ce que l'agent a produit sans skill : imports des tests, `@Validated` sur le controller, réponse de l'API à `?status=done` et `?status=archived`.

## Contraintes

- Les commandes injectées ne font que lire (ou lancer le build). `$ARGUMENTS` n'est jamais passé à une commande injectée.
- La sortie du build injectée tient en moins de 80 lignes.

## Comment vérifier

| Vérification | Attendu |
|---|---|
| `/` dans le TUI | vos 4 commandes avec leur description |
| `/verify` | « Build vert » et le nombre de tests par module, sans que l'agent lance le build |
| `/gaps tasks-api/src/main/java/com/example/tasks/task` | session enfant `test-gaps` |
| `/contract` sur la branche | l'écart sur `GET /tasks` est signalé |
| `/release-notes` sur la branche | une ligne pour le filtre dans `CHANGELOG.md` |

Si `/verify` ne reçoit aucun résultat de build : dans quel environnement la commande injectée s'exécute-t-elle ? (Étape 00.)

## Questions à se poser

- Une commande injectée est-elle soumise aux permissions de l'agent ?
- Une commande du projet peut-elle remplacer une commande intégrée du même nom ?
- `opencode run "/verify"` exécute-t-il la commande ?

## Documentation

- OpenCode — Commands : https://opencode.ai/docs/commands/
