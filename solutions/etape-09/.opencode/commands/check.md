---
description: Lance le build Maven complet (compilation + tests) et analyse les échecs
agent: developer
---

Voici le résultat du build complet du projet :

!`./mvnw -B verify 2>&1 | grep -E "Tests run:|ERROR|FAIL|BUILD|Reactor Summary|SUCCESS" | tail -80`

Si le build est `BUILD SUCCESS`, réponds simplement « Projet vert » avec le nombre total de tests.
Sinon, pour chaque échec : module, classe et méthode de test, cause probable, fichier et ligne, correction proposée.
N'applique aucune correction sans que je le demande.
