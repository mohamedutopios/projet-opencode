#!/usr/bin/env python3
"""Résume la sortie JSON de `opencode debug agents` (OpenCode 2.x), sans appel au modèle.

OpenCode 2 n'a plus `opencode agent list` ni `opencode debug agent <nom>` : ce script les remplace.

Usage, depuis la racine du projet :
  opencode debug agents --standalone | python3 resume_agents.py                 # tous les agents visibles
  opencode debug agents --standalone | python3 resume_agents.py tester          # un agent et ses règles
  opencode debug agents --standalone | python3 resume_agents.py tester skill    # ses règles d'une action

Actions de permission en V2 : read, edit, shell (ex-bash), webfetch, skill, subagent (ex-task),
external_directory, doom_loop... L'ordre affiché est l'ordre d'évaluation : la DERNIÈRE règle qui
correspond l'emporte.
Bibliothèque standard uniquement (Python 3.10+).
"""
from __future__ import annotations

import json
import sys


def main(argv: list[str]) -> int:
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
        sys.stderr.reconfigure(encoding="utf-8")
    texte = sys.stdin.read()
    try:
        agents = json.loads(texte[texte.index("["):])
    except (ValueError, json.JSONDecodeError):
        if not texte.strip() or "debug agent <name>" in texte:
            print("Aucun JSON reçu : `opencode debug agents` a échoué.\n"
                  "Cette commande n'existe qu'en OpenCode 2.x : vérifiez `opencode --version` (étape 00).",
                  file=sys.stderr)
        else:
            print("Entrée illisible : lancer `opencode debug agents --standalone | python3 resume_agents.py`",
                  file=sys.stderr)
        return 2
    nom = argv[1] if len(argv) > 1 else None
    action = argv[2] if len(argv) > 2 else None

    if nom is None:
        print(f"{'agent':<14} {'mode':<9} {'modèle':<34} temperature")
        for a in agents:
            if a.get("hidden"):
                continue
            modele = a.get("model") or {}
            modele = f"{modele.get('providerID')}/{modele.get('id')}" if modele else "(modèle global)"
            temp = ((a.get("request") or {}).get("body") or {}).get("temperature", "-")
            print(f"{a['id']:<14} {a.get('mode', ''):<9} {modele:<34} {temp}")
        return 0

    trouve = [a for a in agents if a["id"] == nom]
    if not trouve:
        print(f"Agent inconnu : {nom}. Agents : {', '.join(a['id'] for a in agents)}", file=sys.stderr)
        return 1
    agent = trouve[0]
    modele = agent.get("model") or {}
    print(f"# {nom} ({agent.get('mode')}) — modèle : "
          f"{modele.get('providerID', '')}/{modele.get('id', '(global)')}" if modele else
          f"# {nom} ({agent.get('mode')}) — modèle global")
    for regle in agent.get("permissions", []):
        if action and regle["action"] != action:
            continue
        print(f"  {regle['action']:<20} {regle['resource']:<60} {regle['effect']}")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
