# Étape 02 — Les règles du projet

**Durée indicative** : 25 min

## Contexte

À l'étape 01, l'agent a redécouvert le projet et deviné les attentes de l'équipe. Il doit recevoir ces informations à **chaque** session, quel que soit l'agent.

## Ce que vous devez obtenir

Un fichier `AGENTS.md` à la racine de `projet/` qui donne :

1. les deux modules et leur rôle ;
2. les commandes **exactes** : build complet, tests d'un module, un test précis, lancement de l'API ;
3. la structure des packages ;
4. les règles impératives : validation des entrées, format des erreurs, injection, configuration, `.env`, dépendances, stdout dans `tasks-mcp` ;
5. ce que l'agent ne doit jamais lancer lui-même ;
6. une définition de « terminé » en trois points vérifiables.

## Marche à suivre

1. Rassemblez les informations dans `README.md`, les packages et `ApiExceptionHandler`.
2. Écrivez `AGENTS.md` avec une section par point ci-dessus.
3. Testez chaque commande en la copiant telle quelle (le test précis doit afficher `Tests run:`).
4. Reposez les deux questions de l'étape 01 et comparez le nombre d'appels d'outils.
5. Commitez.

## Contraintes

- 60 lignes au plus.
- Chaque règle se vérifie dans un diff (« Pas de `@Validated` sur un controller », pas « écrire du code propre »).
- Écrit par vous, pas par `/init`.

## Comment vérifier

| Vérification | Attendu |
|---|---|
| `wc -l AGENTS.md` | 60 au plus |
| `Recopie, sans utiliser d'outil, les titres de premier niveau des fichiers d'instructions de ton prompt système.` | le titre de votre `AGENTS.md` |
| Les deux questions de l'étape 01 | 0 à 2 appels d'outils, commande `./mvnw` exacte, « terminé » cité |
| `Lance l'application pour que je puisse la tester.` | l'agent donne la commande sans la lancer |

## Questions à se poser

- Qu'est-ce qui empêche **techniquement** l'agent de lire `.env` ? (Réponse à l'étape 04.)
- Une commande générée par `/init` serait-elle aussi fiable que la vôtre ?

## Documentation

- OpenCode — Rules : https://opencode.ai/docs/rules/
