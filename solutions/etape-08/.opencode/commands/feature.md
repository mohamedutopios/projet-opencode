---
description: Implémente une fonctionnalité de bout en bout (DTO, service, controller, tests, doc)
agent: developer
---

Implémente la fonctionnalité suivante : $ARGUMENTS

Déroulé obligatoire :
1. Résume en 3 à 5 lignes ce que tu vas modifier (modules, classes et comportement).
2. Découpe la demande en comportements, écris d'abord les tests, lance `./mvnw -pl <module> test` et montre qu'ils échouent pour la bonne raison.
3. Implémente le code minimal dans `src/main` en respectant `AGENTS.md`.
4. Lance `./mvnw -B verify` jusqu'à ce que tout passe.
5. Mets à jour `README.md` si l'API publique change.
6. Termine par : comportements couverts, fichiers modifiés, tests ajoutés, résultat de `./mvnw -B verify`.

Ne commite pas. Ne lance pas l'application.
