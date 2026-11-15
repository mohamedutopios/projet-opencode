---
name: debug-failing-tests
description: Méthode pour diagnostiquer un build Maven ou un test Spring Boot qui échoue (isoler, lire surefire-reports, causes fréquentes Spring, hypothèse puis preuve). À charger quand ./mvnw verify ou ./mvnw test est rouge.
---

# Diagnostiquer un échec Maven / Spring

## 1. Isoler

```bash
./mvnw -pl <module> test -Dtest=<Classe>#<méthode> -Dsurefire.failIfNoSpecifiedTests=false
```

Erreur de compilation : lire **la première** ligne `[ERROR] …java:[ligne,colonne]`, les suivantes en découlent souvent.

## 2. Lire l'erreur avant le code

- Résumé : `<module>/target/surefire-reports/<Classe>.txt`
- Détail (stack + logs) : `<module>/target/surefire-reports/<Classe>-output.txt` s'il existe, sinon relancer avec `-Dsurefire.useFile=false`.
- Relever : valeur attendue vs obtenue (`Status expected:<400> but was:<500>`), et la **première** ligne de stack appartenant à `com.example.tasks`.
- Échec de démarrage du contexte : chercher la **dernière** cause `Caused by:` de la stack, c'est la vraie.

## 3. Causes fréquentes dans ce projet

| Symptôme | Cause habituelle |
|---|---|
| 500 au lieu de 400 | `@Valid` absent sur `@RequestBody`, ou exception métier sans `@ExceptionHandler` |
| 500 sur un `@RequestParam` hors bornes | `@Validated` ajouté sur la classe du controller : `ConstraintViolationException` au lieu de `HandlerMethodValidationException` ; le retirer |
| 400 au lieu du 404 attendu | id de test qui n'est pas un UUID valide |
| `jsonPath` ne trouve rien sur une erreur | réponse 401 de `ApiTokenInterceptor` (token défini) au lieu de la réponse attendue |
| `UnsatisfiedDependencyException` / `NoSuchBeanDefinitionException` | classe hors du package `com.example.tasks`, annotation `@Component`/`@Service` manquante, ou deux beans du même type |
| `Failed to bind properties under 'tasks…'` | nom de propriété YAML différent du composant du record (`api-token` ↔ `apiToken`) |
| Test qui passe seul mais échoue en suite | état partagé : `repository.deleteAll()` manquant dans `@BeforeEach` |
| `ClassNotFoundException` sur une annotation de test | import Spring Boot 3 au lieu de Boot 4 (voir le skill `write-tests`) |
| Enum `status` refusé en query | conversion par nom Java (`DONE`) : converter manquant dans `WebConfig` |
| `tasks-mcp` : outil absent après modification | jar non reconstruit, ou OpenCode non relancé |

## 4. Hypothèse puis preuve

Énoncer l'hypothèse en une phrase, la vérifier par l'expérience la plus petite possible (un test ciblé, `.andDo(print())` sur MockMvc). Une seule modification à la fois.

## 5. Corriger la cause, pas le symptôme

- Interdit : `@Disabled`, affaiblir une assertion, `-DskipTests`, `-Dmaven.test.failure.ignore`, catch vide.
- Si le test est faux (il contredit `AGENTS.md` ou `docs/conventions.md`), le dire explicitement avant de le modifier.

## 6. Clôturer

`./mvnw -B verify`, puis résumer : cause, correction, test qui la couvre.
