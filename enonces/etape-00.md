# Étape 00 — Poste prêt

**Durée indicative** : 20 min

## Contexte

OpenCode pilote le modèle d'un provider. Il exécute ses outils dans un **service d'arrière-plan** qui a son propre environnement. Le projet demande un JDK 21.

## Ce que vous devez obtenir

1. OpenCode **2.x** installé.
2. Le provider Anthropic connecté, avec Opus 5.5, Sonnet 5.5 et Haiku 5.5 disponibles.
3. Un JDK 21 visible **par votre terminal et par le service d'OpenCode**.
4. Python 3.10 ou plus.

## Marche à suivre

1. Installez OpenCode 2 (`curl -fsSL https://opencode.ai/v2/install | bash`), ou passez une 1.x en 2 avec `opencode upgrade --major`. Puis `opencode --version` dans un nouveau terminal.
2. `opencode auth login` (Anthropic) : collez la clé dans l'invite, jamais en argument.
3. `opencode models | grep anthropic/` : relevez les trois identifiants, vous en aurez besoin à l'étape 03.
4. Définissez `JAVA_HOME` dans **votre profil**, ouvrez un nouveau terminal, puis `opencode service restart` depuis ce terminal : le service hérite de son environnement.

## Contraintes

- Aucune clé d'API dans un fichier du dépôt ni dans l'historique du shell.
- Pas de modification des variables du système entier si le poste ne vous appartient pas.

## Comment vérifier

| Commande | Attendu |
|---|---|
| `opencode --version` | 2.x |
| `opencode auth list` | Anthropic |
| `opencode models \| grep anthropic/` | les trois modèles |
| `java -version` | 21 |
| `opencode service status` | une URL locale |
| `python3 --version` | 3.10 ou plus |
| `opencode debug agents --standalone \| python3 ../outils/resume_agents.py`, depuis `projet/` | `build`, `plan`, `explore`, `general` |

## Questions à se poser

- Où OpenCode range-t-il les identifiants du provider (`opencode debug paths`) ? Ce dossier est-il dans le dépôt ?
- Pourquoi un outil lancé par l'agent pourrait-il ne pas voir une variable que votre terminal voit ?

## Documentation

- OpenCode 2 — Installation : https://opencode.ai/v2/docs
- OpenCode — Introduction : https://opencode.ai/docs/
- OpenCode — Providers : https://opencode.ai/docs/providers/
- OpenCode — CLI (`auth`, `models`, `service`) : https://opencode.ai/docs/cli/
