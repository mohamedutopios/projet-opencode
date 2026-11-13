# Étape 03 — La configuration partagée de l'équipe

**Durée indicative** : 20 min

## Contexte

À l'étape 01, le modèle dépendait du poste. L'équipe veut les mêmes réglages pour tous, versionnés, et des conventions détaillées dans un fichier séparé des règles.

## Ce que vous devez obtenir

Un `opencode.json` à la racine de `projet/` qui :

1. fixe le modèle principal sur Sonnet 5.5 et le modèle des tâches annexes sur Haiku 5.5 ;
2. désactive le partage des sessions et la mise à jour automatique ;
3. sélectionne l'agent `build` au démarrage ;
4. charge `docs/conventions.md` en plus de `AGENTS.md`.

Et `docs/conventions.md` : nommage Java, tableau des codes HTTP du projet, conventions de tests, messages de commit (Conventional Commits, en français, 72 caractères au plus).

## Marche à suivre

1. Créez `opencode.json` avec les clés suivantes (valeurs dans la page Config) :

   ```json
   {
     "$schema": "https://opencode.ai/config.json",
     "model": "…",
     "small_model": "…",
     "share": "…",
     "autoupdate": …,
     "default_agent": "…",
     "instructions": ["…"]
   }
   ```

2. Écrivez `docs/conventions.md` à partir du code existant.
3. `opencode reload` puis `opencode debug config --standalone`.
4. Faites une faute dans un nom de clé, puis un chemin faux dans `instructions`, et observez `debug config`. Corrigez, commitez.

## Contraintes

- Aucun secret. Le fichier valide le schéma officiel.

## Comment vérifier

| Vérification | Attendu |
|---|---|
| `opencode debug config --standalone` | vos modèles, `instructions`, l'agent par défaut |
| Question des titres (étape 02) | `AGENTS.md` **et** `docs/conventions.md` |
| `Quel format de message de commit dois-je utiliser ?` | réponse tirée de `docs/conventions.md` |
| Clé mal orthographiée | une erreur qui la désigne |
| Chemin d'instructions faux | y a-t-il une erreur ? |

## Questions à se poser

- Configuration du projet et configuration personnelle : laquelle l'emporte ?
- Imposer le modèle à toute l'équipe : avantages, inconvénients ?

## Documentation

- OpenCode — Config : https://opencode.ai/docs/config/
- OpenCode — Models : https://opencode.ai/docs/models/
